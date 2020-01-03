package com.example.atelier.models;

import com.google.firebase.database.Exclude;

public class Comments {

    private String comment;
    private String mKey;
    private String c_userID;
    private String c_postID;

    public Comments() {
    }

    public Comments(String comment, String c_userID, String c_postID) {
        this.comment = comment;
        this.c_userID = c_userID;
        this.c_postID = c_postID;
    }

    public String getComment() {
        return comment;
    }

    public String getC_UserID() {
        return c_userID;
    }

    public String getC_PostID() {
        return c_postID;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setC_PostID(String c_postID) {
        this.c_postID = c_postID;
    }

    public void setC_UserID(String c_userID) {
        this.c_userID = c_userID;
    }


    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }

}
