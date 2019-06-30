package com.idealist.www.useopencvwithcmake;

public class Chat_Post_Item {

    String UrlString;
    String id;
    String postText;
    public boolean isUserLike;
    public String postLikeCount;
    public String commentCount;
    String time;


    public Chat_Post_Item(String UrlString, String id, String postText, boolean isUserLike , String postLikeCount, String commentCount,
                          String time) {
        this.UrlString = UrlString;
        this.id = id;
        this.postText = postText;
        this.isUserLike = isUserLike;
        this.postLikeCount = postLikeCount;
        this.commentCount = commentCount;
        this.time = time;
    }


    public String getUrlString() {return UrlString;}
    public String getId() {
        return id;
    }
    public String getPostText() {
        return postText;
    }
    public boolean getisUserLike() { return isUserLike; }
    public String getPostLikeCount() {
        return postLikeCount;
    }
    public String getCommentCount(){ return commentCount;}
    public String getTime(){return time;}

    public void setPostchecked(boolean ischecked){ this.isUserLike = ischecked;}
    public void setPostLikeCount_plus_one(){
        int intcount = Integer.parseInt(postLikeCount);
        intcount++;
        postLikeCount = intcount+"";
    }
    public void setPostLikeCount_minus_one(){
        int intcount = Integer.parseInt(postLikeCount);
        intcount--;
        postLikeCount = intcount+"";
    }
}
