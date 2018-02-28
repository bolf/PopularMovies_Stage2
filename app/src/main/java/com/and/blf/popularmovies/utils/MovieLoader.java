package com.and.blf.popularmovies.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.model.Movie;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {
    public MovieLoader(Context context) {
        super(context);
    }

    @Override
    public List<Movie> loadInBackground() {
        //get the sorting mode from the SharedPreferences
        String previousSortSetting = SharedPreferencesUtils.readFromSharedPreferences(getContext(),
                getContext().getString(R.string.sharedPrefFileName),
                getContext().getString(R.string.sort_mode));
        String requestEndPoint;
        if (previousSortSetting.equals(getContext().getString(R.string.sortByRating))) {
            requestEndPoint = MovieNetworkUtils.TOP_RATED_ENDPOINT;            
        } else {
            requestEndPoint = MovieNetworkUtils.POPULAR_ENDPOINT;
        }

        //get the movies from the server
        try {
            String movieServiceJsonResponse = MovieNetworkUtils.getMovies(requestEndPoint, getContext());
            if (!TextUtils.isEmpty(movieServiceJsonResponse)){
                return JsonUtils.getMoviesFromJson(movieServiceJsonResponse, getContext());
            }
        } catch (IOException e) {
            Log.d(getContext().getString(R.string.IO_EXCEPTION), e.getMessage() + "\n" + Log.getStackTraceString(e));
        } catch (JSONException e){
            Log.d(getContext().getString(R.string.JSON_EXCEPTION), e.getMessage() + "\n" + Log.getStackTraceString(e));
        } catch (ParseException e){
            Log.d(getContext().getString(R.string.PARSE_EXCEPTION), e.getMessage() + "\n" + Log.getStackTraceString(e));
        }
        return null;
    }
}