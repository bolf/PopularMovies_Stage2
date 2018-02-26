package com.and.blf.popularmovies.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.model.Movie;

public class MovieDetailsActivity extends AppCompatActivity {
    public static final String MOVIE_PARCEL = "movieDetails";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        Movie movie = null;
        try {
            movie = intent.getParcelableExtra(MOVIE_PARCEL);
        }catch (NullPointerException e){
            closeOnError();
        }
        populateUI(movie);
    }

    private void populateUI(Movie movie) {
        TextView titleTv = findViewById(R.id.movie_title);
        titleTv.setText(movie.getTitle());

        TextView overviewTv = findViewById(R.id.overview);
        overviewTv.setText(movie.getOverview());

        TextView releaseDateTv = findViewById(R.id.release_date);
        releaseDateTv.setText("2010-05-05");
        //releaseDateTv.setText(movie.getReleaseDate().toString());

        TextView voteAverageTv = findViewById(R.id.vote_average);
        voteAverageTv.setText(Float.valueOf(movie.getVoteAverage()).toString());
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, "couldn't get the detailed movie", Toast.LENGTH_SHORT).show();
    }
}
