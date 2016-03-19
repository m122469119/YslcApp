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

    /**
     * 使用Toast显示信息
     * <p>改善了toast,重用了一个toast，并且重复信息的toast,不可在短时间连续发送</p>
     * @param context 上下文
     * @param msg 信息
     */
    public static void showMessage(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), msg,
                    Toast.LENGTH_SHORT);
            toast.show();
            oldTime = System.currentTimeMillis();
        } else {
            long currentTime = System.currentTimeMillis();
            if (msg.equals(oldMsg)) {//如果跟上次显示的信息相同，则需要有时间间隔
                if (currentTime - oldTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {//否则显示
                oldMsg = msg;
                toast.setText(msg);
                toast.show();
            }
            oldTime = currentTime;
        }
    }

    /**
     * 使用Toast显示信息
     * @param context 上下文
     * @param id 资源文件String字符的id
     * @see #showMessage(Context, String)
     */
    public static void showMessage(Context context, int id) {
        showMessage(context, context.getString(id));
    }

}
