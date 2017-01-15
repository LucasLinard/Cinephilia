package tech.linard.android.cinephilia.Activities;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import tech.linard.android.cinephilia.BuildConfig;
import tech.linard.android.cinephilia.Model.Review;
import tech.linard.android.cinephilia.Model.Trailer;
import tech.linard.android.cinephilia.R;
import tech.linard.android.cinephilia.Util.QueryUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrailersFragment extends Fragment {

    public  String BASE_MOVIE_REQUEST_URL =
            "https://api.themoviedb.org/3/movie/";
    public String TRAILER_PATH_URL = "videos";
    private TrailerAdapter mAdpter;
    ListView listView;
    private List<Trailer> trailersList;

    public TrailersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_trailers, container, false);
        int movieID = getArguments().getInt("movieID");
        readDataFromNetwork(movieID);
        listView = (ListView) rootView.findViewById(R.id.trailers_listview);
        mAdpter = new TrailerAdapter(getContext(), new ArrayList<Trailer>());
        listView.setAdapter(mAdpter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String BASE_TRAILER_URL = "https://www.youtube.com/watch?";
                Uri baseUri = Uri.parse(BASE_TRAILER_URL);
                Uri.Builder uriBuilder = baseUri.buildUpon();
                uriBuilder.appendQueryParameter("v", mAdpter.getItem(position).getKey());
                String url = uriBuilder.toString();
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    public void setData(List<Trailer> data) {
        this.trailersList = data;
    }

    public List<Trailer> getData() {
        return trailersList;
    }

    private void readDataFromNetwork(int movieID) {
        Uri baseUri = Uri.parse(BASE_MOVIE_REQUEST_URL);

        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendEncodedPath(String.valueOf(movieID));
        uriBuilder.appendEncodedPath(TRAILER_PATH_URL);
        uriBuilder.appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY);
        uriBuilder.appendQueryParameter("language", "en");
        uriBuilder.appendQueryParameter("page", "1");
        String TRAILER_REQUEST_URL = uriBuilder.toString();

        TrailersFragment.TrailerAsyncTask networkAsyncTask = new TrailerAsyncTask();
        networkAsyncTask.execute(TRAILER_REQUEST_URL);
    }

    public class TrailerAsyncTask extends AsyncTask<String, Void, List<Trailer>> {
        @Override
        protected List<Trailer> doInBackground(String... params) {
            List<Trailer> asyncReviews = QueryUtils.fetchTrailersData(params[0]);
            return asyncReviews;
        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {
            super.onPostExecute(trailers);
            if (trailers != null) {
                setData(trailers);
                mAdpter.addAll(trailers);
            }
        }
    }

}
