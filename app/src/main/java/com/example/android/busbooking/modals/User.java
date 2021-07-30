package com.example.android.busbooking.modals;

public class User {


    String uid="";
    String displayName ="";
    String imgUrl="";

    public User(String uid, String displayName, String imgUrl) {
        this.uid = uid;
        this.displayName = displayName;
        this.imgUrl = imgUrl;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }


}
