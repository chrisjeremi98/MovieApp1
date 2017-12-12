package com.example.asus.movieapp1.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by asus on 08-Dec-17.
 */

public class TrailerResponse {

    @SerializedName("id")
    private int id_trailer;
    @SerializedName("results")
    private List<Trailer> results;

    public int getId_trailer(){
        return id_trailer;
    }

    public void setId_trailer(int trailer){

        this.id_trailer=trailer;
    }

    public List<Trailer> getResults(){
        return  results;
    }

    public void setResults(List<Trailer> results){
        this.results=results;
    }


}
