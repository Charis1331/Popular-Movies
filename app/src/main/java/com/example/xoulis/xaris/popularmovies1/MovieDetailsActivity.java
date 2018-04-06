package com.example.xoulis.xaris.popularmovies1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;

import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xoulis.xaris.popularmovies1.data.FavoriteMoviesContract;
import com.example.xoulis.xaris.popularmovies1.model.Movie;
import com.example.xoulis.xaris.popularmovies1.model.MovieResponse;
import com.example.xoulis.xaris.popularmovies1.model.Review;
import com.example.xoulis.xaris.popularmovies1.model.ReviewsResponse;
import com.example.xoulis.xaris.popularmovies1.model.Trailer;
import com.example.xoulis.xaris.popularmovies1.model.TrailersResponse;
import com.example.xoulis.xaris.popularmovies1.retrofit.ApiClient;
import com.example.xoulis.xaris.popularmovies1.retrofit.ApiInterface;
import com.github.florent37.glidepalette.GlidePalette;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.ACTION_VIEW;
import static com.example.xoulis.xaris.popularmovies1.MainActivity.API_KEY;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, TrailersAdapter.TrailerClickedListener {

    private static final int FAVORITE_MOVIE_LOADER = 0;
    private final int TRAILERS_TO_SHOW_AT_MAX = 3;
    private final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";

    private ApiInterface apiService;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout layout;

    @BindView(R.id.trailers_layout)
    LinearLayout trailersLayout;
    @BindView(R.id.reviews_linearLayout)
    LinearLayout reviewsLayout;

    @BindView(R.id.movieDetailsBigImageView)
    ImageView bigMoviePosterImageView;
    @BindView(R.id.movieDetailsThumbnailImageView)
    ImageView thumbnailImageView;
    @BindView(R.id.releaseDateTextView)
    TextView releaseDateTextView;
    @BindView(R.id.ratingTextView)
    TextView ratingTextView;
    @BindView(R.id.plotSynopsisTextView)
    TextView synopsisTextView;
    @BindView(R.id.add_to_favorites_fab)
    FloatingActionButton favoritesFab;
    @BindView(R.id.movie_review_author_textView)
    TextView reviewAuthorTextView;
    @BindView(R.id.movie_review_content_textView)
    TextView reviewContentTextView;

    private Movie movie;

    @BindView(R.id.movie_trailer_recyclerView)
    RecyclerView trailersRecyclerView;
    private TrailersAdapter trailersAdapter;

    @Override
    public void onTrailerClicked(Trailer trailer) {
        Uri uri = Uri.parse(YOUTUBE_BASE_URL)
                .buildUpon()
                .appendQueryParameter("v", trailer.getYoutubeKey())
                .build();

        Intent intent = new Intent(ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

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

        // Trailers RecyclerView
        trailersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        trailersRecyclerView.setHasFixedSize(true);
        trailersAdapter = new TrailersAdapter(this, this);
        trailersRecyclerView.setAdapter(trailersAdapter);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        fetchMovieTrailers();
        fetchMovieReviews();

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

    private void fetchMovieTrailers() {
        Call<TrailersResponse> call = apiService.getMovieTrailers(movie.getId(), API_KEY);
        call.enqueue(new Callback<TrailersResponse>() {
            @Override
            public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
                if (response.isSuccessful() && !response.body().getTrailers().isEmpty()) {
                    List<Trailer> trailers = response.body().getTrailers();
                    ArrayList<Trailer> temp;

                    // Show, at max, 3 trailers.
                    if (trailers.size() > TRAILERS_TO_SHOW_AT_MAX) {
                        temp = new ArrayList<>();
                        for (int i = 0; i < TRAILERS_TO_SHOW_AT_MAX; i++) {
                            temp.add(trailers.get(i));
                        }
                    } else {
                        temp = (ArrayList<Trailer>) trailers;
                    }
                    trailersAdapter.setTrailers(temp);
                } else {
                    trailersLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<TrailersResponse> call, Throwable t) {
                trailersLayout.setVisibility(View.GONE);
            }
        });
    }

    private void fetchMovieReviews() {
        Call<ReviewsResponse> call = apiService.getMovieReviews(movie.getId(), API_KEY);
        call.enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                if (response.isSuccessful() && !response.body().getReviews().isEmpty()) {
                    Review firstReview = response.body().getReviews().get(0);
                    String author = firstReview.getAuthor();
                    String content = firstReview.getContent();
                    final String url = firstReview.getUrl();
                    reviewAuthorTextView.setText(author);
                    reviewContentTextView.setText(content);
                    reviewContentTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Uri site = Uri.parse(url);
                            Intent intent = new Intent(ACTION_VIEW, site);
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        }
                    });
                } else {
                    reviewsLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                reviewsLayout.setVisibility(View.GONE);
            }
        });

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
