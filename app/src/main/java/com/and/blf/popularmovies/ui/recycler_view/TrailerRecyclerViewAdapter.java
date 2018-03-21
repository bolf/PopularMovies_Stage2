package com.and.blf.popularmovies.ui.recycler_view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.utils.MovieNetworkUtils;
import com.squareup.picasso.Picasso;

public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.TrailerViewHolder>{
    private final String[] imageKeys;

    public TrailerRecyclerViewAdapter(String[] imageKeys) {
        this.imageKeys = imageKeys;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        //layoutView.setTag(m_movieList);
        return new TrailerViewHolder(layoutView);
    }

    @Override
    public int getItemCount() {
        return imageKeys.length;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        Picasso.with(holder.m_trailerThumbnail.getContext())
                .load(MovieNetworkUtils.buildTrailerThumbnailRequestUrl(imageKeys[position]))
                .into(holder.m_trailerThumbnail);
    }

    public static class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView m_trailerThumbnail;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            m_trailerThumbnail = itemView.findViewById(R.id.trailer_imageView);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "!", Toast.LENGTH_SHORT).show();
        }
    }

}
