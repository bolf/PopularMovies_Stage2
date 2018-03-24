package com.and.blf.popularmovies.ui.recycler_view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.utils.MovieNetworkUtils;
import com.squareup.picasso.Picasso;

public class TrailerRecyclerViewAdapter extends RecyclerView.Adapter<TrailerRecyclerViewAdapter.TrailerViewHolder>{
    private final String[] imageKeys;
    private final static String YOUTUBE_URI = "http://www.youtube.com/watch?v=";
    private final static String YOUTUBE_APP_URI ="vnd.youtube:";

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

        TrailerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            m_trailerThumbnail = itemView.findViewById(R.id.trailer_imageView);
        }

        @Override
        public void onClick(View v) {
            String[] imageKeys = (String[])itemView.getTag();
            String curKey = imageKeys[getAdapterPosition()];
            try {
                Intent intnt = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_APP_URI + curKey));
                v.getContext().startActivity(intnt);
            } catch (ActivityNotFoundException ex) {
                Intent intnt = new Intent(Intent.ACTION_VIEW,Uri.parse(YOUTUBE_URI + curKey));
                v.getContext().startActivity(intnt);
            } catch (Exception ex) {
                Toast.makeText(v.getContext(), R.string.couldnt_start_trailer_msg, Toast.LENGTH_SHORT).show();
            }
        }

    }

}
