package com.sudo.equeue.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class HHContentProvider extends ContentProvider {

    private static final UriMatcher uriMatcher;

    private static final String AUTHORITY = "com.example.alex.headhunter.provider";

    private static final String SEARCH_RESULTS_PATH = "search_result";
    private static final int SEARCH_RESULTS_CODE = 1;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, SEARCH_RESULTS_PATH, SEARCH_RESULTS_CODE);
    }

    private DBHelper dbHelper;

    @Override
    public boolean onCreate() {

        dbHelper = new DBHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {

            case SEARCH_RESULTS_CODE:
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query(
                        SEARCH_RESULTS_PATH,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;

        }

        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {

            case SEARCH_RESULTS_CODE:
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.insert(
                        SEARCH_RESULTS_PATH,
                        null,
                        values
                );
                getContext().getContentResolver().notifyChange(uri, null);
                break;

        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {

            case SEARCH_RESULTS_CODE:
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int deleted = db.delete(
                        SEARCH_RESULTS_PATH,
                        null,
                        null
                );
                getContext().getContentResolver().notifyChange(uri, null);
                return deleted;

        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
