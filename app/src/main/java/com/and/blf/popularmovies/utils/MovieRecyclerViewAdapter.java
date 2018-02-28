package com.and.blf.popularmovies.utils;

import android.support.v4.app.LoaderManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.model.Movie;
import com.and.blf.popularmovies.ui.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    private List<Movie> m_movieList;
    private boolean mIsLoadingNow;
    public static int loadedPageCount = 1;
    private LoaderManager mLoaderManager;
    private boolean mShouldClearList;

    public void setShouldClearList(boolean mShouldClearList) {
        this.mShouldClearList = mShouldClearList;
    }

    public MovieRecyclerViewAdapter(List<Movie> movieList, LoaderManager loaderManager) {
        this.m_movieList = movieList;
        this.mLoaderManager = loaderManager;
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
                .load(MovieNetworkUtils.buildImageRequestUrl("w200", m_movieList.get(position).getPosterPath()))
                .into(holder.m_moviePosterThumbnail);
    }

    @Override
    public int getItemCount() {
        return m_movieList.size();
    }

    public void setMovieList(ArrayList<Movie> lst) {
        if(mShouldClearList){
            m_movieList.clear();
            setShouldClearList(false);
        }
        m_movieList.addAll(lst);
        notifyDataSetChanged();
        mIsLoadingNow = false;
    }

    @Override
    public void onViewAttachedToWindow(MovieViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int layoutPosition = holder.getLayoutPosition();
        if (!mIsLoadingNow && layoutPosition > m_movieList.size() - 5) {
            mIsLoadingNow = true;
            loadedPageCount++;
            mLoaderManager.getLoader(MainActivity.MOVIE_LOADER_ID).forceLoad();
        }

    }
}