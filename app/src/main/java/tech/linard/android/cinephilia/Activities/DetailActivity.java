package tech.linard.android.cinephilia.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

import tech.linard.android.cinephilia.Data.MovieContract;
import tech.linard.android.cinephilia.R;

import static tech.linard.android.cinephilia.Activities.MovieAdapter.BASE_IMG_URL;
import static tech.linard.android.cinephilia.Util.QueryUtils.createUrl;

public class DetailActivity extends AppCompatActivity {

    private Uri mCurrentMovieUri;
    public int movieID;
    private static final String BASE_IMG_SIZE = "w154/";
    private ReviewsFragment reviewsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        mCurrentMovieUri = intent.getData();
        Cursor cursor = readDataFromDB();
        displayData(cursor);

    }

    @Override
    protected void onResume() {
        Bundle bundle = new Bundle();
        bundle.putInt("movieID", movieID);
        // set Fragmentclass Arguments
        reviewsFragment = new ReviewsFragment();
        reviewsFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_reviews_placeholder, reviewsFragment).commit();
        super.onResume();
    }

    private void displayData(Cursor cursor) {
        cursor.moveToFirst();

        this.movieID = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
        final String originalTitle = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE ));
        final String localTitle = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_LOCAL_TITLE ));
        final String overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW ));
        final String releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE ));
        final String posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH ));
        final Double popularity = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY ));
        final float voteAverage = (float) cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE ));
        final int voteCount = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT ));
        final int favorite = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE ));


        ImageView imageView = (ImageView) findViewById(R.id.detail_poster);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.detail_rating_bar);
        ratingBar.setIsIndicator(true);
        ratingBar.setMax(10);
        float voteWeighted = (float) (voteAverage / 2.0);

        ratingBar.setRating(voteWeighted);


        String posterUrl = BASE_IMG_URL + BASE_IMG_SIZE + posterPath;
        URL url = createUrl(posterUrl);

        Picasso.with(this)
                .load(String.valueOf(url))
                .resize(600,900)
                .into(imageView);

        ((TextView) findViewById(R.id.detail_title))
                .setText(originalTitle);
        ((TextView) findViewById(R.id.detail_release_date))
                .setText(releaseDate);
        ((TextView) findViewById(R.id.plot_synopsis))
                .setText(overview);
        ((TextView) findViewById(R.id.detail_txt_vote_average))
                .setText(String.valueOf(voteAverage));
    }

    private Cursor readDataFromDB() {
        String[] projection = {
                MovieContract.MovieEntry._ID
                , MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE
                , MovieContract.MovieEntry.COLUMN_LOCAL_TITLE
                , MovieContract.MovieEntry.COLUMN_OVERVIEW
                , MovieContract.MovieEntry.COLUMN_RELEASE_DATE
                , MovieContract.MovieEntry.COLUMN_POSTER_PATH
                , MovieContract.MovieEntry.COLUMN_POPULARITY
                , MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE
                , MovieContract.MovieEntry.COLUMN_VOTE_COUNT
                , MovieContract.MovieEntry.COLUMN_FAVORITE
        };
        return getContentResolver().query(mCurrentMovieUri, projection, null, null, null);
    }
}
