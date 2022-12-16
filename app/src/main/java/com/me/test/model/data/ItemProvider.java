package com.me.test.model.data;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.me.test.model.data.ItemContract.ItemList;
import java.util.HashMap;

public class ItemProvider extends ContentProvider {
    /**
     * Utilizing the SQLiteOpenHelper {@link ItemDbHelper} Class, build a DB in a simple SQL format
     * and use a class that extends ContentProvider to bring the data
     * matched to Uri according to your taste.*/

    private static final String TAG = "Provider";
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ItemDbHelper mOpenHelper;

    private static final int ITEM = 1;
    private static final int ITEM_WITH_CATEGORY = 2;
    private static final int SEARCH_SUGGEST = 3;


    private static final HashMap<String, String> sColumnMap = buildColumnMap();
    private static final SQLiteQueryBuilder sItemsContainingQueryBuilder;
    private static final String[] sItemsContainingQueryColumns;


    private static HashMap<String, String> buildColumnMap() {
        HashMap<String, String>map = new HashMap<>();
        map.put(ItemList._ID, ItemList._ID);
        map.put(ItemList.COLUMN_NO, ItemList.COLUMN_NO);
        map.put(ItemList.COLUMN_CATEGORY, ItemList.COLUMN_CATEGORY);
        map.put(ItemList.COLUMN_NAME, ItemList.COLUMN_NAME);
        map.put(ItemList.COLUMN_DESC, ItemList.COLUMN_DESC);
        map.put(ItemList.COLUMN_VER, ItemList.COLUMN_VER);
        map.put(ItemList.COLUMN_DATE, ItemList.COLUMN_DATE);
        map.put(ItemList.COLUMN_CODE, ItemList.COLUMN_CODE);
        map.put(ItemList.COLUMN_SIZE, ItemList.COLUMN_SIZE);
        map.put(ItemList.COLUMN_PACKAGE_NAME, ItemList.COLUMN_PACKAGE_NAME);
        map.put(ItemList.COLUMN_FILE, ItemList.COLUMN_FILE);
        return map;
    }



    private ContentResolver mContentResolver;


    @Override
    public boolean onCreate() {
        Context context = getContext();
        mContentResolver = context.getContentResolver();
        mOpenHelper = new ItemDbHelper(context);
        return true;
    }
    static {
        sItemsContainingQueryBuilder = new SQLiteQueryBuilder();
        sItemsContainingQueryBuilder.setTables(ItemList.TABLE_NAME);
        sItemsContainingQueryBuilder.setProjectionMap(sColumnMap);
        sItemsContainingQueryColumns = new String[]{
                ItemList._ID,
                ItemList.COLUMN_NO,
                ItemList.COLUMN_CATEGORY,
                ItemList.COLUMN_NAME,
                ItemList.COLUMN_DESC,
                ItemList.COLUMN_VER,
                ItemList.COLUMN_DATE,
                ItemList.COLUMN_CODE,
                ItemList.COLUMN_SIZE,
                ItemList.COLUMN_FILE,
                ItemList.COLUMN_PACKAGE_NAME,

        };
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case SEARCH_SUGGEST: {
                String rawQuery = "";
                if (selectionArgs != null && selectionArgs.length > 0) {
                    rawQuery = selectionArgs[0];
                }
                retCursor = getSuggestions(rawQuery);
                break;
            }
            case ITEM: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ItemList.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        retCursor.setNotificationUri(mContentResolver, uri);
        return retCursor;
    }

    private Cursor getSuggestions(String rawQuery) {
        rawQuery = rawQuery.toLowerCase();
        return sItemsContainingQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                sItemsContainingQueryColumns,
                ItemList.COLUMN_NAME+ " LIKE ? OR " +
                        ItemList.COLUMN_DESC + "LIKE ? ",
                new String[]{"%" + rawQuery + "%", "%" + rawQuery + "%"},
                null,
                null,
                null
        );
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        if (sUriMatcher.match(uri) == ITEM) {
            return ItemList.COLUMN_CONTENT_TYPE;
        }
        throw new UnsupportedOperationException("Unknown Uri" + uri);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final Uri returnUri;
        final int match = sUriMatcher.match(uri);

        if (match == ITEM) {
            long _id = mOpenHelper.getWritableDatabase().insert(
                    ItemList.TABLE_NAME, null, values);
            if (_id > 0) {
                returnUri = ItemList.buildItemUri(_id);
            } else {
                throw new SQLException("Failed to insert row into " + uri);
            }
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        mContentResolver.notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int rowsDeleted;

        if (selection == null) {
            throw new UnsupportedOperationException("Cannot delete without selection specified.");
        }
        if (sUriMatcher.match(uri) == ITEM) {
            rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                    ItemList.TABLE_NAME, selection, selectionArgs);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            mContentResolver.notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int rowsUpdated;

        if (sUriMatcher.match(uri) == ITEM) {
            rowsUpdated = mOpenHelper.getWritableDatabase().update(
                    ItemList.TABLE_NAME, values, selection, selectionArgs);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            mContentResolver.notifyChange(uri, null);
        }

        return rowsUpdated;
    }
    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ItemContract.AUTHORITY;
        matcher.addURI(authority, ItemContract.PATH_ITEM, ITEM);
        matcher.addURI(authority, ItemContract.PATH_ITEM + "/*", ITEM_WITH_CATEGORY);
        matcher.addURI(authority, "search/" + SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(authority, "search/" + SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
        return matcher;
    }
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        if (sUriMatcher.match(uri) == ITEM) {
            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            int returnCount = 0;

            db.beginTransaction();
            try {
                for (ContentValues value : values) {
                    long _id = db.insertWithOnConflict(ItemList.TABLE_NAME,
                            null, value, SQLiteDatabase.CONFLICT_REPLACE);
                    if (_id != -1) {
                        returnCount++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            mContentResolver.notifyChange(uri, null);
            return returnCount;
        }
        return super.bulkInsert(uri, values);
    }
}
