package tech.linard.android.cinephilia.Activities;

import android.content.ContentUris;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import tech.linard.android.cinephilia.Data.MovieContract.MovieEntry;
import tech.linard.android.cinephilia.Model.Movie;
import tech.linard.android.cinephilia.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private static final int MOVIE_LOADER_ID = 2;

    public MovieAdapter mAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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


        return rootView;
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
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default)
        );

        String sortOrder = MovieEntry.COLUMN_POPULARITY + " DESC LIMIT 20";

        if (orderBy.equals(getString(R.string.settings_order_by_top_rated_value))) {
            sortOrder = MovieEntry.COLUMN_VOTE_AVERAGE + " DESC LIMIT 20";
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
        return new CursorLoader(getContext(), MovieEntry.CONTENT_URI, projection, null, null, sortOrder);
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
