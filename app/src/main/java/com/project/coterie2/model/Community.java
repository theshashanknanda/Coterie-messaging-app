package com.project.coterie2.model;

import java.util.ArrayList;

public class Community {
    private String name, imageURL, adminID;

    public Community(String name, String imageURL, String adminID) {
        this.name = name;
        this.imageURL = imageURL;
        this.adminID = adminID;
    }

    @Override
    public String toString() {
        return "Community{" +
                "name='" + name + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", adminID='" + adminID + '\'' +
                '}';
    }

    public Community(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }
}
