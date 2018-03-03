package com.example.xoulis.xaris.popularmovies1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.xoulis.xaris.popularmovies1.Model.Movie;
import com.example.xoulis.xaris.popularmovies1.Model.MovieResponse;
import com.example.xoulis.xaris.popularmovies1.Retrofit.ApiClient;
import com.example.xoulis.xaris.popularmovies1.Retrofit.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieClickListener, View.OnClickListener {

    @BindView(R.id.loadMoviesProgressBar)
    public ProgressBar progressBar;
    @BindView(R.id.error_views_layout_include)
    public View errorViews;
    private Button retryButton;

    private List<Movie> dataSource;
    private MoviesAdapter adapter;

    private boolean sortByPopularity = true;

    public final static String API_KEY = "YOUR_API_KEY";

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
        RecyclerView recyclerView = findViewById(R.id.moviesRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.getItemAnimator().setChangeDuration(0);

        // Adapter
        adapter = new MoviesAdapter(dataSource, this, this);
        recyclerView.setAdapter(adapter);

        if (isNetworkAvailable()) {
            // Get the movies
            fetchData();
        } else {
            showOrHideErrorView(true);
        }
    }

    private void fetchData() {
        // Show the progressBar
        progressBar.setVisibility(View.VISIBLE);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<MovieResponse> call;
        if (sortByPopularity) {
            sortByPopularity = false;
            call = apiService.getPopularMovies(API_KEY);
        } else {
            sortByPopularity = true;
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
                showOrHideErrorView(true);
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        changeMenuItemTitle(menu.getItem(0));
        return true;
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
                fetchData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeMenuItemTitle(MenuItem item) {
        if (sortByPopularity) {
            item.setTitle(R.string.sort_by_popularity);
        } else {
            item.setTitle(R.string.sort_by_rating);
        }
    }

    private void showOrHideErrorView(boolean show) {
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
                showOrHideErrorView(false);
                fetchData();
            } else {
                String toastText = getString(R.string.error_toast);
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
