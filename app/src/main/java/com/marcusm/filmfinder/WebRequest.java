package com.marcusm.filmfinder;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Marcus on 1/14/2016.
 */
public class WebRequest extends AsyncTask<Void, Void, String> {
    URL url;

    public WebRequest(String urlString) {
        url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Void... arg0) {
        String result = "";
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader br = new BufferedReader(in);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null){
                sb.append(line+"\n");
            }
            br.close();
            return sb.toString();
        } catch (IOException e){
            e.printStackTrace();
            return null;
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        /*HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        request.addHeader("Accept", "application/json");
        // request.addHeader("deviceId", deviceId);
        HttpResponse response;
        ResponseHandler<String> handler = new BasicResponseHandler();
        try {
            result = httpclient.execute(request, handler);
            // response = httpclient.execute(request);
            // Log.i("Test",response.getStatusLine().toString());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpclient.getConnectionManager().shutdown();

        return result;*/
    }

    protected String onPostExectue(String s) {
        return s;
    }

    public static JSONObject APICall(String requestString) {
        String JSONString = "";
        WebRequest request = new WebRequest(requestString);
        try {
            JSONString = request.execute().get();
        } catch (Exception e) {
            return null;
        }

        JSONObject obj;
        try {
            obj = new JSONObject(JSONString);
        } catch (JSONException e) {
            return null;
        } catch (NullPointerException e){
            return null;
        }

        return obj;
    }

}
