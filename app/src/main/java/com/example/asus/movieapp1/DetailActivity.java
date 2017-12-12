package com.example.asus.movieapp1;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.asus.movieapp1.adapter.TrailerAdapter;
import com.example.asus.movieapp1.api.Client;
import com.example.asus.movieapp1.api.Service;
import com.example.asus.movieapp1.model.Trailer;
import com.example.asus.movieapp1.model.TrailerResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by asus on 06-Dec-17.
 */

public class DetailActivity extends AppCompatActivity{

    TextView nameOfMovie, plotSynopsis, userRating, releaseDate;
    ImageView imageView;
    private List<Trailer> trailerList;
    private TrailerAdapter trailerAdapter;
    private RecyclerView recyclerView;

    int movie_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initColapsingToolbar();

        imageView=(ImageView) findViewById(R.id.thumbnail);
        nameOfMovie=(TextView) findViewById(R.id.title);
        plotSynopsis=(TextView) findViewById(R.id.plotsynopsis);
        userRating=(TextView) findViewById(R.id.userrating);
        releaseDate=(TextView) findViewById(R.id.releasedate);

        Intent intentThatStartedThisActivity=getIntent();
        if(intentThatStartedThisActivity.hasExtra("original_title")){
            String thumbnail=getIntent().getExtras().getString("poster_path");
            String movieName=getIntent().getExtras().getString("original_title");
            String rating=getIntent().getExtras().getString("vote_average");
            String dateOfRelease=getIntent().getExtras().getString("release_date");
            String synopsis=getIntent().getExtras().getString("overview");
            movie_id=getIntent().getExtras().getInt("id");


            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500"+thumbnail)
                    .placeholder(R.drawable.load)
                    .into(imageView);

            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);
            userRating.setText(rating);
            releaseDate.setText(dateOfRelease);


        }else {
            Toast.makeText(this,"No Api Data", Toast.LENGTH_SHORT).show();

        }

        initViews();
    }

    private void initColapsingToolbar(){

        final CollapsingToolbarLayout collapsingToolbarLayout=
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout=(AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow=false;
            int scrollRange= -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1){

                    scrollRange=appBarLayout.getTotalScrollRange();
                }

                if (scrollRange+verticalOffset==0 ){

                    collapsingToolbarLayout.setTitle(getString(R.string.movie_details));
                    isShow=true;
                } else if (isShow){

                    collapsingToolbarLayout.setTitle(" ");
                    isShow=false;
                }


            }
        });


    }

        private void initViews(){

            trailerList=new ArrayList<>();
            trailerAdapter=new TrailerAdapter(this,trailerList);
            recyclerView=(RecyclerView) findViewById(R.id.recycler_view1);
            RecyclerView.LayoutManager mLayoutManager=new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(trailerAdapter);
            trailerAdapter.notifyDataSetChanged();

            loadJson();

        }

        private void loadJson(){

                try{
                    if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                        Toast.makeText(getApplicationContext(),"Tidak ada API", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Client Client = new Client();
                    Service apiService=Client.getClient().create(Service.class);
                    Call<TrailerResponse> call=apiService.getMovieTrailer(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
                    call.enqueue(new Callback<TrailerResponse>() {
                        @Override
                        public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                            List<Trailer> trailer=response.body().getResults();
                            recyclerView.setAdapter(new TrailerAdapter(getApplicationContext(),trailer));
                            recyclerView.smoothScrollToPosition(0);
                        }

                        @Override
                        public void onFailure(Call<TrailerResponse> call, Throwable t) {
                            Log.d("Error", t.getMessage());
                            Toast.makeText(DetailActivity.this, "Error Fetching", Toast.LENGTH_SHORT).show();;
                        }
                    });


                } catch (Exception e){
                    Log.d("Errors", e.getMessage());
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }

        }

}



