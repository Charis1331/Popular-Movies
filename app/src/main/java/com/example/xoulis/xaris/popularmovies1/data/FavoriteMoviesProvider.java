package com.example.xoulis.xaris.popularmovies1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.xoulis.xaris.popularmovies1.data.FavoriteMoviesContract.FavoriteMovieEntry;

public class FavoriteMoviesProvider extends ContentProvider {
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private FavoriteMoviesDBHelper dbHelper;

    private static final int FAVORITE_MOVIE = 100;
    private static final int FAVORITE_MOVIE_WITH_ID = 200;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteMoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FavoriteMovieEntry.TABLE_FAVORITES, FAVORITE_MOVIE);
        matcher.addURI(authority, FavoriteMoviesContract.FavoriteMovieEntry.TABLE_FAVORITES + "/#", FAVORITE_MOVIE_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new FavoriteMoviesDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case FAVORITE_MOVIE: {
                return FavoriteMovieEntry.CONTENT_DIR_TYPE;
            }
            case FAVORITE_MOVIE_WITH_ID: {
                return FavoriteMovieEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case FAVORITE_MOVIE:
                cursor = dbHelper.getReadableDatabase().query(
                        FavoriteMovieEntry.TABLE_FAVORITES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITE_MOVIE_WITH_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        FavoriteMovieEntry.TABLE_FAVORITES,
                        projection,
                        FavoriteMovieEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        switch (uriMatcher.match(uri)) {
            case FAVORITE_MOVIE: {
                long _id = db.insert(FavoriteMovieEntry.TABLE_FAVORITES, null, contentValues);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = FavoriteMovieEntry.buildFavoritesUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int numUpdated;

        if (contentValues == null){
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch(uriMatcher.match(uri)){
            case FAVORITE_MOVIE:{
                numUpdated = db.update(FavoriteMovieEntry.TABLE_FAVORITES,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case FAVORITE_MOVIE_WITH_ID: {
                numUpdated = db.update(FavoriteMovieEntry.TABLE_FAVORITES,
                        contentValues,
                        FavoriteMovieEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numUpdated;
    }
}
