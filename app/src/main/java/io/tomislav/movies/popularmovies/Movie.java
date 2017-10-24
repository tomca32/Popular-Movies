package io.tomislav.movies.popularmovies;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

class Movie {
    private String TITLE = "original_title";
    private String POSTER = "poster_path";
    private String DATE = "release_date";
    private String RUNTIME = "runtime";
    private String VOTE = "vote_average";
    private String OVERVIEW = "overview";

    String title;
    String posterPath;
    String date;
    String runtime;
    Double vote;
    String overview;

    Movie(JSONObject movieJson) {
        try {
            title = movieJson.getString(TITLE);
            posterPath = movieJson.getString(POSTER);
            date = movieJson.getString(DATE);
            runtime = movieJson.getString(RUNTIME);
            vote = movieJson.getDouble(VOTE);
            overview = movieJson.getString(OVERVIEW);
        } catch (JSONException e) {
            System.out.print(e.toString());
        }
    }

    Movie(Bundle bundle) {
        title = bundle.getString(TITLE);
        posterPath = bundle.getString(POSTER);
        date = bundle.getString(DATE);
        runtime = bundle.getString(RUNTIME);
        vote = bundle.getDouble(VOTE);
        overview = bundle.getString(OVERVIEW);
    }

    Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putString(TITLE, title);
        bundle.putString(POSTER, posterPath);
        bundle.putString(DATE, date);
        bundle.putString(RUNTIME, runtime);
        bundle.putDouble(VOTE, vote);
        bundle.putString(OVERVIEW, overview);

        return bundle;
    }
}
