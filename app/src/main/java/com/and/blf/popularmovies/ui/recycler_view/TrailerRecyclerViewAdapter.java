package com.and.blf.popularmovies.ui.recycler_view;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.model.movie.Movie;
import com.and.blf.popularmovies.ui.MainActivity;
import com.and.blf.popularmovies.ui.MovieDetailsActivity;
import com.and.blf.popularmovies.utils.MovieNetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.TrailerViewHolder>{
    private final String[] imageKeys;

    public TrailerRecyclerViewAdapter(String[] imageKeys) {
        this.imageKeys = imageKeys;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        layoutView.setTag(imageKeys);
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
            String[] imageKeys = (String[])itemView.getTag();
            String curKey = imageKeys[getAdapterPosition()];

            try {
                Intent intnt = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + curKey));
                v.getContext().startActivity(intnt);
            } catch (ActivityNotFoundException ex) {
                Intent intnt = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.youtube.com/watch?v=" + curKey));
                v.getContext().startActivity(intnt);
            } catch (Exception ex) {
                Toast.makeText(v.getContext(),"Application for watching YouTube videos not found.", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
