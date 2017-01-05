package tech.linard.android.cinephilia.Activities;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import tech.linard.android.cinephilia.Model.Movie;
import tech.linard.android.cinephilia.R;
import tech.linard.android.cinephilia.Util.MovieLoader;

import static tech.linard.android.cinephilia.Util.QueryUtils.LOG_TAG;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Movie>>{

    private static final int MOVIE_LOADER_ID = 2;
    public  String BASE_MOVIE_REQUEST_URL =
            "https://api.themoviedb.org/3/movie/popular?";
    public MovieAdapter mAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mAdapter = new MovieAdapter(getContext(), new ArrayList<Movie>());
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid_view);
        gridView.setAdapter(mAdapter);



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
    public android.support.v4.content.Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(BASE_MOVIE_REQUEST_URL);

        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("api_key", "b5256f2714cce99d576a349d1d31aea7");
        uriBuilder.appendQueryParameter("language", "pt-BR");
        uriBuilder.appendQueryParameter("page", "1");
        String MOVIE_REQUEST_URL = uriBuilder.toString();

        return new MovieLoader(getContext(), MOVIE_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Movie>> loader, List<Movie> data) {
        Log.e(LOG_TAG, ": onLoadFinished");
        mAdapter.clear();
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Movie>> loader) {
        Log.e(LOG_TAG, ": onLoadReset");
        mAdapter.clear();
    }
}
