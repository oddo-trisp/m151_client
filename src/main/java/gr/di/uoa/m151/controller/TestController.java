package gr.di.uoa.m151.controller;

import gr.di.uoa.m151.entity.AppUser;
import gr.di.uoa.m151.entity.Post;
import gr.di.uoa.m151.service.RestClientService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TestController {

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
    private static final String ENABLE_FOLLOW_BUTTON = "enableFollowButton";

    private static final String USER_NAME = "user_name";
    private static final String USER_ID = "user_id";

    private static final String NEW_APP_USER = "newAppUser";
    private static final String NEW_POST = "newPost";
    private static final String SUGGESTIONS = "suggestions";
    private static final String RECENT_POSTS = "recentPosts";

    public TestController(RestClientService restClientService) {
        this.restClientService = restClientService;
    }

    @RequestMapping(value = "/indexTest", method = RequestMethod.GET)
    public void  indexPage(Model model, @RequestParam String email) {
        List<AppUser> suggestions = restClientService.loadSuggestions(email, 12);
        List<Post> recentPosts = restClientService.loadRecentPosts(email);
        recentPosts.forEach(p -> p.likedByUser(email));
        model.addAttribute(SUGGESTIONS, suggestions);
        model.addAttribute(RECENT_POSTS, recentPosts);
    }

    @RequestMapping(value = { "/peopleTest" }, method = RequestMethod.GET)
    public void peoplePage(Model model,  @RequestParam String email) {
        List<AppUser> suggestions = restClientService.loadSuggestionsWithPosts(email);
        model.addAttribute(SUGGESTIONS, suggestions);
    }

    @RequestMapping(value = { "/profileTest" }, method = RequestMethod.GET)
    public void profilePageOtherUsers(@RequestParam String email, @RequestParam String id, Model model) {
       
        AppUser currentAppUser = restClientService.getUserData(email);

        AppUser user = restClientService.getUserData(Long.valueOf(id));
      
        model.addAttribute(USER_NAME, user.getFullName());
        model.addAttribute(USER_ID, user.getId());
        model.addAttribute(POSTS,user.getPosts());
        model.addAttribute(MY_PROFILE, Boolean.FALSE);
        model.addAttribute(ENABLE_FOLLOW_BUTTON,Boolean.TRUE);
        model.addAttribute(IMAGE,user.getUserImage());
        model.addAttribute(FOLLOWERS,user.getFollowersShort());
        model.addAttribute(FOLLOWINGS,user.getFollowingsShort());

        //Hack because object comparison isn't working on that case
        model.addAttribute(FOLLOWER_IDS,user.getFollowersShort().stream().map(AppUser::getId).collect(Collectors.toSet()));
        model.addAttribute(FOLLOWING_IDS,user.getFollowingsShort().stream().map(AppUser::getId).collect(Collectors.toSet()));

    }
}
