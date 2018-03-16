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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MovieAsyncQueryHandler extends AsyncQueryHandler {
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
        List<Movie> movies = new ArrayList<Movie>();
        if (cursor.moveToFirst()) {
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
        }
        cursor.close();
        ((MainActivity) weakContext.get()).mAdapter.setMovieList(movies, true);
        ((MainActivity) weakContext.get()).mLoadingIndicator.setVisibility(View.GONE);
        ((MainActivity) weakContext.get()).isLoadingNow = false;
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
        Toast.makeText(weakContext.get(), "inserted!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
    }
}
