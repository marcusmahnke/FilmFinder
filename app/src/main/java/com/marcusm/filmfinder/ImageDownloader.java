package com.marcusm.filmfinder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Marcus on 1/14/2016.
 */
public class ImageDownloader extends AsyncTask<Void, Void, Bitmap> {

    //String urlString;
    URL url;

    public ImageDownloader(String urlString) {
        //this.urlString = urlString;
        url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Bitmap doInBackground(Void... arg0) {
        //String result = "";

        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally{
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        /*HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        request.addHeader("Accept", "application/json");
        // request.addHeader("deviceId", deviceId);
        HttpResponse response;

        try {
            response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        httpclient.getConnectionManager().shutdown();
        return null; */
    }

    protected Bitmap onPostExectue(Bitmap b) {
        return b;
    }

    public static Bitmap loadImage(String imageurl){
        Bitmap image = null;
        ImageDownloader id = new ImageDownloader(imageurl);
        try {
            image = id.execute().get();
        } catch (Exception e) {
        }

        return image;
    }
}
