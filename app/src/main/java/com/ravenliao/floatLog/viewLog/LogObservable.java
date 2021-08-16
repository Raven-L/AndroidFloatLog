package com.ravenliao.floatLog.viewLog;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.ravenliao.floatLog.log.FLog.FLogPrinter;
import com.ravenliao.floatLog.log.FLog.LogMsg;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

public class LogObservable implements FLogPrinter {
    private static final int LOG_MAX_COUNT = 100;

    private static boolean logDisplay = false;

    public static void setLogDisplay(boolean isVisible) {
        LogObservable.logDisplay = isVisible;
    }

    public static boolean isLogDisplay() {
        return logDisplay;
    }

    private final LinkedList<WeakReference<LogObserver>> observers = new LinkedList<>();

    private final LinkedList<LogMsg> logs = new LinkedList<>();

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message m) {
            LogMsg msg = (LogMsg) m.obj;
            if (logs.size() >= LOG_MAX_COUNT) {
                logs.removeLast();
            }
            logs.addFirst(msg);

            for (WeakReference<LogObserver> ref : observers) {
                LogObserver observer = ref.get();
                if (observer != null) {
                    observer.addLog(msg);
                }
            }
        }
    };

    private LogObservable() {
    }

    public static LogObservable get() {
        return Holder.instance;
    }

    @Override
    public void print(LogMsg log) {
        Message message = handler.obtainMessage();
        message.obj = log;
        handler.sendMessage(message);
    }

    public void clearLogs() {
        logs.clear();

        for (WeakReference<LogObserver> ref : observers) {
            LogObserver observer = ref.get();
            if (observer != null) {
                observer.clearLog();
            }
        }
    }

    public void setDisplay(boolean isShow) {
        for (WeakReference<LogObserver> ref : observers) {
            LogObserver observer = ref.get();
            if (observer != null) {
                observer.setDisplay(isShow);
            }
        }
    }

    public void registerObserver(LogObserver observer) {
        if (observer == null) return;
        for (WeakReference<LogObserver> ref : observers) {
            if (ref != null && ref.get() == observer) {
                return;
            }
        }
        observers.addFirst(new WeakReference<>(observer));
    }

    public void unregisterObserver(LogObserver observer) {
        if (observer == null) return;
        for (Iterator<WeakReference<LogObserver>> iterator = observers.iterator(); iterator.hasNext(); ) {
            WeakReference<LogObserver> ref = iterator.next();
            if (ref == null || ref.get() == observer) {
                iterator.remove();
                break;
            }
        }
    }

    public LinkedList<LogMsg> logs() {
        return logs;
    }

    interface LogObserver {
        void addLog(LogMsg log);

        void clearLog();

        void setDisplay(boolean isShow);
    }

    private static class Holder {
        private static final LogObservable instance = new LogObservable();
    }

}
