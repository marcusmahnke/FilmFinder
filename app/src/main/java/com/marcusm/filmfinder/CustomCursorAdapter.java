package com.marcusm.filmfinder;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Marcus on 1/25/2016.
 */
public class CustomCursorAdapter extends CursorAdapter{
    LayoutInflater inflater;
    boolean isUnseen;

    public CustomCursorAdapter(Context context, Cursor c, int flags, boolean isUnseen) {
        super(context, c, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.isUnseen = isUnseen;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {
        TextView movieTitleView = (TextView) v.findViewById(R.id.movie_title);
        movieTitleView.setText(c.getString(2));

        ImageView posterThumbnail = (ImageView) v.findViewById(R.id.movie_image);
        Bitmap image = ImageDownloader.loadImage(c.getString(6));
        posterThumbnail.setImageBitmap(image);

    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup parent) {
        View v = inflater.inflate(R.layout.movie_list_item, parent, false);
        return v;
    }
}
