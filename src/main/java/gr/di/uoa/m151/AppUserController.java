package gr.di.uoa.m151;

import com.amazonaws.util.StringUtils;
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
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@SessionAttributes({ "currentAppUser", "userPostsMap"})
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
    private static final String LIKE_REACTION = "likeReaction";
    private static final String POSTS = "posts";
    private static final String FOLLOWERS = "followers";
    private static final String FOLLOWER_IDS = "followerIds";
    private static final String FOLLOWINGS = "followings";
    private static final String FOLLOWING_IDS = "followingIds";
    private static final String COMMENT_REACTION = "commentReaction";
    private static final String COMMENTS = "comments";
    private static final String MY_PROFILE = "myProfile";
    private static final String FOLLOWS = "follows";

    private static final String USER_NAME = "user_name";

    private static final String NEW_APP_USER = "newAppUser";
    private static final String NEW_POST = "newPost";
    private static final String SUGGESTIONS = "suggestions";
    private static final String RECENT_POSTS = "recentPosts";

    @Autowired
    public AppUserController(RestClientService restClientService) {
        this.restClientService = restClientService;
    }

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String indexPage(Model model, Principal principal) {
        List<AppUser> suggestions = restClientService.loadSuggestions(principal.getName());
        List<Post> recentPosts = restClientService.loadRecentPosts(principal.getName());
        recentPosts.forEach(p -> p.likedByUser(principal.getName()));
        model.addAttribute(SUGGESTIONS, suggestions);
        model.addAttribute(RECENT_POSTS, recentPosts);
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
            if(file != null && !StringUtils.isNullOrEmpty(file.getName())){
                String imagePathOnS3 = restClientService.uploadImageOnS3(newAppUser.getEmail(), file, 0);
                // upload the image on S3. Number 0 means it's a profile image for user newAppUser.
                newAppUser.setUserImage(imagePathOnS3); // save the path to the DB
            }
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

        if(file != null && !StringUtils.isNullOrEmpty(file.getName()))
        {
            String imagePathOnS3 =  restClientService.uploadImageOnS3(appUser.getEmail(), file, 1);
            // upload the image on S3. Number 1 means it's a post image for principal user in the current session.
            newPost.setPostImage(imagePathOnS3);
        }
        appUser = restClientService.addNewPost(principal.getName(), newPost);

        if(appUser != null){
            session.removeAttribute("currentAppUser");
            session.setAttribute("currentAppUser", appUser);
            return "redirect:/index";
        }
        else
            return NEWPOST;
    }

    @RequestMapping(value = { "/people" }, method = RequestMethod.GET)
    public String peoplePage(Model model, Principal principal) {
        List<AppUser> suggestions = restClientService.loadSuggestionsWithPosts(principal.getName());
        model.addAttribute(SUGGESTIONS, suggestions);
        return PEOPLE;
    }

    @RequestMapping(value = { "/profile" }, method = RequestMethod.GET)
    public String profilePage(Model model, Principal principal, HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("currentAppUser");
        if(user == null){
            user =restClientService.getUserData(principal.getName());
            session.removeAttribute("currentAppUser");
            session.setAttribute("currentAppUser", user);
        }
        model.addAttribute(USER_NAME, user.getFullName());
        model.addAttribute(POSTS,user.getPosts());

        model.addAttribute(FOLLOWERS,user.getFollowersShort());
        model.addAttribute(FOLLOWINGS,user.getFollowingsShort());

        //Hack because object comparison isn't working on that case
        model.addAttribute(FOLLOWER_IDS,user.getFollowersShort().stream().map(AppUser::getId).collect(Collectors.toSet()));
        model.addAttribute(FOLLOWING_IDS,user.getFollowingsShort().stream().map(AppUser::getId).collect(Collectors.toSet()));

        model.addAttribute(MY_PROFILE, Boolean.TRUE);
        model.addAttribute(IMAGE,user.getUserImage());

        Map<Long, Post> userPostsMap = user.getPosts().stream().collect(Collectors.toMap(Post::getId, p -> p));
        session.setAttribute("userPostsMap", userPostsMap);

        return PROFILE;
    }

    @RequestMapping(value = { "/profile/{id}" }, method = RequestMethod.GET)
    public String profilePage(Model model, @PathVariable Long id, @PathVariable Boolean follows) {
        AppUser user =restClientService.getUserData(id);

        model.addAttribute(USER_NAME, user.getFullName());
        model.addAttribute(POSTS,user.getPosts());
        model.addAttribute(MY_PROFILE, Boolean.FALSE);
        model.addAttribute(FOLLOWS, follows);

        model.addAttribute(IMAGE,user.getUserImage());

        return PROFILE;
    }

    @RequestMapping(value = {"/post/{id}" }, method = RequestMethod.GET)
    public String postPage(@PathVariable Long id, RedirectAttributes ra, HttpSession session, Principal principal) {
        Map<Long, Post> userPostsMap = (Map<Long, Post>) session.getAttribute("userPostsMap");
        AppUser currentUser = (AppUser) session.getAttribute("currentAppUser");
        if(currentUser == null){
            currentUser =restClientService.getUserData(principal.getName());
            session.removeAttribute("currentAppUser");
            session.setAttribute("currentAppUser", currentUser);
        }

        Post post;
        //That a post from currentUser
        if(principal.getName().equals(currentUser.getEmail())){
            if(userPostsMap == null) userPostsMap = new HashMap<>();
            post = userPostsMap.getOrDefault(id, null);
            if(post == null){
                post = restClientService.getPostData(id);
                userPostsMap.put(id, post);
            }
            session.setAttribute("userPostsMap", userPostsMap);
        }
        else
            post = restClientService.getPostData(id);

        post.likedByUser(currentUser.getEmail());
        ra.addFlashAttribute(CURRENT_POST, post);
        //ra.addFlashAttribute(LIKE_REACTION, post.likedByUser(currentUser.getEmail()));
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
    public String addCommentReaction(@PathVariable Long id, @ModelAttribute UserPostReaction commentReaction, Principal principal, HttpSession session) {
        Post persistedPost = restClientService.addCommentReaction(principal.getName(), id, commentReaction);
        return redirectToPost(id,persistedPost,session);
    }

    @RequestMapping(value = "/addLikeReaction/{id}", method = RequestMethod.GET)
    public String addLikeReaction(@PathVariable Long id, Principal principal, HttpSession session) {
        Post persistedPost = restClientService.addLikeReaction(principal.getName(), id);
        return redirectToPost(id, persistedPost, session);
    }

    @RequestMapping(value = "/removeLikeReaction/{id}/{likeId}/index", method = RequestMethod.GET)
    public String removeLikeReactionFromIndex(@PathVariable Long id, @PathVariable Long likeId, Principal principal) {
        restClientService.removeLikeReaction(principal.getName(), id, likeId);
        return "redirect:/index";
    }

    @RequestMapping(value = "/addLikeReaction/{id}/index", method = RequestMethod.GET)
    public String addLikeReactionFromIndex(@PathVariable Long id, Principal principal) {
        restClientService.addLikeReaction(principal.getName(), id);
        return "redirect:/index";
    }

    @RequestMapping(value = "/removeLikeReaction/{id}/{likeId}", method = RequestMethod.GET)
    public String removeLikeReaction(@PathVariable Long id, @PathVariable Long likeId, Principal principal, HttpSession session) {
        Post persistedPost = restClientService.removeLikeReaction(principal.getName(), id, likeId);
        return redirectToPost(id, persistedPost, session);
    }

    private String redirectToPost(Long id, Post persistedPost, HttpSession session){
        if(persistedPost != null){

            Map<Long, Post> userPostsMap = (Map<Long, Post>) session.getAttribute("userPostsMap");
            userPostsMap.put(persistedPost.getId(), persistedPost);
            session.setAttribute("userPostsMap", userPostsMap);

            return "redirect:/post/"+id;
        }
        return "redirect:/post/"+id;
    }

    @RequestMapping(value = {"/followUser/{id}" }, method = RequestMethod.GET)
    public String followUser(@PathVariable Long id, Principal principal){
        AppUser persistedUser = restClientService.followUser(principal.getName(), id);
        return "redirect:/index";
    }

}

