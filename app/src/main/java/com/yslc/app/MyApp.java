package com.yslc.app;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.sharesdk.framework.ShareSDK;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.yslc.R;
import com.yslc.util.HttpUtil;
import com.yslc.util.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 全局配置类
 * 极光推送
 * @author HH
 */
public class MyApp extends Application {
    private SharedPreferencesUtil pre;

    @Override
    public void onCreate() {
        super.onCreate();
        pre = new SharedPreferencesUtil(this, Constant.SPF_USER_INFO_NAME);
        // 初始化分享组件
        ShareSDK.initSDK(this);

        // 初始化图片加载对象
        initImageLoader(getApplicationContext());

        // 初始化全局异常扑捉
        //CrashExceptionHandler.getInstance().init(getApplicationContext());

        // 启动推送服务
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        // 把注册id传给后台
//        String registrationId = JPushInterface.getRegistrationID(this);

        //确保AsyncTask方法在UI线程中初始化，防止onPostExecute不能回调
        try {
            Class.forName("android.os.AsyncTask");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JpushSetting();

    }

    /**
     * 极光推送设置标签
     */
    public void JpushSetting(){
        // ---------极光推送,判断有没有登录---------
        RequestParams params = new RequestParams();
        if(SharedPreferencesUtil.isLogin(this)){//判断是否登录
            //如果登录了就把用户名传给后台
            String userName = pre.getString(Constant.SPF_USER_PHONE_KEY);
            params.put("account", userName);
        }else{//没有登录，删除极光推送别名
            JPushInterface.setAlias(this,"", null);
        }
        HttpUtil.post(HttpUtil.POST_JPUSH_TAG ,this,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(JSONObject jsonObject) {
                super.onSuccess(jsonObject);
                try {
                    int flat = jsonObject.getInt("Status");//判断成功
                    if(flat != 0){//0代表返回数据成功
                        Log.d("MyApp.java","从后台获取要设置的JPush标签失败");
                        return;
                    }
                    String tag = jsonObject.getString("msg");
                    // 判断这个标签和原来的标签是否相同，不相同重新设置
                    if(!pre.getString(Constant.SPF_USER_JPUSH_TAG).equals(tag)){
                        //标签不一致，重新设置标签
                        HashSet tags = new HashSet<String>();
                        tags.add(tag);
                        JPushInterface.setTags(MyApp.this, tags, new TagAliasCallback() {
                            @Override
                            public void gotResult(int i, String s, Set<String> set) {
                                if(i== 0){//0代表设置成功
                                    pre.setString(Constant.SPF_USER_JPUSH_TAG, tag);//更新本地标签
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable, JSONObject jsonObject) {
                super.onFailure(throwable, jsonObject);
            }
        });
        //------极光推送结束-----
    }
    /**
     * 初始化图片加载对象
     */
    private void initImageLoader(Context context) {
        // 磁盘缓存目录
        File cacheDir = StorageUtils.getOwnCacheDirectory(
                getApplicationContext(), "imageloader/Cache");

        // 设置图片加载过程中，URL为空，图片解码出错，时的默认图片
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading).considerExifParams(true)
                .cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .memoryCacheExtraOptions(320, 480)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(30 * 1024 * 1024))
                .memoryCacheSize(30 * 1024 * 1024)
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .diskCacheSize(80 * 1024 * 1024)
                .defaultDisplayImageOptions(options)
                .imageDownloader(
                        new BaseImageDownloader(context, 5 * 1000, 30 * 1000))
                .build();

        // 初始化配置
        ImageLoader.getInstance().init(config);
    }

}
