package tech.linard.android.cinephilia.Activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tech.linard.android.cinephilia.BuildConfig;
import tech.linard.android.cinephilia.Data.MovieContract;
import tech.linard.android.cinephilia.Model.Review;
import tech.linard.android.cinephilia.Model.Trailer;
import tech.linard.android.cinephilia.R;
import tech.linard.android.cinephilia.Util.QueryUtils;

import static tech.linard.android.cinephilia.Activities.MovieAdapter.BASE_IMG_URL;
import static tech.linard.android.cinephilia.Util.QueryUtils.createUrl;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private Uri mCurrentMovieUri;
    public int movieID;
    private static final String BASE_IMG_SIZE = "w154/";
    List<Review> reviews;
    int favorite = 0;
    List<Trailer> trailers;
    ReviewAdapter reviewAdapter;
    TrailerAdapter trailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        mCurrentMovieUri = intent.getData();
        Cursor cursor = readDataFromDB();
        displayData(cursor);
        cursor.close();

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setOnClickListener(this);

        ExpandableHeightListView reviewListView = (ExpandableHeightListView) findViewById(R.id.reviews_listview);
        reviewAdapter = new ReviewAdapter(this, new ArrayList<Review>());
        reviewListView.setAdapter(reviewAdapter);
        reviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(reviewAdapter.getItem(position).getUrl()));
                startActivity(intent);
            }
        });

        ExpandableHeightListView trailerListView = (ExpandableHeightListView) findViewById(R.id.trailers_listview);
        trailerAdapter = new TrailerAdapter(this, new ArrayList<Trailer>());
        trailerListView.setAdapter(trailerAdapter);
        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String BASE_TRAILER_URL = "https://www.youtube.com/watch?";
                Uri baseUri = Uri.parse(BASE_TRAILER_URL);
                Uri.Builder uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("v", trailerAdapter.getItem(position).getKey());
                String url = uriBuilder.toString();
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        startNetworkTask();


    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startNetworkTask() {
        String BASE_MOVIE_REQUEST_URL =
                "https://api.themoviedb.org/3/movie/";
        String REVIEW_PATH_URL = "reviews";
        String TRAILER_PATH_URL = "videos";
        RequestQueue queue = Volley.newRequestQueue(this);

        //fetch reviews
        Uri baseUri;
        Uri.Builder uriBuilder;

        baseUri = Uri.parse(BASE_MOVIE_REQUEST_URL);
        uriBuilder = baseUri.buildUpon();
        uriBuilder.appendEncodedPath(String.valueOf(movieID));
        uriBuilder.appendEncodedPath(REVIEW_PATH_URL);
        uriBuilder.appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY);
        uriBuilder.appendQueryParameter("language", "en");
        uriBuilder.appendQueryParameter("page", "1");
        String REVIEW_REQUEST_URL = uriBuilder.toString();

        JsonObjectRequest jsonReviewsRequest = new JsonObjectRequest(
                Request.Method.GET,
                REVIEW_REQUEST_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        reviews = QueryUtils.extractReviewsData(response);
                        reviewAdapter.addAll(reviews);
                        ExpandableHeightListView reviewListView = (ExpandableHeightListView) findViewById(R.id.reviews_listview);
                        reviewListView.setExpanded(true);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "ERROR VOLLEY!");
            }
        });
        queue.add(jsonReviewsRequest);

        //fetch trailers

        uriBuilder = baseUri.buildUpon();
        uriBuilder.appendEncodedPath(String.valueOf(movieID));
        uriBuilder.appendEncodedPath(TRAILER_PATH_URL);
        uriBuilder.appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY);
        uriBuilder.appendQueryParameter("language", "en");
        uriBuilder.appendQueryParameter("page", "1");
        String TRAILER_REQUEST_URL = uriBuilder.toString();

        JsonObjectRequest jsonTrailersRequest = new JsonObjectRequest(
                Request.Method.GET,
                TRAILER_REQUEST_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        trailers = QueryUtils.extractTrailersData(response);
                        trailerAdapter.addAll(trailers);
                        ExpandableHeightListView trailerListView = (ExpandableHeightListView) findViewById(R.id.trailers_listview);
                        trailerListView.setExpanded(true);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "ERROR VOLLEY!");
            }
        });
        queue.add(jsonTrailersRequest);
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

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);

        if (favorite == 0) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }

        ImageView imageView = (ImageView) findViewById(R.id.detail_poster);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.detail_rating_bar);
        ratingBar.setIsIndicator(true);
        ratingBar.setMax(10);
        float voteWeighted = (float) (voteAverage / 3.0);

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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkBox) {
            CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
            if (checkBox.isChecked()) {
                favorite = 1;
                Toast.makeText(this, "FAVORITE " + String.valueOf(favorite), Toast.LENGTH_SHORT).show();
            } else {
                favorite = 0;
                Toast.makeText(this, "FAVORITE " + String.valueOf(favorite), Toast.LENGTH_SHORT).show();
            }
            updateMovieDB();
        }
    }

    private void updateMovieDB() {
        Cursor cursor = readDataFromDB();

        cursor.moveToFirst();

        final String originalTitle = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE ));
        final String localTitle = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_LOCAL_TITLE ));
        final String overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW ));
        final String releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE ));
        final String posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH ));
        final Double popularity = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY ));
        final float voteAverage = (float) cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE ));
        final int voteCount = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT ));

        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry._ID, this.movieID);
        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
        values.put(MovieContract.MovieEntry.COLUMN_LOCAL_TITLE, localTitle);
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
        values.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, voteCount);
        values.put(MovieContract.MovieEntry.COLUMN_FAVORITE, favorite);

        Uri singleUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, this.movieID);

        int result = getContentResolver().update(singleUri, values, null, null);
        Log.v("RESULT UPDATE", String.valueOf(result));

    }
}
