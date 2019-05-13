package gr.di.uoa.m151.entity;


public class CommentReaction extends UserPostReaction {

    public CommentReaction(){

    }

    public CommentReaction(Post post, AppUser appUser){
        super(post, appUser);
    }

    private String commentTitle;

    private String commentText;

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

}
