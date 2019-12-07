package com.marcusm.filmfinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class MovieCardAdapter extends ArrayAdapter<Movie> {

    public MovieCardAdapter(Context context){
        super(context, 0);
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent){

        if (contentView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            contentView = inflater.inflate(R.layout.movie_card, parent, false);
        }

        ImageView movieImageView = contentView.findViewById(R.id.movie_card_image);

        Movie m = getItem(position);

        Bitmap image = ImageDownloader.loadImage(m.getPosterURL());
        movieImageView.setImageBitmap(image);

        return contentView;
    }
}
