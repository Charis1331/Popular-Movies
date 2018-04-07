package com.example.xoulis.xaris.popularmovies1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.xoulis.xaris.popularmovies1.data.FavoriteMoviesContract;
import com.example.xoulis.xaris.popularmovies1.model.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, FavoriteMoviesAdapter.FavoriteMovieClickListener {

    private final int CURSOR_LOADER_ID = 0;

    private FavoriteMoviesAdapter adapter;
    private RecyclerView favoriteMoviesRecyclerView;

    public FavoritesFragment() {
    }

    @Override
    public void onFavoriteMovieClicked(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry._ID));
        String posterPath = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_POSTER));
        String title = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_TITLE));
        String synopsis = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_SYNOPSIS));
        String rating = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_RATING));
        String releaseDate = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE));
        Movie movie = new Movie(id, posterPath, title, synopsis, rating, releaseDate);

        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // initialize loader
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.favorites_actionBar_title);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate fragment_main layout
        final View rootView = inflater.inflate(R.layout.favorites_fragment, container, false);

        // RecyclerView adapter
        adapter = new FavoriteMoviesAdapter(getContext(), null, this);
        favoriteMoviesRecyclerView = rootView.findViewById(R.id.favorite_movies_recyclerView);
        favoriteMoviesRecyclerView.setHasFixedSize(true);
        favoriteMoviesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        favoriteMoviesRecyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Bundle args = getArguments();
        String previousTitle = args.getString("previousTitle");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(previousTitle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getActivity(),
                FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        loader.reset();
    }
}
