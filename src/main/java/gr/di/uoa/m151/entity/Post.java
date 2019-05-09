package gr.di.uoa.m151.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Post {

    private Long id;

    private String postText;

    private Timestamp creationDate;

    private AppUser appUser;

    private List<UserPostReaction> userReactions = new ArrayList<>();



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
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

    public void addUserReaction(AppUser appUser) {
        UserPostReaction userPostReaction = new LikeReaction(this, appUser);
        userReactions.add(userPostReaction);
    }

    public void removeUserReaction(AppUser appUser) {
        userReactions.stream()
                .filter(ur -> this.equals(ur.getPost()))
                .filter(ur -> appUser.equals(ur.getAppUser()))
                .forEach(ur ->{
                    userReactions.remove(ur);
                    ur.getAppUser().removePostReaction(ur);
                    ur.setPost(null);
                    ur.setAppUser(null);
                });
        //userReactions.forEach(ur -> {
        //    if(ur.getPost().equals(this) && ur.getAppUser().equals(appUser)){
        //        userReactions.remove(ur);
        //        ur.getAppUser().getPostReactions().remove(ur);
        //        ur.setPost(null);
        //        ur.setAppUser(null);
        //    }
        //});
    }
}
