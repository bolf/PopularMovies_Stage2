package com.and.blf.popularmovies.model.trailer;

import java.util.List;

public class TrailerWrapper {
    List<Trailer> results;

    public TrailerWrapper(List<Trailer> results) {
        this.results = results;
    }

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }

    public String[] getMovieThumbnailsKeys(){
        String[] retArr = new String[results.size()];
        for (int i = 0; i < results.size(); i++){
            retArr[i] = results.get(i).getKey();
        }
        return retArr;
    }
}
