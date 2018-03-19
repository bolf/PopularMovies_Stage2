package com.and.blf.popularmovies.model;

import java.util.List;

public class MovieReviewWrapper {
    private List<MovieReview> results;

    public MovieReviewWrapper(){}

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
