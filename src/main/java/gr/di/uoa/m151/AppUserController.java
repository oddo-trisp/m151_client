package gr.di.uoa.m151;

import gr.di.uoa.m151.entity.AppUser;
import gr.di.uoa.m151.entity.Post;
import gr.di.uoa.m151.entity.UserPostReaction;
import gr.di.uoa.m151.service.RestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@SessionAttributes({ "currentAppUser", "currentPost", "userPostsMap"})
public class AppUserController {

    private final RestClientService restClientService;

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
        model.addAttribute(NEW_APP_USER, new AppUser());
        return SIGN_UP;
    }

    @RequestMapping(value = "/signin", method = RequestMethod.GET)
    public String signInPage(Model model) {
        model.addAttribute(NEW_APP_USER, new AppUser());
        return SIGN_IN;
    }


    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registrationPage(@ModelAttribute AppUser newAppUser) {
        return restClientService.checkIfAppUserExists(newAppUser.getEmail()) ? SIGN_UP
                : restClientService.signUp(newAppUser) != null
                ? SIGN_IN : SIGN_UP;
    }


    @RequestMapping(value = {"/newpost"}, method = RequestMethod.GET)
    public String newpostPage(Model model) {
        model.addAttribute(NEW_POST, new Post());
        return NEWPOST;
    }

    @RequestMapping(value = "/addNewPost", method = RequestMethod.POST)
    public String addNewPost(@ModelAttribute Post newPost, Principal principal) {
        return restClientService.addNewPost(principal.getName(), newPost) != null
                ? INDEX : NEWPOST;
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
        return POST;
    }

    @RequestMapping(value = "//addCommentReaction/{id}", method = RequestMethod.POST)
    public String addUserPostReaction(@PathVariable Long id, @ModelAttribute UserPostReaction commentReaction, Principal principal, Model model) {
        return restClientService.addCommentReaction(principal.getName(), id, commentReaction) != null
                ? PROFILE : POST;
    }
}

