package io.tomislav.movies.popularmovies;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.TrailerViewHolder>{

    private JSONArray trailers;
    private TrailerClickListener listener;

    MovieTrailersAdapter(JSONArray trailers, TrailerClickListener listener) {
        super();
        this.trailers = trailers;
        this.listener = listener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return trailers.length();
    }


    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView trailerNameView;

        TrailerViewHolder(View itemView) {
            super(itemView);

            trailerNameView = (TextView) itemView.findViewById(R.id.tv_trailer_name_item);
            itemView.setOnClickListener(this);
        }

        void bind(int index) {
            String trailerName = getTrailerNameFor(index);
            trailerNameView.setText(trailerName);
        }

        private String getTrailerNameFor(int index) {
            String trailerName;
            try {
                trailerName = trailers.getJSONObject(index).getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            return trailerName;
        }

        @Override
        public void onClick(View v) {
            try {
                listener.onTrailerClick(trailers.getJSONObject(getAdapterPosition()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    interface TrailerClickListener {
        void onTrailerClick(JSONObject trailerClicked) throws JSONException;
    }
}
