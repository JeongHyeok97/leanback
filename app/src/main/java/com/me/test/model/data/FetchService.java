package com.me.test.model.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.me.test.R;

import org.json.JSONException;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;

public class FetchService extends IntentService {

    private static final String TAG = "FetchService";

    public FetchService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        DbBuilder builder = new DbBuilder(getApplicationContext());
        try {
            List<ContentValues> contentValuesList =
                    builder.fetch(getResources().getString(R.string.base_server) + "/get.php");
            ContentValues[] downloadedContentValues =
                    contentValuesList.toArray(new ContentValues[contentValuesList.size()]);
            getApplicationContext().getContentResolver().bulkInsert(ItemContract.ItemList.PROVIDE_URI,
                    downloadedContentValues);
        } catch (IOException | JSONException | CertificateException e) {
            Log.e(TAG, "Error occurred");
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
