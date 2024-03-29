package gr.di.uoa.m151.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppUser implements Serializable {

    private Long id;

    private String fullName;

    private String email;

    private String password;

    private String encryptedPassword;

    private String userImage;

    private Boolean enabled = true;

    private List<Post> posts = new ArrayList<>();

    private Set<AppUser> followingsShort = new HashSet<>();

    private Set<AppUser> followersShort = new HashSet<>();



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

    public String getUserImage() { return userImage; }

    public void setUserImage(String userImage) { this.userImage = userImage; }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public Set<AppUser> getFollowingsShort() {
        return followingsShort;
    }

    public void setFollowingsShort(Set<AppUser> followingsShort) {
        this.followingsShort = followingsShort;
    }

    public Set<AppUser> getFollowersShort() {
        return followersShort;
    }

    public void setFollowersShort(Set<AppUser> followersShort) {
        this.followersShort = followersShort;
    }
}
