package tech.linard.android.cinephilia.Util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import tech.linard.android.cinephilia.Model.Movie;

/**
 * Created by llinard on 05/01/17.
 */

public final class QueryUtils {

    public static String LOG_TAG = "QueryUtils";

    private QueryUtils() {

    }
    public static List<Movie> fetchMoviesData (String mUrl) {
        URL url = createUrl(mUrl);
        String jsonResponse =null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e ) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        return extractMovies(jsonResponse);
    }

    public static URL createUrl(String mUrl) {
        URL url = null;
        try {
            url = new URL(mUrl);
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "Error creating URL", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        Log.e(LOG_TAG, ": httprequest");
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;


    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        Log.e(LOG_TAG, ": read");
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Movie> extractMovies(String jsonResponse) {

        ArrayList<Movie> stories = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray results = jsonObject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject currentJSON = results.getJSONObject(i);

                String posterPath = currentJSON.optString("poster_path");
                String overview = currentJSON.optString("overview");
                String releaseDate = currentJSON.optString("release_date");
                int id = currentJSON.optInt("id", 0);
                String originalTitle = currentJSON.optString("original_title");
                String title = currentJSON.optString("title");
                double popularity = currentJSON.optDouble("popularity");
                int voteCount = currentJSON.optInt("vote_count", 0);
                double voteAverage = currentJSON.optDouble("vote_average");

                Movie currentMovie = new Movie(
                        id,
                        originalTitle,
                        title,
                        overview,
                        releaseDate,
                        posterPath,
                        popularity,
                        voteAverage,
                        voteCount);

                stories.add(currentMovie);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
        }
        return stories;
    }


}
