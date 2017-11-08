package com.example.yelia.experiment2_vocabularybook;

import android.net.Uri;

/**
 * Created by yelia on 2017/11/7.
 */

public class AppContentConstruct {
    // Manifest授权者
    final static String AUTHORITY = "com.example.yelia.lalala";
    final static String TABLE_NAME = WordDBConstruct.TABLE_NAME;

    // 访问该ContentProvider的Uri
    final static String CONTENT_URI_STRING = "content://" + AUTHORITY + "/" + TABLE_NAME;
    final static Uri CONTENT_URI = Uri.parse(CONTENT_URI_STRING);

    // MIME返回类型
    final static String MIME_ITEM_PREFIX = "vnd.android.cursor.item/";
    final static String MIME_DIR_PREFIX = "vnd.android.cursor.dir/";
    final static String MIME_TYPE_SINGLE = MIME_ITEM_PREFIX + TABLE_NAME;
    final static String MIME_TYPE_MULTIPLE = MIME_DIR_PREFIX + TABLE_NAME;

    // 数据路径
    final static String PATH_SINGLE = TABLE_NAME + "/*";
    final static String PATH_MULTIPLE = TABLE_NAME;
}
