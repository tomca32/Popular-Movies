package io.tomislav.movies.popularmovies;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ReviewViewHolder>{

    private JSONArray reviews;

    MovieReviewsAdapter(JSONArray reviews) {
        super();
        this.reviews = reviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return reviews.length();
    }


    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView reviewView;
        TextView authorView;

        ReviewViewHolder(View itemView) {
            super(itemView);

            authorView = (TextView) itemView.findViewById(R.id.tv_author_name);
            reviewView = (TextView) itemView.findViewById(R.id.tv_review);
        }

        void bind(int index) {
            authorView.setText(getAuthorFor(index));
            reviewView.setText(getReviewFor(index));
        }

        private String getReviewFor(int index) {
            String reviewName;
            try {
                reviewName = reviews.getJSONObject(index).getString("content");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            return reviewName;
        }

        private String getAuthorFor(int index) {
            String reviewName;
            try {
                reviewName = reviews.getJSONObject(index).getString("author");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            return reviewName;
        }
    }
}
