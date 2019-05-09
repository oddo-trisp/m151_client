package gr.di.uoa.m151.entity;

public class LikeReaction extends UserPostReaction {
    public LikeReaction(){

    }

    public LikeReaction(Post post, AppUser appUser){
        super(post, appUser);
    }
}
