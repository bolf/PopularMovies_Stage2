package com.and.blf.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.model.movie.Movie;
import com.and.blf.popularmovies.model.movie.MovieWrapper;
import com.and.blf.popularmovies.persistence.MovieAsyncQueryHandler;
import com.and.blf.popularmovies.persistence.MovieContract;
import com.and.blf.popularmovies.retrofit.MovieService;
import com.and.blf.popularmovies.ui.recycler_view.MovieRecyclerViewAdapter;
import com.and.blf.popularmovies.utils.MovieNetworkUtils;
import com.and.blf.popularmovies.utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private String mCurrentSortMode;
    int mCurrentPageNum = 1;
    public boolean mIsLoadingNow = false;

    public final MovieRecyclerViewAdapter mAdapter = new MovieRecyclerViewAdapter(new ArrayList<Movie>());
    GridLayoutManager mLayoutManager;
    MovieService mMovieService;

    public ProgressBar mLoadingIndicator;
    private BottomNavigationView mBottomNavView;

    //listener for BottomNavigationView
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_popular:
                    if (mCurrentSortMode == getString(R.string.sortModeByPopularity)){break;}
                    mCurrentPageNum = 1;
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    setTitle(getString(R.string.title_popular));
                    SharedPreferencesUtils.writeToSharedPreferences(MainActivity.this, getString(R.string.sharedPrefFileName), getString(R.string.sort_mode), getString(R.string.sortModeByPopularity));
                    mCurrentSortMode = getString(R.string.sortModeByPopularity);
                    loadMovies(MovieAsyncQueryHandler.ASYNC_GET_FAVORITES_REPLACE_ADAPTER_LIST,getString(R.string.sortModeByPopularity), true);
                    return true;
                case R.id.navigation_top_rated:
                    if (mCurrentSortMode == getString(R.string.sortModeByRating)){break;}
                    mCurrentPageNum = 1;
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    setTitle(getString(R.string.title_top_rated));
                    SharedPreferencesUtils.writeToSharedPreferences(MainActivity.this, getString(R.string.sharedPrefFileName), getString(R.string.sort_mode), getString(R.string.sortModeByRating));
                    mCurrentSortMode = getString(R.string.sortModeByRating);
                    loadMovies(MovieAsyncQueryHandler.ASYNC_GET_FAVORITES_REPLACE_ADAPTER_LIST,getString(R.string.sortModeByRating), true);
                    return true;
                case R.id.navigation_marked:
                    if (mCurrentSortMode == getString(R.string.sortMode_favorite)){break;}
                    mIsLoadingNow = true;
                    mCurrentSortMode = getString(R.string.sortMode_favorite);
                    SharedPreferencesUtils.writeToSharedPreferences(MainActivity.this, getString(R.string.sharedPrefFileName), getString(R.string.sort_mode), getString(R.string.sortMode_favorite));
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    MovieAsyncQueryHandler asyncQueryHandler = new MovieAsyncQueryHandler(getContentResolver(), new WeakReference<Context>(MainActivity.this));
                    asyncQueryHandler.startQuery(MovieAsyncQueryHandler.ASYNC_GET_ALL_FAVORITES,
                            null,
                            MovieContract.FavoriteMovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                    setTitle(getString(R.string.title_favorite));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mBottomNavView = findViewById(R.id.navigation);
        RecyclerView movieRecyclerView = findViewById(R.id.rvMovies);

        mLayoutManager = new GridLayoutManager(MainActivity.this, getColumnCount());

        movieRecyclerView.setHasFixedSize(true);
        movieRecyclerView.setLayoutManager(mLayoutManager);
        movieRecyclerView.setAdapter(mAdapter);

        mMovieService = MovieNetworkUtils.getMovieService();

        mBottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setBottomNavigationViewSelectedItem();

        //endless scroll
        movieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mAdapter.getItemCount() - 10 < mLayoutManager.findLastVisibleItemPosition() && !mIsLoadingNow && mCurrentSortMode != getString(R.string.sortMode_favorite)) {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    if (MovieNetworkUtils.networkIsAvailable(MainActivity.this)) {
                        mIsLoadingNow = true;
                        loadMovies(MovieAsyncQueryHandler.ASYNC_GET_FAVORITES_NO_REPLACE_ADAPTER_LIST,SharedPreferencesUtils.readFromSharedPreferences(MainActivity.this, getString(R.string.sharedPrefFileName), getString(R.string.sort_mode)), false);
                    } else if (dy < 0) {
                        mLoadingIndicator.setVisibility(View.GONE);
                    }
                }
                if (dy < 0 || mAdapter.getItemCount() < 10) {
                    mBottomNavView.setVisibility(View.VISIBLE);
                } else {
                    mBottomNavView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setBottomNavigationViewSelectedItem() {
        String previousSortSetting = SharedPreferencesUtils.readFromSharedPreferences(this, getString(R.string.sharedPrefFileName), getString(R.string.sort_mode));
        switch (previousSortSetting) {
            case "popular":
                mBottomNavView.setSelectedItemId(R.id.navigation_popular);
                setTitle(getString(R.string.title_popular));
                break;
            case "top_rated":
                mBottomNavView.setSelectedItemId(R.id.navigation_top_rated);
                setTitle(getString(R.string.title_top_rated));
                break;
            case "favorite":
                mBottomNavView.setSelectedItemId(R.id.navigation_marked);
                setTitle(getString(R.string.title_favorite));
                break;
            default:
                mBottomNavView.setSelectedItemId(R.id.navigation_popular);
                setTitle(getString(R.string.title_popular));
        }

    }

    private void loadMovies(final int requestType, String endPoint, final boolean reloadAdapterCollection) {
        Call<MovieWrapper> wrapperCall = mMovieService.getMovies(endPoint, getString(R.string.api_key), mCurrentPageNum);
        wrapperCall.enqueue(new Callback<MovieWrapper>() {

            @Override
            public void onResponse(Call<MovieWrapper> call, Response<MovieWrapper> response) {
                List<Movie> lst = response.body().getResults();
                mAdapter.updateMovieList(requestType, lst, reloadAdapterCollection, getContentResolver(),new WeakReference<Context>(MainActivity.this));
                mCurrentPageNum++;
                mIsLoadingNow = false;
                mLoadingIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MovieWrapper> call, Throwable t) {
                Log.d(getString(R.string.loadingMovieListExceptionTag), t.getMessage());
                mIsLoadingNow = false;
                mLoadingIndicator.setVisibility(View.GONE);
            }
        });
    }

    int getColumnCount() {
        int cardViewWidth = 166; //150 + 16
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, cardViewWidth, r.getDisplayMetrics());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return metrics.widthPixels / px;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null){
            int locDbMovieId = data.getIntExtra(getString(R.string.locDbMovieID_extra), -1);
            int movieId = data.getIntExtra(getString(R.string.movieID_extra), -1);
            mAdapter.setLocalDbIdOnItem(movieId, locDbMovieId);
        }
    }
}
