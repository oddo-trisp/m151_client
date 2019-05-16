package gr.di.uoa.m151.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Post {

    private Long id;

    private String postTitle;

    private String postText;

    private String postImage = "";

    private Timestamp creationDate;

    private AppUser appUser;

    private List<UserPostReaction> userReactions = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public List<UserPostReaction> getUserReactions() {
        return userReactions;
    }

    public void setUserReactions(List<UserPostReaction> userReactions) {
        this.userReactions = userReactions;
    }

    public UserPostReaction likedByUser(String email){
        return userReactions.stream()
                .filter(r -> "LIKE".equals(r.getReactionType()))
                .filter(r -> email.equals(r.getUserEmail())).findFirst().orElse(null);

    }
}
