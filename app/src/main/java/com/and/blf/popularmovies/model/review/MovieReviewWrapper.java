package com.and.blf.popularmovies.model.review;

import com.and.blf.popularmovies.model.review.MovieReview;

import java.util.List;

public class MovieReviewWrapper {
    private List<MovieReview> results;

    public MovieReviewWrapper(List<MovieReview> results) {
        this.results = results;
    }

    public List<MovieReview> getResults() {
        return results;
    }

    public void setResults(List<MovieReview> results) {
        this.results = results;
    }
}
