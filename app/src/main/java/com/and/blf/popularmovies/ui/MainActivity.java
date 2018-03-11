package com.and.blf.popularmovies.ui;

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
import com.and.blf.popularmovies.model.Movie;
import com.and.blf.popularmovies.model.MovieWrapper;
import com.and.blf.popularmovies.retrofit.MovieService;
import com.and.blf.popularmovies.ui.recycler_view.MovieRecyclerViewAdapter;
import com.and.blf.popularmovies.utils.MovieNetworkUtils;
import com.and.blf.popularmovies.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    MovieRecyclerViewAdapter mAdapter;
    MovieService movieService;
    GridLayoutManager mLayoutManager;
    int curPageNum = 1;
    boolean isLoadingNow = false;
    ProgressBar mLoadingIndicator;

    private BottomNavigationView botNavView;

    //listener for BottomNavigationView
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_popular:
                    curPageNum = 1;
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    setAppTitle("popular");
                    SharedPreferencesUtils.writeToSharedPreferences(MainActivity.this, getString(R.string.sharedPrefFileName), getString(R.string.sort_mode), getString(R.string.sortByPopularity));
                    loadMovies(getString(R.string.sortByPopularity), true);
                    return true;
                case R.id.navigation_top_rated:
                    curPageNum = 1;
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    setAppTitle("top_rated");
                    SharedPreferencesUtils.writeToSharedPreferences(MainActivity.this, getString(R.string.sharedPrefFileName), getString(R.string.sort_mode), getString(R.string.sortByRating));
                    loadMovies(getString(R.string.sortByRating), true);
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

        List<Movie> movieList = new ArrayList<>();
        mLayoutManager = new GridLayoutManager(MainActivity.this, getcolumnCount());
        RecyclerView movieRecyclerView = findViewById(R.id.rvMovies);
        movieRecyclerView.setHasFixedSize(true);
        movieRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MovieRecyclerViewAdapter(movieList);
        movieRecyclerView.setAdapter(mAdapter);

        movieService = MovieNetworkUtils.getMovieService();

        botNavView = findViewById(R.id.navigation);
        botNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setBottomNavigationViewSelectedItem();

        //endless scroll
        movieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mAdapter.getItemCount() - 10 < mLayoutManager.findLastCompletelyVisibleItemPosition() && !isLoadingNow) {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    if (MovieNetworkUtils.networkIsAvailable(MainActivity.this)) {
                        isLoadingNow = true;
                        loadMovies(SharedPreferencesUtils.readFromSharedPreferences(MainActivity.this, getString(R.string.sharedPrefFileName), getString(R.string.sort_mode)), false);
                    } else if (dy < 0) {
                        mLoadingIndicator.setVisibility(View.GONE);
                    }
                }
                if (dy < 0) {
                    botNavView.setVisibility(View.VISIBLE);
                } else {
                    botNavView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setBottomNavigationViewSelectedItem() {
        String previousSortSetting = SharedPreferencesUtils.readFromSharedPreferences(this, getString(R.string.sharedPrefFileName), getString(R.string.sort_mode));
        switch (previousSortSetting) {
            case "popular":
                botNavView.setSelectedItemId(R.id.navigation_popular);
                setAppTitle("Popular");
                break;
            case "top_rated":
                botNavView.setSelectedItemId(R.id.navigation_top_rated);
                setAppTitle("Top rated");
                break;
            default:
                botNavView.setSelectedItemId(R.id.navigation_popular);
                setAppTitle("popular");
        }

    }

    private void loadMovies(String endPoint, final boolean reloadAdapterCollection) {
        Call<MovieWrapper> wrapperCall = movieService.getMovies(endPoint, "1d0f6fe52ffd029bdfb40c1c3c780b73", curPageNum);
        wrapperCall.enqueue(new Callback<MovieWrapper>() {

            @Override
            public void onResponse(Call<MovieWrapper> call, Response<MovieWrapper> response) {
                List<Movie> lst = response.body().getResults();
                mAdapter.setMovieList(lst, reloadAdapterCollection);
                curPageNum++;
                isLoadingNow = false;
                mLoadingIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MovieWrapper> call, Throwable t) {
                Log.d("ON_FAILURE", t.getMessage());
                isLoadingNow = false;
                mLoadingIndicator.setVisibility(View.GONE);
            }
        });
    }

    private void setAppTitle(String adding){
        setTitle(getString(R.string.app_name) + " (" + adding + ")");
    }

    int getcolumnCount() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 166, r.getDisplayMetrics());

        int width = metrics.widthPixels;
        return width / px;
    }

}
