package com.example.androidpostsapp.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Post {

    private String id;
    private String authorUId;

    private String body;




    public Post() {

    }

    public Post(String authorUId, String body, String id) {
        this.id = id;
        this.authorUId = authorUId;
        this.body = body;


    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("authorUId", authorUId);
        result.put("body", body);



        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorUId() {
        return authorUId;
    }



    public void setAuthorUId(String authorUId) {
        this.authorUId = authorUId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }



}