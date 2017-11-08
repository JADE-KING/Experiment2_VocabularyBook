package com.example.yelia.experiment2_vocabularybook;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class AppContentProvider extends ContentProvider {
    private WordDBHelper wordDBHelper;
    private SQLiteDatabase db;

    private final static int CODE_SINGLE = 1;
    private final static int CODE_MULTIPLE = 2;

    private final static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AppContentConstruct.AUTHORITY, AppContentConstruct.PATH_SINGLE, CODE_SINGLE);
        uriMatcher.addURI(AppContentConstruct.AUTHORITY, AppContentConstruct.PATH_MULTIPLE, CODE_MULTIPLE);
    }

    public AppContentProvider() {
    }

    @Override
    public boolean onCreate() {
        wordDBHelper = new WordDBHelper(getContext());
        db = wordDBHelper.getReadableDatabase();
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CODE_SINGLE:
                return AppContentConstruct.MIME_TYPE_SINGLE;
            case CODE_MULTIPLE:
                return AppContentConstruct.MIME_TYPE_MULTIPLE;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = db.insert(AppContentConstruct.TABLE_NAME, null, values);
        Uri returnUri = ContentUris.withAppendedId(AppContentConstruct.CONTENT_URI, id);
        // 通知ContentResolver数据已变更
        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String segment = uri.getPathSegments().get(1);
        int count = db.delete(AppContentConstruct.TABLE_NAME,
                WordDBConstruct.COLUMN_NAME_WORD + "='" + segment + "'", selectionArgs);
        // 通知ContentResolver数据已变更
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String segment = uri.getPathSegments().get(1);
        int count = db.update(AppContentConstruct.TABLE_NAME, values,
                WordDBConstruct.COLUMN_NAME_WORD + "='" + segment + "'", selectionArgs);
        // 通知ContentResolver数据已变更
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case CODE_MULTIPLE:
                cursor = db.query(AppContentConstruct.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_SINGLE:
                String segment = uri.getPathSegments().get(1);
                cursor = db.query(AppContentConstruct.TABLE_NAME, projection,
                        WordDBConstruct.COLUMN_NAME_WORD + " like ?",
                        new String[] { "%" + segment + "%" }, null, null, sortOrder);
                break;
        }
        return cursor;
    }
}
