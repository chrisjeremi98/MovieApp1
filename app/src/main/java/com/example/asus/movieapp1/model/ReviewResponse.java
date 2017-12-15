package com.example.asus.movieapp1.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by asus on 14-Dec-17.
 */

public class ReviewResponse {

    @SerializedName("id")
    private int id_trailer;

    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<Review> results;

    public int getId_trailer(){
        return id_trailer;
    }

    public void setId_Trailer(int trailer){
        this.id_trailer=trailer;
    }

    public  int getPage(){
        return page;
    }

    public  void setPage (int page){
        this.page=page;
    }

    public List<Review> getResults(){
        return results;
    }

    public void setResults(List<Review> results){
        this.results=results;
    }
}
