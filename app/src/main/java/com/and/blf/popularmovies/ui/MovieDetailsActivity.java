package com.and.blf.popularmovies.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.model.Movie;
import com.and.blf.popularmovies.utils.MovieNetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {
    public static final String MOVIE_PARCEL = "movieDetails";

    private Movie movie;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        try {
            movie = intent.getParcelableExtra(MOVIE_PARCEL);
        }catch (NullPointerException e){
            Log.d(getString(R.string.PARCELABLE_EXCEPTION), Log.getStackTraceString(e));
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
        releaseDateTv.setText("Release date " + movie.getReleaseDate());

        TextView voteAverageTv = findViewById(R.id.vote_average);

        voteAverageTv.setText(getString(R.string.Raiting_tv) + Float.valueOf(movie.getVoteAverage()).toString());

        ImageView backdropIv = findViewById(R.id.backdrop);
        try{
            Picasso.with(this)
                    .load(MovieNetworkUtils.buildImageRequestUrl("w300",movie.getBackdropPath()))
                    .into(backdropIv);
        } catch (NullPointerException e) {
            Log.d(getString(R.string.PICASSO_EXCEPTION), Log.getStackTraceString(e));
            closeOnError();
        }

        ImageView posterIv = findViewById(R.id.poster);
        try{
            Picasso.with(this)
                    .load(MovieNetworkUtils.buildImageRequestUrl("w200",movie.getPosterPath()))
                    .into(posterIv);
        } catch (NullPointerException e) {
            Log.d(getString(R.string.PICASSO_EXCEPTION), Log.getStackTraceString(e));
            closeOnError();
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, "Couldn't get the detailed movie", Toast.LENGTH_SHORT).show();
    }

    public void mark_favorite(View view) {

    }
}
