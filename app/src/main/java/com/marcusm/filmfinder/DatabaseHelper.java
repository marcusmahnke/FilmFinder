package com.marcusm.filmfinder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Marcus on 1/23/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "DBName";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE = "create table Movies( " +
            "_id integer primary key," +
            "imdb_id text," +
            "title text not null," +
            "year text not null," +
            "image_url text not null," +
            "backdrop_url text," +
            "thumb_url text not null," +
            //"image blob," +
            "synopsis text, " +
            "critics_score integer, " +
            "audience_score integer, " +
            "rating text," +
            "runtime integer," +
            "actors text," +
            "consensus text," +
            "similar integer not null," +
            "seen integer not null," +
            "liked integer not null);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS Movies");
        onCreate(db);

    }
}
