package com.example.atelier.models;
import com.google.firebase.database.Exclude;

public class Users {

    private String name;
    private String email;
    private String mKey;
    // private String userID;

    public Users() {
    }

    public Users(String name, String email) {
        //if description field left empty
        if (name.trim().equals("")) {
            name = "Guest";
        }

        this.name = name;
        this.email = email;
        //   this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
//    public String getUserID() {
//        return userID;
//    }


    public void setEmail(String email) {
        this.email = email;
    }
    public void setName(String name) {
        this.name = name;
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