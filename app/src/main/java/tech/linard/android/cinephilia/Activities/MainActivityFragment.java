package tech.linard.android.cinephilia.Activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;

import tech.linard.android.cinephilia.BuildConfig;
import tech.linard.android.cinephilia.Data.MovieContract;
import tech.linard.android.cinephilia.Data.MovieContract.MovieEntry;
import tech.linard.android.cinephilia.Model.Movie;
import tech.linard.android.cinephilia.R;
import tech.linard.android.cinephilia.Util.QueryUtils;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static String LOG_TAG = MainActivity.class.getSimpleName();

    public  String BASE_MOVIE_REQUEST_URL =
            "https://api.themoviedb.org/3/movie/";
    private static final int MOVIE_LOADER_ID = 2;
    List<Movie> movies;

    public MovieAdapter mAdapter;
    int page = 1;
    SwipeRefreshLayout swipeRefreshLayout;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState  ) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid_view);
        mAdapter = new MovieAdapter(getContext(), null);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), DetailActivity.class);
                Uri currentMovieUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
                intent.setData(currentMovieUri);
                startActivity(intent);
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page++;
                startNetworkTask();

            }
        });
        return rootView;
    }

    private void updateDb(List<Movie> movies) {
        int favorite = 0;
        for (int x = 0; x<movies.size(); x++) {
            Movie currentMovie = movies.get(x);
            int movieID = currentMovie.getId();
            Cursor cursor = readMovieDataFromDB(movieID);

            int movieId = currentMovie.getId();
            String originalTitle = currentMovie.getOriginalTitle();
            String localTitle = currentMovie.getLocalTitle();
            String overview = currentMovie.getOverview();
            String releaseDate = currentMovie.getReleaseDate();
            String posterPath = currentMovie.getPosterPath();
            Double popularity = currentMovie.getPopularity();
            Double voteAverage = currentMovie.getVoteAverage();
            int voteCount = currentMovie.getVoteCount();

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


            if (cursor.getCount() == 0) {
                values.put(MovieEntry.COLUMN_FAVORITE, favorite);
                insertNewMovie(values);
            } else {

                updateCurrentMovie(values, cursor);
            }
        }
    }

    private void updateCurrentMovie(ContentValues values, Cursor cursor) {
        cursor.moveToFirst();
        int movieId = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
        Uri singleUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, movieId);
        getActivity().getContentResolver().update(singleUri, values, null, null);
    }


    private Cursor readMovieDataFromDB(int movieID) {
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
        singleUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, movieID);
        return getActivity().getContentResolver().query(singleUri, projection, null, null, null);
    }

    private void insertNewMovie(ContentValues values) {
        int favorite = 0;

        values.put(MovieEntry.COLUMN_FAVORITE, favorite);

        Uri newUri = getActivity()
                .getContentResolver()
                .insert(MovieEntry.CONTENT_URI, values);

        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(getContext(), "FAILED TO INSERT MOVIE", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(MOVIE_LOADER_ID, null, this);

    }

    @Override
    public void onResume() {
        super.onResume();
        this.page = 1;
        startNetworkTask();
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
    }

    public void startNetworkTask() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(BASE_MOVIE_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendEncodedPath(orderBy);
        uriBuilder.appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY);
        uriBuilder.appendQueryParameter("language", "en");
        uriBuilder.appendQueryParameter("page", String.valueOf(page));
        String MOVIE_REQUEST_URL = uriBuilder.toString();

        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                MOVIE_REQUEST_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        movies = QueryUtils.extractMoviesData(response);
                        updateDb(movies);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "ERROR VOLLEY!");
            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default)
        );

        String sortOrder = null;
        String selection = null;
        String[] selectionArgs = null;


        if (orderBy.equals(getString(R.string.settings_order_by_popularity_value))) {
            sortOrder = MovieEntry.COLUMN_POPULARITY + " DESC LIMIT 21";
        }

        if (orderBy.equals(getString(R.string.settings_order_by_top_rated_value))) {
            sortOrder = MovieEntry.COLUMN_VOTE_AVERAGE + " DESC LIMIT 21";
        }

        if (orderBy.equals("favorites")) {
            sortOrder = MovieEntry.COLUMN_LOCAL_TITLE + " ASC LIMIT 21";
            selection = MovieEntry.COLUMN_FAVORITE + " =?";
            selectionArgs = new String[] {"1"};
        }

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
        return new CursorLoader(getContext(), MovieEntry.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }




}
