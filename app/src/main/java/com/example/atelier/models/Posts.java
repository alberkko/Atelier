package com.example.atelier.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.List;

public class Posts {

    private String description;
    private String image_url;
    private String mKey;
    private String userID;
    //private HashMap<String, Object> timestampCreated;
    private String category;
    private String path;
    private long ts;

    public Posts() {
    }

    public Posts(long ts) {
        this.ts = ts;
    }

    public Posts(String path, String image_url, String description, String userID, String category) {
        //if description field left empty
        if (description.trim().equals("")) {
            description = "";
        }


        this.image_url = image_url;
        this.description = description;
        this.userID = userID;
        this.category = category;
        this.path = path;

        /*  HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;   */

    }

    public String getDescription() {
        return description;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getUserID() {
        return userID;
    }

    public String getCategory() {
        return category;
    }

    public String getPath() {
        return path;
    }

    public long getTs() {
        return ts;
    }


    /*
    public HashMap<String, Object> getTimestampCreated(){
        return timestampCreated;
    }
    */

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }


    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }

    /*  @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }   */
}