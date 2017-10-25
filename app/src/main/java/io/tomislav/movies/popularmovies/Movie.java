package io.tomislav.movies.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.tomislav.movies.popularmovies.data.FavoritesDbHelper;

import static io.tomislav.movies.popularmovies.data.FavoritesContract.FavoriteEntry.COLUMN_MOVIE_ID;
import static io.tomislav.movies.popularmovies.data.FavoritesContract.FavoriteEntry.COLUMN_OVERVIEW;
import static io.tomislav.movies.popularmovies.data.FavoritesContract.FavoriteEntry.COLUMN_POSTER_PATH;
import static io.tomislav.movies.popularmovies.data.FavoritesContract.FavoriteEntry.COLUMN_RELEASE_DATE;
import static io.tomislav.movies.popularmovies.data.FavoritesContract.FavoriteEntry.COLUMN_REVIEWS;
import static io.tomislav.movies.popularmovies.data.FavoritesContract.FavoriteEntry.COLUMN_RUNTIME;
import static io.tomislav.movies.popularmovies.data.FavoritesContract.FavoriteEntry.COLUMN_TITLE;
import static io.tomislav.movies.popularmovies.data.FavoritesContract.FavoriteEntry.COLUMN_TRAILERS;
import static io.tomislav.movies.popularmovies.data.FavoritesContract.FavoriteEntry.COLUMN_VOTE;
import static io.tomislav.movies.popularmovies.data.FavoritesContract.FavoriteEntry.TABLE_NAME;

class Movie {
    private FavoritesDbHelper dbHelper;
    private SQLiteDatabase db;

    private String MOVIE_ID = "id";
    private String TITLE = "original_title";
    private String POSTER = "poster_path";
    private String DATE = "release_date";
    private String RUNTIME = "runtime";
    private String VOTE = "vote_average";
    private String OVERVIEW = "overview";
    private String TRAILERS = "trailers";
    private String REVIEWS = "reviews";
    private String IS_FAVORITE = "is_favorite";


    private int movieId;
    String title;
    String posterPath;
    String date;
    String runtime;
    Double vote;
    String overview;
    JSONArray trailers;
    JSONArray reviews;
    boolean isFavorite;

    Movie(Context context) {
        dbHelper = new FavoritesDbHelper(context);
        db = dbHelper.getWritableDatabase();
        isFavorite = false;
    }

    Movie(Bundle bundle, Context context) {
        dbHelper = new FavoritesDbHelper(context);
        db = dbHelper.getWritableDatabase();

        movieId = bundle.getInt(MOVIE_ID);
        title = bundle.getString(TITLE);
        posterPath = bundle.getString(POSTER);
        date = bundle.getString(DATE);
        runtime = bundle.getString(RUNTIME);
        vote = bundle.getDouble(VOTE);
        overview = bundle.getString(OVERVIEW);
        isFavorite = bundle.getBoolean(IS_FAVORITE);
        if (bundle.containsKey(TRAILERS)) {
            try {
                trailers = new JSONArray(bundle.getString(TRAILERS));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (bundle.containsKey(REVIEWS)) {
            try {
                reviews = new JSONArray(bundle.getString(REVIEWS));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Movie(Cursor c, Context context) {
        dbHelper = new FavoritesDbHelper(context);
        db = dbHelper.getWritableDatabase();

        movieId = c.getInt(c.getColumnIndex(COLUMN_MOVIE_ID));
        title = c.getString(c.getColumnIndex(COLUMN_TITLE));
        posterPath = c.getString(c.getColumnIndex(COLUMN_POSTER_PATH));
        date = c.getString(c.getColumnIndex(COLUMN_RELEASE_DATE));
        runtime = c.getString(c.getColumnIndex(COLUMN_RUNTIME));
        vote = c.getDouble(c.getColumnIndex(COLUMN_VOTE));
        overview = c.getString(c.getColumnIndex(COLUMN_OVERVIEW));
        try {
            trailers = new JSONArray(c.getString(c.getColumnIndex(COLUMN_TRAILERS)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            reviews = new JSONArray(c.getString(c.getColumnIndex(COLUMN_REVIEWS)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        isFavorite = true;
        c.close();
    }

    Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putInt(MOVIE_ID, movieId);
        bundle.putString(TITLE, title);
        bundle.putString(POSTER, posterPath);
        bundle.putString(DATE, date);
        bundle.putString(RUNTIME, runtime);
        bundle.putDouble(VOTE, vote);
        bundle.putString(OVERVIEW, overview);
        bundle.putBoolean(IS_FAVORITE, isFavorite);
        if (trailers != null) {
            bundle.putString(TRAILERS, trailers.toString());
        }
        if (reviews != null) {
            bundle.putString(REVIEWS, reviews.toString());
        }

        return bundle;
    }

    static Movie find(int movieId, Context context) {
        SQLiteDatabase db = new FavoritesDbHelper(context).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_MOVIE_ID + " = " + movieId + ";", null);
        if (c.moveToFirst()) {
            return new Movie(c, context);
        }
        c.close();
        return null;
    }

    void save() {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MOVIE_ID, movieId);
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_POSTER_PATH, posterPath);
        cv.put(COLUMN_RELEASE_DATE, date);
        cv.put(COLUMN_RUNTIME, runtime);
        cv.put(COLUMN_VOTE, vote);
        cv.put(COLUMN_OVERVIEW, overview);
        cv.put(COLUMN_TRAILERS, trailers.toString());
        cv.put(COLUMN_REVIEWS, reviews.toString());

        db.insert(TABLE_NAME, null, cv);
        isFavorite = true;
    }

    void delete() {
        db.delete(TABLE_NAME, COLUMN_MOVIE_ID + "=" + movieId, null);
        isFavorite = false;
    }

    void parseJsonMovieDetails(JSONObject movieJson) throws JSONException {
        movieId = movieJson.getInt(MOVIE_ID);
        title = movieJson.getString(TITLE);
        posterPath = movieJson.getString(POSTER);
        date = movieJson.getString(DATE);
        runtime = movieJson.getString(RUNTIME);
        vote = movieJson.getDouble(VOTE);
        overview = movieJson.getString(OVERVIEW);
    }
}
