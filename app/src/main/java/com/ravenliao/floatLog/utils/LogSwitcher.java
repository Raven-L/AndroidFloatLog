package com.ravenliao.floatLog.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.ravenliao.floatLog.floatType.bottomLog.BottomLogView;
import com.ravenliao.floatLog.floatType.floatLog.ControlView;
import com.ravenliao.floatLog.log.FLog;
import com.ravenliao.floatLog.viewLog.LogObservable;

public class LogSwitcher {

    private ControlView logView;
    private BottomLogView bottomLogView;

    private Context context;

    private boolean isShow = false;

    public @interface LogType {
        int FloatLog = 0;
        int BottomFloatLog = 1;
        int ViewLog = 2;
    }

    private @LogType
    int type = LogType.FloatLog;

    static class Holder {
        @SuppressLint("StaticFieldLeak")
        static LogSwitcher INSTANCE = new LogSwitcher();
    }

    public @LogType
    int getType() {
        return type;
    }

    private LogSwitcher() {
    }

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public static LogSwitcher get() {
        return Holder.INSTANCE;
    }

    public void switchType(@LogType int type) {
        this.type = type;
        if (isShow) {
            displayLog(true);
        }
    }

    public void displayLog(boolean isShow) {
        this.isShow = isShow;
        displayFloatLog(isShow && type == LogType.FloatLog);
        displayBottomLog(isShow && type == LogType.BottomFloatLog);
        displayViewLog(isShow && type == LogType.ViewLog);
    }

    public boolean isShow() {
        return isShow;
    }

    private void displayFloatLog(boolean isShow) {
        if (isShow) {
            if (logView != null) return;
            logView = new ControlView(context);
            FLog.setPrinter(logView.getLogPrinter());
            logView.show();
        } else {
            if (logView == null) return;
            logView.destroy();
            logView = null;
        }
    }

    private void displayBottomLog(boolean isShow) {
        if (isShow) {
            if (bottomLogView != null) return;
            bottomLogView = new BottomLogView(context);
            FLog.setPrinter(bottomLogView.logAdapter);
            bottomLogView.show();
        } else {
            if (bottomLogView == null) return;
            bottomLogView.destroy();
            bottomLogView = null;
        }
    }

    private void displayViewLog(boolean isShow) {
        if (isShow) {
            FLog.setPrinter(LogObservable.get());
        } else {
            LogObservable.get().clearLogs();
        }
        LogObservable.get().setDisplay(isShow);
    }
}
