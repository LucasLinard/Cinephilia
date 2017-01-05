package tech.linard.android.cinephilia.Activities;

import android.content.Context;
import android.content.UriMatcher;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import tech.linard.android.cinephilia.Model.Movie;
import tech.linard.android.cinephilia.R;

import static tech.linard.android.cinephilia.Util.QueryUtils.createUrl;

/**
 * Created by llinard on 04/01/17.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String BASE_IMG_URL = "https://image.tmdb.org/t/p/";
    private static final String BASE_IMG_SIZE = "w342/";
    private static final String LOG_TAG = "ADAPTER";

    public MovieAdapter(Context context,  List<Movie> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridItemView = convertView;
        if (gridItemView == null) {
            gridItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_grid_item
                    , parent
                    , false );
        }
        Movie movie = getItem(position);

        ImageView imageView = (ImageView) gridItemView.findViewById(R.id.item_poster_thumb);


        String posterUrl = BASE_IMG_URL + BASE_IMG_SIZE + movie.getPosterPath();
        URL url = createUrl(posterUrl);

        Picasso.with(getContext()).load(String.valueOf(url)).into(imageView);

        TextView titleTxt = (TextView) gridItemView.findViewById(R.id.item_title);
        titleTxt.setText(movie.getLocalTitle());

        return gridItemView;
    }
}
