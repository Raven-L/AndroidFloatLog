package com.ravenliao.floatLog;


import androidx.appcompat.app.AppCompatActivity;

import com.ravenliao.floatLog.viewLog.LogView;

/**
 * 封装Log功能
 */
public abstract class BaseActivity extends AppCompatActivity {

    private LogView logView;

    protected void setLogDisplay(boolean isVisible) {
        logView.setLogDisplay(isVisible);
    }

    /**
     * 请在onCreate中调用setContentView()后调用
     */
    protected void initLog() {
        logView = (LogView) findViewById(R.id.logView);
        logView.registerObserver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logView.unregisterObserver();
    }

}
