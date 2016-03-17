package com.yslc.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DeBug日志工具
 *
 * @author HH
 */
public class LogUtil {
    public static final boolean debug = true;    //调试模式
    public static final String logFileTag = "/yslc/";
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static void logI(String msg) {
        if (debug) {
            Log.i(logFileTag, msg);
            writeToFile(msg, "---------info---------");
        }
    }

    public static void logD(String msg) {
        if (debug) {
            Log.d(logFileTag, msg);
            writeToFile(msg, "---------debug---------");
        }
    }

    public static void logE(String msg) {
        if (debug) {
            Log.e(logFileTag, msg);
            writeToFile(msg, "---------error---------");
        }
    }

    public static void logW(String msg) {
        if (debug) {
            Log.w(logFileTag, msg);
            writeToFile(msg, "---------warming---------");
        }
    }

    /**
     * 将Log写入SD
     *
     * @param msg
     */
    public static void writeToFile(String msg, String mode) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }

        try {
            File files = new File(Environment.getExternalStorageDirectory().getPath() + logFileTag);
            if (!files.exists()) {
                files.mkdir();
            }
            String file = Environment.getExternalStorageDirectory().getPath() + logFileTag + "log.txt";
            File f = new File(file);
            if (!f.exists()) {
                f.createNewFile();
            }

            msg = formatter.format(new Date()) + mode + "\n" + msg + "\n\n";
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write(msg.toCharArray());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
