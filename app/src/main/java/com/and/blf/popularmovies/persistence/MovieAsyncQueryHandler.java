package com.and.blf.popularmovies.persistence;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.and.blf.popularmovies.model.Movie;
import com.and.blf.popularmovies.ui.MainActivity;
import com.and.blf.popularmovies.ui.MovieDetailsActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MovieAsyncQueryHandler extends AsyncQueryHandler {
    public static final int ASYNC_READ_ONE_ID = 5;
    public static final int ASYNC_READ_ALL_ID = 7;
    public static final int ASYNC_WRITE_ID = 9;
    public static final int ASYNC_DELETE_ID = 11;
    WeakReference<Context> weakContext;

    public MovieAsyncQueryHandler(ContentResolver cr, WeakReference<Context> weakContext) {
        super(cr);
        this.weakContext = weakContext;
    }

    @Override
    public void startQuery(int token, Object cookie, Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
        super.startQuery(token, cookie, uri, projection, selection, selectionArgs, orderBy);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        if (token == ASYNC_READ_ALL_ID) {
            List<Movie> movies = new ArrayList<>();
            while (cursor.moveToNext()) {
                movies.add(new Movie(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getFloat(6),
                        cursor.getString(7)
                ));
            }
            ((MainActivity) weakContext.get()).mAdapter.setMovieList(movies, true);
            ((MainActivity) weakContext.get()).mLoadingIndicator.setVisibility(View.GONE);
            ((MainActivity) weakContext.get()).isLoadingNow = false;
        } else if (token == ASYNC_READ_ONE_ID) {
            int _id = -1;
            if (cursor.moveToFirst()) {
                _id = cursor.getInt(0);
            }
            ((MovieDetailsActivity) weakContext.get()).setMovieLocalDbId(_id);
        }
        cursor.close();
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
        ((MovieDetailsActivity) weakContext.get()).setMovieLocalDbId(-1);
        int _id = Integer.parseInt(uri.toString().replace(MovieContract.FavoriteMovieEntry.CONTENT_URI.toString() + "/", ""));
        ((MovieDetailsActivity) weakContext.get()).setMovieLocalDbId(_id);
        Toast.makeText(weakContext.get(), "added to favorites", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        if (result > 0) {
            ((MovieDetailsActivity) weakContext.get()).setMovieLocalDbId(-1);
            Toast.makeText(weakContext.get(), "removed from favorites", Toast.LENGTH_SHORT).show();
        }
    }
}
