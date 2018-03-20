package com.and.blf.popularmovies.ui.ViewPager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.utils.MovieNetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieTrailerPagerAdapter extends PagerAdapter {
    Context context;
    String[] imageKeys;
    LayoutInflater layoutInflater;


    public MovieTrailerPagerAdapter(Context context, String imageKeys[]) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageKeys = imageKeys;
    }

    @Override
    public int getCount(){
        return imageKeys.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.trailer_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.trailer_imageView);
        //TODO get images from picasso
        Picasso.with(imageView.getContext())
                .load(MovieNetworkUtils.buildTrailerThumbnailRequestUrl(imageKeys[position]))
                .into(imageView);

        container.addView(itemView);

        //listening to image click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "you clicked image " + (position + 1), Toast.LENGTH_SHORT).show();
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
