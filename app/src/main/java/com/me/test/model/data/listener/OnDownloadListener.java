package com.me.test.model.data.listener;

import android.content.Context;

public interface OnDownloadListener {
    void show(Context context);
    void setUpdateDownloader(String url);
}
