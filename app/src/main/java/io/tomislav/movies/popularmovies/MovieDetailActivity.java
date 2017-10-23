package io.tomislav.movies.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.id;
import static io.tomislav.movies.popularmovies.Connectivity.displayOfflineWarning;
import static io.tomislav.movies.popularmovies.Connectivity.isOffline;
import static io.tomislav.movies.popularmovies.UrlService.getMovieDetailsUrl;
import static io.tomislav.movies.popularmovies.UrlService.getMovieReviewsUrl;
import static io.tomislav.movies.popularmovies.UrlService.getMovieTrailersUrl;

public class MovieDetailActivity extends AppCompatActivity implements MovieTrailersAdapter.TrailerClickListener {
    public static final String ID_EXTRA = "ID_EXTRA";
    private static final String MOVIE_TAG = "MOVIE_TAG";
    private static final String TRAILERS_TAG = "TRAILERS_TAG";
    private static final String REVIEWS_TAG = "REVIEWS_TAG";
    private JSONObject currentMovie;
    private JSONArray currentTrailers;
    private JSONArray currentReviews;

    TextView tvMovieTitle;
    TextView tvMovieDate;
    TextView tvRunningTime;
    TextView tvRating;
    TextView tvPlot;
    ImageView ivPoster;

    MovieTrailersAdapter trailersAdapter;
    MovieReviewsAdapter reviewsAdapter;
    RecyclerView trailerRecyclerView;
    RecyclerView reviewRecyclerView;
    DividerItemDecoration divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        trailerRecyclerView = setupRecyclerWithLinearManager(R.id.rv_trailers_list);
        reviewRecyclerView = setupRecyclerWithLinearManager(R.id.rv_reviews_list);

        int movieId = getIntent().getIntExtra(ID_EXTRA, -1);

        if (isOffline(this)) {
            displayOfflineWarning(this);
            return;
        }

        try {
            initializeCurrentMovie(savedInstanceState, movieId);
        } catch (JSONException e) {
            (new GetMovieDetailsTask()).execute(getMovieDetailsUrl(this, movieId));
            (new GetMovieTrailersTask()).execute(getMovieTrailersUrl(this, movieId));
            (new GetMovieReviewsTask()).execute(getMovieReviewsUrl(this, movieId));
        }
    }

    private RecyclerView setupRecyclerWithLinearManager(int id) {
        RecyclerView recyclerView = (RecyclerView) findViewById(id);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        divider = new DividerItemDecoration(
                recyclerView.getContext(),
                manager.getOrientation()
        );
        recyclerView.addItemDecoration(divider);

        return recyclerView;
    }

    private void initializeCurrentMovie(Bundle state, int movieId) throws JSONException {
        if (state != null) {
            if (state.containsKey(MOVIE_TAG)) {
                currentMovie = new JSONObject(state.getString(MOVIE_TAG));
                updateMovieDetails();
            } else {
                (new GetMovieDetailsTask()).execute(getMovieDetailsUrl(this, movieId));
            }

            if (state.containsKey(TRAILERS_TAG)) {
                currentTrailers = new JSONArray(state.getString(TRAILERS_TAG));
                updateTrailers();
            } else {
                (new GetMovieTrailersTask()).execute(getMovieTrailersUrl(this, movieId));
            }

            if (state.containsKey(REVIEWS_TAG)) {
                currentReviews = new JSONArray(state.getString(REVIEWS_TAG));
                updateReviews();
            } else {
                (new GetMovieReviewsTask()).execute(getMovieReviewsUrl(this, movieId));
            }
        } else {
            (new GetMovieDetailsTask()).execute(getMovieDetailsUrl(this, movieId));
            (new GetMovieTrailersTask()).execute(getMovieTrailersUrl(this, movieId));
            (new GetMovieReviewsTask()).execute(getMovieReviewsUrl(this, movieId));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(MOVIE_TAG, currentMovie.toString());
        outState.putString(TRAILERS_TAG, currentTrailers.toString());
        outState.putString(REVIEWS_TAG, currentReviews.toString());
    }

    private class GetMovieDetailsTask extends GetMovieDataTask {
        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                currentMovie = result;
                updateMovieDetails();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetMovieTrailersTask extends GetMovieDataTask {
        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                currentTrailers = result.getJSONArray("results");
                updateTrailers();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetMovieReviewsTask extends GetMovieDataTask {
        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                currentReviews = result.getJSONArray("results");
                updateReviews();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void updateMovieDetails() throws JSONException {
        ivPoster = (ImageView) findViewById(R.id.iv_detail_poster);
        Picasso.with(this).load(UrlService.getPosterUrl(this, currentMovie.getString("poster_path")).toString()).into(ivPoster);

        tvMovieTitle = (TextView)findViewById(R.id.tv_movie_title);
        tvMovieTitle.setText(currentMovie.getString("original_title"));

        tvMovieDate = (TextView)findViewById(R.id.tv_movie_year);
        tvMovieDate.setText(currentMovie.getString("release_date").split("-")[0]);

        tvRunningTime = (TextView) findViewById(R.id.tv_running_time);
        tvRunningTime.setText(String.format(getString(R.string.runtime), currentMovie.getString("runtime")));

        tvRating = (TextView) findViewById(R.id.tv_rating);
        tvRating.setText(String.format(getString(R.string.rating), currentMovie.getDouble("vote_average")));

        tvPlot = (TextView) findViewById(R.id.tv_plot);
        tvPlot.setText(currentMovie.getString("overview"));
    }

    private void updateTrailers() {
        if (trailersAdapter == null) {
            trailersAdapter = new MovieTrailersAdapter(currentTrailers, this);
            trailerRecyclerView.swapAdapter(trailersAdapter, true);
        }
    }

    private void updateReviews() {
        if (reviewsAdapter == null) {
            reviewsAdapter = new MovieReviewsAdapter(currentReviews);
            reviewRecyclerView.swapAdapter(reviewsAdapter, true);
        }
    }

    @Override
    public void onTrailerClick(JSONObject trailerClicked) throws JSONException {
        String videoId = trailerClicked.getString("key");
        try {
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.youtube_app) + videoId)));
        } catch (ActivityNotFoundException ex) {
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.youtube_url) + id)));
        }
    }
}
