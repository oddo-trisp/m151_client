package gr.di.uoa.m151.entity;

import java.io.Serializable;

public class UserPostReaction implements Serializable {

    private Long id;

    private Post post;

    private AppUser appUser;

    private String commentTitle;

    private String commentText;

    private String reactionType;

    private AppUser appUserShort;



    public UserPostReaction() {
    }

    public UserPostReaction(Post post, AppUser appUser) {
        this.post = post;
        this.appUser = appUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public String getCommentTitle() {
        return commentTitle;
    }

    public void setCommentTitle(String commentTitle) {
        this.commentTitle = commentTitle;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getReactionType() {
        return reactionType;
    }

    public void setReactionType(String reactionType) {
        this.reactionType = reactionType;
    }

    public AppUser getAppUserShort() {
        return appUserShort;
    }

    public void setAppUserShort(AppUser appUserShort) {
        this.appUserShort = appUserShort;
    }
}
