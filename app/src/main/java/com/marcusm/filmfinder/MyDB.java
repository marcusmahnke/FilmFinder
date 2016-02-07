package com.marcusm.filmfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Marcus on 1/23/2016.
 */
public class MyDB {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public final static String MOVIE_ID = "_id";
    public final static String MOVIE_TITLE = "title";

    public MyDB(Context context){
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long createMovieRecord(String id, String IMDBid, String title, String year, String imageURL, String backdropURL,
                                  String thumbURL, String synopsis, int criticScore, int audienceScore,
                                  String rating, int runtime, String cast, String consensus, int similar, int seen, int liked){
        ContentValues values = new ContentValues();
        values.put(MOVIE_ID, id);
        values.put("imdb_id", IMDBid);
        values.put(MOVIE_TITLE, title);
        values.put("year", year);
        //values.put("image", image);
        values.put("image_url", imageURL);
        values.put("backdrop_url", backdropURL);
        values.put("thumb_url", thumbURL);
        values.put("synopsis", synopsis);
        values.put("critics_score", criticScore);
        values.put("audience_score", audienceScore);
        values.put("rating", rating);
        values.put("runtime", runtime);
        values.put("actors", cast);
        values.put("consensus", consensus);
        values.put("similar", similar);
        values.put("seen", seen);
        values.put("liked", liked);
        return db.insertWithOnConflict("Movies", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public long createMovieRecord(String id, String title, String year, String imageURL,
                                  String thumbURL, int similar, int seen, int liked){
        ContentValues values = new ContentValues();
        values.put(MOVIE_ID, id);
        values.put(MOVIE_TITLE, title);
        values.put("year", year);
        values.put("image_url", imageURL);
        values.put("thumb_url", thumbURL);
        values.put("similar", similar);
        values.put("seen", seen);
        values.put("liked", liked);
        return db.insertWithOnConflict("Movies", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public Cursor selectMovieRecords(boolean similar, boolean seen, String orderBy){
        String selection;
        if(similar)
            selection = "similar = 1";
        else if(seen)
            selection = "seen = 1";
        else
            selection = "seen = 0 and similar = 0";

        String[] cols = new String[] {MOVIE_ID, "imdb_id", MOVIE_TITLE, "year", "image_url", "backdrop_url", "thumb_url",
                "synopsis", "critics_score", "audience_score", "rating", "runtime", "actors", "consensus", "liked"};

        Cursor mCursor = db.query(true, "Movies", cols, selection, null, null, null, orderBy, null);
        if (mCursor != null){
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public boolean isMovieRecorded(String id){
        String query = "Select _id from Movies where _id = " + id;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public long updateMovieRecord(String id, int similar, int seen, int liked){
        ContentValues values = new ContentValues();
        values.put("seen", seen);
        values.put("similar", similar);
        values.put("liked", liked);
        return db.updateWithOnConflict("Movies", values, "_id ='" + id +"'", null, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public long updateMovieRecord(String id, int similar, int seen, int liked, byte[] image){
        ContentValues values = new ContentValues();
        values.put("seen", seen);
        values.put("similar", similar);
        values.put("liked", liked);
        //values.put("image", image);
        return db.updateWithOnConflict("Movies", values, "_id ='" + id +"'", null, SQLiteDatabase.CONFLICT_IGNORE);
    }
}
