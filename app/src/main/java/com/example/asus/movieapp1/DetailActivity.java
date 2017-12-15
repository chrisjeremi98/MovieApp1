package com.example.asus.movieapp1;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.asus.movieapp1.adapter.ReviewAdapter;
import com.example.asus.movieapp1.adapter.TrailerAdapter;
import com.example.asus.movieapp1.api.Client;
import com.example.asus.movieapp1.api.Service;
import com.example.asus.movieapp1.data.FavoriteContract;
import com.example.asus.movieapp1.data.FavoriteDbHelper;
import com.example.asus.movieapp1.model.Movie;
import com.example.asus.movieapp1.model.Review;
import com.example.asus.movieapp1.model.ReviewResponse;
import com.example.asus.movieapp1.model.Trailer;
import com.example.asus.movieapp1.model.TrailerResponse;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by asus on 06-Dec-17.
 */

public class DetailActivity extends AppCompatActivity {

    TextView nameOfMovie, plotSynopsis, userRating, releaseDate;
    ImageView imageView;
    private List<Trailer> trailerList;
    private TrailerAdapter trailerAdapter;
    private RecyclerView recyclerView;

    //review
    private List<Review> reviewList;
    private ReviewAdapter reviewAdapter;
    private RecyclerView recyclerViewReview;

    int movie_id;

    //database
    private FavoriteDbHelper favoriteDbHelper;
    private Movie favorite;
    private final AppCompatActivity activity = DetailActivity.this;
    private SQLiteDatabase mDb;

    private String thumbnail, movieName, rating, dateOfRelease, synopsis;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        favoriteDbHelper= new FavoriteDbHelper(this);
        mDb = favoriteDbHelper.getWritableDatabase();

        initColapsingToolbar();

        imageView = findViewById(R.id.thumbnail);
        nameOfMovie = findViewById(R.id.title);
        plotSynopsis = findViewById(R.id.plotsynopsis);
        userRating = findViewById(R.id.userrating);
        releaseDate = findViewById(R.id.releasedate);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra("original_title")) {
            thumbnail = getIntent().getExtras().getString("poster_path");
            movieName = getIntent().getExtras().getString("original_title");
            rating = getIntent().getExtras().getString("vote_average");
            dateOfRelease = getIntent().getExtras().getString("release_date");
            synopsis = getIntent().getExtras().getString("overview");
            movie_id = getIntent().getExtras().getInt("id");


            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500" + thumbnail)
                    .placeholder(R.drawable.load)
                    .into(imageView);

            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);
            userRating.setText(rating);
            releaseDate.setText(dateOfRelease);


        } else {
            Toast.makeText(this, "Tidak ada data API", Toast.LENGTH_SHORT).show();

        }
        MaterialFavoriteButton materialFavoriteButton =
                findViewById(R.id.favorite_button);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (Exists(movieName)) {
            materialFavoriteButton.setFavorite(true);
            materialFavoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    if (favorite == true) {
                        saveFavorite();
                        Snackbar.make(buttonView, "Ditambah Ke Favorit", Snackbar.LENGTH_SHORT).show();
                    } else {
                        favoriteDbHelper = new FavoriteDbHelper(DetailActivity.this);
                        favoriteDbHelper.deleteFavorite(movie_id);
                        Snackbar.make(buttonView, "Dihapus dari Favorit", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

        } else {

            materialFavoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                @Override
                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    if (favorite) {
                        SharedPreferences.Editor editor = getSharedPreferences("com.example.asus.movieapp1.DetailActivity", MODE_PRIVATE).edit();
                        editor.putBoolean("Favorit Ditambahkan", true);
                        editor.commit();
                        saveFavorite();
                        Snackbar.make(buttonView, "Ditambahkan ke Favorit", Snackbar.LENGTH_SHORT).show();
                    } else {
                        int movie_id = getIntent().getExtras().getInt("id");
                        favoriteDbHelper = new FavoriteDbHelper(DetailActivity.this);
                        favoriteDbHelper.deleteFavorite(movie_id);
                        SharedPreferences.Editor editor = getSharedPreferences("com.example.asus.movieapp1.DetailActivity", MODE_PRIVATE).edit();
                        editor.putBoolean("Favorit Dibuang", true);
                        editor.commit();
                        Snackbar.make(buttonView, "Dihapus dari favorit",
                                Snackbar.LENGTH_SHORT).show();

                    }
                }
            });
        }


        initViews();
    }

    public boolean Exists(String searchItem) {

        String[] projection = {
                FavoriteContract.FavoriteEntry._ID,
                FavoriteContract.FavoriteEntry.COLUMN_MOVIEID,
                FavoriteContract.FavoriteEntry.COLUMN_TITLE,
                FavoriteContract.FavoriteEntry.COLUMN_USERRATING,
                FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH,
                FavoriteContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS,
                FavoriteContract.FavoriteEntry.COLUMN_DATE_RELEASE
        };

        String selection= FavoriteContract.FavoriteEntry.COLUMN_TITLE+"=?";
        String[] selectionArgs={searchItem};
        String limit="1";

        Cursor cursor=mDb.query(FavoriteContract.FavoriteEntry.TABLE_NAME, projection,selection,selectionArgs,null,null,null,limit);
        boolean exists=(cursor.getCount()>0);
        cursor.close();
        return exists;


    }

    private void initColapsingToolbar() {

        final CollapsingToolbarLayout collapsingToolbarLayout =
                findViewById(R.id.collapsing_toolbar);

        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {

                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset == 0) {

                    collapsingToolbarLayout.setTitle(getString(R.string.movie_details));
                    isShow = true;
                } else if (isShow) {

                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }


            }
        });


    }

    private void initViews() {

        trailerList = new ArrayList<>();
        trailerAdapter = new TrailerAdapter(this, trailerList);
        recyclerView = findViewById(R.id.recycler_view1);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(trailerAdapter);
        trailerAdapter.notifyDataSetChanged();

        //review
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this, reviewList);
        recyclerViewReview = findViewById(R.id.recycler_view_review);
        RecyclerView.LayoutManager mLayoutManagerReview = new LinearLayoutManager(getApplicationContext());
        recyclerViewReview.setLayoutManager(mLayoutManagerReview);
        recyclerViewReview.setAdapter(reviewAdapter);
        reviewAdapter.notifyDataSetChanged();

        loadJson();

    }

    private void loadJson() {

        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Tidak ada API", Toast.LENGTH_SHORT).show();
                return;
            }

            Client Client = new Client();
            Service apiService = com.example.asus.movieapp1.api.Client.getClient().create(Service.class);
            Call<TrailerResponse> call = apiService.getMovieTrailer(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
            Call<ReviewResponse> callReview = apiService.getMovieReview(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    List<Trailer> trailer = response.body().getResults();
                    recyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), trailer));
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(DetailActivity.this, "Error Fetching", Toast.LENGTH_SHORT).show();
                }
            });

            callReview.enqueue(new Callback<ReviewResponse>() {
                @Override
                public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                    List<Review> review = response.body().getResults();
                    recyclerViewReview.setAdapter(new ReviewAdapter(getApplicationContext(), review));
                    recyclerViewReview.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<ReviewResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(DetailActivity.this, "Error Fetching", Toast.LENGTH_SHORT).show();
                }
            });


        } catch (Exception e) {
            Log.d("Errors", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


    }

    public void saveFavorite() {
        favoriteDbHelper = new FavoriteDbHelper(activity);
        favorite = new Movie();
        int movie_id = getIntent().getExtras().getInt("id");
        String rate = getIntent().getExtras().getString("vote_average");
        String poster = getIntent().getExtras().getString("poster_path");

        favorite.setId(movie_id);
        favorite.setOriginalTitle(nameOfMovie.getText().toString().trim());
        favorite.setPosterPath(poster);
        favorite.setVoteAverage(Double.parseDouble(rate));
        favorite.setOverview(plotSynopsis.getText().toString().trim());
        favorite.setReleaseDate(releaseDate.getText().toString().trim());

        favoriteDbHelper.addFavorite(favorite);

    }

}



