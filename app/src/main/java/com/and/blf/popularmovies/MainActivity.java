package com.and.blf.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.and.blf.popularmovies.model.Movie;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Movie> movieList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
