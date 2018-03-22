package com.and.blf.popularmovies.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.model.movie.Movie;
import com.and.blf.popularmovies.model.review.MovieReview;
import com.and.blf.popularmovies.model.review.MovieReviewWrapper;
import com.and.blf.popularmovies.model.trailer.TrailerWrapper;
import com.and.blf.popularmovies.persistence.MovieAsyncQueryHandler;
import com.and.blf.popularmovies.persistence.MovieContract;
import com.and.blf.popularmovies.ui.expandable_list_view.CustomExpandableListAdapter;
import com.and.blf.popularmovies.ui.recycler_view.TrailerRecyclerViewAdapter;
import com.and.blf.popularmovies.utils.MovieNetworkUtils;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {
    public static final String MOVIE_PARCEL = "movieDetails";
    private ImageButton imgBut;
    private Movie movie;
    private ExpandableListView reviewExpandableListView;
    private TextView trailerHeader;
    private TextView reviewHeader;
    List<String> expandableListTitle;
    ExpandableListAdapter expandableListAdapter;
    String[] trailerLst;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        imgBut = findViewById(R.id.imageButton_mark_favorite);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        try {
            movie = intent.getParcelableExtra(MOVIE_PARCEL);
        } catch (NullPointerException e) {
            Log.d(getString(R.string.PARCELABLE_EXCEPTION), Log.getStackTraceString(e));
            closeOnError();
        }
        populateUI(movie);
    }

    private void populateUI(Movie movie) {
        trailerHeader = findViewById(R.id.trailerHeader);
        reviewHeader = findViewById(R.id.reviews_header);
        TextView titleTv = findViewById(R.id.movie_title);
        titleTv.setText(movie.getTitle());

        TextView overviewTv = findViewById(R.id.overview);
        overviewTv.setText(movie.getOverview());

        TextView releaseDateTv = findViewById(R.id.release_date);
        releaseDateTv.setText("Release date " + movie.getReleaseDate());

        TextView voteAverageTv = findViewById(R.id.vote_average);

        voteAverageTv.setText(getString(R.string.Raiting_tv) + Float.valueOf(movie.getVoteAverage()).toString());

        ImageView backdropIv = findViewById(R.id.backdrop);
        try {
            Picasso.with(this)
                    .load(MovieNetworkUtils.buildImageRequestUrl("w300", movie.getBackdropPath()))
                    .into(backdropIv);
        } catch (NullPointerException e) {
            Log.d(getString(R.string.PICASSO_EXCEPTION), Log.getStackTraceString(e));
            closeOnError();
        }

        ImageView posterIv = findViewById(R.id.poster);
        try {
            Picasso.with(this)
                    .load(MovieNetworkUtils.buildImageRequestUrl("w200", movie.getPosterPath()))
                    .into(posterIv);
        } catch (NullPointerException e) {
            Log.d(getString(R.string.PICASSO_EXCEPTION), Log.getStackTraceString(e));
            closeOnError();
        }
        setMovieStarVisibility();

        if(MovieNetworkUtils.networkIsAvailable(this)){
            loadMovieReviews();
            loadTrailers();
        }else{
            trailerHeader.setVisibility(View.GONE);
            reviewHeader.setVisibility(View.GONE);
        }
    }

    private void loadTrailers() {
        Call<TrailerWrapper> wrapperCall = MovieNetworkUtils.getMovieService().getTrailers(String.valueOf(movie.getId()), "1d0f6fe52ffd029bdfb40c1c3c780b73");
        wrapperCall.enqueue(new Callback<TrailerWrapper>() {
            @Override
            public void onResponse(Call<TrailerWrapper> call, Response<TrailerWrapper> response) {
                trailerLst = response.body().getMovieThumbnailsKeys();
                if(trailerLst != null && trailerLst.length > 0){
                    findViewById(R.id.trailerHeader).setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<TrailerWrapper> call, Throwable t) {
                //TODO logCat

            }
        });

    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, "Couldn't get the detailed movie", Toast.LENGTH_SHORT).show();
    }

    public void mark_favorite(View view) {
        if (movie.getLocalDbId() > -1) { //it's removal
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_local_movies_black_24dp)
                    .setTitle("Removing from favorites")
                    .setMessage("Are you sure you want to remove " + movie.getTitle() + " from the favorite movie list?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MovieAsyncQueryHandler asyncQueryHandler = new MovieAsyncQueryHandler(getContentResolver(), new WeakReference<Context>(MovieDetailsActivity.this));
                            asyncQueryHandler.startDelete(MovieAsyncQueryHandler.ASYNC_DELETE_ID,
                                    null,
                                    MovieContract.FavoriteMovieEntry.CONTENT_URI,
                                    "_id = ?",
                                    new String[]{String.valueOf(movie.getLocalDbId())});
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            MovieAsyncQueryHandler asyncQueryHandler = new MovieAsyncQueryHandler(getContentResolver(), new WeakReference<Context>(this));
            ContentValues movieStruct = new ContentValues();
            movieStruct.put(MovieContract.FavoriteMovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
            movieStruct.put(MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, movie.getId());
            movieStruct.put(MovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            movieStruct.put(MovieContract.FavoriteMovieEntry.COLUMN_TITLE, movie.getTitle());
            movieStruct.put(MovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            movieStruct.put(MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            movieStruct.put(MovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            asyncQueryHandler.startInsert(MovieAsyncQueryHandler.ASYNC_WRITE_ID,
                    null,
                    MovieContract.FavoriteMovieEntry.CONTENT_URI,
                    movieStruct);
        }
    }

    public void setMovieStarVisibility(){
        if (movie.getLocalDbId() > -1) {
            imgBut.setImageResource(R.drawable.ic_star_golden_24dp);
        } else {
            imgBut.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
    }

    public void setMovieLocalDbId(int id) {
        movie.setLocalDbId(id);
        setMovieStarVisibility();
        Intent intent = new Intent();
        intent.putExtra("LocalDbmovieId",id);
        intent.putExtra("movieId",movie.getId());
        setResult(RESULT_OK, intent);
    }

    private void loadMovieReviews() {
        Call<MovieReviewWrapper> wrapperCall = MovieNetworkUtils.getMovieService().getReviews(String.valueOf(movie.getId()), "1d0f6fe52ffd029bdfb40c1c3c780b73");
        wrapperCall.enqueue(new Callback<MovieReviewWrapper>() {
            @Override
            public void onResponse(Call<MovieReviewWrapper> call, Response<MovieReviewWrapper> response) {
                List<MovieReview> lst = response.body().getResults();
                if(lst != null && lst.size() > 0 ){
                    MovieDetailsActivity.this.reviewHeader.setVisibility(View.VISIBLE);
                    HashMap<String, List<String>> expandableListDetail = new HashMap<>();
                    for(MovieReview mR : lst){

                        List<String> review = new ArrayList<>();
                        review.add(mR.getContent());

                        expandableListDetail.put(mR.getAuthor(), review);
                    }
                    reviewExpandableListView = findViewById(R.id.review_expandableListView);

                    expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
                    expandableListAdapter = new CustomExpandableListAdapter(MovieDetailsActivity.this, expandableListTitle, expandableListDetail);
                    reviewExpandableListView.setAdapter(expandableListAdapter);
                } else {
                    MovieDetailsActivity.this.reviewHeader.setVisibility(View.GONE);
                };
            }

            @Override
            public void onFailure(Call<MovieReviewWrapper> call, Throwable t) {
                Log.d("ON_FAILURE", t.getMessage());
            }
        });
    }

    public void setVisible_trailers_frame(View view) {
        if(findViewById(R.id.trailers_frame).getVisibility() == View.GONE) {
            findViewById(R.id.trailers_frame).setVisibility(View.VISIBLE);
            RecyclerView trailerRecyclerView = findViewById(R.id.tariler_list);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            TrailerRecyclerViewAdapter trailerAdapter = new TrailerRecyclerViewAdapter(trailerLst);
            trailerRecyclerView.setHasFixedSize(true);
            trailerRecyclerView.setLayoutManager(linearLayoutManager);
            trailerRecyclerView.setAdapter(trailerAdapter);
        }else{
            findViewById(R.id.trailers_frame).setVisibility(View.GONE);
        }
    }

    public void setVisible_reviews_frame(View view) {
        FrameLayout reviewsFl = findViewById(R.id.reviews_frame);
        if(reviewsFl.getVisibility() == View.GONE){
            reviewsFl.setVisibility(View.VISIBLE);
        }else{
            reviewsFl.setVisibility(View.GONE);
        }
    }
}