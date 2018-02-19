package com.and.blf.popularmovies.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MovieNetworkUtils {
    private final static String MOVIE_BASE_URL = "https://api.themoviedb.org/3";
    public final static String POPULAR_ENDPOINT = "/movie/popular";
    public final static String TOP_RATED_ENDPOINT = "/movie/top_rated";
    final static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p";

    private final static String MALFORMED_URL_EXCEPTION_TAG = "MalformedURLException";
    private final static String IO_EXCEPTION_TAG = "IOException";

    public static String getMovies(@NonNull String endPoint){
        String moviesJson = null;
        URL requestUrl = null;
        try{
            requestUrl = buildMovieRequestUrl(endPoint);
        }catch (MalformedURLException e){
            Log.e(MALFORMED_URL_EXCEPTION_TAG,e.getMessage());
        }

        if(requestUrl != null){
            try{
                moviesJson = getResponseFromHttpUrl(requestUrl);
            }catch (IOException e){
                Log.e(IO_EXCEPTION_TAG,e.getMessage());
            }

        }
        return  moviesJson;
    }

    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private static URL buildMovieRequestUrl(String endpoint) throws MalformedURLException{
        Uri requestUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendEncodedPath(endpoint).build();

        return new URL(requestUri.toString());
    }
}