package com.me.test.view.background;

import android.net.Uri;

public class UpdateBackgroundTask implements Runnable{
    Uri mBackgroundURI;

    public UpdateBackgroundTask(Uri uri) {
        this.mBackgroundURI = uri;
    }

    @Override
    public void run() {
        if (mBackgroundURI == null){
            /**
             Todo
             */
        }
    }
}
