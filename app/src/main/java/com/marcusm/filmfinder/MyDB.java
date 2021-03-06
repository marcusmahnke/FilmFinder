package com.marcusm.filmfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

/**
 * Created by Marcus on 1/23/2016.
 */
public class MyDB {
    public final static String MOVIES_TABLE = "Movies";
    public final static String MOVIE_ID = "_id";
    public final static String IMDB_ID = "imdb_id";
    public final static String MOVIE_TITLE = "title";
    public final static String YEAR = "year";
    public final static String POSTER_URL = "poster_url";
    public final static String BACKDROP_URL = "backdrop_url";
    public final static String THUMBNAIL_URL = "thumb_url";
    public final static String SYNOPSIS = "synopsis";
    public final static String CRITICS_SCORE = "critics_score";
    public final static String AUDIENCE_SCORE = "audience_score";
    public final static String MPAA_RATING = "rating";
    public final static String RUNTIME = "runtime";
    public final static String ACTORS = "actors";
    public final static String CONSENSUS = "consensus";
    public final static String GENRE = "genre_";
    public final static String GENRE_1 = "genre_1";
    public final static String GENRE_2 = "genre_2";
    public final static String GENRE_3 = "genre_3";
    public final static String GENRE_4 = "genre_4";
    public final static String SIMILAR = "similar";
    public final static String SEEN = "seen";
    public final static String LIKED = "liked";
    public final static int NUM_GENRES = 4;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public MyDB(Context context){
        dbHelper = DatabaseHelper.getHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long createMinimalMovieRecord(Movie movie, int similar, int seen, int liked){
        ContentValues values = new ContentValues();
        values.put(MOVIE_ID, movie.getId());
        values.put(MOVIE_TITLE, movie.getTitle());
        values.put(YEAR, movie.getYear());
        values.put(POSTER_URL, movie.getPosterURL());
        values.put(THUMBNAIL_URL, movie.getThumbURL());
        int[] genres = movie.getGenreIDsArray();
        int length = genres.length < NUM_GENRES ? genres.length : NUM_GENRES;
        for(int i = 0; i < length; i++){
            values.put(GENRE + (i + 1), genres[i]);
        }
        //values.put(GENRE_1, genres[0]);
        //values.put(GENRE_2, genres[1]);
        //values.put(GENRE_3, genres[2]);
        //values.put(GENRE_4, genres[3]);
        values.put(SIMILAR, similar);
        values.put(SEEN, seen);
        values.put(LIKED, liked);
        return db.insertWithOnConflict(MOVIES_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public long createDetailedMovieRecord(Movie movie, int similar, int seen, int liked){
        ContentValues values = new ContentValues();
        values.put(MOVIE_ID, movie.getId());
        values.put(IMDB_ID, movie.getIMDBid());
        values.put(MOVIE_TITLE, movie.getTitle());
        values.put(YEAR, movie.getYear());
        values.put(POSTER_URL, movie.getPosterURL());
        values.put(BACKDROP_URL, movie.getBackdropURL());
        values.put(THUMBNAIL_URL, movie.getThumbURL());
        values.put(SYNOPSIS, movie.getSynopsis());
        values.put(CRITICS_SCORE, movie.getCriticScore());
        values.put(AUDIENCE_SCORE, movie.getAudienceScore());
        values.put(MPAA_RATING, movie.getRating());
        values.put(RUNTIME, movie.getRuntime());
        values.put(ACTORS, movie.getCastString());
        values.put(CONSENSUS, movie.getConsensus());
        int[] genres = movie.getGenreIDsArray();
        int length = genres.length < NUM_GENRES ? genres.length : NUM_GENRES;
        for(int i = 0; i < length; i++){
            values.put(GENRE + (i + 1), genres[i]);
        }
        //values.put(GENRE_1, genres[1]);
        //values.put(GENRE_2, genres[2]);
        //values.put(GENRE_3, genres[3]);
        //values.put(GENRE_4, genres[4]);
        values.put(SIMILAR, similar);
        values.put(SEEN, seen);
        values.put(LIKED, liked);
        return db.insertWithOnConflict(MOVIES_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public Cursor selectMovieRecords(boolean similar, boolean seen, String orderBy){
        String selection;
        if(similar) {
            selection = SIMILAR + " = 1";
        } else if(seen) {
            selection = SEEN + " = 1";
        } else {
            selection = SEEN + " = 0 and " + SIMILAR + " = 0";
        }

        String[] cols = new String[] {MOVIE_ID, IMDB_ID, MOVIE_TITLE, YEAR, POSTER_URL, BACKDROP_URL, THUMBNAIL_URL,
                SYNOPSIS, CRITICS_SCORE, AUDIENCE_SCORE, MPAA_RATING, RUNTIME, ACTORS, CONSENSUS, LIKED};

        Cursor mCursor = db.query(true, MOVIES_TABLE, cols, selection, null, null, null, orderBy, null);
        if (mCursor != null){
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public Cursor selectMovieRecords(boolean similar, boolean seen, int year, int genre, String orderBy){
        StringBuilder sb = new StringBuilder();
        if(similar) {
            sb.append(SIMILAR + " = 1");
        } else if(seen) {
            sb.append(SEEN + " = 1");
        } else {
            sb.append(SEEN + " = 0 and " + SIMILAR + " = 0");
        }

        if(year != -1) {
            sb.append(" and " + YEAR + " = " + year);
        }

        if(genre != -1){
            sb.append(" and (genre_1 = " + genre);
            sb.append(" or genre_2 = " + genre);
            sb.append(" or genre_3 = " + genre);
            sb.append(" or genre_4 = " + genre + ")");
        }

        String[] cols = new String[] {MOVIE_ID, IMDB_ID, MOVIE_TITLE, YEAR, POSTER_URL, BACKDROP_URL, THUMBNAIL_URL,
                SYNOPSIS, CRITICS_SCORE, AUDIENCE_SCORE, MPAA_RATING, RUNTIME, ACTORS, CONSENSUS, LIKED};

        Cursor mCursor = db.query(true, MOVIES_TABLE, cols, sb.toString(), null, null, null, orderBy, null);
        if (mCursor != null){
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public boolean isMovieRecorded(String id){
        String query = "Select " + MOVIE_ID + " from " + MOVIES_TABLE + " where " + MOVIE_ID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public long updateMovieRecord(Movie movie){
        ContentValues values = new ContentValues();
        values.put(IMDB_ID, movie.getIMDBid());
        values.put(BACKDROP_URL, movie.getBackdropURL());
        values.put(SYNOPSIS, movie.getSynopsis());
        values.put(CRITICS_SCORE, movie.getCriticScore());
        values.put(AUDIENCE_SCORE, movie.getAudienceScore());
        values.put(MPAA_RATING, movie.getRating());
        values.put(RUNTIME, movie.getRuntime());
        values.put(ACTORS, movie.getCastString());
        values.put(CONSENSUS, movie.getConsensus());
        return db.updateWithOnConflict(MOVIES_TABLE, values, MOVIE_ID + " ='" + movie.getId() +"'", null, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public long updateMovieRecord(String id, int similar, int seen, int liked){
        ContentValues values = new ContentValues();
        values.put(SEEN, seen);
        values.put(SIMILAR, similar);
        values.put(LIKED, liked);
        return db.updateWithOnConflict(MOVIES_TABLE, values, MOVIE_ID + " ='" + id +"'", null, SQLiteDatabase.CONFLICT_IGNORE);
    }
}
