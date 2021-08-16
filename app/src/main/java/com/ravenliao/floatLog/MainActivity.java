package com.ravenliao.floatLog;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ravenliao.floatLog.floatType.FloatPermission;
import com.ravenliao.floatLog.log.FLog;
import com.ravenliao.floatLog.utils.ForeBackGroundUtil;
import com.ravenliao.floatLog.utils.LogSwitcher;

import java.text.MessageFormat;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "MainActivity" + ForeBackGroundUtil.get().getActivityLevel();
    private static final int REQUEST_CODE_FLOAT_PERMISSION = 100;

    private @LogSwitcher.LogType
    int logType = LogSwitcher.LogType.FloatLog;
    private boolean isShow = false;

    private TextView txt;
    private Button setLogBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogSwitcher.get().setContext(this);

        initView();

    }



    private void initView() {
        findViewById(R.id.btn_log).setOnClickListener(this);
        findViewById(R.id.btn_print1).setOnClickListener(this);
        findViewById(R.id.btn_print2).setOnClickListener(this);
        Button openActivity = findViewById(R.id.btn_open_activity);
        openActivity.setOnClickListener(this);
        openActivity.setText(MessageFormat.format("{0}{1}", getString(R.string.open_activity), ForeBackGroundUtil.get().getActivityLevel()));

        txt = findViewById(R.id.txt_illustrate);
        setLogBtn = findViewById(R.id.btn_log);
        setButtonName(LogSwitcher.get().isShow());
    }

    private void initRadioGroup() {
        ((RadioButton) findViewById(R.id.rb_float_log)).setChecked(LogSwitcher.get().getType() == LogSwitcher.LogType.FloatLog);
        ((RadioButton) findViewById(R.id.rb_bottom_log)).setChecked(LogSwitcher.get().getType() == LogSwitcher.LogType.BottomFloatLog);
        ((RadioButton) findViewById(R.id.rb_view_log)).setChecked(LogSwitcher.get().getType() == LogSwitcher.LogType.ViewLog);

        ((RadioGroup) findViewById(R.id.radioGroup)).setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_float_log) {
                logType = LogSwitcher.LogType.FloatLog;
                txt.setText(R.string.log_float_illustrate);
                if (FloatPermission.isNoPermission(this)) {
                    isShow = false;
                    setButtonName(false);

                }
            }
            if (checkedId == R.id.rb_bottom_log) {
                logType = LogSwitcher.LogType.BottomFloatLog;
                txt.setText(R.string.log_bottom_illustrate);
                if (FloatPermission.isNoPermission(this)) {
                    isShow = false;
                    setButtonName(false);
                }
            } else if (checkedId == R.id.rb_view_log) {
                logType = LogSwitcher.LogType.ViewLog;
                txt.setText(R.string.log_view_illustrate);
                setLogDisplay(LogSwitcher.get().isShow());
            }
            LogSwitcher.get().switchType(logType);
        });
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_print1) {
            FLog.i(TAG, "你按下了按钮1");
        } else if (v.getId() == R.id.btn_print2) {
            FLog.e(TAG, "你按下了按钮2");
        } else if (v.getId() == R.id.btn_open_activity) {
            Intent start = new Intent(MainActivity.this, MainActivity.class);
            startActivity(start);
        } else if (v.getId() == R.id.btn_log) {
            isShow = !isShow;
            if (logType != LogSwitcher.LogType.ViewLog) {
                if (applyPermission()) {
                    isShow = false;
                }
            } else {
                setLogDisplay(isShow);
            }
            LogSwitcher.get().displayLog(isShow);
            setButtonName(isShow);
        } else {
            throw new IllegalStateException("No such View!");
        }
    }

    private void setButtonName(boolean isOpen) {
        setLogBtn.setText(isOpen ? R.string.close_log : R.string.open_log);
    }

    /**
     * 检查若没有权限并申请权限
     *
     * @return 去申请则返回真
     */
    private boolean applyPermission() {
        if (isShow && FloatPermission.isNoPermission(this)) {
            Toast.makeText(this, "请先允许悬浮框权限!", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                FloatPermission.apply(this, REQUEST_CODE_FLOAT_PERMISSION);
            } else {
                FloatPermission.openAppSettingIntent(this);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FLog.v(TAG, "onStart: 222222222222222222222222222222222222222222222222222222222222222222222222222");
    }


    @Override
    protected void onResume() {
        super.onResume();
        initRadioGroup();
        initLog();
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
        LogSwitcher.get().displayLog(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FLOAT_PERMISSION) {
            if (FloatPermission.isNoPermission(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                isShow = false;
            } else {
                Toast.makeText(this, "授权成功, 请重新打开", Toast.LENGTH_SHORT).show();
            }
        }
    }
}