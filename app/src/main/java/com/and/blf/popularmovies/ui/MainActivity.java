package com.and.blf.popularmovies.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity {
    MovieRecyclerViewAdapter mAdapter;
    MovieService movieService;
    GridLayoutManager mLayoutManager;
    int curPageNum = 1;
    boolean isLoadingNow = false;
    ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        List<Movie> movieList = new ArrayList<>();
        mLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        RecyclerView movieRecyclerView = findViewById(R.id.rvMovies);
        movieRecyclerView.setHasFixedSize(true);
        movieRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MovieRecyclerViewAdapter(movieList);
        movieRecyclerView.setAdapter(mAdapter);

        movieService = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/")
                .addConverterFactory(MoshiConverterFactory.create())
                .client(MovieNetworkUtils.getHttpClient())
                .build()
                .create(MovieService.class);

        loadMovies(SharedPreferencesUtils.readFromSharedPreferences(this, getString(R.string.sharedPrefFileName), getString(R.string.sort_mode)), false);

        movieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mAdapter.getItemCount() - 10 < mLayoutManager.findLastCompletelyVisibleItemPosition() && !isLoadingNow) {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    if (networkIsAvailable()) {
                        isLoadingNow = true;
                        loadMovies(SharedPreferencesUtils.readFromSharedPreferences(MainActivity.this, getString(R.string.sharedPrefFileName), getString(R.string.sort_mode)), false);
                    } else if (dy < 0) {
                        mLoadingIndicator.setVisibility(View.GONE);
                    }
                }
            }
        });
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

    //Sorting menu handling
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_sort_menu, menu);
        String previousSortSetting = SharedPreferencesUtils.readFromSharedPreferences(this,getString(R.string.sharedPrefFileName),getString(R.string.sort_mode));
        switch (previousSortSetting){
            case "popular":
                menu.findItem(R.id.sortByPopularityMenuItem).setChecked(true);
                setAppTitle("popular");
                break;
            case "rated":
                menu.findItem(R.id.sortByRatingMenuItem).setChecked(true);
                setAppTitle("top_rated");
                break;
            default:
                menu.findItem(R.id.sortByPopularityMenuItem).setChecked(true);
                setAppTitle("popular");
        }
        return true;
    }

    public void onSortByPopularityMenuItemClick(MenuItem menuItem){
        curPageNum = 1;
        mLoadingIndicator.setVisibility(View.VISIBLE);
        menuItem.setChecked(true);
        setAppTitle("popular");
        SharedPreferencesUtils.writeToSharedPreferences(this,getString(R.string.sharedPrefFileName),getString(R.string.sort_mode),getString(R.string.sortByPopularity));
        loadMovies(getString(R.string.sortByPopularity), true);
    }

    public void onSortByRatingMenuItemClick(MenuItem menuItem){
        curPageNum = 1;
        mLoadingIndicator.setVisibility(View.VISIBLE);
        menuItem.setChecked(true);
        setAppTitle("rated");
        SharedPreferencesUtils.writeToSharedPreferences(this,getString(R.string.sharedPrefFileName),getString(R.string.sort_mode),getString(R.string.sortByRating));
        loadMovies(getString(R.string.sortByRating), true);
    }

    private void setAppTitle(String adding){
        setTitle(getString(R.string.app_name) + " (" + adding + ")");
    }

    private boolean networkIsAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}