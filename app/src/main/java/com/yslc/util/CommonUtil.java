package com.yslc.util;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 各种公共工具类
 *
 * @author HH
 */
public class CommonUtil {
    /**
     * 根据手机分辨率从DP转成PX
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率PX(像素)转成DP
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 隐藏软键盘
     *
     * @param activity
     */
    public static void hiddenSoftInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus()) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getWindowToken(), 0);
        }
    }

    /**
     * 判断网络连接情况
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvalible(Context context) {
        // 获得网络状态管理器
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 建立网络数组
            NetworkInfo[] net_info = connectivityManager.getAllNetworkInfo();

            if (net_info != null) {
                for (NetworkInfo ni : net_info) {
                    // 判断获得的网络状态是否是处于连接状态
                    if (ni.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }

            }
        }

        return false;
    }

    /**
     * 是否WIFI环境
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判别手机是否为正确手机号码；
     *
     * @param mobile
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkMobile(String mobile) {
        return mobile.matches("[1][358]\\d{9}");
    }

    /**
     * 验证密码格式是否正确
     * 验证两次密码输入是否一致
     */
    public static boolean checkPasswordFromat(Context context, EditText pass, EditText passTwo) {
        if (inputFilter(pass).length() < 8) {
            ToastUtil.showMessage(context, "请输入8-15位有效的密码");
            return false;
        }

        if (!inputFilter(pass).equals(inputFilter(passTwo))) {
            ToastUtil.showMessage(context, "密码输入不一致");
            return false;
        }

        return true;
    }

    /**
     * 输入框字符过滤
     */
    public static String inputFilter(EditText tv) {
        return tv.getText().toString().trim();
    }

    /**
     * 输入框是否为空
     *
     * @EditText tv 可判断多个输入框
     */
    public static boolean isInputEmpty(EditText... tv) {
        for (EditText t : tv) {
            if (inputFilter(t).length() == 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断当前系统版本号
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getPhoneAndroidSDK() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return version;

    }

    /**
     * 获取应用程序当前的版本号
     */
    public static int getVersionCode(Context context) {
        int versionCode;
        try {
            versionCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
            versionCode = 0;
        }
        return versionCode;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusHeight(Context context) {
        // 获取状态栏高度
        Rect frame = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusHeightForFragment(Context context) {
        //获取手机状态栏高度
        Class<?> c;
        Object obj;
        Field field;
        int x, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

}
