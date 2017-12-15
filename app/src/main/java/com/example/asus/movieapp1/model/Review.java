package com.example.asus.movieapp1.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asus on 14-Dec-17.
 */

public class Review {

    @SerializedName("author")
    private String author;

    @SerializedName("content")
    private String content;

    public Review (String author, String content){
        this.author=author;
        this.content=content;
    }

    public String getAuthor(){
        return author;
    }

    public String getContent(){
        return content;
    }

    public  void setAuthor(String author){
        this.author=author;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
