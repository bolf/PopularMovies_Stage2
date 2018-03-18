package com.and.blf.popularmovies.persistence;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.and.blf.popularmovies.model.Movie;
import com.and.blf.popularmovies.ui.MainActivity;
import com.and.blf.popularmovies.ui.MovieDetailsActivity;
import com.and.blf.popularmovies.ui.recycler_view.MovieRecyclerViewAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieAsyncQueryHandler extends AsyncQueryHandler {
    public static final int ASYNC_GET_ALL_FAVORITES = 3;
    public static final int ASYNC_GET_FAVORITES_REPLACE_ADAPTER_LIST = 5;
    public static final int ASYNC_GET_FAVORITES_NO_REPLACE_ADAPTER_LIST = 7;
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
        if (token == ASYNC_GET_ALL_FAVORITES) { //get all favorite movies from local db making movie-object for each
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
            ((MainActivity) weakContext.get()).mAdapter.setMovieList(movies,true);
            ((MainActivity) weakContext.get()).isLoadingNow = false;
            ((MainActivity) weakContext.get()).mLoadingIndicator.setVisibility(View.GONE);
        }else if(token == ASYNC_GET_FAVORITES_REPLACE_ADAPTER_LIST || token == ASYNC_GET_FAVORITES_NO_REPLACE_ADAPTER_LIST){
            Map<Integer,Integer> idMap = new HashMap<>();
            while (cursor.moveToNext()) {
                idMap.put(new Integer(cursor.getInt(1)),new Integer(cursor.getInt(0)));
            }
            ArrayList<Movie> movieLst = (ArrayList<Movie>)cookie;
            for (Movie m : movieLst) {
                Integer locDbId = idMap.get(new Integer(m.getId()));
                if(locDbId != null){
                    m.setLocalDbId(locDbId.intValue());
                }else{
                    m.setLocalDbId(-1);
                }
            }
            ((MainActivity) weakContext.get()).mAdapter.setMovieList(movieLst, token == ASYNC_GET_FAVORITES_REPLACE_ADAPTER_LIST);
        }
        cursor.close();
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
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