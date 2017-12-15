package com.example.asus.movieapp1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.asus.movieapp1.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 15-Dec-17.
 */

public class FavoriteDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="favorite.db";
    private static final int DATABASE_VERSION=5;
    public static final String LOGTAG="FAVORITE";

    SQLiteOpenHelper dbHandler;
    SQLiteDatabase db;

    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open(){
        Log.i(LOGTAG,"Database Opened");
        db=dbHandler.getWritableDatabase();
    }

    public  void close(){
        Log.i(LOGTAG,"Database closed");
        dbHandler.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITE_TABLE ="CREATE TABLE " + FavoriteContract.FavoriteEntry.TABLE_NAME + " ("+
                FavoriteContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                FavoriteContract.FavoriteEntry.COLUMN_MOVIEID + " INTEGER, "+
                FavoriteContract.FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, "+
                FavoriteContract.FavoriteEntry.COLUMN_USERRATING + " REAL NOT NULL, "+
                FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "+
                FavoriteContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL, "+
                FavoriteContract.FavoriteEntry.COLUMN_DATE_RELEASE + " TEXT NOT NULL" + ");";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavoriteEntry.TABLE_NAME);
            onCreate(sqLiteDatabase);

    }

    public void addFavorite(Movie movie){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIEID, movie.getId());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, movie.getOriginalTitle());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_USERRATING, movie.getVoteAverage());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS, movie.getOverview());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_DATE_RELEASE,movie.getReleaseDate());

        db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null,values);
        db.close();

    }

    public void deleteFavorite(int id){

        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(FavoriteContract.FavoriteEntry.TABLE_NAME, FavoriteContract.FavoriteEntry.COLUMN_MOVIEID+"="+id,null);
    }

    public List<Movie> getAllFavorite(){

        String[] columns ={
                FavoriteContract.FavoriteEntry._ID,
                FavoriteContract.FavoriteEntry.COLUMN_MOVIEID,
                FavoriteContract.FavoriteEntry.COLUMN_TITLE,
                FavoriteContract.FavoriteEntry.COLUMN_USERRATING,
                FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH,
                FavoriteContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS,
                FavoriteContract.FavoriteEntry.COLUMN_DATE_RELEASE
        };

        String sortOrder=
                FavoriteContract.FavoriteEntry._ID +" ASC ";

        List<Movie> favoriteList=new ArrayList<>();

        SQLiteDatabase db=this.getReadableDatabase();

        Cursor cursor=db.query(FavoriteContract.FavoriteEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);

        if (cursor.moveToFirst()){
            do {
                Movie movie=new Movie();
                movie.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIEID))));
                movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE)));
                movie.setVoteAverage(Double.parseDouble(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_USERRATING))));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS)));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_DATE_RELEASE)));

                favoriteList.add(movie);

            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favoriteList;
    }

}
