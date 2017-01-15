package tech.linard.android.cinephilia.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by llinard on 07/01/17.
 */

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "tech.linard.android.cinephilia";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    // private constuctor

    private MovieContract() {

    }
    public static class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public final static String TABLE_NAME = "movies";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public final static String COLUMN_LOCAL_TITLE = "localTitle";
        public final static String COLUMN_OVERVIEW = "overview";
        public final static String COLUMN_RELEASE_DATE = "releaseDate";
        public final static String COLUMN_POSTER_PATH = "posterPath";
        public final static String COLUMN_POPULARITY = "popularity";
        public final static String COLUMN_VOTE_AVERAGE = "voteAverage";
        public final static String COLUMN_VOTE_COUNT = "voteCount";
        public final static String COLUMN_FAVORITE = "favorite";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;


        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
