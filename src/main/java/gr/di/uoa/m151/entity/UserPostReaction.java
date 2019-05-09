package gr.di.uoa.m151.entity;

import java.io.Serializable;

public abstract class UserPostReaction implements Serializable {

    private Long id;

    private Post post;

    private AppUser appUser;

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
}
