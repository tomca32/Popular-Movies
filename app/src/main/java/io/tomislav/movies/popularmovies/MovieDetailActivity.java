package io.tomislav.movies.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String TITLE_EXTRA = "TITLE_EXTRA";
    public static final String POSTER_EXTRA = "POSTER_EXTRA";
    public static final String PLOT_EXTRA = "PLOT_EXTRA";
    public static final String RATING_EXTRA = "RATING_EXTRA";
    public static final String DURATION_EXTRA = "DURATION_EXTRA";
    public static final String DATE_EXTRA = "DATE_EXTRA";

    private String plot;
    private String rating;
    private String date;

    TextView tvMovieTitle;
    TextView tvMovieDate;
    Bundle bundle;
    ImageView ivPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {
            bundle = getIntent().getExtras();
        }

        setView();
    }

    private void setView() {
        ivPoster = (ImageView) findViewById(R.id.iv_detail_poster);
        Picasso.with(this).load(UrlService.getPosterUrl(this, bundle.getString(POSTER_EXTRA)).toString()).into(ivPoster);

        tvMovieTitle = (TextView)findViewById(R.id.tv_movie_title);
        tvMovieTitle.setText(bundle.getString(TITLE_EXTRA));

        tvMovieDate = (TextView)findViewById(R.id.tv_movie_year);
        tvMovieDate.setText(bundle.getString(DATE_EXTRA).split("-")[0]);
    }
}
