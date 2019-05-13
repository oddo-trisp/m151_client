package gr.di.uoa.m151;

import gr.di.uoa.m151.entity.AppUser;
import gr.di.uoa.m151.entity.Post;
import gr.di.uoa.m151.service.RestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
public class AppUserController {

    private final RestClientService restClientService;

    private static final String INDEX = "index";
    private static final String SIGN_UP = "signup";
    private static final String SIGN_IN = "signin";
    private static final String NEWPOST = "newpost";
    private static final String PEOPLE = "people";
    private static final String PROFILE = "profile";
    private static final String POST = "post";

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
        AppUser newAppUser = new AppUser();
        model.addAttribute(NEW_APP_USER, newAppUser);
        return SIGN_UP;
    }

    @RequestMapping(value = "/signin", method = RequestMethod.GET)
    public String signInPage(Model model) {
        AppUser newAppUser = new AppUser();
        model.addAttribute(NEW_APP_USER, newAppUser);
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
        Post newPost = new Post();
        model.addAttribute(NEW_POST, newPost);
        return NEWPOST;
    }

    @RequestMapping(value = "/addNewPost", method = RequestMethod.POST)
    public String registrationPage(@ModelAttribute Post newPost, Principal principal) {
        return restClientService.addNewPost(principal.getName(), newPost) != null
                ? INDEX : NEWPOST;
    }

    @RequestMapping(value = { "/people" }, method = RequestMethod.GET)
    public String peoplePage(Model model) {
        return PEOPLE;
    }

    @RequestMapping(value = { "/profile" }, method = RequestMethod.GET)
    public String profilePage(Model model, Principal principal) {
        AppUser user = restClientService.getUserData(principal.getName());
        model.addAttribute(USER_NAME, user.getFullName());
        model.addAttribute("posts",user.getPosts());
        return PROFILE;
    }

    @RequestMapping(value = {"/post" }, method = RequestMethod.GET)
    public String postPage(Model model) {
        return POST;
    }
}

