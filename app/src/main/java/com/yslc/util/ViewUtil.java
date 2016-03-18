package com.yslc.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yslc.R;
import com.yslc.view.CircleDisplayer;

/**
 * View工具类
 * <p>
 * Created by HH on 2015/12/25.
 */
public class ViewUtil {
    /**
     * 截屏
     *
     * @param activity 上下文
     * @param left     截屏左上角坐标X
     * @param top      截屏左上角坐标Y
     * @param right    截屏右下角坐标X
     * @param bottom   截屏右下角坐标Y
     * @return finalBitmap 截取得Bitmap
     */
    public static Bitmap screenShot(Activity activity, int left, int top, int right, int bottom) {
        // 获取截屏
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        //获取状态栏高度
        int statusBarHeight = CommonUtil.getStatusHeight(activity);
        int width = right - left < view.getWidth() ? right - left : view.getWidth();
        int height = bottom - top < view.getHeight() - statusBarHeight ? bottom - top : view.getHeight() - statusBarHeight;

        Bitmap finalBitmap = Bitmap.createBitmap(view.getDrawingCache(),
                left, top + statusBarHeight, width, height);
        view.destroyDrawingCache();

        return finalBitmap;
    }

    /**
     * 获取圆形图片配置
     */
    public static DisplayImageOptions getCircleOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading_shape)
                .showImageForEmptyUri(R.drawable.loading_shape)
                .showImageOnFail(R.drawable.loading_shape)
                .considerExifParams(true).cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleDisplayer())
                .build();
    }

    /**
     * 获取较模糊图片配置(正方形图片)
     */
    public static DisplayImageOptions getVagueOptions() {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 8;
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .considerExifParams(true).cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .decodingOptions(opts)
                .build();
    }

    /**
     * 设置状态栏透明
     */
    public static void TranslucentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.greyTrans));
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }
}
