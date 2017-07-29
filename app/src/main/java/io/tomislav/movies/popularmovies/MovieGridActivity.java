package io.tomislav.movies.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieGridActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);

        recyclerView = (RecyclerView) findViewById(R.id.rv_movies_grid);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);

        GetMoviesTask task = new GetMoviesTask();


        task.execute(getPopularMoviesUrl());
    }


    private URL getPopularMoviesUrl() {
        URL url;
        try {
            url = new URL(getString(R.string.popular_movies_url) + "=" + getString(R.string.movie_db_api_key));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return url;
    }

    void updateMovieGrid(JSONObject movies) {
        Logger.getAnonymousLogger().log(Level.INFO, movies.toString());
        try {
            MovieGridAdapter adapter = new MovieGridAdapter((JSONArray) movies.get("results"), this);
            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class GetMoviesTask extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... params) {
            URL url = params[0];
            JSONObject result = null;

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
            updateMovieGrid(result);
        }
    }
}
