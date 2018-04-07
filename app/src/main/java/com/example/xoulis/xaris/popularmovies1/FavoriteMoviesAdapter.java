package com.example.xoulis.xaris.popularmovies1;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xoulis.xaris.popularmovies1.data.FavoriteMoviesContract;
import com.example.xoulis.xaris.popularmovies1.retrofit.ApiClient;

public class FavoriteMoviesAdapter extends CursorRecyclerViewAdapter<FavoriteMoviesAdapter.FavoriteMoviesViewHolder> {

    private Context context;

    private FavoriteMovieClickListener listener;

    public FavoriteMoviesAdapter(Context context, Cursor cursor, FavoriteMovieClickListener listener) {
        super(context, cursor);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new FavoriteMoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteMoviesViewHolder viewHolder, Cursor cursor) {
        viewHolder.bind(cursor);
    }

    public interface FavoriteMovieClickListener {
        void onFavoriteMovieClicked(Cursor cursor);
    }

    class FavoriteMoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView moviePosterImageView;

        FavoriteMoviesViewHolder(View view) {
            super(view);
            moviePosterImageView = view.findViewById(R.id.movieBigImageView);

            view.setOnClickListener(this);
        }

        void bind(Cursor cursor) {
            int imageIndex = cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_POSTER);
            String imagePath = cursor.getString(imageIndex);
            String imgUrl = HelperClass.buildMovieImgUrl(imagePath, ApiClient.W500_POSTER_SIZE);
            Glide.with(context)
                    .load(imgUrl)
                    .into(moviePosterImageView);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Cursor cursor = getCursor();
            cursor.moveToPosition(position);
            listener.onFavoriteMovieClicked(cursor);
        }
    }
}
