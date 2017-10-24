package io.tomislav.movies.popularmovies.data;

import android.provider.BaseColumns;

public class FavoritesContract {
    public static final class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_VOTE = "vote";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_TRAILERS = "trailers";
    }
}
