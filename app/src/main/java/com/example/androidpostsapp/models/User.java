package com.example.androidpostsapp.models;

public class User {
    private String id;
    private String username;
    private String imageURL;
    private String birthday;
    private String gender;


    public User(String id, String username, String imageURL, String birthday, String gender) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.birthday = birthday;
        this.gender = gender;
    }

    public User() {
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
