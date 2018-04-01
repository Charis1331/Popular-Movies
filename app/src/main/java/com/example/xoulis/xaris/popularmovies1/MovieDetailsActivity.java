package com.example.xoulis.xaris.popularmovies1;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;

import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xoulis.xaris.popularmovies1.data.FavoriteMoviesContract;
import com.example.xoulis.xaris.popularmovies1.model.Movie;
import com.example.xoulis.xaris.popularmovies1.retrofit.ApiClient;
import com.github.florent37.glidepalette.GlidePalette;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FAVORITE_MOVIE_LOADER = 0;

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
    @BindView(R.id.add_to_favorites_fab)
    FloatingActionButton favoritesFab;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Get the parceable movie object, if it exists
        Bundle data = getIntent().getExtras();
        if (data == null) {
            finish();
            return;
        }

        ButterKnife.bind(this);
        movie = data.getParcelable("movie");

        // Provide UP navigation
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set movie title
        layout.setTitle(movie.getTitle());

        favoritesFab.setOnClickListener(this);

        getLoaderManager().initLoader(FAVORITE_MOVIE_LOADER, null, this);

        populateUi();
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

    private void populateUi() {
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

    @Override
    public void onClick(View view) {
        if (view == favoritesFab) {
            ContentValues cv = new ContentValues();
            cv.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_POSTER, movie.getPosterPath());
            cv.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_SYNOPSIS, movie.getOverview());
            cv.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_RATING, movie.getRating());
            cv.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());

            Uri uri = this.getContentResolver().insert(FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_URI,
                    cv);

            if (uri == null) {
                Toast.makeText(this, "Movie couldn't be added to favorites!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Movie saved.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                FavoriteMoviesContract.FavoriteMovieEntry._ID,
                FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_POSTER
        };

        String selection = FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_POSTER + " = ?";
        String[] selectionArgs = {movie.getPosterPath()};

        return new android.content.CursorLoader(this,
                FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }


    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            favoritesFab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        loader.reset();
    }
}
