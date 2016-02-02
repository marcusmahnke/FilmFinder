package com.marcusm.filmfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieActivity extends AppCompatActivity {

    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        TextView titleView = (TextView) this.findViewById(R.id.movie_title);
        TextView criticsScoreView = (TextView) this.findViewById(R.id.movie_critics_score);
        TextView audienceScoreView = (TextView) this.findViewById(R.id.movie_audience_score);
        TextView synopsisView = (TextView) this.findViewById(R.id.movie_synopsis);
        ImageView movieImageView = (ImageView) this.findViewById(R.id.movie_image);

        Bundle extras = getIntent().getExtras();
        movie = extras.getParcelable("movie");



        movie.getMovieDetailsFromJSON();
        //System.out.println(movie.getBackdropURL());
        movieImageView.setImageBitmap(ImageDownloader.loadImage(movie.getBackdropURL()));
        titleView.setText(movie.getTitle());
        criticsScoreView.setText(Integer.toString(movie.getCriticScore()));
        audienceScoreView.setText(Integer.toString(movie.getAudienceScore()));
        synopsisView.setText(movie.getSynopsis());

    }

    void getExtraMovieDetails(){

    }
}
