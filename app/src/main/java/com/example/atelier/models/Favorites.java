package com.example.atelier.models;

import com.google.firebase.database.Exclude;

public class Favorites {

    private String image_url2;
    private String user_id;
    private String mKey;


    public Favorites() {
    }

    public Favorites(String image_url2, String user_id) {
        this.image_url2 = image_url2;
        this.user_id = user_id;
    }

    public String getImage_url2() {
        return image_url2;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setImage_url2(String image_url2) {
        this.image_url2 = image_url2;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

