package gr.di.uoa.m151.entity;


public class CommentReaction extends UserPostReaction {

    public CommentReaction(){

    }

    public CommentReaction(Post post, AppUser appUser){
        super(post, appUser);
    }

    private String commentText;

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
