package gr.di.uoa.m151.service;

import gr.di.uoa.m151.entity.AppUser;
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
import java.util.HashMap;
import java.util.Map;

@Service
public class RestClientService implements UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    private final String REST_SERVER = "http://localhost:8080/";
    private final String SIGN_UP = "signup";
    private final String SIGN_IN = "signin";
    private final String FIND_APPUSER_BY_EMAIL = "findAppUserByEmail";


    public boolean checkIfAppUserExists(String email){
        URI targetUrl= UriComponentsBuilder.fromUriString(REST_SERVER)  // Build the base link
                .path(FIND_APPUSER_BY_EMAIL)                            // Add path
                .queryParam("email", email)                                // Add one or more query params
                .build()                                                 // Build the URL
                .encode()                                                // Encode any URI items that need to be encoded
                .toUri();                                                // Convert to URI

        return restTemplate.getForObject(targetUrl, AppUser.class) != null;
    }

    //Test call to server api
    public AppUser signUp(AppUser newAppUser){
        newAppUser.setEncryptedPassword(bCryptPasswordEncoder.encode(newAppUser.getPassword()));

        return restTemplate.postForEntity(REST_SERVER+SIGN_UP, newAppUser, AppUser.class).getBody();
    }

    public String signIn(AppUser newAppUser){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", newAppUser.getEmail());
        parameters.put("password", newAppUser.getPassword());
        return restTemplate.postForEntity(REST_SERVER+SIGN_IN, parameters, String.class).getBody();
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        URI targetUrl= UriComponentsBuilder.fromUriString(REST_SERVER)  // Build the base link
                .path(FIND_APPUSER_BY_EMAIL)                            // Add path
                .queryParam("email", userName)                                // Add one or more query params
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
}
