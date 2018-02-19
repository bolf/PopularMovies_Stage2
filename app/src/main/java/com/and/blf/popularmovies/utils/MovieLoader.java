package com.and.blf.popularmovies.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.and.blf.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {
    public static final String QUERY_MODE = "query_mode";

    public MovieLoader(Context context) {
        super(context);
    }

    @Override
    public List<Movie> loadInBackground() {
        ArrayList<Movie> movieList = new ArrayList<>();

        return movieList;
    }
}
