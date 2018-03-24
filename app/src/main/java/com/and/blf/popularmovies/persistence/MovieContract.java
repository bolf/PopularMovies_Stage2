package com.and.blf.popularmovies.persistence;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {
    static final String AUTHORITY = "com.and.blf.popularmovies";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    static final String PATH_FAVORITE_MOVIES = "movies";

    public static final class FavoriteMovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_MOVIES).build();

        public static final String TABLE_NAME = "favorite_movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                COLUMN_VOTE_AVERAGE + " REAL NOT NULL);";

        public static final String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}
