package com.me.test.model;

import android.database.Cursor;

import androidx.leanback.database.CursorMapper;
import com.me.test.model.data.ItemContract.ItemList;
public class ItemCursorMapper extends CursorMapper {
    private static int idIndex;
    private static int noIndex;
    private static int categoryIndex;
    private static int nameIndex;
    private static int descIndex;
    private static int verIndex;
    private static int codeIndex;
    private static int sizeIndex;
    private static int dateIndex;
    private static int packagenameIndex;
    private static int fileIndex;


    @Override
    protected void bindColumns(Cursor cursor) {
        idIndex = cursor.getColumnIndex(ItemList._ID);
        noIndex = cursor.getColumnIndex(ItemList.COLUMN_NO);
        categoryIndex = cursor.getColumnIndex(ItemList.COLUMN_CATEGORY);
        nameIndex = cursor.getColumnIndex(ItemList.COLUMN_NAME);
        descIndex = cursor.getColumnIndex(ItemList.COLUMN_DESC);
        verIndex = cursor.getColumnIndex(ItemList.COLUMN_VER);
        codeIndex = cursor.getColumnIndex(ItemList.COLUMN_CODE);
        sizeIndex = cursor.getColumnIndex(ItemList.COLUMN_SIZE);
        dateIndex = cursor.getColumnIndex(ItemList.COLUMN_DATE);
        packagenameIndex = cursor.getColumnIndex(ItemList.COLUMN_PACKAGE_NAME);
        fileIndex = cursor.getColumnIndex(ItemList.COLUMN_FILE);
    }

    @Override
    protected Object bind(Cursor cursor) {
        long id = cursor.getLong(idIndex);
        int no = cursor.getInt(noIndex);
        String category = cursor.getString(categoryIndex);
        String ver = cursor.getString(verIndex);
        String name = cursor.getString(nameIndex);
        String desc = cursor.getString(descIndex);
        String packagename = cursor.getString(packagenameIndex);
        int size = cursor.getInt(sizeIndex);
        int date = cursor.getInt(dateIndex);
        String file = cursor.getString(fileIndex);
        int code = cursor.getInt(codeIndex);


        return new Item.ItemBuilder()
                .id(id)
                .category(category)
                .code(code)
                .date(date)
                .desc(desc)
                .file(file)
                .name(name)
                .no(no)
                .size(size)
                .packagename(packagename)
                .ver(ver)
                .build();
    }
}
