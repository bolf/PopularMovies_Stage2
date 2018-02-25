package com.and.blf.popularmovies.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.and.blf.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MovieNetworkUtils {
    private final static String MOVIE_BASE_URL = "https://api.themoviedb.org/3";
    public final static String POPULAR_ENDPOINT = "movie/popular";
    public final static String TOP_RATED_ENDPOINT = "movie/top_rated";
    final static String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w200";

    private final static String MALFORMED_URL_EXCEPTION_TAG = "MalformedURLException";
    private final static String IO_EXCEPTION_TAG = "IOException";

    public static String getMovies(@NonNull String endPoint, Context context){
        String moviesJson = null;
        URL requestUrl = null;
        try{
            requestUrl = buildMovieRequestUrlWithAPIKey(endPoint, context);
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
        }catch (IOException e){
            Log.d("getInputStream","MSG:"+e.getMessage() + "\nSTACK");
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        return "";
    }

    private static URL buildMovieRequestUrlWithAPIKey(String endpoint, Context context) throws MalformedURLException{
        Uri requestUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendEncodedPath(endpoint)
                .appendQueryParameter("api_key", context.getString(R.string.api_key))
                .build();

        return new URL(requestUri.toString());
    }

    public static String buildImageRequestUrl(String path){
        Uri requestUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(path).build();
        return requestUri.toString();
    }
}