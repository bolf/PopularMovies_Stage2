package com.and.blf.popularmovies.ui;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.model.Movie;
import com.and.blf.popularmovies.utils.MovieLoader;
import com.and.blf.popularmovies.utils.MovieNetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {
    private ProgressBar mLoadingIndicator;

    private ArrayList<Movie> movieList;
    private static final int MOVIE_LOADER_ID = 45;
    private int searchMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        Bundle queryBundle  = new Bundle();
        queryBundle.putString(MovieLoader.QUERY_MODE, searchMode == 0 ? MovieNetworkUtils.POPULAR_ENDPOINT : MovieNetworkUtils.TOP_RATED_ENDPOINT);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Movie>> movieLoader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if (movieLoader == null){
            loaderManager.initLoader(MOVIE_LOADER_ID,queryBundle,this);
        }else{
            loaderManager.restartLoader(MOVIE_LOADER_ID,queryBundle,this);
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        return new MovieLoader(this);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Toast.makeText(this,"#######",Toast.LENGTH_SHORT).show();
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
