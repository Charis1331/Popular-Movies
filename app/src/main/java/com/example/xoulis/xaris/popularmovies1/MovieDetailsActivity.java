package com.example.xoulis.xaris.popularmovies1;

import android.content.Intent;
import android.media.Image;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewDebug;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.xoulis.xaris.popularmovies1.Model.Movie;
import com.example.xoulis.xaris.popularmovies1.Retrofit.ApiClient;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout layout;

    @BindView(R.id.movieDetailsBigImageView)
    ImageView bigMoviePosterImageView;
    @BindView(R.id.movieDetailsThumbnailImageView)
    ImageView thumbnailImageView;
    @BindView(R.id.movieTitleTextView)
    TextView titleTextView;
    @BindView(R.id.releaseDateTextView)
    TextView releaseDateTextView;
    @BindView(R.id.ratingTextView)
    TextView ratingTextView;
    @BindView(R.id.plotSynopisTextView)
    TextView synopsisTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Get the parceable movie object, if it exists
        Bundle data = getIntent().getExtras();
        if (data == null) {
            return;
        }

        ButterKnife.bind(this);
        Movie movie = data.getParcelable("movie");

        // Configure toolbar
        //toolbar.setTitle(movie.getTitle());
        layout.setTitle(movie.getTitle());

        populateUi(movie);
    }

    private void populateUi(Movie movie) {
        // Big poster image
        String imgUrl = HelperClass.buildMovieImgUrl(movie.getPosterPath(),
                ApiClient.W500_POSTER_SIZE);
        Picasso.with(this)
                .load(imgUrl)
                .into(bigMoviePosterImageView);

        // Movie thumbnail
        imgUrl = HelperClass.buildMovieImgUrl(movie.getPosterPath(),
                ApiClient.W185_POSTER_SIZE);
        Picasso.with(this)
                .load(imgUrl)
                .into(thumbnailImageView);

        // Movie Title
        titleTextView.setText(movie.getTitle());

        // Movie release data
        releaseDateTextView.setText(movie.getReleaseDate());

        // Movie rating
        String ratingText = movie.getRating() + "/10";
        ratingTextView.setText(ratingText);

        // Movie plot synopsis
        synopsisTextView.setText(movie.getOverview());
    }
}
