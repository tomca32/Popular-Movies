package io.tomislav.movies.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import java.net.URL;

class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.PosterViewHolder> {

    private JSONArray movies;
    private Context context;

    MovieGridAdapter(JSONArray movies, Context context) {
        super();
        this.movies = movies;
        this.context = context;
    }

    public void changeMovieSet(JSONArray movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.movie_poster_grid_item, parent, false);

        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return movies.length();
    }

    class PosterViewHolder extends RecyclerView.ViewHolder {

        ImageView posterImageView;

        PosterViewHolder(View itemView) {
            super(itemView);

            posterImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster_grid_item);
        }

        void bind(int index) {
            String url = getPosterUrlFor(index);
            Picasso.with(context).load(url).into(posterImageView);
        }

        private String getPosterUrlFor(int index) {
            String baseImageUrl = context.getString(R.string.base_image_url);
            String posterPath;
            try {
                posterPath = movies.getJSONObject(index).getString("poster_path");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            return baseImageUrl + "w185" + posterPath;
        }
    }
}
