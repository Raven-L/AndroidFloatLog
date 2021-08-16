package com.ravenliao.floatLog.viewLog;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ravenliao.floatLog.R;
import com.ravenliao.floatLog.log.FLog.LogMsg;
import com.ravenliao.floatLog.log.LogAdapter;

import java.util.LinkedList;

public class LogView extends ConstraintLayout implements LogObservable.LogObserver {

    private MyAdapter logAdapter;
    private RecyclerView logsView;

    public LogView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public LogView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LogView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 设置全局Log显示
     *
     * @param isVisible true为显示
     */
    public void setLogDisplay(boolean isVisible) {
        LogObservable.setLogDisplay(isVisible);
        visibility(isVisible);
        registerObserver();
    }

    /**
     * 请在onCreate中调用
     */
    public void registerObserver() {
        if (LogObservable.isLogDisplay()) {
            LogObservable.get().registerObserver(this);
        }
    }

    /**
     * 请在onDestroy中调用
     */
    public void unregisterObserver() {
        LogObservable.get().unregisterObserver(this);
    }

    private void visibility(boolean visible) {
        Log.e("visibility: ", visible + "");
        this.setVisibility(visible ? VISIBLE : GONE);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.view_bottom_log, this);
        ((TextView) findViewById(R.id.txt_log_title)).setText(R.string.bottom_view_title);
        logsView = findViewById(R.id.recycler_bottom_logs);
        logsView.setVerticalScrollBarEnabled(true);
        logAdapter = new MyAdapter(context, LogObservable.get().logs());
        logsView.setAdapter(logAdapter);
        logsView.setLayoutManager(new LinearLayoutManager(context));
        logsView.setItemAnimator(null);//取消动画, 避免日志多时产生重叠

        findViewById(R.id.txt_log_title).setOnClickListener(v -> LogObservable.get().clearLogs());

        visibility(LogObservable.isLogDisplay());
    }

    @Override
    public void addLog(LogMsg log) {
        logAdapter.print(log);
    }

    @Override
    public void clearLog() {
        logAdapter.clearLog();
    }

    @Override
    public void setDisplay(boolean isShow) {
        visibility(isShow);
    }

    class MyAdapter extends LogAdapter {
        public MyAdapter(Context context, LinkedList<LogMsg> logs) {
            super(context, logs);
        }

        @Override
        public void print(LogMsg log) {
            super.print(log);
            logsView.scrollToPosition(0);
        }
    }
}
