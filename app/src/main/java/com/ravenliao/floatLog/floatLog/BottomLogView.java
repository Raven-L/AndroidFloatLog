package com.ravenliao.floatLog.floatLog;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ravenliao.floatLog.R;

public class BottomLogView extends BaseFloatView {

    private RecyclerView logsView;
    public MyAdapter logAdapter;

    private boolean logShow = true;

    private int lastHeight;

    public BottomLogView(Context context) {
        super(context, R.layout.view_bottom_log, true);
        initListener();
        initRecyclerView();
        initSize();
    }

    @Override
    public synchronized void show() {
        show(Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void destroy() {
        logAdapter.clearLog();
        logAdapter = null;
        hide();
        super.destroy();
    }

    public FLog.Printer getLogPrinter() {
        if (logAdapter == null) throw new IllegalStateException("LogAdapter is null");
        return logAdapter;
    }

    private void initListener() {
        final GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            float totalY;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }


            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                totalY += distanceY;
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (logShow) {//收起时不能改变
                    layoutParams.height += totalY;
                    windowManager.updateViewLayout(inflate, layoutParams);
                }
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                super.onShowPress(e);
                totalY = 0;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                logAdapter.clearLog();
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                logShow = !logShow;
                displayLog(logShow);
                return true;
            }
        };
        GestureDetector gestureDetector = new GestureDetector(context, mOnGestureListener);
        inflate.findViewById(R.id.txt_log_title).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            return gestureDetector.onTouchEvent(event);
        });
    }

    private void initRecyclerView() {
        logsView = inflate.findViewById(R.id.recycler_bottom_logs);
        logsView.setVerticalScrollBarEnabled(true);
        logAdapter = new MyAdapter(context);
        logsView.setAdapter(logAdapter);
        logsView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void initSize() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        layoutParams.width = outMetrics.widthPixels;
        lastHeight = outMetrics.heightPixels / 4;
        layoutParams.height = lastHeight;

    }

    private void displayLog(boolean isShow) {
        logsView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        changeHeight(!isShow);
    }

    private void changeHeight(boolean min) {
        if (min) {
            lastHeight = layoutParams.height;
            layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT;
        } else {
            layoutParams.height = lastHeight;
        }
        windowManager.updateViewLayout(inflate, layoutParams);
    }

    class MyAdapter extends LogAdapter {
        public MyAdapter(Context context) {
            super(context);
        }

        @Override
        public void printLog(FLog.LogMsg log) {
            super.printLog(log);
            logsView.scrollToPosition(0);
        }
    }
}
