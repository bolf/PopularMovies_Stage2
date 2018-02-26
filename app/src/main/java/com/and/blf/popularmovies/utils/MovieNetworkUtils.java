package com.and.blf.popularmovies.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.and.blf.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MovieNetworkUtils {
    private final static String MOVIE_BASE_URL = "https://api.themoviedb.org/3";
    final static String POPULAR_ENDPOINT = "movie/popular";
    final static String TOP_RATED_ENDPOINT = "movie/top_rated";
    private final static String IMAGE_BASE_URL = "https://image.tmdb.org/t/p";


    static String getMovies(@NonNull String endPoint, Context context) throws IOException {
        URL requestUrl = buildMovieRequestUrlWithAPIKey(endPoint, context);
        return getResponseFromHttpUrl(requestUrl);
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
            e.printStackTrace();
            throw e;
        }
        finally {
            urlConnection.disconnect();
        }
    }

    private static URL buildMovieRequestUrlWithAPIKey(String endpoint, Context context) throws MalformedURLException{
        Uri requestUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendEncodedPath(endpoint)
                .appendQueryParameter("api_key", context.getString(R.string.api_key))
                .build();

        return new URL(requestUri.toString());
    }

    public static String buildImageRequestUrl(String sizePart, String picPath){
        Uri requestUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(sizePart)
                .appendEncodedPath(picPath)
                .build();
        return requestUri.toString();
    }
}