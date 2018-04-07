package com.example.xoulis.xaris.popularmovies1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.xoulis.xaris.popularmovies1.data.FavoriteMoviesContract.FavoriteMovieEntry;

public class FavoriteMoviesDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteMoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                FavoriteMovieEntry.TABLE_FAVORITES + "(" +
                FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                FavoriteMovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
