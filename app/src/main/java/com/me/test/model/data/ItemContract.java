package com.me.test.model.data;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class ItemContract {
    public static final String AUTHORITY = "com.me.test";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_ITEM = "app";

    public static final class ItemList implements BaseColumns {
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "." + PATH_ITEM;
        public static final Uri PROVIDE_URI = BASE_URI.buildUpon().appendPath(PATH_ITEM).build();

        public static final String TABLE_NAME = "apk";
        public static final String COLUMN_NO = "no";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
        public static final String COLUMN_DESC = SearchManager.SUGGEST_COLUMN_TEXT_2;
        public static final String COLUMN_VER = "ver";
        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_PACKAGE_NAME = "packagename";
        public static final String COLUMN_FILE = "file";
        public static final String COLUMN_CONTENT_TYPE = SearchManager.SUGGEST_COLUMN_CONTENT_TYPE;


        public static Uri buildItemUri(long id) {
            return ContentUris.withAppendedId(PROVIDE_URI, id);
        }
    }
}
