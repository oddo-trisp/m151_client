package gr.di.uoa.m151.service;

import gr.di.uoa.m151.entity.AppUser;
import gr.di.uoa.m151.entity.Post;
import gr.di.uoa.m151.entity.UserPostReaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;

@Service
public class RestClientService implements UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    private final String REST_SERVER = "http://localhost:8580/";
    private final String SIGN_UP = "signup";
    private final String FIND_APPUSER_BY_EMAIL = "findAppUserByEmail";
    private final String FIND_POST_BY_ID = "findPostById";
    private final String ADD_NEW_POST = "addNewPost";
    private final String ADD_COMMENT_REACTION = "addCommentReaction";


    public boolean checkIfAppUserExists(String email){
        URI targetUrl= UriComponentsBuilder.fromUriString(REST_SERVER)  // Build the base link
                .path(FIND_APPUSER_BY_EMAIL)                            // Add path
                .queryParam("email", email)                                // Add one or more query params
                .build()                                                 // Build the URL
                .encode()                                                // Encode any URI items that need to be encoded
                .toUri();                                                // Convert to URI

        return restTemplate.getForObject(targetUrl, AppUser.class) != null;
    }

    public AppUser signUp(AppUser newAppUser){
        newAppUser.setEncryptedPassword(bCryptPasswordEncoder.encode(newAppUser.getPassword()));

        return restTemplate.postForEntity(REST_SERVER+SIGN_UP, newAppUser, AppUser.class).getBody();
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        URI targetUrl= UriComponentsBuilder.fromUriString(REST_SERVER)   // Build the base link
                .path(FIND_APPUSER_BY_EMAIL)                             // Add path
                .queryParam("email", userName)                     // Add one or more query params
                .build()                                                 // Build the URL
                .encode()                                                // Encode any URI items that need to be encoded
                .toUri();                                                // Convert to URI

        AppUser appUser = restTemplate.getForObject(targetUrl, AppUser.class);

        if (appUser == null) {
            System.out.println("User not found! " + userName);
            return null;
        }

        System.out.println("Found User: " + appUser);

        return new User(appUser.getEmail(), appUser.getEncryptedPassword(), new ArrayList<>());
    }

    public AppUser getUserData(String email){
        URI targetUrl= UriComponentsBuilder.fromUriString(REST_SERVER)  // Build the base link
                .path(FIND_APPUSER_BY_EMAIL)                            // Add path
                .queryParam("email", email)                       // Add one or more query params
                .build()                                                // Build the URL
                .encode()                                               // Encode any URI items that need to be encoded
                .toUri();                                               // Convert to URI

        return restTemplate.getForObject(targetUrl, AppUser.class);
    }

    public Post getPostData(Long id){
        URI targetUrl= UriComponentsBuilder.fromUriString(REST_SERVER)  // Build the base link
                .path(FIND_POST_BY_ID)                            // Add path
                .queryParam("id", id)                       // Add one or more query params
                .build()                                                // Build the URL
                .encode()                                               // Encode any URI items that need to be encoded
                .toUri();                                               // Convert to URI

        return restTemplate.getForObject(targetUrl, Post.class);
    }

    public AppUser addNewPost(String email, Post newPost){

        URI targetUrl= UriComponentsBuilder.fromUriString(REST_SERVER)  // Build the base link
                .path(ADD_NEW_POST)                                     // Add path
                .queryParam("email", email)                      // Add one or more query params
                .build()                                                 // Build the URL
                .encode()                                                // Encode any URI items that need to be encoded
                .toUri();                                                // Convert to URI

        return restTemplate.postForObject(targetUrl, newPost, AppUser.class);
    }

    public Post addCommentReaction(String email, Long postId ,UserPostReaction userPostReaction){

        URI targetUrl= UriComponentsBuilder.fromUriString(REST_SERVER)  // Build the base link
                .path(ADD_COMMENT_REACTION)                                     // Add path
                .queryParam("email", email)                      // Add one or more query params
                .queryParam("postId", postId)                      // Add one or more query params
                .build()                                                 // Build the URL
                .encode()                                                // Encode any URI items that need to be encoded
                .toUri();                                                // Convert to URI

        return restTemplate.postForObject(targetUrl, userPostReaction, Post.class);
    }
}
