package com.ravenliao.floatLog.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ravenliao.floatLog.log.FLog;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;


public class ForeBackGroundUtil implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "ForeBackGroundUtil";
    private boolean inited = false;


    private int foregroundCount = 0;
    private volatile boolean isForeground;
    private int activityLevel = 0;

    private final CopyOnWriteArrayList<WeakReference<Listener>> listeners = new CopyOnWriteArrayList<>();


    public boolean isForeground() {
        return isForeground;
    }

    public int getActivityLevel() {
        return activityLevel;
    }

    private ForeBackGroundUtil() {
    }

    private static class Holder {
        final static ForeBackGroundUtil INSTANCE = new ForeBackGroundUtil();
    }

    public static ForeBackGroundUtil get() {
        return Holder.INSTANCE;
    }

    public void init(Context context) {
        if (context != null) {
            context = context.getApplicationContext();
            if (context instanceof Application) {
                init((Application) context);
            }
        }
        throw new IllegalArgumentException("Can't get Application");
    }

    public void init(Application application) {
        if (inited) return;
        application.registerActivityLifecycleCallbacks(this);
        inited = true;
    }

    interface Listener {
        void onForeground();

        void onBackground();
    }

    public void addListener(Listener listener) {
        for (Iterator<WeakReference<Listener>> iterator = listeners.iterator(); iterator.hasNext(); ) {
            WeakReference<Listener> ref = iterator.next();
            Listener temp;
            if (ref == null || (temp = ref.get()) == listener) {
                return;
            } else if (temp == null) {
                iterator.remove();
            }
        }
        listeners.add(new WeakReference<>(listener));
    }

    public void removeListener(Listener listener) {
        if (listener == null) return;
        for (Iterator<WeakReference<Listener>> iterator = listeners.iterator(); iterator.hasNext(); ) {
            WeakReference<Listener> ref = iterator.next();
            if (ref == null || ref.get() == listener) {
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        activityLevel++;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (foregroundCount++ == 0) {
            FLog.e(TAG, "App进入前台");
            isForeground = true;
            for (WeakReference<Listener> ref : listeners) {
                if (ref == null) continue;
                Listener listener = ref.get();
                if (listener == null) continue;
                listener.onForeground();
            }
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if (--foregroundCount == 0) {
            FLog.e(TAG, "App进入后台");
            isForeground = false;
            for (WeakReference<Listener> ref : listeners) {
                if (ref == null) continue;
                Listener listener = ref.get();
                if (listener == null) continue;
                listener.onBackground();
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        activityLevel--;
    }
}
