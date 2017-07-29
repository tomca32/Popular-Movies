package io.tomislav.movies.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MovieGridActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);

        imageView = (ImageView) findViewById(R.id.popularMovie);

        Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").into(imageView);
    }
}
