package com.me.test.model.data;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import java.net.HttpURLConnection;

public class DbBuilder {
    /**
     * The package you are looking at
     * including this class
     * is the process of building a Database.
     */

    public static final String TAG_NAME = "name";
    public static final String TAG_NO = "no";
    public static final String TAG_APP = "app";
    public static final String TAG_CATEGORY = "category";
    public static final String TAG_DESCRIPTION = "desc";
    public static final String TAG_VERSION = "ver";
    public static final String TAG_CODE = "code";
    public static final String TAG_FILE_SIZE = "file_size";
    public static final String TAG_DATE = "date";
    public static final String TAG_PACKAGE_NAME = "package_name";
    public static final String TAG_FILE = "file";

    private static final String TAG = "DBBuilder";
    private Context mContext;

    public static final int WEB_QUERY_PROCESS = 8000;

    public DbBuilder(Context mContext) {
        this.mContext = mContext;
    }


    public @NonNull
    List<ContentValues> fetch(String url)
            throws Exception {
        JSONArray itemData = fetchJSON(url);
        return buildByJSON(itemData);
    }

    private List<ContentValues> buildByJSON(JSONArray jsonArray) throws JSONException {

        /**
         * Since this is just a parsing method tailored to the data structure
         * within the company server that I have been involved in
         * you must take care of the custom yourself.
         */
        List<ContentValues> itemArrays = new ArrayList<>();

        for (int j = 0; j<jsonArray.length(); j++) {
            JSONArray categoryArray;
            JSONObject categoryObject = jsonArray.getJSONObject(j);
            String categoryName = categoryObject.getString(TAG_CATEGORY);
            categoryArray = categoryObject.getJSONArray(TAG_APP);
            for (int i = 0; i < categoryArray.length(); i++) {

                JSONObject jsonObject = categoryArray.getJSONObject(i);
                String no = jsonObject.optString(TAG_NO);
                String name = jsonObject.optString(TAG_NAME);
                String description = jsonObject.optString(TAG_DESCRIPTION);
                String version = jsonObject.optString(TAG_VERSION);
                String code = jsonObject.optString(TAG_CODE);
                String size = jsonObject.optString(TAG_FILE_SIZE);
                String date = jsonObject.optString(TAG_DATE);
                String packagename = jsonObject.optString(TAG_PACKAGE_NAME);
                String file = jsonObject.optString(TAG_FILE);


                ContentValues contentValues = new ContentValues();
                contentValues.put(ItemContract.ItemList.COLUMN_NO, no);
                contentValues.put(ItemContract.ItemList.COLUMN_CATEGORY, categoryName);
                contentValues.put(ItemContract.ItemList.COLUMN_NAME, name);
                contentValues.put(ItemContract.ItemList.COLUMN_DESC, description);
                contentValues.put(ItemContract.ItemList.COLUMN_VER, version);
                contentValues.put(ItemContract.ItemList.COLUMN_CODE, code);
                contentValues.put(ItemContract.ItemList.COLUMN_SIZE, size);
                contentValues.put(ItemContract.ItemList.COLUMN_PACKAGE_NAME, packagename);
                contentValues.put(ItemContract.ItemList.COLUMN_FILE, file);
                contentValues.put(ItemContract.ItemList.COLUMN_DATE, date);
                itemArrays.add(contentValues);
            }
        }
        return itemArrays;
    }


    private JSONArray fetchJSON(String urlString) throws Exception {
        BufferedReader reader = null;
        /**
         *  This part is also my custom method due to the existing server method using https.*/
        /*Utils.ignoreSSL();*/
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),
                    "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            return new JSONArray(json);
        } finally {
            urlConnection.disconnect();
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "JSON feed closed", e);
                }

            }
        }


    }
}
