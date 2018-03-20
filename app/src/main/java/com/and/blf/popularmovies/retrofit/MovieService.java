package com.and.blf.popularmovies.retrofit;

import com.and.blf.popularmovies.model.review.MovieReviewWrapper;
import com.and.blf.popularmovies.model.movie.MovieWrapper;
import com.and.blf.popularmovies.model.trailer.TrailerWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieService {
    @GET("3/movie/{endpoint}")
    Call<MovieWrapper> getMovies(@Path("endpoint") String endpoint, @Query("api_key") String api_key, @Query("page") int page);

    @GET("3/movie/{movie_id}/reviews")
    Call<MovieReviewWrapper> getReviews(@Path("movie_id") String movieId, @Query("api_key") String api_key);

    @GET("3/movie/{movie_id}/videos")
    Call<TrailerWrapper> getTrailers(@Path("movie_id") String movieId, @Query("api_key") String api_key);
}