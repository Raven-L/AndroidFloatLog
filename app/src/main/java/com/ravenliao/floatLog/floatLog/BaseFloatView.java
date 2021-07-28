package com.ravenliao.floatLog.floatLog;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static android.view.WindowManager.LayoutParams.TYPE_TOAST;

public abstract class BaseFloatView {
    Context context;
    WindowManager windowManager;
    WindowManager.LayoutParams layoutParams;

    View inflate;

    private boolean hasCreated = false;

    public BaseFloatView(Context context, @LayoutRes int layoutID, boolean isTouchable) {
        this.context = context;
        windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        initLayoutParams();
        setTouchable(isTouchable);
        initInflate(layoutID);
    }

    public void setTouchable(boolean isTouchable) {
        if (isTouchable) {
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;//消flag
        } else {
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        }
        if (inflate != null) {
            windowManager.updateViewLayout(inflate, layoutParams);
        }
    }

    public synchronized void show() {
        show(layoutParams.x, layoutParams.y);
    }

    public synchronized void show(int x, int y) {
        show(layoutParams.gravity, x, y);
    }

    /**
     * @param gravity Gravity类中的Flag
     */
    public synchronized void show(int gravity, int x, int y) {
        if (inflate == null) {
            throw new IllegalStateException("Inflate can't be null");
        }

        inflate.setVisibility(View.VISIBLE);

        try {
            layoutParams.gravity = gravity;
            layoutParams.x = x;
            layoutParams.y = y;
            if (hasCreated) {
                windowManager.updateViewLayout(inflate, layoutParams);
            } else {
                windowManager.addView(inflate, layoutParams);
                hasCreated = true;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void hide() {
        if (inflate == null) {
            throw new IllegalStateException("Inflate can't be null");
        }
        inflate.setVisibility(View.GONE);
    }

    public abstract void destroy();

    private void initLayoutParams() {
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = TYPE_APPLICATION_OVERLAY;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                //刘海屏延伸到刘海里面
                layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            layoutParams.type = TYPE_TOAST;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        layoutParams.packageName = context.getPackageName();

        //非全屏
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        layoutParams.flags |= WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.format = PixelFormat.TRANSPARENT;

    }

    private void initInflate(@LayoutRes int layoutID) {
        inflate = View.inflate(context, layoutID, null);
    }


    /**
     * 实现了可拖动的onTouchListener.
     */
    class DraggableListener implements View.OnTouchListener {
        private float lastX;
        private float lastY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float x = event.getRawX();
            float y = event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = x;
                    lastY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveX = x - lastX;
                    float moveY = y - lastY;
                    layoutParams.x += moveX;
                    layoutParams.y += moveY;

                    windowManager.updateViewLayout(inflate, layoutParams);
                    lastX = x;
                    lastY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    v.performClick();
                    break;
            }
            return true;
        }


    }
}
