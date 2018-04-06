package com.example.xoulis.xaris.popularmovies1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xoulis.xaris.popularmovies1.model.Trailer;

import java.util.ArrayList;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {

    ArrayList<Trailer> trailers;
    Context context;

    public TrailersAdapter(ArrayList<Trailer> trailers, Context context) {
        this.trailers = trailers;
        this.context = context;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }

    @NonNull
    @Override
    public TrailersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_trailer_item, parent, false);
        return new TrailersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (trailers != null) {
            return trailers.size();
        }
        return 0;
    }

    public static class TrailersViewHolder extends RecyclerView.ViewHolder {

        public TrailersViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(int index) {

        }
    }
}
