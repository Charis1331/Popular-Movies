package com.example.xoulis.xaris.popularmovies1;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.ThumbnailImageViewTarget;
import com.example.xoulis.xaris.popularmovies1.Model.Movie;
import com.example.xoulis.xaris.popularmovies1.Retrofit.ApiClient;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;

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
    @BindView(R.id.releaseDateTextView)
    TextView releaseDateTextView;
    @BindView(R.id.ratingTextView)
    TextView ratingTextView;
    @BindView(R.id.plotSynopisTextView)
    TextView synopsisTextView;

    private ImageView im1;


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

        // Provide UP navigation
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set movie title
        layout.setTitle(movie.getTitle());

        populateUi(movie);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateUi(Movie movie) {
        // Big poster image
        String imgUrl = HelperClass.buildMovieImgUrl(movie.getPosterPath(),
                ApiClient.W500_POSTER_SIZE);
        Glide.with(this)
                .load(imgUrl)
                .listener(GlidePalette.with(imgUrl)
                    .use(GlidePalette.Profile.VIBRANT)
                    .intoCallBack(new GlidePalette.CallBack() {
                        @Override
                        public void onPaletteLoaded(@Nullable Palette palette) {
                            changeToolbarAndStatusBarColors(palette);
                        }
                    }))
                .into(bigMoviePosterImageView);

        // Movie thumbnail
        imgUrl = HelperClass.buildMovieImgUrl(movie.getPosterPath(),
                ApiClient.W185_POSTER_SIZE);
        Glide.with(this)
                .load(imgUrl)
                .into(thumbnailImageView);

        // Movie release data
        releaseDateTextView.setText(movie.getReleaseDate());

        // Movie rating
        String ratingText = movie.getRating() + "/10";
        ratingTextView.setText(ratingText);

        // Movie plot synopsis
        synopsisTextView.setText(movie.getOverview());
    }

    private void changeToolbarAndStatusBarColors(Palette palette) {
        int color = palette.getVibrantColor(getResources()
                .getColor(R.color.colorPrimary));

        // Change ContentScrimColor of collapsingToolbarLayout
        layout.setContentScrimColor(color);

        // Change the color of the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }
}
