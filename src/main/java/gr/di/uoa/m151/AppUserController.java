package gr.di.uoa.m151;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.opsworks.model.App;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import gr.di.uoa.m151.entity.AppUser;
import gr.di.uoa.m151.entity.Post;
import gr.di.uoa.m151.entity.UserPostReaction;
import gr.di.uoa.m151.service.RestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@SessionAttributes({ "currentAppUser", "currentPost", "userPostsMap"})
public class AppUserController {

    private final RestClientService restClientService;

    private static final String IMAGE = "image";
    private static final String INDEX = "index";
    private static final String SIGN_UP = "signup";
    private static final String SIGN_IN = "signin";
    private static final String NEWPOST = "newpost";
    private static final String PEOPLE = "people";
    private static final String PROFILE = "profile";
    private static final String POST = "post";
    private static final String CURRENT_POST = "currentPost";
    private static final String POSTS = "posts";
    private static final String COMMENT_REACTION = "commentReaction";
    private static final String COMMENTS = "comments";

    private static final String USER_NAME = "user_name";

    private static final String NEW_APP_USER = "newAppUser";
    private static final String NEW_POST = "newPost";

    @Autowired
    public AppUserController(RestClientService restClientService) {
        this.restClientService = restClientService;
    }

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String indexPage(Model model) {
        //model.addAttribute(TITLE, "Home");
        return INDEX;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signUpPage(Model model) {
        //restClientService.uploadImageOnS3();
        model.addAttribute(NEW_APP_USER, new AppUser());
        return SIGN_UP;
    }

    @RequestMapping(value = "/signin", method = RequestMethod.GET)
    public String signInPage(Model model) {
        model.addAttribute(NEW_APP_USER, new AppUser());
        return SIGN_IN;
    }


    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registrationPage(@ModelAttribute AppUser newAppUser, @RequestParam("file") MultipartFile file) {
        boolean userExists = restClientService.checkIfAppUserExists(newAppUser.getEmail());
        if(!userExists) {
            String imagePathOnS3 = restClientService.uploadImageOnS3(newAppUser.getEmail(), file, 0);
            // upload the image on S3. Number 0 means it's a profile image for user newAppUser.
            newAppUser.setUserImage(imagePathOnS3); // save the path to the DB
            AppUser persistedUser = restClientService.signUp(newAppUser);
            if(persistedUser != null){
                return SIGN_IN;
            }
            else return SIGN_UP;
        }
        else
            return SIGN_UP;
    }


    @RequestMapping(value = {"/newpost"}, method = RequestMethod.GET)
    public String newpostPage(Model model) {
        model.addAttribute(NEW_POST, new Post());
        return NEWPOST;
    }

    @RequestMapping(value = "/addNewPost", method = RequestMethod.POST)
    public String addNewPost(@ModelAttribute Post newPost, Principal principal, @RequestParam("file") MultipartFile file, HttpSession session) {

        AppUser appUser = (AppUser) session.getAttribute("currentAppUser");
        if(appUser == null)
            appUser =restClientService.getUserData(principal.getName());

        String imagePathOnS3 =  restClientService.uploadImageOnS3(appUser.getEmail(), file, 1);
        // upload the image on S3. Number 1 means it's a post image for principal user in the current session.
        newPost.setPostImage(imagePathOnS3);
        appUser = restClientService.addNewPost(principal.getName(), newPost);

        if(appUser != null){
            session.setAttribute("currentAppUser", appUser);
            return INDEX;
        }
        else
            return NEWPOST;
    }

    @RequestMapping(value = { "/people" }, method = RequestMethod.GET)
    public String peoplePage(Model model) {
        return PEOPLE;
    }

    @RequestMapping(value = { "/profile" }, method = RequestMethod.GET)
    public String profilePage(Model model, Principal principal, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("currentAppUser");
        if(user == null){
            user =restClientService.getUserData(principal.getName());
            session.setAttribute("currentAppUser", user);
        }
        model.addAttribute(USER_NAME, user.getFullName());
        model.addAttribute(POSTS,user.getPosts());

        model.addAttribute(IMAGE,user.getUserImage());

        Map<Long, Post> userPostsMap = user.getPosts().stream().collect(Collectors.toMap(Post::getId, p -> p));
        session.setAttribute("userPostsMap", userPostsMap);

        return PROFILE;
    }

    @RequestMapping(value = {"/post/{id}" }, method = RequestMethod.GET)
    public String postPage(@PathVariable Long id, RedirectAttributes ra, HttpSession session) {
        Map<Long, Post> userPostsMap = (Map<Long, Post>) session.getAttribute("userPostsMap");
        if(userPostsMap == null) userPostsMap = new HashMap<>();
        Post post = userPostsMap.getOrDefault(id, null);
        if(post == null){
            post = restClientService.getPostData(id);
            userPostsMap.put(id, post);
        }
        session.setAttribute("userPostsMap", userPostsMap);
        ra.addFlashAttribute(CURRENT_POST, post);
        return "redirect:/post";
    }

    @RequestMapping(value = {"/post" }, method = RequestMethod.GET)
    public String postPage(Model model) {
        UserPostReaction commentReaction = new UserPostReaction();
        model.addAttribute(COMMENT_REACTION ,commentReaction);
        Post post = (Post) model.asMap().get(CURRENT_POST);
        List<UserPostReaction> comments = post.getUserReactions()
                .stream()
                .filter(r -> r.getReactionType().equals("COMMENT"))
                .collect(Collectors.toList());
        model.addAttribute(COMMENTS , comments);
        model.addAttribute(IMAGE,post.getPostImage());
        return POST;
    }

    @RequestMapping(value = "/addCommentReaction/{id}", method = RequestMethod.POST)
    public String addUserPostReaction(@PathVariable Long id, @ModelAttribute UserPostReaction commentReaction, Principal principal, Model model, HttpSession session) {
        Post persistedPost = restClientService.addCommentReaction(principal.getName(), id, commentReaction);
        if(persistedPost != null){

            Map<Long, Post> userPostsMap = (Map<Long, Post>) session.getAttribute("userPostsMap");
            userPostsMap.put(persistedPost.getId(), persistedPost);
            session.setAttribute("userPostsMap", userPostsMap);

            return "redirect:/profile";
        }
        else return POST;
    }
}

