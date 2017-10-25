package io.tomislav.movies.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 5;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                FavoritesContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                COLUMN_RUNTIME + " TEXT NOT NULL, " +
                COLUMN_VOTE + " TEXT NOT NULL, " +
                COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                COLUMN_TRAILERS + " TEXT, " +
                COLUMN_REVIEWS + " TEXT " +
                ");";
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
