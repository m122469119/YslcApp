package com.yslc.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yslc.ui.activity.IndexFragementActivity;
import com.yslc.ui.dialog.ProgressDialog;
import com.yslc.ui.dialog.UpdateDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/5/30.
 * 此类用于更新新版本
 */
public class UpdateManager {

    //保存解析的XML信息
    HashMap<String, String> mHashMap;
    //下载保存路径
    private String mSavePath;



    private Context mContext;
    private ProgressDialog progressDialog;//进度对话框

    public UpdateManager(Context context){
        this.mContext = context;
    }

    private class DownLoadApk extends AsyncTask<Void, Integer, Boolean >{
        @Override
        protected void onPreExecute() {
            showDownloadDialog();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                //判断SD卡是否存在，并且是否具有读写权限
                if(Environment.getExternalStorageState().equals
                        (Environment.MEDIA_MOUNTED)){
                    //获取存储卡路径
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download";
                    URL url = new URL(mHashMap.get("url"));
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();
                    //获取文件大小
                    int length= conn.getContentLength();
                    //创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    //判断文件目录是否存在
                    if (!file.exists()){
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, mHashMap.get("name"));
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    //缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    int numread;
                    while((numread =is.read(buf)) >0){
                        count += numread;
                        //计算进度条位置
                        int progress = ((int) (((float)count/length)*100));
                        //更新进度
                        publishProgress(progress);
                        fos.write(buf,0,numread);
                    }
                    fos.close();
                    is.close();
                    return true;
                }
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
//            mDownloadDialog.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                installApk();
                progressDialog.dismiss();
            }
        }
    }


    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(mSavePath, mHashMap.get("name"));
        if(!apkfile.exists()){
            return;
        }
        //通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive" );
        mContext.startActivity(i);

    }


    DownLoadApk task;
    /**
     * 显示软件更新对话框
     *
     */
    private void showNoticeDialog() {
        UpdateDialog dialog = new UpdateDialog();
        dialog.setUpdateListener(new UpdateDialog.UpdateListener() {
            @Override
            public void update() {
//                showDownloadDialog();
                task = new DownLoadApk();
                task.execute();
                dialog.dismiss();
            }
        });
        dialog.show(((IndexFragementActivity)mContext).getFragmentManager(), "update_dialog");

    }

    /**
     * 显示下载对话框
     *
     */
    private void showDownloadDialog() {

        progressDialog = new ProgressDialog();
        progressDialog.show(((IndexFragementActivity)mContext).getFragmentManager(), "progress_dialog");
        progressDialog.setCancelListener(new ProgressDialog.CancelListener() {
            @Override
            public void cancel() {
                task.cancel(true);
            }
        });

    }




    /**
     * 比较版本号判断是否需要更新
     * @return
     */
    public void checkUpdate() {
        HttpUtil.get(HttpUtil.CHECK_UPDATE, mContext,
                null, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String s) {
                        super.onSuccess(s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            mHashMap = new HashMap<String, String>();
                            mHashMap.put("version", jsonObject.optString("code"));
                            mHashMap.put("url", jsonObject.optString("url"));
                            mHashMap.put("name", jsonObject.optString("name"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //比较版本号
                        if (null != mHashMap) {
                            int serviceCode = Integer.valueOf(mHashMap.get("version"));
                            //版本判断
                            if (serviceCode > getVersionCode(mContext)) {
                                //下载apk更新
                                showNoticeDialog();
                            }
                        }
                    }


                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);
                        ToastUtil.showMessage(mContext, "获取大盘信息失败");
                    }
                });


    }

    /**
     * 获取本软件版本号
     * @param context
     * @return
     */
    private int getVersionCode(Context context) {
        int versionCode = 0;
        try{
            versionCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
//            PackageManager.GET_CONFIGURATIONS
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return versionCode;
    }
}
