package tech.linard.android.cinephilia.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

import tech.linard.android.cinephilia.Model.Movie;
import tech.linard.android.cinephilia.R;

import static tech.linard.android.cinephilia.Activities.MovieAdapter.BASE_IMG_URL;
import static tech.linard.android.cinephilia.Util.QueryUtils.createUrl;

public class DetailActivity extends AppCompatActivity {
    private static final String BASE_IMG_SIZE = "w780/";
    Movie currentMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        currentMovie = intent.getParcelableExtra("movie");
        preencheCampos();
    }

    private void preencheCampos() {

        ImageView imageView = (ImageView) findViewById(R.id.detail_poster);


        String posterUrl = BASE_IMG_URL + BASE_IMG_SIZE + currentMovie.getPosterPath();
        URL url = createUrl(posterUrl);

        Picasso.with(this).load(String.valueOf(url)).into(imageView);


        ((TextView) findViewById(R.id.detail_title))
                .setText(currentMovie.getLocalTitle());
        ((TextView) findViewById(R.id.detail_release_date))
                .setText(currentMovie.getReleaseDate());
        ((TextView) findViewById(R.id.plot_synopsis))
                .setText(currentMovie.getOverview());
        ((TextView) findViewById(R.id.detail_txt_vote_average))
                .setText(String.valueOf(currentMovie.getVoteAverage()));
    }
}
