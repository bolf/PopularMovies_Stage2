package com.and.blf.popularmovies.model.trailer;

public class Trailer {
    private int movieId;
    private String key;
    private String name;
    private String id;

    public Trailer() {
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Trailer(int movieId, String key, String name, String id) {
        this.movieId = movieId;
        this.key = key;
        this.name = name;
        this.id = id;
    }
}
