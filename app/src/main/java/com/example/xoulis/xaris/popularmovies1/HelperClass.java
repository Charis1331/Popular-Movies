package com.example.xoulis.xaris.popularmovies1;

import android.net.Uri;

import com.example.xoulis.xaris.popularmovies1.Retrofit.ApiClient;

public class HelperClass {

    public static String buildMovieImgUrl(String path, String imgSize) {
        Uri uri = Uri.parse(ApiClient.POSTER_BASE_URL)
                .buildUpon()
                .appendPath(imgSize)
                .appendEncodedPath(path)
                .build();
        return uri.toString();
    }

}
