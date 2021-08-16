package com.ravenliao.floatLog.floatType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

public class FloatPermission {
    /**
     * @return 无权限返回true.
     */
    public static boolean isNoPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !Settings.canDrawOverlays(context);
        }
        return false;
    }

    private static Uri getPackageName(Context context) {
        return Uri.fromParts("package", context.getPackageName(), null);
    }

    /**
     * 打开默认的app设置, 让用户到权限栏中把悬浮框权限打开.
     */
    public static void openAppSettingIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(getPackageName(context));
        context.startActivity(intent);
    }

    /**
     * 申请悬浮框权限, 在Activity的onActivityResult中接收结果.
     *
     * @param activity    context
     * @param requestCode 即onActivityResult的requestCode
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void apply(Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, getPackageName(activity)), requestCode);
    }

}
