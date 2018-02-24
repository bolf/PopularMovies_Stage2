package com.and.blf.popularmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
    public static void writeToSharedPreferences(Context context, String shPrefName, String prefName, String prefVal){
        SharedPreferences sharedPref = context.getSharedPreferences(shPrefName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(prefName, prefVal);
        editor.apply();
    }

    public static String readFromSharedPreferences(Context context, String shPrefName, String prefName) {
        SharedPreferences sharedPref = context.getSharedPreferences(shPrefName,Context.MODE_PRIVATE);
        return sharedPref.getString(prefName,"");
    }
}
