package com.yslc.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 *
 * @author HH
 */
public class ToastUtil {

    private static String oldMsg;
    private static Toast toast;
    private static long oldTime = 0;

    public static void showMessage(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), msg,
                    Toast.LENGTH_SHORT);
            toast.show();
            oldTime = System.currentTimeMillis();
        } else {
            long currentTime = System.currentTimeMillis();
            if (msg.equals(oldMsg)) {
                if (currentTime - oldTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = msg;
                toast.setText(msg);
                toast.show();
            }
            oldTime = currentTime;
        }
    }

    public static void showMessage(Context context, int id) {
        showMessage(context, context.getString(id));
    }

}
