package io.tomislav.movies.popularmovies;

import org.json.JSONObject;

public class JsonResultWrapper {
    public String url;
    public JSONObject result;

    JsonResultWrapper(String url, JSONObject result) {
        this.url = url;
        this.result = result;
    }
}
