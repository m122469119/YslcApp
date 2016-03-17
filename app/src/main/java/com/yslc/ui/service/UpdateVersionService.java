package com.yslc.ui.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yslc.R;
import com.yslc.app.Constant;
import com.yslc.util.FileUtil;
import com.yslc.util.HttpUtil;

/**
 * 实现应用程序版本检测下载更新
 * ● 检测版本是否有更新
 * ● 开启线程下载APK
 * ● 开启Notification检测下载过程
 * ● 进行安装
 *
 * @author HH
 */
public class UpdateVersionService extends Service {
    private static final int NOTIFICATION_ID = 1000;
    private int fileSize = 100;
    private NotificationManager nm;
    private Notification n;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!FileUtil.sdCardIsAvailable()) {
            // SD卡不可用，结束Service
            stopSelf();
        }

        // 获取最新版本号
        getVersion();
    }

    /**
     * 获取最新版本号
     *
     * @return
     */
    private void getVersion() {
        Log.i("", "getVersion.................");
        HttpUtil.post("www.baidu.com", this, null,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);

                        notifacationDownloadFailure("下载失败，请检查网络设置");
                    }

                    @Override
                    public void onSuccess(JSONObject json) {
                        super.onSuccess(json);

                        Log.i("", "onSuccess.................");
                        if (checkVersion(json.optInt("version", 0))) {
                            // 有更新，提示用户是否下载Dialog
                            showDialog();
                        } else {
                            // 无更新，关闭服务
                            UpdateVersionService.this.stopSelf();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();

                    }

                });
    }

    /**
     * 检测版本是否更新
     *
     * @param newVersion
     * @return
     */
    private boolean checkVersion(int newVersion) {
        PackageInfo pi = null;
        try {
            pi = getPackageManager().getPackageInfo(Constant.PACKGER_NAME, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return pi != null && pi.versionCode == newVersion;
    }

    /**
     * 弹出是否更新下载APK提示框
     */
    private void showDialog() {
        Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("温馨提示");
        String sb = "有更新，是否下载\n\n当前网络环境为：2G网络";
        dialog.setMessage(sb);
        dialog.setNegativeButton("是", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 显示通知栏下载
                showNotification();

                dialog.cancel();
            }
        });
        dialog.setPositiveButton("否", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog aDialog = dialog.create();
        aDialog.getWindow().setType(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        aDialog.show();
    }

    /**
     * 开启通知栏，进行下载进度显示
     */
    private void showNotification() {
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        RemoteViews view = new RemoteViews(Constant.PACKGER_NAME,
                R.layout.notification_download_apk);
        view.setTextViewText(R.id.notificationInfo, "正在下载");
        view.setProgressBar(R.id.numberbar, 100, 0, false);
        n = new Notification();
        n.flags = Notification.FLAG_ONGOING_EVENT;
        n.contentView = view;
        n.icon = R.drawable.ic_launcher;
        nm.notify(NOTIFICATION_ID, n);

        this.stopSelf();
    }

    /**
     * 下载失败
     */
    private void notifacationDownloadFailure(String info) {
        n.contentView.setTextViewText(R.id.notificationInfo, info);
        n.contentView.setProgressBar(R.id.numberbar, fileSize, 0, false);
        nm.notify(NOTIFICATION_ID, n);
    }

    /**
     * 设置通知栏内的内容
     *
     * @param progress 当前进度
     * @param info     显示信息
     */
    private void setNotificationValue(int progress, String info) {
        n.contentView.setTextViewText(R.id.notificationInfo, info);
        n.contentView.setProgressBar(R.id.numberbar, fileSize, progress, false);
        nm.notify(NOTIFICATION_ID, n);
    }

    /**
     * 开启线程进行APK下载
     *
     * @param apkUrl APK网络路径
     */
    private void downloadApk(String apkUrl) {
        new DownloadFileAsync().execute(apkUrl);
    }

    private class DownloadFileAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... aurl) {

            try {
                // 连接地址
                URL u = new URL(aurl[0]);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                // 计算文件长度
                fileSize = c.getContentLength();

                if (fileSize > FileUtil.sdRemainSize()) {
                    notifacationDownloadFailure("下载失败，磁盘剩余空间不足");
                    return null;
                }

                FileOutputStream f = new FileOutputStream(new File("文件下载的地方"));
                InputStream in = c.getInputStream();

                // 下载的代码
                byte[] buffer = new byte[1024];
                int len1;
                long total = 0;

                while ((len1 = in.read(buffer)) > 0) {
                    total += len1;
                    publishProgress("" + (int) ((total * 100) / fileSize));
                    f.write(buffer, 0, len1);
                }
                f.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            setNotificationValue(Integer.parseInt(progress[0]), "正在下载");
        }

        @Override
        protected void onPostExecute(String unused) {
            // 下载结束
            installApk("");
        }
    }

    /**
     * 下载完成，进行APK安装
     *
     * @param apkPath APK文件路径
     */
    public void installApk(String apkPath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkPath),
                "application/vnd.android.package-archive");
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        n.defaults = Notification.DEFAULT_SOUND; // 铃声提醒
        n.contentIntent = pi;
        nm.notify(NOTIFICATION_ID, n);

        // 结束服务
        this.stopSelf();
    }
}
