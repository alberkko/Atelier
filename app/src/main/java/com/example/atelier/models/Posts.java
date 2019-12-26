package com.example.atelier.models;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class Posts {

    private String description;
    private String image_url;
    private String mKey;
    private String userID;
    private String path;
    private String ts;
//    private HashMap<String, Object> timestampCreated;


    public Posts() {
    }

    public Posts(String ts){
        this.ts = ts;
    }

    public Posts(String path,String image_url, String description, String userID) {
        //if description field left empty
        if (description.trim().equals("")) {
            description = "";
        }


        this.image_url = image_url;
        this.description = description;
        this.userID = userID;
        this.path = path;
//        HashMap<String, Object> timestampNow = new HashMap<>();
//        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
//        this.timestampCreated = timestampNow;

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

    public String getPath() {
        return path;
    }

    public String getTs() {
        return ts;
    }

//    public HashMap<String, Object> getTimestampCreated(){
//        return timestampCreated;
//    }


    public void setDescription(String description) {
        this.description = description;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTs(String ts) {
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

//    @Exclude
//    public long getTimestampCreatedLong(){
//        return (long)timestampCreated.get("timestamp");
//    }

}