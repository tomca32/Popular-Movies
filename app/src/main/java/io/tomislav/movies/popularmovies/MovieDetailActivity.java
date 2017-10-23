package io.tomislav.movies.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.attr.id;
import static io.tomislav.movies.popularmovies.Connectivity.displayOfflineWarning;
import static io.tomislav.movies.popularmovies.Connectivity.isOffline;
import static io.tomislav.movies.popularmovies.UrlService.getMovieDetailsUrl;
import static io.tomislav.movies.popularmovies.UrlService.getMovieTrailersUrl;

public class MovieDetailActivity extends AppCompatActivity implements MovieTrailersAdapter.TrailerClickListener {

    OkHttpClient client = new OkHttpClient();

    public static final String ID_EXTRA = "ID_EXTRA";
    private static final String MOVIE_TAG = "MOVIE_TAG";
    private static final String TRAILERS_TAG = "TRAILERS_TAG";
    private JSONObject currentMovie;
    private JSONArray currentTrailers;

    TextView tvMovieTitle;
    TextView tvMovieDate;
    TextView tvRunningTime;
    TextView tvRating;
    TextView tvPlot;
    ImageView ivPoster;

    MovieTrailersAdapter trailersAdapter;
    RecyclerView recyclerView;
    DividerItemDecoration divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        recyclerView = (RecyclerView) findViewById(R.id.rv_trailers_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        divider = new DividerItemDecoration(
                recyclerView.getContext(),
                manager.getOrientation()
        );
        recyclerView.addItemDecoration(divider);

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
        }
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
        } else {
            (new GetMovieDetailsTask()).execute(getMovieDetailsUrl(this, movieId));
            (new GetMovieTrailersTask()).execute(getMovieTrailersUrl(this, movieId));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(MOVIE_TAG, currentMovie.toString());
        outState.putString(TRAILERS_TAG, currentTrailers.toString());
    }

    private class GetMovieDetailsTask extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... params) {
            URL url = params[0];
            JSONObject result;

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                result = new JSONObject(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            return result;
        }

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

    private class GetMovieTrailersTask extends AsyncTask<URL, Void, JSONObject> implements MovieTrailersAdapter.TrailerClickListener {

        @Override
        protected JSONObject doInBackground(URL... params) {
            URL url = params[0];
            JSONObject result;

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                result = new JSONObject(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            return result;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONArray trailerList = result.getJSONArray("results");
                currentTrailers = trailerList;
                updateTrailers();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTrailerClick(JSONObject trailerClicked) {

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
            recyclerView.swapAdapter(trailersAdapter, true);
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
