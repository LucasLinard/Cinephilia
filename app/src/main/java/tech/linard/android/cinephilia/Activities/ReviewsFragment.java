package tech.linard.android.cinephilia.Activities;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import tech.linard.android.cinephilia.BuildConfig;
import tech.linard.android.cinephilia.Model.Review;
import tech.linard.android.cinephilia.R;
import tech.linard.android.cinephilia.Util.QueryUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewsFragment extends Fragment {
    public  String BASE_MOVIE_REQUEST_URL =
            "https://api.themoviedb.org/3/movie/";
    public String REVIEW_PATH_URL = "reviews";
    private ReviewAdapter mAdpter;
    ListView listView;
    // data object we want to retain
    private List<Review> reviewsList;
    public ReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);
        int movieID = getArguments().getInt("movieID");
        readDataFromNetwork(movieID);

        listView = (ListView) rootView.findViewById(R.id.reviews_listview);
        mAdpter = new ReviewAdapter(getContext(), new ArrayList<Review>());
        listView.setAdapter(mAdpter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mAdpter.getItem(position).getUrl()));
                startActivity(intent);
            }
        });
        return rootView;
    }

    public void setData(List<Review> data) {
        this.reviewsList = data;
    }

    public List<Review> getData() {
        return reviewsList;
    }

    private void readDataFromNetwork(int movieID) {
        Uri baseUri = Uri.parse(BASE_MOVIE_REQUEST_URL);

        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendEncodedPath(String.valueOf(movieID));
        uriBuilder.appendEncodedPath(REVIEW_PATH_URL);
        uriBuilder.appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY);
        uriBuilder.appendQueryParameter("language", "en");
        uriBuilder.appendQueryParameter("page", "1");
        String REVIEW_REQUEST_URL = uriBuilder.toString();

        ReviewAsyncTask networkAsyncTask = new ReviewAsyncTask();
        networkAsyncTask.execute(REVIEW_REQUEST_URL);
    }

    public class ReviewAsyncTask extends AsyncTask<String, Void, List<Review>> {
        @Override
        protected List<Review> doInBackground(String... params) {
            List<Review> asyncReviews = (List<Review>) QueryUtils.fetchReviewsData(params[0]);
            return asyncReviews;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            super.onPostExecute(reviews);
            if (reviews != null) {
                setData(reviews);
                mAdpter.addAll(reviews);
            }
        }
    }
}
