package com.marcusm.filmfinder;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marcus on 1/15/2016.
 */
public class Movie implements Parcelable{
    public static String POSTERS = "posters";
    public static String POSTER_DETAILED = "detailed";
    public static String POSTER_THUMB = "thumbnail";
    public static String RATINGS = "ratings";
    public static String CRITICS_SCORE = "critics_score";
    public static String CRITICS_CONSENSUS = "critics_consensus";
    public static String AUDIENCE_SCORE = "audience_score";
    public static String CAST = "abridged_cast";
    public static String ACTOR_NAME = "name";
    public static String RELEASE_YEAR = "year";
    public static String SYNOPSIS = "synopsis";
    public static String MPAA_RATING = "mpaa_rating";
    public static String RUNTIME = "runtime";
    public static String TITLE = "title";

    String[] castArray;
    String id, IMDBid, title, year, imageURL, thumbURL, backdropURL, synopsis, rating, consensus;
    int criticScore, audienceScore, runtime;
    enum API {
        RT, TMDB
    }

    Movie(String id, String IMDBid, String title, String year, String imageURL, String backdropURL,
          String thumbURL, String synopsis, int criticScore, int audienceScore,
          String rating, int runtime, String[] cast, String consensus){

        initMovie(id, IMDBid, title, year, imageURL, backdropURL, thumbURL, synopsis,
                criticScore, audienceScore, rating, runtime, cast, consensus);
    }

    Movie(){
        initMovie("","","","","","","","", -1, -1, "", -1, null, "");
    }

    Movie(JSONObject obj) {
        try {
            id = obj.getString("id");
            title = obj.getString("title");
            imageURL = "https://image.tmdb.org/t/p/w780" + Uri.decode(obj.getString("poster_path"));
            thumbURL = "https://image.tmdb.org/t/p/w92" + imageURL;
            year = obj.getString("release_date").substring(0, 4);

        } catch (JSONException e) {
            Log.e("JSON ERROR", this.toString());
        }
    }

    Movie(Parcel in){
        String[] cast;
        int arrayLength = in.readInt();
        if(arrayLength != 0) {
            cast = new String[in.readInt()];
            in.readStringArray(cast);
        } else {
            cast = null;
        }

        initMovie(in.readString(), in.readString(), in.readString(), in.readString(), in.readString(), in.readString(),
                in.readString(), in.readString(), in.readInt(), in.readInt(), in.readString(), in.readInt(),
                cast, in.readString());
    }

    void initMovie(String id, String IMDBid, String title, String year, String imageURL, String backdropURL, String thumbURL,
                   String synopsis, int criticScore, int audienceScore,
                   String rating, int runtime, String[] cast, String consensus){
        this.id = id;
        this.title = title;
        this.year = year;
        this.imageURL = imageURL;
        this.thumbURL = thumbURL;
        this.synopsis = synopsis;
        this.audienceScore = audienceScore;
        this.criticScore = criticScore;
        this.rating = rating;
        this.runtime = runtime;
        this.castArray = cast;
        this.consensus = consensus;
    }

    @Override
    public boolean equals(Object other){
        Movie m = (Movie) other;
        if(m.getId().equals(this.id))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode(){
        return this.id.hashCode();
    }

    @Override
    public String toString(){
        return id + ": " + title + " (" + year + ")";
    }

    public String getCastString() {
        String c = "";
        if(castArray!=null){
            for(int i=0; i<castArray.length; i++){
                c += castArray[i];
                if(i!=castArray.length-1)
                    c+=",";
            }
        }
        return c;
    }

    public String getCastStringFormatted() {
        String c = "";
        for(int i=0; i<castArray.length; i++){
            c += castArray[i];
            if(i!=castArray.length-1)
                c+=", ";
        }
        return c;
    }

    void getMovieDetailsFromJSON() {
        JSONObject TMDBObject = WebRequest.APICall("https://api.themoviedb.org/3/movie/" + this.id + "?api_key=1b482df935405b1a9df063071c476c4f");
        try{
            IMDBid = TMDBObject.getString("imdb_id");
            backdropURL = "https://image.tmdb.org/t/p/w1280" + Uri.decode(TMDBObject.getString("backdrop_path"));
        } catch (JSONException e){
            Log.e("JSON ERROR", this.toString());
        }

        JSONObject RTObject = WebRequest.APICall("http://api.rottentomatoes.com/api/public/v1.0/movie_alias.json?apikey=nukgabadpcrm73c8qa68zksc&type=imdb&id=" + IMDBid.substring(2));
        try {
            JSONObject scores = RTObject.getJSONObject(RATINGS);
            criticScore = scores.getInt(CRITICS_SCORE);
            audienceScore = scores.getInt(AUDIENCE_SCORE);
            synopsis = RTObject.getString("synopsis");
            rating = RTObject.getString("mpaa_rating");
            runtime = RTObject.getInt("runtime");
            consensus = RTObject.getString(CRITICS_CONSENSUS);

            JSONArray abridged_cast = RTObject.getJSONArray(CAST);
            castArray = new String[abridged_cast.length()];
            for (int i = 0; i < abridged_cast.length(); i++) {
                JSONObject actor = abridged_cast.getJSONObject(i);
                castArray[i] = actor.getString(ACTOR_NAME);
            }

        } catch (JSONException e) {
            Log.e("JSON ERROR", this.toString());
        }
    }

    public String[] getCastArray() {
        return castArray;
    }

    public void setCastArray(String[] castArray) {
        this.castArray = castArray;
    }

    public String getConsensus() {
        return consensus;
    }

    public void setConsensus(String consensus) {
        this.consensus = consensus;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public int getCriticScore() {
        return criticScore;
    }

    public void setCriticScore(int criticScore) {
        this.criticScore = criticScore;
    }

    public int getAudienceScore() {
        return audienceScore;
    }

    public void setAudienceScore(int audienceScore) {
        this.audienceScore = audienceScore;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    public String getBackdropURL() {
        return backdropURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(castArray==null){
            dest.writeInt(0);
        } else {
            dest.writeInt(castArray.length);
        }
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(year);
        dest.writeString(imageURL);
        dest.writeString(thumbURL);
        dest.writeString(synopsis);
        dest.writeInt(audienceScore);
        dest.writeInt(criticScore);
        dest.writeString(rating);
        dest.writeInt(runtime);
        dest.writeStringArray(castArray);
        dest.writeString(consensus);

    }

    public static final Parcelable.Creator<Movie> CREATOR= new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
// TODO Auto-generated method stub
            return new Movie(source);  //using parcelable constructor
        }

        @Override
        public Movie[] newArray(int size) {
// TODO Auto-generated method stub
            return new Movie[size];
        }
    };
}
