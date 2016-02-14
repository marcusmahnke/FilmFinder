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

    private static final String RT_MOVIE_URL = "http://api.rottentomatoes.com/api/public/v1.0/movie_alias.json?apikey=##&type=imdb&id=";
    private static final String RATINGS = "ratings";
    private static final String CRITICS_SCORE = "critics_score";
    private static final String CRITICS_CONSENSUS = "critics_consensus";
    private static final String AUDIENCE_SCORE = "audience_score";
    private static final String CAST = "abridged_cast";
    private static final String ACTOR_NAME = "name";
    private static final String SYNOPSIS = "synopsis";
    private static final String MPAA_RATING = "mpaa_rating";
    private static final String RUNTIME = "runtime";

    private static final String TMDB_API_KEY = "api_key=##";
    private static final String TMDB_MOVIE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String TMDB_POSTER_URL = "https://image.tmdb.org/t/p/w780";
    private static final String TMDB_BACKDROP_URL = "https://image.tmdb.org/t/p/w1280";
    private static final String TMDB_THUMBNAIL_URL = "https://image.tmdb.org/t/p/w92";
    private static final String MOVIE_ID = "id";
    private static final String IMDB_ID = "imdb_id";
    private static final String TITLE = "title";
    private static final String POSTER = "poster_path";
    private static final String RELEASE_DATE = "release_date";
    private static final String BACKDROP = "backdrop_path";

    String[] castArray;
    String id, IMDBid, title, year, posterURL, thumbURL, backdropURL, synopsis, rating, consensus,
    castString;

    int criticScore, audienceScore, runtime;

    Movie(){
        initMovie("","","","","","","","", -1, -1, "", -1, null, "");
    }

    Movie(String id, String IMDBid, String title, String year, String posterURL, String backdropURL,
          String thumbURL, String synopsis, int criticScore, int audienceScore,
          String rating, int runtime, String cast, String consensus){

        initMovie(id, IMDBid, title, year, posterURL, backdropURL, thumbURL, synopsis,
                criticScore, audienceScore, rating, runtime, cast, consensus);
    }

    Movie(JSONObject obj) {
        try {
            id = obj.getString(MOVIE_ID);
            title = obj.getString(TITLE);
            String imageURL = Uri.decode(obj.getString(POSTER));
            posterURL = TMDB_POSTER_URL + imageURL;
            thumbURL = TMDB_THUMBNAIL_URL + imageURL;
            year = obj.getString(RELEASE_DATE).substring(0, 4);

        } catch (JSONException e) {
            Log.e("JSON ERROR", this.toString());
        }
    }

    Movie(Parcel in){
        /*String[] cast;
        int arrayLength = in.readInt();
        if(arrayLength != 0) {
            cast = new String[in.readInt()];
            in.readStringArray(cast);
        } else {
            cast = null;
        }*/

        initMovie(in.readString(), in.readString(), in.readString(), in.readString(), in.readString(), in.readString(),
                in.readString(), in.readString(), in.readInt(), in.readInt(), in.readString(), in.readInt(),
                in.readString(), in.readString());
    }

    void initMovie(String id, String IMDBid, String title, String year, String posterURL, String backdropURL, String thumbURL,
                   String synopsis, int criticScore, int audienceScore,
                   String rating, int runtime, String cast, String consensus){
        this.id = id;
        this.IMDBid = IMDBid;
        this.title = title;
        this.year = year;
        this.posterURL = posterURL;
        this.backdropURL = backdropURL;
        this.thumbURL = thumbURL;
        this.synopsis = synopsis;
        this.audienceScore = audienceScore;
        this.criticScore = criticScore;
        this.rating = rating;
        this.runtime = runtime;
        this.castString = cast;
        if(cast != null) {
            this.castArray = cast.split(",");
        }
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

    public String castArrayToString() {
        StringBuilder sb = new StringBuilder(120);
        if(castArray!=null){
            for(int i=0; i<castArray.length; i++){
                sb.append(castArray[i]);
                if(i!=castArray.length-1)
                    sb.append(", ");
            }
        }
        return sb.toString();
    }

    void getMovieDetailsFromJSON() {
        JSONObject TMDBObject = WebRequest.APICall(TMDB_MOVIE_URL + this.id + "?" + TMDB_API_KEY);
        try{
            IMDBid = TMDBObject.getString(IMDB_ID);
            synopsis = TMDBObject.getString("overview");
            backdropURL = TMDB_BACKDROP_URL + Uri.decode(TMDBObject.getString(BACKDROP));
        } catch (JSONException e){
            Log.e("JSON ERROR", this.toString());
        }

        System.out.println(RT_MOVIE_URL + IMDBid.substring(2));

        JSONObject RTObject = WebRequest.APICall(RT_MOVIE_URL + IMDBid.substring(2));
        try {
            JSONObject scores = RTObject.getJSONObject(RATINGS);
            criticScore = scores.getInt(CRITICS_SCORE);
            audienceScore = scores.getInt(AUDIENCE_SCORE);
            rating = RTObject.getString(MPAA_RATING);
            runtime = RTObject.getInt(RUNTIME);
            consensus = RTObject.getString(CRITICS_CONSENSUS);

            JSONArray abridged_cast = RTObject.getJSONArray(CAST);
            int length = abridged_cast.length();
            StringBuilder sb = new StringBuilder(120);
            castArray = new String[length];
            for (int i = 0; i < length; i++) {
                JSONObject actor = abridged_cast.getJSONObject(i);
                String actorString = actor.getString(ACTOR_NAME);
                castArray[i] = actorString;
                sb.append(actorString);
                if(i != length-1){
                    sb.append(", ");
                }
            }
            castString = sb.toString();

        } catch (JSONException e) {
            Log.e("JSON ERROR", this.toString());
        }
    }

    public String getCastString(){
        return castString;
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

    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
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

    public String getIMDBid(){
        return this.IMDBid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //if(castArray==null){
        //    dest.writeInt(0);
        //} else {
        //    dest.writeInt(castArray.length);
       // }
        dest.writeString(id);
        dest.writeString(IMDBid);
        dest.writeString(title);
        dest.writeString(year);
        dest.writeString(posterURL);
        dest.writeString(thumbURL);
        dest.writeString(synopsis);
        dest.writeInt(audienceScore);
        dest.writeInt(criticScore);
        dest.writeString(rating);
        dest.writeInt(runtime);
        dest.writeString(getCastString());
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
