package com.example.plantleafclassification;

import com.google.firebase.database.ServerValue;

public class Post {
    private String postKey;
    private String description;
    private float myRating;
    private String postImage;
    private String userId;
    private Object timeStamp;
    private int likes;
    private int comments;

    public Post(String description, float myRating, String postImage, String userId, int likes, int comments) {
        this.description = description;
        this.myRating = myRating;
        this.postImage = postImage;
        this.userId = userId;
        this.timeStamp = ServerValue.TIMESTAMP;
        this.likes = likes;
        this.comments = comments;
    }

    public Post() {
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public float getMyRating() {
        return myRating;
    }

    public void setMyRating(float myRating) {
        this.myRating = myRating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}
