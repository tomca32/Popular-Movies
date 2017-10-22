package io.tomislav.movies.popularmovies;

import android.content.Context;

import java.net.MalformedURLException;
import java.net.URL;

class UrlService {

    static URL getPopularMoviesUrl(Context context) {
        URL url;
        try {
            url = new URL(context.getString(R.string.popular_movies_url) + "=" + context.getString(R.string.movie_db_api_key));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return url;
    }

    static URL getTopRatedMoviesUrl(Context context) {
        URL url;
        try {
            url = new URL(context.getString(R.string.top_rated_movies_url) + "=" + context.getString(R.string.movie_db_api_key));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return url;
    }

    static URL getMovieDetailsUrl(Context context, int id) {
        URL url;
        String str = context.getString(R.string.movie_details_url) + "=" + context.getString(R.string.movie_db_api_key);
        try {
            url = new URL(String.format(str, id + ""));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return url;
    }

    static URL getMovieTrailersUrl(Context context, int id) {
        URL url;
        String str = context.getString(R.string.movie_trailers_url) + "=" + context.getString(R.string.movie_db_api_key);
        try {
            url = new URL(String.format(str, id + ""));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return url;
    }

    static URL getPosterUrl(Context context, String posterPath) {
        URL url;
        try {
            url = new URL(context.getString(R.string.base_image_url) + "w185" + posterPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return url;
    }
}
