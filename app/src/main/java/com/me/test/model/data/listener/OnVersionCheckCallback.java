package com.me.test.model.data.listener;

import android.content.Context;
import android.util.Pair;

public interface OnVersionCheckCallback {

    int checkVersion();

    Pair getPackageVersionInfo(Context context, String packageName);
}
