package com.and.blf.popularmovies.utils;

import android.content.Context;
import android.net.Uri;

import com.and.blf.popularmovies.R;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MovieNetworkUtils {
    private final static String IMAGE_BASE_URL = "https://image.tmdb.org/t/p";

    public static String buildImageRequestUrl(String sizePart, String picPath){
        Uri requestUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(sizePart)
                .appendEncodedPath(picPath)
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
}