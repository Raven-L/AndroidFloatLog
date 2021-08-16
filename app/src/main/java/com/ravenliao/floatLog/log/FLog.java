package com.ravenliao.floatLog.log;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FLog {

    private static int printLevel = Log.VERBOSE;

    private static FLogPrinter printer;

    /**
     * 设置要打印的级别
     *
     * @param level 传入Log类中的等级
     */
    public static void setPrintLevel(int level) {
        printLevel = level;
    }

    public static void setPrinter(FLogPrinter printer) {
        FLog.printer = printer;
    }

    public static int v(String tag, String msg) {
        print(Log.VERBOSE, tag, msg);
        return Log.v(tag, msg);
    }


    public static int v(String tag, String msg, Throwable tr) {
        print(Log.VERBOSE, tag, msg);
        return Log.v(tag, msg, tr);
    }


    public static int d(String tag, String msg) {
        print(Log.DEBUG, tag, msg);
        return Log.d(tag, msg);
    }


    public static int d(String tag, String msg, Throwable tr) {
        print(Log.DEBUG, tag, msg);
        return Log.d(tag, msg, tr);
    }


    public static int i(String tag, String msg) {
        print(Log.INFO, tag, msg);
        return Log.i(tag, msg);
    }


    public static int i(String tag, String msg, Throwable tr) {
        print(Log.INFO, tag, msg);
        return Log.i(tag, msg, tr);
    }


    public static int w(String tag, String msg) {
        print(Log.WARN, tag, msg);
        return Log.w(tag, msg);
    }


    public static int w(String tag, String msg, Throwable tr) {
        print(Log.WARN, tag, msg);
        return Log.w(tag, msg, tr);
    }


    public static int e(String tag, String msg) {
        print(Log.ERROR, tag, msg);
        return Log.e(tag, msg);
    }


    public static int e(String tag, String msg, Throwable tr) {
        print(Log.ERROR, tag, msg);
        return Log.e(tag, msg, tr);
    }

    private static void print(int level, String tag, String msg) {
        if (level >= printLevel && printer != null) {
            Message message = handler.obtainMessage();
            message.obj = new LogMsg(level, tag, msg);
            handler.sendMessage(message);
        }
    }

    private final static Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            printer.print((LogMsg) msg.obj);
        }
    };

    public interface FLogPrinter {
        void print(LogMsg log);
    }

    public static class LogMsg {
        @SuppressLint("ConstantLocale")
        private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault());
        private static final Date TIME = new Date();


        private static String getTime() {
            TIME.setTime(System.currentTimeMillis());
            return format.format(TIME);
        }

        public int level;
        private final String levelString;
        public String time;
        public String tag;
        public String msg;

        public LogMsg(int level, String tag, String msg) {
            time = getTime();
            this.level = level;
            this.tag = tag;
            this.msg = msg;

            switch (level) {
                case Log.VERBOSE:
                    levelString = "V";
                    break;
                case Log.DEBUG:
                    levelString = "B";
                    break;
                case Log.INFO:
                    levelString = "I";
                    break;
                case Log.WARN:
                    levelString = "W";
                    break;
                case Log.ERROR:
                    levelString = "E";
                    break;
                default:
                    throw new IllegalArgumentException("Log level is illegal!");
            }
        }

        @NonNull
        @NotNull
        @Override
        public String toString() {
            return time +
                    ' ' +
                    levelString +
                    "/" +
                    tag +
                    ": " +
                    msg;
        }
    }
}
