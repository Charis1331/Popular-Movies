package com.example.xoulis.xaris.popularmovies1.retrofit;


import com.example.xoulis.xaris.popularmovies1.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<MovieResponse> getMovieTrailers(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/reviews")
    Call<MovieResponse> getMovieReviews(@Path("movie_id") int movieId, @Query("api_key") String apiKey);
}
