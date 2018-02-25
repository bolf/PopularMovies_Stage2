package com.and.blf.popularmovies.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.model.Movie;

import java.util.List;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {
    public MovieLoader(Context context) {
        super(context);
    }

    @Override
    public List<Movie> loadInBackground() {
        String previousSortSetting = SharedPreferencesUtils.readFromSharedPreferences(getContext(),
                                                getContext().getString(R.string.sharedPrefFileName),
                                                getContext().getString(R.string.sort_mode));
        String requestEndPoint = null;
        if(previousSortSetting.equals(getContext().getString(R.string.sortByPopularity))){
            requestEndPoint = MovieNetworkUtils.POPULAR_ENDPOINT;
        }else
            if(previousSortSetting.equals(getContext().getString(R.string.sortByRating))){
                requestEndPoint = MovieNetworkUtils.TOP_RATED_ENDPOINT;
        }
        if(requestEndPoint == null) return null;
        String movieServiceJsonResponse = MovieNetworkUtils.getMovies(requestEndPoint, getContext());
        if(! TextUtils.isEmpty(movieServiceJsonResponse)){

        }

        return TmpUtils.getMovieList();
    }
}