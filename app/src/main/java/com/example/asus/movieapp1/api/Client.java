package com.example.asus.movieapp1.api;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by asus on 06-Dec-17.
 */

public class Client {

    public static final String BASE_URL="http://api.themoviedb.org/3/";
    public static Retrofit retrofit=null;

    public static Retrofit getClient(){

        if(retrofit==null){
            retrofit =new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

}
