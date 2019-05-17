package gr.di.uoa.m151.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import gr.di.uoa.m151.entity.AppUser;
import gr.di.uoa.m151.entity.Post;
import gr.di.uoa.m151.entity.UserPostReaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class RestClientService implements UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${aws.access-key}")
    private String awsKey;

    @Value("${aws.secret-key}")
    private String awsSecretKey;

    @Value("${aws.bucket}")
    private String bucketName;


    private final String REST_SERVER = "http://localhost:8580/";
    private final String SIGN_UP = "signup";
    private final String FIND_APPUSER_BY_EMAIL = "findAppUserByEmail";
    private final String FIND_LATEST_APP_USERS = "findLatestAppUsers";
    private final String FIND_POST_BY_ID = "findPostById";
    private final String ADD_NEW_POST = "addNewPost";
    private final String ADD_COMMENT_REACTION = "addCommentReaction";
    private final String ADD_LIKE_REACTION = "addLikeReaction";
    private final String REMOVE_LIKE_REACTION = "removeLikeReaction";
    private final String FOLLOW_USER = "followUser";


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

    private AmazonS3 buildS3client(){
        AWSCredentials credentials = new BasicAWSCredentials(
                awsKey,
                awsSecretKey
        );
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_WEST_2)
                .build();
    }

    public String uploadImageOnS3(String email, MultipartFile file, int p){

        AmazonS3 s3client = buildS3client();
        try {

            InputStream is = file.getInputStream();
            // The filename has the format userId_fn (eg. 18_me.png) which means user with id = 18 has profile image named
            // me.png. Adding the userId makes sure that there will never be an image with the same name.
            String fileName = generateFileNameForS3(email, file, p);
            s3client.putObject(new PutObjectRequest(bucketName,fileName,is,new ObjectMetadata())
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            return downloadImageFromS3(s3client, fileName);

        } catch (IOException ioe) {
            System.out.println("IOException when writing on S3");
        }

        return null;
    }

    public String generateFileNameForS3(String email, MultipartFile file, int p){
        String fileName;
        String fn = file.getOriginalFilename(); // original filename
        if(p==0) { // if p == 0 we add picture to the profile folder on S3
            fileName = "profile/" + email + "/" + fn;
        } else { // else we add picture to the post folder on S3
            fileName = "post/" + email + "/" + fn;
        }
        return fileName;
    }

    private String downloadImageFromS3(AmazonS3 s3client, String imagePath){

        S3Object s3object = s3client.getObject(bucketName, imagePath);
        //S3ObjectInputStream inputStream = s3object.getObjectContent();
        //FileUtils.copyInputStreamToFile(inputStream, new File("/Users/user/Desktop/hello.txt"));
        return s3object.getObjectContent().getHttpRequest().getURI().toString();
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

    public Post addLikeReaction(String email, Long postId){

        URI targetUrl= UriComponentsBuilder.fromUriString(REST_SERVER)  // Build the base link
                .path(ADD_LIKE_REACTION)                                     // Add path
                .queryParam("email", email)                      // Add one or more query params
                .queryParam("postId", postId)                      // Add one or more query params
                .build()                                                 // Build the URL
                .encode()                                                // Encode any URI items that need to be encoded
                .toUri();                                                // Convert to URI

        return restTemplate.postForObject(targetUrl, null, Post.class);
    }

    public Post removeLikeReaction(String email, Long postId, Long likeId){

        URI targetUrl= UriComponentsBuilder.fromUriString(REST_SERVER)  // Build the base link
                .path(REMOVE_LIKE_REACTION)                                     // Add path
                .queryParam("email", email)                      // Add one or more query params
                .queryParam("postId", postId)                      // Add one or more query params
                .queryParam("likeId", likeId)                      // Add one or more query params
                .build()                                                 // Build the URL
                .encode()                                                // Encode any URI items that need to be encoded
                .toUri();                                                // Convert to URI

        return restTemplate.postForObject(targetUrl, null, Post.class);
    }

    public List<AppUser> loadSuggestions(){
        return restTemplate.exchange(REST_SERVER+FIND_LATEST_APP_USERS, HttpMethod.GET, null, new ParameterizedTypeReference<List<AppUser>>(){}).getBody();
    }

    public AppUser followUser(String email, Long userId){

        URI targetUrl= UriComponentsBuilder.fromUriString(REST_SERVER)  // Build the base link
                .path(FOLLOW_USER)                                     // Add path
                .queryParam("email", email)                      // Add one or more query params
                .queryParam("userId", userId)                      // Add one or more query params
                .build()                                                 // Build the URL
                .encode()                                                // Encode any URI items that need to be encoded
                .toUri();

        return restTemplate.postForObject(targetUrl, null, AppUser.class);

    }
}
