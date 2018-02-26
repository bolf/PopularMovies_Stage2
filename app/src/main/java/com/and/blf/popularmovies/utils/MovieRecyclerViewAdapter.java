package com.and.blf.popularmovies.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    private List<Movie> m_movieList;

    public MovieRecyclerViewAdapter(List<Movie> movieList){
        this.m_movieList = movieList;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, null);
        layoutView.setTag(m_movieList);
        return new MovieViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.movieTitle.setText(m_movieList.get(position).getTitle());
        Picasso.with(holder.moviePosterThumbnail.getContext())
                .load(MovieNetworkUtils.buildImageRequestUrl("w200",m_movieList.get(position).getPosterPath()))
                .into(holder.moviePosterThumbnail);
    }

    @Override
    public int getItemCount() {
        return m_movieList.size();
    }

    public void setMovieList(ArrayList<Movie> lst){
        m_movieList.clear();
        m_movieList.addAll(lst);
        notifyDataSetChanged();
    }


}