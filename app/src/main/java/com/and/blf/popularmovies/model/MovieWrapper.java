package com.and.blf.popularmovies.model;

import java.util.List;

public class MovieWrapper {
    private List<Movie> results;

    public MovieWrapper() {
    }

    public MovieWrapper(List<Movie> results) {
        this.results = results;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
