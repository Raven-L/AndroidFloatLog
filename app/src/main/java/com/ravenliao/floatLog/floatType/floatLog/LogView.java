package com.ravenliao.floatLog.floatType.floatLog;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ravenliao.floatLog.R;
import com.ravenliao.floatLog.floatType.BaseFloatView;
import com.ravenliao.floatLog.log.FLog.LogMsg;
import com.ravenliao.floatLog.log.LogAdapter;

public class LogView extends BaseFloatView {
    public MyAdapter logAdapter;
    private RecyclerView logsView;

    private boolean shouldShow = false;

    public LogView(Context context) {
        super(context, R.layout.view_float_log, false);
        setOnTouchListener();
        initRecyclerView();
    }

    @Override
    public void destroy() {
        logAdapter.clearLog();
        logAdapter = null;
        super.destroy();
    }

    @Override
    public synchronized void show() {
        super.show();
        shouldShow = true;
        if (logAdapter.getItemCount() == 0) {
            //无log先隐藏
            tempHide();
        }
    }

    @Override
    public synchronized void hide() {
        shouldShow = false;
        super.hide();
    }

    private void tempHide() {
        inflate.setVisibility(View.GONE);
    }

    private void setOnTouchListener() {
        inflate.setOnTouchListener(new DraggableListener());
    }

    private void initRecyclerView() {
        logsView = inflate.findViewById(R.id.recycler_logs);
        logsView.setVerticalScrollBarEnabled(true);
        logsView.setItemAnimator(null);//取消动画, 避免日志多时产生重叠
        logAdapter = new MyAdapter(context);
        logsView.setAdapter(logAdapter);
        logsView.setLayoutManager(new LinearLayoutManager(context));
    }

    class MyAdapter extends LogAdapter {
        public MyAdapter(Context context) {
            super(context);
        }

        @Override
        public void clearLog() {
            super.clearLog();
            tempHide();
        }

        @Override
        public void print(LogMsg log) {
            super.print(log);
            if (shouldShow) {
                show();
            }
            logsView.scrollToPosition(0);
        }
    }
}
