package com.example.yelia.experiment2_vocabularybook;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yelia on 2017/10/24.
 */

public class WordDBImplement extends WordDBConstruct {
    private static WordDBHelper wordDBHelper;
    private static SQLiteDatabase db;

    // 增
    private final static String SQL_INSERT = "INSERT INTO " + TABLE_NAME + " VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?)";

    // 删
    private final static String SQL_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME_ID + "=?";

    // 改
    private final static String SQL_UPDATE = "UPDATE " + TABLE_NAME + " SET " +
            COLUMN_NAME_TRANSLATION + "=?," +
            COLUMN_NAME_PHONETIC + "=?," +
            COLUMN_NAME_UKPHONETIC + "=?," +
            COLUMN_NAME_USPHONETIC + "=?," +
            COLUMN_NAME_EXPLAINS + "=?," +
            COLUMN_NAME_WEBEXPLAINS + "=?," +
            COLUMN_NAME_USERNOTE + "=? " +
            "WHERE " + COLUMN_NAME_WORD + "=?";
    private final static String SQL_UPDATE_USERNOTE = "UPDATE " + TABLE_NAME + " SET " +
            COLUMN_NAME_USERNOTE + "=? WHERE " + COLUMN_NAME_WORD + "=?";

    // 查
    private final static String SQL_SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
    private final static String SQL_SELECT_ALL_ID_ASC = SQL_SELECT_ALL + " ORDER BY " + COLUMN_NAME_ID + " ASC";
    private final static String SQL_SELECT_ALL_ID_DESC = SQL_SELECT_ALL + " ORDER BY " + COLUMN_NAME_ID + " DESC";
    private final static String SQL_SELECT_ALL_WORD_ASC = SQL_SELECT_ALL + " ORDER BY " + COLUMN_NAME_WORD + " ASC";
    private final static String SQL_SELECT_ALL_WORD_DESC = SQL_SELECT_ALL + " ORDER BY " + COLUMN_NAME_WORD + " DESC";
    private final static String SQL_SELECT_FUZZILY = SQL_SELECT_ALL + " WHERE " + COLUMN_NAME_WORD + " LIKE ?";
    private final static String SQL_SELECT = SQL_SELECT_ALL + " WHERE " + COLUMN_NAME_ID + "=?";

    public WordDBImplement(Context context) {
        wordDBHelper = new WordDBHelper(context);
    }

    public void closeDB() {
        if (db != null) db.close();
    }

    public int getVersion() {
        return db.getVersion();
    }

    public void dbUpgrade(int oldVersion, int newVersion) {
        wordDBHelper.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public boolean addWord(WordDetailData data) {
        try {
            db = wordDBHelper.getWritableDatabase();
            db.execSQL(SQL_INSERT, new Object[] {
                    data.getWord(),
                    data.getTranslation(),
                    data.getPhonetic(),
                    data.getUkPhonetic(),
                    data.getUsPhonetic(),
                    data.explainsToString(),
                    data.webExplainsToString(),
                    data.getUserNote()
            });
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            this.closeDB();
        }
    }

    @Override
    public boolean delWord(int id) {
        try {
            db = wordDBHelper.getWritableDatabase();
            db.execSQL(SQL_DELETE, new Integer[] { id });
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            this.closeDB();
        }
    }

    @Override
    public boolean updateWord(WordDetailData data) {
        try {
            db = wordDBHelper.getWritableDatabase();
            db.execSQL(SQL_UPDATE, new String[] {
                    data.getTranslation(),
                    data.getPhonetic(),
                    data.getUkPhonetic(),
                    data.getUsPhonetic(),
                    data.explainsToString(),
                    data.webExplainsToString(),
                    data.getUserNote(),
                    data.getWord()
            });
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            this.closeDB();
        }
    }

    @Override
    public boolean updateUserNote(String word, String note) {
        try {
            db = wordDBHelper.getWritableDatabase();
            db.execSQL(SQL_UPDATE_USERNOTE, new String[] { note, word });
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            this.closeDB();
        }
    }

    @Override
    public WordDetailData getWord(int id) {
        try {
            db = wordDBHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(SQL_SELECT, new String[] { String.valueOf(id) });
            WordDetailData data = new WordDetailData(cursor);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            this.closeDB();
        }
    }

    @Override
    public List<Map<String, Object>> getWordList(String mode, String[] str) {
        try {
            List<Map<String, Object>> wordList = new ArrayList<Map<String, Object>>();
            db = wordDBHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(mode, str);
            while (cursor.moveToNext()) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put(COLUMN_NAME_ID, cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID)));
                item.put(COLUMN_NAME_WORD, cursor.getString(cursor.getColumnIndex(COLUMN_NAME_WORD)));
                item.put(COLUMN_NAME_TRANSLATION, cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TRANSLATION)));
                wordList.add(item);
            }
            return wordList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            this.closeDB();
        }
    }

    @Override
    public List<Map<String, Object>> getWordListByTimeOrder() {
        return this.getWordList(SQL_SELECT_ALL_ID_ASC, null);
    }

    @Override
    public List<Map<String, Object>> getWordListReserveTimeOrder() {
        return this.getWordList(SQL_SELECT_ALL_ID_DESC, null);
    }

    @Override
    public List<Map<String, Object>> getWordListByAlphabetOrder() {
        return this.getWordList(SQL_SELECT_ALL_WORD_ASC, null);
    }

    @Override
    public List<Map<String, Object>> getWordListReserveAlphabetOrder() {
        return this.getWordList(SQL_SELECT_ALL_WORD_DESC, null);
    }

    @Override
    public List<Map<String, Object>> getWordListFuzzily(String str) {
        return this.getWordList(SQL_SELECT_FUZZILY, new String[] { "%" + str + "%" });
    }

    @Override
    public List<WordDetailData> getWordDetailList() {
        try {
            List<WordDetailData> wordDetailList = new ArrayList<WordDetailData>();
            db = wordDBHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(SQL_SELECT_ALL, null);
            while (cursor.moveToNext()) {
                WordDetailData data = new WordDetailData(cursor);
                wordDetailList.add(data);
            }
            return wordDetailList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            this.closeDB();
        }
    }
}
