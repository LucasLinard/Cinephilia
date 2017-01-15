package tech.linard.android.cinephilia.Util;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import tech.linard.android.cinephilia.Model.Movie;

/**
 * Created by llinard on 05/01/17.
 */

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private String mUrl;
    public MovieLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<Movie> movies = (List<Movie>) QueryUtils.fetchMoviesData(mUrl);
        return movies;
    }
}
