package com.and.blf.popularmovies.persistence;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class FavoriteMovieContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //directory
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITE_MOVIES, MOVIES);
        //single item
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITE_MOVIES + "/#", MOVIES_WITH_ID);
        return uriMatcher;
    }

    private MovieDbHelper movieDbHelper;

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                cursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return cursor;
            }
            case MOVIES_WITH_ID: {
                cursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.FavoriteMovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return cursor;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                long id = db.insert(MovieContract.FavoriteMovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.FavoriteMovieEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int numDeleted;
        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                numDeleted = db.delete(MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.FavoriteMovieEntry.TABLE_NAME + "'");
                break;
            }
            case MOVIES_WITH_ID: {
                numDeleted = db.delete(MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        MovieContract.FavoriteMovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.FavoriteMovieEntry.TABLE_NAME + "'");
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return numDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int numUpdated;

        if (values == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                numUpdated = db.update(MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case MOVIES_WITH_ID: {
                numUpdated = db.update(MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        values,
                        MovieContract.FavoriteMovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                db.beginTransaction();

                int numInserted = 0;
                try {
                    for (ContentValues value : values) {
                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long id = -1;
                        try {
                            id = db.insertOrThrow(MovieContract.FavoriteMovieEntry.TABLE_NAME,
                                    null, value);
                        } catch (SQLiteConstraintException e) {
                            Log.w("SQL_", "Attempting to insert " +
                                    value.getAsString(
                                            MovieContract.FavoriteMovieEntry.COLUMN_TITLE)
                                    + " but value is already in database.");
                        }
                        if (id != -1) {
                            numInserted++;
                        }
                    }
                    if (numInserted > 0) {
                        db.setTransactionSuccessful();
                    }
                } finally {
                    db.endTransaction();
                }
                if (numInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numInserted;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }
}


























