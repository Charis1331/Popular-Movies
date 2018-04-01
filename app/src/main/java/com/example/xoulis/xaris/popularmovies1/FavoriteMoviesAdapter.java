package com.example.xoulis.xaris.popularmovies1;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xoulis.xaris.popularmovies1.data.FavoriteMoviesContract;

public class FavoriteMoviesAdapter extends CursorAdapter {

    public FavoriteMoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorite_movie_item, parent, false);
        FavoriteMoviesViewHolder viewHolder = new FavoriteMoviesViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        FavoriteMoviesViewHolder viewHolder = (FavoriteMoviesViewHolder) view.getTag();

        int imageIndex = cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_POSTER);
        int image = cursor.getInt(imageIndex);

        viewHolder.moviePosterImageView.setImageResource(image);
    }

    public static class FavoriteMoviesViewHolder {
        TextView movieNameTextView;
        ImageView moviePosterImageView;

        public FavoriteMoviesViewHolder(View view) {
            movieNameTextView = view.findViewById(R.id.favorite_movie_textView);
            moviePosterImageView = view.findViewById(R.id.favorite_movie_imageView);
        }
    }
}
