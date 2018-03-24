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
    private ImageButton mImgBut;
    private Movie mMovie;
    private FrameLayout mTrailers_frame;
    private FrameLayout mReviews_frame;
    private RecyclerView mTrailerRecyclerView;
    private LinearLayoutManager mTrailerLinearLayoutManager;
    private ExpandableListView mReviewExpandableListView;
    private TextView mReviewHeader;
    private List<String> mExpandableListTitle;
    private ExpandableListAdapter mExpandableListAdapter;
    private String[] mTrailerLst;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        try {
            mMovie = intent.getParcelableExtra(MOVIE_PARCEL);
        } catch (NullPointerException e) {
            Log.d(getString(R.string.PARCELABLE_EXCEPTION), Log.getStackTraceString(e));
            closeOnError();
        }

        mImgBut = findViewById(R.id.imageButton_mark_favorite);
        mReviewHeader = findViewById(R.id.reviews_header);
        mTrailers_frame = findViewById(R.id.trailers_frame);
        mReviews_frame = findViewById(R.id.reviews_frame);
        mTrailerRecyclerView = findViewById(R.id.trailer_rv);
        populateUI(mMovie);
    }

    private void populateUI(Movie movie) {
        TextView trailerHeader = findViewById(R.id.trailerHeader);
        TextView titleTv = findViewById(R.id.movie_title);
        titleTv.setText(movie.getTitle());

        TextView overviewTv = findViewById(R.id.overview);
        overviewTv.setText(movie.getOverview());

        TextView releaseDateTv = findViewById(R.id.release_date);
        releaseDateTv.setText(getString(R.string.release_date_text, movie.getReleaseDate()));

        TextView voteAverageTv = findViewById(R.id.vote_average);

        voteAverageTv.setText(getString(R.string.Rating_tv, movie.getVoteAverage()));

        ImageView backdropIv = findViewById(R.id.backdrop);
        try {
            Picasso.with(this)
                    .load(MovieNetworkUtils.buildImageRequestUrl(getString(R.string.backdrop_detail_image_size), movie.getBackdropPath()))
                    .into(backdropIv);
        } catch (NullPointerException e) {
            Log.d(getString(R.string.PICASSO_EXCEPTION), Log.getStackTraceString(e));
            closeOnError();
        }

        ImageView posterIv = findViewById(R.id.poster);
        try {
            Picasso.with(this)
                    .load(MovieNetworkUtils.buildImageRequestUrl(getString(R.string.detailed_poster_size), movie.getPosterPath()))
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
            mReviewHeader.setVisibility(View.GONE);
        }
    }

    private void loadTrailers() {
        Call<TrailerWrapper> wrapperCall = MovieNetworkUtils.getMovieService().getTrailers(String.valueOf(mMovie.getId()), getString(R.string.api_key));
        wrapperCall.enqueue(new Callback<TrailerWrapper>() {
            @Override
            public void onResponse(Call<TrailerWrapper> call, Response<TrailerWrapper> response) {
                if(response.body() != null){
                    mTrailerLst = response.body().getMovieThumbnailsKeys();
                    if(mTrailerLst.length > 0){
                        findViewById(R.id.trailerHeader).setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(Call<TrailerWrapper> call, Throwable t) {
                Log.d(getString(R.string.load_trailer_failure_tag),t.getMessage());
            }
        });

    }

    private void closeOnError() {
        Toast.makeText(this, R.string.couldnt_open_detail_msg,Toast.LENGTH_SHORT).show();
        finish();
    }

    public void mark_favorite(View view) {
        if (mMovie.getLocalDbId() > -1) { //it's removal
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_local_movies_black_24dp)
                    .setTitle(R.string.removing_form_favorites_title)
                    .setMessage(getString(R.string.removing_from_favorites_question, mMovie.getTitle()))
                    .setPositiveButton(R.string.answer_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MovieAsyncQueryHandler asyncQueryHandler = new MovieAsyncQueryHandler(getContentResolver(), new WeakReference<Context>(MovieDetailsActivity.this));
                            asyncQueryHandler.startDelete(MovieAsyncQueryHandler.ASYNC_DELETE_ID,
                                    null,
                                    MovieContract.FavoriteMovieEntry.CONTENT_URI,
                                    "_id = ?",
                                    new String[]{String.valueOf(mMovie.getLocalDbId())});
                        }
                    })
                    .setNegativeButton(R.string.answer_no, null)
                    .show();
        } else {
            MovieAsyncQueryHandler asyncQueryHandler = new MovieAsyncQueryHandler(getContentResolver(), new WeakReference<Context>(this));
            ContentValues movieStruct = new ContentValues();
            movieStruct.put(MovieContract.FavoriteMovieEntry.COLUMN_BACKDROP_PATH, mMovie.getBackdropPath());
            movieStruct.put(MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
            movieStruct.put(MovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
            movieStruct.put(MovieContract.FavoriteMovieEntry.COLUMN_TITLE, mMovie.getTitle());
            movieStruct.put(MovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE, mMovie.getVoteAverage());
            movieStruct.put(MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
            movieStruct.put(MovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH, mMovie.getPosterPath());
            asyncQueryHandler.startInsert(MovieAsyncQueryHandler.ASYNC_WRITE_ID,
                    null,
                    MovieContract.FavoriteMovieEntry.CONTENT_URI,
                    movieStruct);
        }
    }

    public void setMovieStarVisibility(){
        if (mMovie.getLocalDbId() > -1) {
            mImgBut.setImageResource(R.drawable.ic_star_golden_24dp);
        } else {
            mImgBut.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
    }

    public void setMovieLocalDbId(int id) {
        mMovie.setLocalDbId(id);
        setMovieStarVisibility();
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.locDbMovieID_extra),id);
        intent.putExtra(getString(R.string.movieID_extra),mMovie.getId());
        setResult(RESULT_OK, intent);
    }

    private void loadMovieReviews() {
        Call<MovieReviewWrapper> wrapperCall = MovieNetworkUtils.getMovieService().getReviews(String.valueOf(mMovie.getId()), getString(R.string.api_key));
        wrapperCall.enqueue(new Callback<MovieReviewWrapper>() {
            @Override
            public void onResponse(Call<MovieReviewWrapper> call, Response<MovieReviewWrapper> response) {
                try {
                    List<MovieReview> lst = response.body().getResults();
                    if (lst != null && lst.size() > 0) {
                        MovieDetailsActivity.this.mReviewHeader.setVisibility(View.VISIBLE);
                        HashMap<String, List<String>> expandableListDetail = new HashMap<>();
                        for (MovieReview mR : lst) {

                            List<String> review = new ArrayList<>();
                            review.add(mR.getContent());

                            expandableListDetail.put(mR.getAuthor(), review);
                        }
                        mReviewExpandableListView = findViewById(R.id.review_expandableListView);

                        mExpandableListTitle = new ArrayList<>(expandableListDetail.keySet());
                        mExpandableListAdapter = new CustomExpandableListAdapter(MovieDetailsActivity.this, mExpandableListTitle, expandableListDetail);
                        mReviewExpandableListView.setAdapter(mExpandableListAdapter);
                    } else {
                        MovieDetailsActivity.this.mReviewHeader.setVisibility(View.GONE);
                    }
                } catch (NullPointerException e){
                    Log.d(getString(R.string.load_reviews_failure),e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<MovieReviewWrapper> call, Throwable t) {
                Log.d(getString(R.string.load_reviews_failure), t.getMessage());
            }
        });
    }

    public void setVisible_trailers_frame(View view) {
        if(mTrailers_frame.getVisibility() == View.GONE) {
            mTrailers_frame.setVisibility(View.VISIBLE);
            if (mTrailerLinearLayoutManager == null) {
                mTrailerLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                TrailerRecyclerViewAdapter trailerAdapter = new TrailerRecyclerViewAdapter(mTrailerLst);
                mTrailerRecyclerView.setHasFixedSize(true);
                mTrailerRecyclerView.setLayoutManager(mTrailerLinearLayoutManager);
                mTrailerRecyclerView.setAdapter(trailerAdapter);
            }
        }else{
            mTrailers_frame.setVisibility(View.GONE);
        }
    }

    public void setVisible_reviews_frame(View view) {
        if(mReviews_frame.getVisibility() == View.GONE){
            mReviews_frame.setVisibility(View.VISIBLE);
        }else{
            mReviews_frame.setVisibility(View.GONE);
        }
    }
}