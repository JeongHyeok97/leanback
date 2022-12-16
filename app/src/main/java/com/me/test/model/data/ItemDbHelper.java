package com.me.test.model.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.me.test.model.data.ItemContract.ItemList;



public class ItemDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "market.db";
    private static final String SQL_DATABASE = "CREATE TABLE " + ItemList.TABLE_NAME + "( " +
            ItemList._ID + " INTEGER PRIMARY KEY, "+
            ItemList.COLUMN_CATEGORY + " TEXT NOT NULL," +
            ItemList.COLUMN_NO + " TEXT NOT NULL, " +
            ItemList.COLUMN_NAME + " TEXT NOT NULL, " +
            ItemList.COLUMN_DESC + " TEXT NOT NULL, " +
            ItemList.COLUMN_VER + " TEXT NOT NULL, " +
            ItemList.COLUMN_CODE + " INTEGER NOT NULL, " +
            ItemList.COLUMN_SIZE + " TEXT NOT NULL, " +
            ItemList.COLUMN_DATE + " TIMESTAMP NOT NULL DEFAULT current_timestamp, " +
            ItemList.COLUMN_FILE + " MEDIUMTEXT NOT NULL, " +
            ItemList.COLUMN_PACKAGE_NAME + " INTEGER NOT NULL " +
            " );";



    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ItemList.TABLE_NAME);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
