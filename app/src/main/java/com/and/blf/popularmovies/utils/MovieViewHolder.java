package com.and.blf.popularmovies.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.and.blf.popularmovies.R;

public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

     TextView movieTitle;
     ImageView moviePosterThumbnail;

    MovieViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        movieTitle = itemView.findViewById(R.id.movie_title);
        moviePosterThumbnail = itemView.findViewById(R.id.movie_poster_thumbnail);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), "Clicked Country Position = " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
    }
}
