package com.yslc.app;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.yslc.R;

/**
 * 全局配置类
 *
 * @author HH
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化分享组件
        ShareSDK.initSDK(this);

        // 初始化图片加载对象
        initImageLoader(getApplicationContext());

        // 初始化全局异常扑捉
        //CrashExceptionHandler.getInstance().init(getApplicationContext());

        // 启动推送服务
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        //确保AsyncTask方法在UI线程中初始化，防止onPostExecute不能回调
        try {
            Class.forName("android.os.AsyncTask");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
