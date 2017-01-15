package tech.linard.android.cinephilia.Activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.Normalizer;
import android.icu.text.StringPrepParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.net.URL;
import java.util.List;

import tech.linard.android.cinephilia.BuildConfig;
import tech.linard.android.cinephilia.Data.MovieContract;
import tech.linard.android.cinephilia.Data.MovieContract.MovieEntry;
import tech.linard.android.cinephilia.Model.Movie;
import tech.linard.android.cinephilia.Model.Review;
import tech.linard.android.cinephilia.R;
import tech.linard.android.cinephilia.Util.MovieLoader;
import tech.linard.android.cinephilia.Util.QueryUtils;

/**
 * Created by llinard on 10/01/17.
 */
public class NetworkFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Movie>> {

    public static final String LOG_TAG = "NetworkFragment";
    private static final int MOVIE_LOADER_ID = 2;
    public  String BASE_MOVIE_REQUEST_URL =
            "https://api.themoviedb.org/3/movie/";
    public String REVIEW_PATH_URL = "reviews";
    private int currentMovieId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        Log.e(LOG_TAG, ": onLoadFinished");

    }

    @Override
    public void onResume() {
        super.onResume();
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(BASE_MOVIE_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendEncodedPath(orderBy);
        uriBuilder.appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY);
        uriBuilder.appendQueryParameter("language", "en");
        uriBuilder.appendQueryParameter("page", "1");
        String MOVIE_REQUEST_URL = uriBuilder.toString();


        return new MovieLoader(getContext(), MOVIE_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Movie>> loader, List<Movie> data) {
        Log.e(LOG_TAG, ": onLoadFinished");
        for (int x = 0; x<data.size(); x++) {
            Movie currentMovie = data.get(x);
            Cursor cursor = readDataFromDB(currentMovie);
            if (cursor.getCount() == 0) {
                saveMovie(currentMovie);
            }
            //readReviewsFromNetwork(currentMovie);
        }
    }

    private void readReviewsFromNetwork(Movie currentMovie) {

        Uri baseUri = Uri.parse(BASE_MOVIE_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendEncodedPath(String.valueOf(currentMovie.getId()));
        uriBuilder.appendEncodedPath(REVIEW_PATH_URL);
        uriBuilder.appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY);
        uriBuilder.appendQueryParameter("language", "en");
        uriBuilder.appendQueryParameter("page", "1");
        String MOVIE_REQUEST_URL = uriBuilder.toString();

        ReviewAsyncTask networkAsyncTask = new ReviewAsyncTask();

        networkAsyncTask.execute(MOVIE_REQUEST_URL);
    }

    private Cursor readDataFromDB(Movie currentMovie) {
        String[] projection = {
                MovieEntry._ID
                , MovieEntry.COLUMN_ORIGINAL_TITLE
                , MovieEntry.COLUMN_LOCAL_TITLE
                , MovieEntry.COLUMN_OVERVIEW
                , MovieEntry.COLUMN_RELEASE_DATE
                , MovieEntry.COLUMN_POSTER_PATH
                , MovieEntry.COLUMN_POPULARITY
                , MovieEntry.COLUMN_VOTE_AVERAGE
                , MovieEntry.COLUMN_VOTE_COUNT
                , MovieEntry.COLUMN_FAVORITE
        };
        Uri singleUri;
        singleUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, currentMovie.getId());

        return getActivity().getContentResolver().query(singleUri, projection, null, null, null);

    }

    private void saveMovie(Movie currentMovie) {
        int movieId = currentMovie.getId();
        String originalTitle = currentMovie.getOriginalTitle();
        String localTitle = currentMovie.getLocalTitle();
        String overview = currentMovie.getOverview();
        String releaseDate = currentMovie.getReleaseDate();
        String posterPath = currentMovie.getPosterPath();
        Double popularity = currentMovie.getPopularity();
        Double voteAverage = currentMovie.getVoteAverage();
        int voteCount = currentMovie.getVoteCount();
        int favorite = 0;

        ContentValues values = new ContentValues();
        values.put(MovieEntry._ID, movieId);
        values.put(MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
        values.put(MovieEntry.COLUMN_LOCAL_TITLE, localTitle);
        values.put(MovieEntry.COLUMN_OVERVIEW, overview);
        values.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        values.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
        values.put(MovieEntry.COLUMN_POPULARITY, popularity);
        values.put(MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        values.put(MovieEntry.COLUMN_VOTE_COUNT, voteCount);
        values.put(MovieEntry.COLUMN_FAVORITE, favorite);

        Uri newUri = getActivity()
                .getContentResolver()
                .insert(MovieEntry.CONTENT_URI, values);

        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(getContext(), "FAIL", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Movie>> loader) {
        Log.e(LOG_TAG, ": onLoadReset");
    }

    public class ReviewAsyncTask extends AsyncTask<String, Void, List<Review>> {
        @Override
        protected List<Review> doInBackground(String... params) {
            List<Review> asyncReviews = QueryUtils.fetchReviewsData(params[0]);
            return asyncReviews;
        }
    }
}
