package com.example.plantleafclassification;

import com.google.firebase.database.ServerValue;

public class Comment {
    private String userImage, fullName, userName, content;
    private Object timestamp;

    public Comment() {
    }

    public Comment(String userImage, String fullName, String userName, String content) {
        this.userImage = userImage;
        this.fullName = fullName;
        this.userName = userName;
        this.content = content;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public Comment(String userImage, String fullName, String userName, String content, Object timestamp) {
        this.userImage = userImage;
        this.fullName = fullName;
        this.userName = userName;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
