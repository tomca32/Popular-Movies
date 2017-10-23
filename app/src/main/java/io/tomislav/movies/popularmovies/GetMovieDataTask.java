package io.tomislav.movies.popularmovies;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class GetMovieDataTask extends AsyncTask<URL, Void, JSONObject> {
    private OkHttpClient client = new OkHttpClient();

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
}
