package io.tomislav.movies.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static io.tomislav.movies.popularmovies.Connectivity.displayOfflineWarning;
import static io.tomislav.movies.popularmovies.Connectivity.isOffline;
import static io.tomislav.movies.popularmovies.UrlService.getMovieDetailsUrl;

public class MovieDetailActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();

    public static final String ID_EXTRA = "ID_EXTRA";

    TextView tvMovieTitle;
    TextView tvMovieDate;
    TextView tvRunningTime;
    TextView tvRating;
    TextView tvPlot;
    ImageView ivPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        int movieId = getIntent().getIntExtra(ID_EXTRA, -1);

        if (isOffline(this)) {
            displayOfflineWarning(this);
            return;
        }
        GetMovieDetailsTask task = new GetMovieDetailsTask();
        task.execute(getMovieDetailsUrl(this, movieId));
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
                updateMovieDetails(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateMovieDetails(JSONObject movie) throws JSONException {
        ivPoster = (ImageView) findViewById(R.id.iv_detail_poster);
        Picasso.with(this).load(UrlService.getPosterUrl(this, movie.getString("poster_path")).toString()).into(ivPoster);

        tvMovieTitle = (TextView)findViewById(R.id.tv_movie_title);
        tvMovieTitle.setText(movie.getString("original_title"));

        tvMovieDate = (TextView)findViewById(R.id.tv_movie_year);
        tvMovieDate.setText(movie.getString("release_date").split("-")[0]);

        tvRunningTime = (TextView) findViewById(R.id.tv_running_time);
        tvRunningTime.setText(String.format(getString(R.string.runtime), movie.getString("runtime")));

        tvRating = (TextView) findViewById(R.id.tv_rating);
        tvRating.setText(String.format(getString(R.string.rating), movie.getDouble("vote_average")));

        tvPlot = (TextView) findViewById(R.id.tv_plot);
        tvPlot.setText(movie.getString("overview"));
    }
}
