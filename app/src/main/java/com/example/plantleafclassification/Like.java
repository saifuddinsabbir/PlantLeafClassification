package com.example.plantleafclassification;

public class Like {

    private String userName;
    private Boolean liked;

    public Like() {
    }

    public Like(String userName, Boolean liked) {
        this.userName = userName;
        this.liked = liked;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }
}
