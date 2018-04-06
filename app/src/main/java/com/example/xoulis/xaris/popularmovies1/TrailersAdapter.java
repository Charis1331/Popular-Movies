package com.example.xoulis.xaris.popularmovies1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xoulis.xaris.popularmovies1.model.Trailer;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {

    ArrayList<Trailer> trailers;
    Context context;

    private TrailerClickedListener trailerClickedListener;

    public TrailersAdapter(Context context, TrailerClickedListener trailerClickedListener) {
        this.context = context;
        this.trailerClickedListener = trailerClickedListener;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrailersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.movie_trailer_item, parent, false);
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

    public interface TrailerClickedListener {
        void onTrailerClicked(Trailer trailer);
    }

    public class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView trailerNameTextView;

        public TrailersViewHolder(View itemView) {
            super(itemView);
            trailerNameTextView = itemView.findViewById(R.id.trailer_name_textView);

            itemView.setOnClickListener(this);
        }

        public void bind(int index) {
            Trailer trailer = trailers.get(index);
            trailerNameTextView.setText(trailer.getName());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Trailer trailer = trailers.get(position);
            trailerClickedListener.onTrailerClicked(trailer);
        }
    }
}
