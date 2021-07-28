package com.ravenliao.floatLog;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ravenliao.floatLog.floatLog.BaseFloatView;
import com.ravenliao.floatLog.floatLog.BottomLogView;
import com.ravenliao.floatLog.floatLog.ControlView;
import com.ravenliao.floatLog.floatLog.FLog;
import com.ravenliao.floatLog.floatLog.FloatPermission;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_FLOAT_PERMISSION = 100;

    private Button floatViewBtn;
    private Button bottomViewBtn;

    private ControlView logView;
    private BottomLogView bottomLogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        (floatViewBtn = findViewById(R.id.btn_float_View)).setOnClickListener(this);
        (bottomViewBtn = findViewById(R.id.btn_bottom_View)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        BaseFloatView view;
        int index;
        if (v.getId() == R.id.btn_float_View) {
            view = logView;
            index = 0;
        } else if (v.getId() == R.id.btn_bottom_View) {
            view = bottomLogView;
            index = 1;
        } else {
            throw new IllegalStateException("No such View!");
        }
        if (view == null) {
            if (!FloatPermission.check(this)) {
                Toast.makeText(this, "请先允许悬浮框权限!", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    FloatPermission.apply(this, REQUEST_CODE_FLOAT_PERMISSION);
                } else {
                    FloatPermission.openAppSettingIntent(this);
                }
            } else {
                switch (index) {
                    case 0: {
                        destroyBottomLog();
                        openFloatLog();
                        break;
                    }
                    case 1: {
                        destroyFloatLog();
                        openBottomLog();
                        break;
                    }
                }
            }
        } else {
            switch (index) {
                case 0: {
                    destroyFloatLog();
                    break;
                }
                case 1: {
                    destroyBottomLog();
                    break;
                }
            }
        }
    }

    private void openFloatLog() {
        if (logView != null) return;
        logView = new ControlView(this);
        FLog.setPrinter(logView.getLogPrinter());
        floatViewBtn.setText(R.string.close_float_view);
        logView.show();
    }

    private void openBottomLog() {
        if (bottomLogView != null) return;
        bottomLogView = new BottomLogView(this);
        FLog.setPrinter(bottomLogView.getLogPrinter());
        bottomViewBtn.setText(R.string.close_bottom_view);
        bottomLogView.show();
    }

    private void destroyFloatLog() {
        if (logView == null) return;
        logView.destroy();
        logView = null;
        floatViewBtn.setText(R.string.open_float_view);
    }

    private void destroyBottomLog() {
        if (bottomLogView == null) return;
        bottomLogView.destroy();
        bottomLogView = null;
        bottomViewBtn.setText(R.string.open_bottom_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FLog.v(TAG, "onStart: 222222222222222222222222222222222222222222222222222222222222222222222222222");
    }


    @Override
    protected void onResume() {
        super.onResume();
        FLog.i(TAG, "onResume: 333333333333333333333333333333333333333333333333333333333333333333333333");
    }

    @Override
    protected void onPause() {
        super.onPause();
        FLog.d(TAG, "onPause: 444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FLog.e(TAG, "onStop: 5555555555");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FLog.w(TAG, "onRestart: 66");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logView != null) {
            logView.destroy();
        }
        if (bottomLogView != null) {
            bottomLogView.destroy();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FLOAT_PERMISSION) {
            if (!FloatPermission.check(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}