package com.me.test.view;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.me.test.R;

public class DetailsActivity extends FragmentActivity {
    private static final String TAG = "DetailsActivity";
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String ITEM = "app";
    public static final String NOTIFICATION_ID = "NotificationId";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
    }
}
