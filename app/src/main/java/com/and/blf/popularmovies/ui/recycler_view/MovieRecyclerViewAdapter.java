package com.and.blf.popularmovies.ui.recycler_view;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.model.movie.Movie;
import com.and.blf.popularmovies.persistence.MovieAsyncQueryHandler;
import com.and.blf.popularmovies.persistence.MovieContract;
import com.and.blf.popularmovies.utils.*;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    private List<Movie> m_movieList;

    public List<Movie> getM_movieList() {
        return m_movieList;
    }

    public MovieRecyclerViewAdapter(List<Movie> movieList) {
        this.m_movieList = movieList;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        layoutView.setTag(m_movieList);
        return new MovieViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.m_movieTitle.setText(m_movieList.get(position).getTitle());
        Picasso.with(holder.m_moviePosterThumbnail.getContext())
                .load(MovieNetworkUtils.buildImageRequestUrl(holder.m_moviePosterThumbnail.getContext().getString(R.string.detailed_poster_size), m_movieList.get(position).getPosterPath()))
                .into(holder.m_moviePosterThumbnail);

        holder.m_favoriteStar.setVisibility(m_movieList.get(position).getLocalDbId() > -1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return m_movieList.size();
    }

    public void setMovieList(List<Movie> lst, boolean reload) {
        if (reload) {
            m_movieList.clear();
        }
        m_movieList.addAll(lst);
        notifyDataSetChanged();
    }

    public void setMovieList(List<Parcelable> lst) {
        for(Parcelable p : lst){
            Movie m = (Movie) p;
            m_movieList.add(m);
        }
        notifyDataSetChanged();
    }

    public void updateMovieList(int requestType, List<Movie> lst, ContentResolver contResolver,WeakReference<Context> weakRef){
        String[] projection;
        String selection = null;
        if (requestType == MovieAsyncQueryHandler.ASYNC_GET_ALL_FAVORITES) {
            projection = new String[]{MovieContract.FavoriteMovieEntry._ID,
                    MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID,
                    MovieContract.FavoriteMovieEntry.COLUMN_TITLE,
                    MovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH,
                    MovieContract.FavoriteMovieEntry.COLUMN_BACKDROP_PATH,
                    MovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW,
                    MovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE,
                    MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE};
        } else {
            projection = new String[]{MovieContract.FavoriteMovieEntry._ID, MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID};
            selection = MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " IN (" + TextUtils.join(",", Collections.nCopies(lst.size(), "?")) + ")";
        }

        MovieAsyncQueryHandler asyncQueryHandler = new MovieAsyncQueryHandler(contResolver, weakRef);
        asyncQueryHandler.startQuery(requestType,
                lst,
                MovieContract.FavoriteMovieEntry.CONTENT_URI,
                projection,
                selection,
                Movie.getStringIdArrayOutOfMovieList(lst),
                null);
    }

    public void setLocalDbIdOnItem(int movieId, int localDbId){
        for(Movie m : m_movieList){
            if(m.getId() == movieId){
                m.setLocalDbId(localDbId);
                notifyDataSetChanged();
                break;
            }
        }
    }
}