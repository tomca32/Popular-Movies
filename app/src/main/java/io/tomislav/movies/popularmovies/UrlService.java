package io.tomislav.movies.popularmovies;

import android.content.Context;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlService {

    public static URL getPopularMoviesUrl(Context context) {
        URL url;
        try {
            url = new URL(context.getString(R.string.popular_movies_url) + "=" + context.getString(R.string.movie_db_api_key));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return url;
    }

    public static URL getTopRatedMoviesUrl(Context context) {
        URL url;
        try {
            url = new URL(context.getString(R.string.top_rated_movies_url) + "=" + context.getString(R.string.movie_db_api_key));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return url;
    }
}
