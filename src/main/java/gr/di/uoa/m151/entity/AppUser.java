package gr.di.uoa.m151.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppUser implements Serializable {

    private Long id;

    private String fullName;

    private String email;

    private String password;

    private String encryptedPassword;

    private boolean enabled = true;

    private List<Post> posts = new ArrayList<>();

    private List<UserPostReaction> postReactions = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<UserPostReaction> getPostReactions() {
        return postReactions;
    }

    public void setPostReactions(List<UserPostReaction> postReactions) {
        this.postReactions = postReactions;
    }

    public void removePostReaction(UserPostReaction userPostReaction){
        postReactions.remove(userPostReaction);
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
}
