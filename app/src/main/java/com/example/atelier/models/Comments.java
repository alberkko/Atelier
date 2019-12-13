package com.example.atelier.models;

import com.google.firebase.database.Exclude;

public class Comments {

    private String comment;
    private String profile_p_url;
    private String mKey;

    public Comments() {
    }

    public Comments(String comment) {
        this.comment = comment;
    }


    public String getComment() {
        return comment;
    }

    public String getProfile_p_url() {
        return profile_p_url;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setProfile_p_url(String profile_p_url) {
        this.profile_p_url = profile_p_url;
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
