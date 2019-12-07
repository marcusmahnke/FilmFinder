package com.marcusm.filmfinder;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Cursor mCursor;

    public ImageAdapter(Context c, Cursor cursor) {
        mContext = c;
        mCursor = cursor;
    }

    public int getCount() {
        return mCursor.getCount();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(325, 488));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        mCursor.moveToPosition(position);
        Bitmap image = ImageDownloader.loadImage(mCursor.getString(6));
        System.out.println(mCursor.getString(6));
        imageView.setImageBitmap(image);
        //imageView.setImageResource(mThumbIds[position]);

        return imageView;
    }
}
