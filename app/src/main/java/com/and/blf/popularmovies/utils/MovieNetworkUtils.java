package com.and.blf.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.and.blf.popularmovies.retrofit.MovieService;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;


public class MovieNetworkUtils {
    private final static String IMAGE_BASE_URL = "https://image.tmdb.org/t/p";

    private final static String TRAILER_THUMBNAIL_IMAGE_URI = "https://img.youtube.com/vi";

    public static String buildImageRequestUrl(String sizePart, String picPath){
        Uri requestUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(sizePart)
                .appendEncodedPath(picPath)
                .build();
        return requestUri.toString();
    }

    public static String buildTrailerThumbnailRequestUrl(String trailerKey){
        Uri requestUri = Uri.parse(TRAILER_THUMBNAIL_IMAGE_URI).buildUpon()
                .appendPath(trailerKey)
                .appendPath("hqdefault.jpg")
                .build();
        return requestUri.toString();
    }

    public static OkHttpClient getHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();
    }

    public static MovieService getMovieService() {
        return new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/")
                .addConverterFactory(MoshiConverterFactory.create())
                .client(getHttpClient())
                .build()
                .create(MovieService.class);
    }

    public static boolean networkIsAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}