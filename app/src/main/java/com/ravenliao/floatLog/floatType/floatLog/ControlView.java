package com.ravenliao.floatLog.floatType.floatLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.ravenliao.floatLog.R;
import com.ravenliao.floatLog.floatType.BaseFloatView;
import com.ravenliao.floatLog.log.FLog.FLogPrinter;

/**
 * LogView的控制类, 控制其显示和是否可触
 */
public class ControlView extends BaseFloatView {

    /**
     * 使用SwitchCompat需要传入Activity的Context, 否则会奔溃
     */
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch showLogView, logTouchableView;
    Button clearLogBtn;

    LogView logView;

    public ControlView(Context context) {
        super(context, R.layout.view_float_control, true);
        initView();
        setOnTouchListener();
        logView = new LogView(context);

    }

    @Override
    public synchronized void show() {
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        show(Gravity.START | Gravity.TOP, point.x, point.y / 3);
    }

    @Override
    public void destroy() {
        logView.hide();
        logView.destroy();
        logView = null;
        hide();
        super.destroy();
    }

    public void clearLog() {
        logView.logAdapter.clearLog();
    }

    public FLogPrinter getLogPrinter() {
        if (logView == null) throw new IllegalStateException("LogView is null");
        return logView.logAdapter;
    }

    private void initView() {
        showLogView = inflate.findViewById(R.id.sw_show_log);
        logTouchableView = inflate.findViewById(R.id.sw_log_touchable);
        clearLogBtn = inflate.findViewById(R.id.btn_clear_log);
        showLogView.setOnClickListener(v -> {
            showOrHideView(showLogView.isChecked() ? View.VISIBLE : View.GONE);
            if (showLogView.isChecked()) {
                logView.show();
                logView.setTouchable(logTouchableView.isChecked());
            } else {
                logView.hide();
            }
        });

        logTouchableView.setOnClickListener(v -> logView.setTouchable(logTouchableView.isChecked()));
        clearLogBtn.setOnClickListener(v -> clearLog());
    }

    private void showOrHideView(int visibility) {
        logTouchableView.setVisibility(visibility);
        clearLogBtn.setVisibility(visibility);
    }

    private void setOnTouchListener() {
        inflate.setOnTouchListener(new DraggableListener());
    }
}
