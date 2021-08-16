package com.ravenliao.floatLog;

import android.app.Application;

import com.ravenliao.floatLog.utils.ForeBackGroundUtil;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ForeBackGroundUtil.get().init(this);
    }
}
