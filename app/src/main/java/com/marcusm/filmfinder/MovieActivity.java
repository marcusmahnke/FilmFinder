package com.marcusm.filmfinder;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String CHANGED = "changed";
    public static final String SEEN = "seen";
    public static final String LIKED = "liked";
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



        if(movie.getIMDBid() == null){
            System.out.println("getting movie details");
            movie.getMovieDetailsFromJSON();
        }

        movieImageView.setImageBitmap(ImageDownloader.loadImage(movie.getBackdropURL()));
        titleView.setText(movie.getTitle());
        criticsScoreView.setText("Critic's Score: " + Integer.toString(movie.getCriticScore()) + "%");
        audienceScoreView.setText("Audience's Score: " + Integer.toString(movie.getAudienceScore()) + "%");
        synopsisView.setText(movie.getSynopsis());

        Button likeButton = (Button) findViewById(R.id.like_button);
        likeButton.setOnClickListener(this);
        Button dislikeButton = (Button) findViewById(R.id.dislike_button);
        dislikeButton.setOnClickListener(this);
        Button skipButton = (Button) findViewById(R.id.skip_button);
        skipButton.setOnClickListener(this);
        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int liked = 0;
        int seen = 0;
        switch(v.getId()){
            case R.id.like_button:
                liked = 1;
                seen = 1;
                break;
            case R.id.dislike_button:
                seen = 1;
                break;
            case R.id.save_button:
                break;
            case R.id.skip_button:
                seen = 2;
                break;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(CHANGED, true);
        resultIntent.putExtra(LIKED, liked);
        resultIntent.putExtra(SEEN, seen);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
