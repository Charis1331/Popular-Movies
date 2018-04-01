package com.example.xoulis.xaris.popularmovies1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.xoulis.xaris.popularmovies1.model.Movie;
import com.example.xoulis.xaris.popularmovies1.retrofit.ApiClient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private List<Movie> dataSource;
    private Context context;

    private MovieClickListener movieClickListener;

    MoviesAdapter(List<Movie> dataSource, Context context,
                  MovieClickListener movieClickListener) {
        this.dataSource = dataSource;
        this.context = context;
        this.movieClickListener = movieClickListener;
    }

    void setDataSource(List<Movie> dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return dataSource.isEmpty() ? 0 : dataSource.size();
    }

    public interface MovieClickListener {
        void onMovieClick(Movie movie);
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.movieBigImageView)
        ImageView bigPosterImageView;

        MoviesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        void bind(int index) {
            Movie movie = dataSource.get(index);
            String posterPath = movie.getPosterPath();
            String posterUrl = HelperClass.buildMovieImgUrl(posterPath, ApiClient.W500_POSTER_SIZE);

            Glide.with(context)
                    .load(posterUrl)
                    .into(bigPosterImageView);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Movie movie = dataSource.get(position);
            movieClickListener.onMovieClick(movie);
        }
    }
}
