package com.and.blf.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Movie implements Parcelable {
    private int localDbId;
    private int id;
    private String title;
    private String poster_path;
    private String backdrop_path;
    private String overview;
    private float vote_average;
    private String release_date;

    public Movie(){}

    public Movie(int localDbId, int id, String title, String posterPath, String backdropPath, String overview, float voteAverage, String releaseDate) {
        this.localDbId = localDbId;
        this.id = id;
        this.title = title;
        this.poster_path = posterPath;
        this.backdrop_path = backdropPath;
        this.overview = overview;
        this.vote_average = voteAverage;
        this.release_date = releaseDate;
    }

    protected Movie(Parcel in) {
        localDbId = in.readInt();
        id = in.readInt();
        title = in.readString();
        poster_path = in.readString();
        backdrop_path = in.readString();
        overview = in.readString();
        vote_average = in.readFloat();
        release_date = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(localDbId);
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeString(backdrop_path);
        dest.writeString(overview);
        dest.writeFloat(vote_average);
        dest.writeString(release_date);
    }

    public int getLocalDbId() {
        return localDbId;
    }

    public void setLocalDbId(int localDbId) {
        this.localDbId = localDbId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public void setPosterPath(String posterPath) {
        this.poster_path = posterPath;
    }

    public String getBackdropPath() {
        return backdrop_path;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdrop_path = backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getVoteAverage() {
        return vote_average;
    }

    public void setVoteAverage(float voteAverage) {
        this.vote_average = voteAverage;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(String releaseDate) {
        this.release_date = releaseDate;
    }

    public static String[] getStringIdArrayOutOfMovieList(List<Movie> lst){
        String[] strArr = new String[lst.size()];
        for(int i = 0; i < lst.size(); i++){
            strArr[i] = String.valueOf(lst.get(i).getId());
        }
        return  strArr;
    }
}