package com.and.blf.popularmovies.retrofit;

import com.and.blf.popularmovies.model.MovieReviewWrapper;
import com.and.blf.popularmovies.model.MovieWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieService {
    @GET("3/movie/{endpoint}")
    Call<MovieWrapper> getMovies(@Path("endpoint") String endpoint, @Query("api_key") String api_key, @Query("page") int page);

    @GET("3/movie/{movie_id}/reviews")
    Call<MovieReviewWrapper> getReviews(@Path("movie_id") String movieId, @Query("api_key") String api_key);
}