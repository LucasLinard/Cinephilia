package tech.linard.android.cinephilia.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tech.linard.android.cinephilia.Data.MovieContract.MovieEntry;

/**
 * Created by llinard on 07/01/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = MovieDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String PRIMARY_KEY = " PRIMARY KEY ";
        final String AUTOINCREMENT = " AUTOINCREMENT ";
        final String NOT_NULL  = " NOT NULL ";
        final String TEXT = " TEXT ";
        final String INTEGER = " INTEGER ";
        final String DOUBLE = " DOUBLE ";

        final String DEFAULT = " DEFAULT ";

        String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME
                + " ( " + MovieEntry._ID + INTEGER + PRIMARY_KEY
                + " , " + MovieEntry.COLUMN_ORIGINAL_TITLE + TEXT + NOT_NULL
                + " , " + MovieEntry.COLUMN_LOCAL_TITLE + TEXT + NOT_NULL
                + " , " + MovieEntry.COLUMN_OVERVIEW + TEXT
                + " , " + MovieEntry.COLUMN_RELEASE_DATE + TEXT
                + " , " + MovieEntry.COLUMN_POSTER_PATH + TEXT
                + " , " + MovieEntry.COLUMN_POPULARITY + DOUBLE
                + " , " + MovieEntry.COLUMN_VOTE_AVERAGE + DOUBLE
                + " , " + MovieEntry.COLUMN_VOTE_COUNT + INTEGER
                + " , " + MovieEntry.COLUMN_FAVORITE + INTEGER + DEFAULT + 0
                + ");"
                ;
        db.execSQL(SQL_CREATE_MOVIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
