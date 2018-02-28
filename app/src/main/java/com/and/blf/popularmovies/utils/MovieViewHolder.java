package com.and.blf.popularmovies.utils;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.model.Movie;
import com.and.blf.popularmovies.ui.MovieDetailsActivity;

import java.util.ArrayList;

public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

     TextView m_movieTitle;
     ImageView m_moviePosterThumbnail;

    MovieViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        m_movieTitle = itemView.findViewById(R.id.movie_title);
        m_moviePosterThumbnail = itemView.findViewById(R.id.movie_poster_thumbnail);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.MOVIE_PARCEL,((ArrayList<Movie>) itemView.getTag()).get(getAdapterPosition()));
        v.getContext().startActivity(intent);
    }
}