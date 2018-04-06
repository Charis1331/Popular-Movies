package com.example.xoulis.xaris.popularmovies1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.xoulis.xaris.popularmovies1.data.FavoriteMoviesContract;
import com.example.xoulis.xaris.popularmovies1.data.FavoriteMoviesDBHelper;
import com.example.xoulis.xaris.popularmovies1.model.Movie;
import com.example.xoulis.xaris.popularmovies1.model.MovieResponse;
import com.example.xoulis.xaris.popularmovies1.retrofit.ApiClient;
import com.example.xoulis.xaris.popularmovies1.retrofit.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieClickListener,
        View.OnClickListener {

    private static final int FAVORITE_MOVIE_LOADER = 0;

    @BindView(R.id.loadMoviesProgressBar)
    public ProgressBar progressBar;
    @BindView(R.id.error_views_layout_include)
    public View errorViews;
    private Button retryButton;

    private RecyclerView recyclerView;
    private List<Movie> dataSource;
    private MoviesAdapter adapter;

    public final static String API_KEY = BuildConfig.API_KEY;
    private final String RECYCLER_STATE_KEY = "recycler_state_key";

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataSource = new ArrayList<>();

        ButterKnife.bind(this);
        retryButton = errorViews.findViewById(R.id.errorLayoutRetryButton);
        retryButton.setOnClickListener(this);

        // RecyclerView
        recyclerView = findViewById(R.id.moviesRecyclerView);
        setRecyclerViewLayout();
        recyclerView.setHasFixedSize(true);
        recyclerView.getItemAnimator().setChangeDuration(0);

        // Adapter
        adapter = new MoviesAdapter(dataSource, this, this);
        recyclerView.setAdapter(adapter);

        if (isNetworkAvailable()) {
            // Get the movies
            fetchData(0);
        } else {
            showErrorViews(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECYCLER_STATE_KEY, recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (savedInstanceState != null) {
                    Parcelable parcelable = savedInstanceState.getParcelable(RECYCLER_STATE_KEY);
                    recyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
                }
            }
        }, 300);
    }

    private void setRecyclerViewLayout() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
    }

    private void fetchData(int sortMode) {
        // Show the progressBar
        progressBar.setVisibility(View.VISIBLE);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<MovieResponse> call;
        if (sortMode == 0) {
            call = apiService.getPopularMovies(API_KEY);
        } else {
            call = apiService.getTopRatedMovies(API_KEY);
        }

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                dataSource = response.body().getResults();
                adapter.setDataSource(dataSource);

                // Hide the progressBar
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // Hide the progressBar
                progressBar.setVisibility(View.GONE);
                // Show error
                showErrorViews(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filterMoviesResult:
                int mode = 0;
                if (item.getTitle().equals(R.string.sort_by_popularity)) {
                    mode = 1;
                    getSupportActionBar().setTitle(getString(R.string.top_rated_title));
                    item.setTitle(R.string.sort_by_rating);
                } else {
                    getSupportActionBar().setTitle(getString(R.string.popular_title));
                    item.setTitle(R.string.sort_by_popularity);
                }
                fetchData(mode);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showErrorViews(boolean show) {
        if (show) {
            progressBar.setVisibility(View.GONE);
            errorViews.setVisibility(View.VISIBLE);
        } else {
            errorViews.setVisibility(View.GONE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onClick(View view) {
        if (view == retryButton) {
            if (isNetworkAvailable()) {
                showErrorViews(false);
                fetchData(0);
            } else {
                String toastText = getString(R.string.error_toast);
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
