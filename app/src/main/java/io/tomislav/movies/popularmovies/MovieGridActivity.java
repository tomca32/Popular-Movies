package io.tomislav.movies.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.idescout.sql.SqlScoutServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static io.tomislav.movies.popularmovies.Connectivity.displayOfflineWarning;
import static io.tomislav.movies.popularmovies.Connectivity.isOffline;
import static io.tomislav.movies.popularmovies.MovieDetailActivity.ID_EXTRA;
import static io.tomislav.movies.popularmovies.UrlService.getPopularMoviesUrl;
import static io.tomislav.movies.popularmovies.UrlService.getTopRatedMoviesUrl;

public class MovieGridActivity extends AppCompatActivity implements MovieGridAdapter.ItemClickListener {

    OkHttpClient client = new OkHttpClient();

    RecyclerView recyclerView;
    MovieGridAdapter adapter;

    HashMap<String, JSONObject> resultCache;

    final String URL_PREFIX = "BUNDLE_URL_PREFIX";
    final String CURRENTLY_SELECTED_KEY = "CURRENTLY_SELECTED_KEY";
    final String POPULAR = "POPULAR";
    final String TOP = "TOP";

    String currentlySelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SqlScoutServer.create(this, getPackageName());
        setContentView(R.layout.activity_movie_grid);
        resultCache = new HashMap<>();
        currentlySelected = POPULAR;

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }

        recyclerView = (RecyclerView) findViewById(R.id.rv_movies_grid);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);

        if (currentlySelected.equals(POPULAR)) {
            popularSortSelected();
        } else if (currentlySelected.equals(TOP)) {
            topSortSelected();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        for (String url : resultCache.keySet()) {
            outState.putString(url, resultCache.get(url).toString());
        }
        outState.putString(CURRENTLY_SELECTED_KEY, currentlySelected);
    }

    void updateMovieGrid(JSONObject movies) {
        Logger.getAnonymousLogger().log(Level.INFO, movies.toString());
        JSONArray movieList = null;
        try {
            movieList = (JSONArray) movies.get("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (adapter == null) {
            adapter = new MovieGridAdapter(movieList, this, this);
            recyclerView.swapAdapter(adapter, true);
        } else {
            adapter.changeMovieSet(movieList);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.sort_order_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.popular_sort_item: {
                popularSortSelected();
                break;
            }

            case R.id.top_sort_item: {
                topSortSelected();
            }
        }
        return true;
    }

    private void popularSortSelected() {
        currentlySelected = POPULAR;
        this.setTitle(R.string.popular_movies);
        URL url = getPopularMoviesUrl(this);

        if (showCachedGrid(url.toString())) {
            return;
        }

        if (isOffline(this)) {
            displayOfflineWarning(this);
            return;
        }
        GetMoviesTask task = new GetMoviesTask();
        task.execute(url);
    }

    private void topSortSelected() {
        currentlySelected = TOP;
        this.setTitle(R.string.top_movies);
        URL url = getTopRatedMoviesUrl(this);

        if (showCachedGrid(url.toString())) {
            return;
        }

        if (isOffline(this)) {
            displayOfflineWarning(this);
            return;
        }
        GetMoviesTask task = new GetMoviesTask();
        task.execute(url);
    }

    @Override
    public void onItemClick(JSONObject movieClicked) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        try {
            intent.putExtra(ID_EXTRA, movieClicked.getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    private boolean showCachedGrid(String url) {
        String key = URL_PREFIX + url;
        if (resultCache.containsKey(key)) {
            updateMovieGrid(resultCache.get(key));
            return true;
        }
        return false;
    }

    private void restoreState(Bundle state) {
        for (String key : state.keySet()) {
            String value = state.getString(key);
            if (value != null && key.contains(URL_PREFIX)) {
                try {
                    resultCache.put(key, new JSONObject(value));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (key.equals(CURRENTLY_SELECTED_KEY)) {
                currentlySelected = value;
            }
        }
    }

    private class GetMoviesTask extends AsyncTask<URL, Void, JsonResultWrapper> {

        @Override
        protected JsonResultWrapper doInBackground(URL... params) {
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

            return new JsonResultWrapper(url.toString(), result);
        }

        @Override
        protected void onPostExecute(JsonResultWrapper wrapper) {
            resultCache.put(URL_PREFIX + wrapper.url, wrapper.result);
            updateMovieGrid(wrapper.result);
        }
    }
}
