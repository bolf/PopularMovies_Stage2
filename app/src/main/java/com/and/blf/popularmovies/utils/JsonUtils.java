package com.and.blf.popularmovies.utils;

import android.content.Context;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    public static List<Movie> getMoviesFromJson(String jsonSource, Context context) throws JSONException, ParseException {
        List<Movie> movieList = new ArrayList<>();
        JSONObject rootObj = new JSONObject(jsonSource);
        JSONArray moviesJsonArray = rootObj.getJSONArray(context.getString(R.string.jsonNames_results));

        for (int i = 0; i < moviesJsonArray.length(); i++) {
            JSONObject jsonMovie = moviesJsonArray.getJSONObject(i);
            movieList.add(new Movie(jsonMovie.getInt(context.getString(R.string.jsonNames_id)),
                    jsonMovie.getString(context.getString(R.string.jsonNames_title)),
                    jsonMovie.getString(context.getString(R.string.jsonNames_poster_path)),
                    jsonMovie.getString(context.getString(R.string.jsonNames_backdrop_path)),
                    jsonMovie.getString(context.getString(R.string.jsonNames_overview)),
                    (float) jsonMovie.getDouble(context.getString(R.string.jsonNames_vote_average)),
                    jsonMovie.getString(context.getString(R.string.jsonNames_release_date))));
        }
        return movieList;
    }
}