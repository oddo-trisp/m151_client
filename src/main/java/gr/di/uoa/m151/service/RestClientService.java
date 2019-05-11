package gr.di.uoa.m151.service;

import gr.di.uoa.m151.entity.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class RestClientService implements UserDetailsService {

    private final RestTemplate restTemplate;

    private final String REST_SERVER = "http://localhost:8080/";
    private final String SIGN_UP = "signup";
    private final String SIGN_IN = "signin";
    private final String FIND_APPUSER_BY_EMAIL = "findAppUserByEmail";


    @Autowired
    public RestClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public boolean checkIfAppUserExists(String email){
        return restTemplate.getForEntity(REST_SERVER + FIND_APPUSER_BY_EMAIL, AppUser.class, email).getBody() != null;
    }

    //Test call to server api
    public AppUser signUp(AppUser newAppUser){
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

        Map<String, String> parameters = new HashMap<>();
        parameters.put("email", userName);
        AppUser appUser = restTemplate.getForObject(REST_SERVER + FIND_APPUSER_BY_EMAIL, AppUser.class, parameters);

        if (appUser == null) {
            System.out.println("User not found! " + userName);
            return null;
        }

        System.out.println("Found User: " + appUser);

        return new User(appUser.getEmail(), appUser.getPassword(), new ArrayList<>());
    }
}
