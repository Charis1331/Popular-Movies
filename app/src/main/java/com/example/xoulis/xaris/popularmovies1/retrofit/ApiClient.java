package com.example.xoulis.xaris.popularmovies1.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;

    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    public static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

    public static final String W500_POSTER_SIZE = "w500";
    public static final String W185_POSTER_SIZE = "w185";

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
