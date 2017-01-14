package tech.linard.android.cinephilia.Activities;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URL;

import tech.linard.android.cinephilia.Data.MovieContract.MovieEntry;
import tech.linard.android.cinephilia.R;

import static tech.linard.android.cinephilia.Util.QueryUtils.createUrl;

/**
 * Created by llinard on 04/01/17.
 */

public class MovieAdapter extends CursorAdapter {

    public static final String BASE_IMG_URL = "https://image.tmdb.org/t/p/";
    private static final String BASE_IMG_SIZE = "w92/";
    private static final String LOG_TAG = "ADAPTER";


    public MovieAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.movie_grid_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final int movieId = cursor.getInt(cursor.getColumnIndex(MovieEntry._ID));
        final String originalTitle = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_ORIGINAL_TITLE ));
        final String localTitle = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_LOCAL_TITLE ));
        final String overview = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW ));
        final String releaseDate = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE ));
        final String posterPath = cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH ));
        final Double popularity = cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_POPULARITY ));
        final Double voteAverage = cursor.getDouble(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE ));
        final int voteCount = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_COUNT ));
        final int favorite = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_FAVORITE ));

        ImageView imageView = (ImageView) view.findViewById(R.id.item_poster_thumb);
        String posterUrl = BASE_IMG_URL + BASE_IMG_SIZE + posterPath;
        URL url = createUrl(posterUrl);
        Picasso.with(view.getContext())
                .load(String.valueOf(url))
                .resize(300,450)
                .into(imageView);
    }

}
