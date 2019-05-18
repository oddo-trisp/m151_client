package gr.di.uoa.m151.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Post {

    private Long id;

    private String postTitle;

    private String postText;

    private String postImage;

    private Timestamp creationDate;

    private AppUser appUser;

    private AppUser appUserShort;

    private List<UserPostReaction> userReactions = new ArrayList<>();

    private UserPostReaction likedByCurrentUser;


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

    public AppUser getAppUserShort() {
        return appUserShort;
    }

    public void setAppUserShort(AppUser appUserShort) {
        this.appUserShort = appUserShort;
    }

    public List<UserPostReaction> getUserReactions() {
        return userReactions;
    }

    public void setUserReactions(List<UserPostReaction> userReactions) {
        this.userReactions = userReactions;
    }

    public UserPostReaction getLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(UserPostReaction likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public UserPostReaction likedByUser(String email){
        likedByCurrentUser = userReactions.stream()
                .filter(r -> r.getReactionType() != null)
                .filter(r -> "LIKE".equals(r.getReactionType()))
                .filter(r -> email.equals(r.getAppUserShort().getEmail()))
                .findFirst().orElse(null);
        return likedByCurrentUser;

    }
}
