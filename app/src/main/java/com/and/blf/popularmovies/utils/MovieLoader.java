package com.and.blf.popularmovies.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.and.blf.popularmovies.model.Movie;

import java.util.List;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {
    public MovieLoader(Context context) {
        super(context);
    }

    @Override
    public List<Movie> loadInBackground() {
        return TmpUtils.getMovieList();
    }
}