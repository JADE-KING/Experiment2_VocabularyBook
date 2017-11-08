package com.example.yelia.experiment2_vocabularybook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yelia on 2017/10/24.
 */

public class WordDBHelper extends SQLiteOpenHelper {
    private final static String SQL_CREATE_TABLE =
            "CREATE TABLE " + WordDBConstruct.TABLE_NAME + "(" +
                    WordDBConstruct.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    WordDBConstruct.COLUMN_NAME_WORD + " TEXT UNIQUE," +
                    WordDBConstruct.COLUMN_NAME_TRANSLATION + " TEXT," +
                    WordDBConstruct.COLUMN_NAME_PHONETIC + " TEXT," +
                    WordDBConstruct.COLUMN_NAME_UKPHONETIC + " TEXT," +
                    WordDBConstruct.COLUMN_NAME_USPHONETIC + " TEXT," +
                    WordDBConstruct.COLUMN_NAME_EXPLAINS + " TEXT," +
                    WordDBConstruct.COLUMN_NAME_WEBEXPLAINS + " TEXT," +
                    WordDBConstruct.COLUMN_NAME_USERNOTE + " TEXT" +
                    ")";
    private final static String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + WordDBConstruct.TABLE_NAME;

    public WordDBHelper(Context context) {
        super(context, WordDBConstruct.DB_NAME, null, WordDBConstruct.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        this.onCreate(db);
    }
}
