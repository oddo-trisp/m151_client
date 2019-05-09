package gr.di.uoa.m151.service;

import gr.di.uoa.m151.entity.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestClientService {

    private final RestTemplate restTemplate;

    private final String REST_SERVER = "http://localhost:8080/";
    private final String SIGN_UP = "signup";


    @Autowired
    public RestClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    //Test call to server api
    public AppUser signUp(AppUser newAppUser){
        ResponseEntity<String> result = restTemplate.postForEntity(REST_SERVER+SIGN_UP, newAppUser, String.class);
        return newAppUser;
    }
}
