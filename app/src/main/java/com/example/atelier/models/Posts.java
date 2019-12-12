package com.example.atelier.models;
import com.google.firebase.database.Exclude;

public class Posts {

    private String description;
    private String image_url;
    private String mKey;
   // private String userID;

    public Posts() {
    }

    public Posts(String image_url, String description) {
        //if description field left empty
        if (description.trim().equals("")) {
            description = "";
        }

        this.image_url = image_url;
        this.description = description;
     //   this.userID = userID;
    }

    public String getDescription() {
        return description;
    }
    public String getImage_url() {
        return image_url;
    }
//    public String getUserID() {
//        return userID;
//    }


    public void setDescription(String description) {
        this.description = description;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
//    public void setUserID(String userID) {
//        this.userID = userID;
//    }


    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }

}