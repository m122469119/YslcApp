package com.yslc.ui.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.app.Constant;
import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.UserModelService;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;
import com.yslc.util.ViewUtil;
import com.yslc.view.CropImageFrameView;
import com.yslc.view.CropImageView;

/**
 * 头像裁剪Activity
 *
 * @author HH
 */
public class CropImageActivity extends BaseActivity {
    private CropImageFrameView cropImageFrameView;
    private ImageLoader imageLoader;
    private UserModelService userService;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_crop_image;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.title_activity_crop_image);
    }

    @Override
    protected void initView() {
        cropImageFrameView = (CropImageFrameView) findViewById(R.id.frameView);//放置图片
        imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage("file://" + getIntent().getStringExtra("path"),
                (CropImageView) findViewById(R.id.userImg));//裁剪框

        userService = new UserModelService(this);//业务类
    }

    /**
     * 确定menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_sd_img, menu);
        menu.findItem(R.id.upload).setEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.upload) {//确定点击事件
            uploadFile(getBitmap());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取裁剪框内截图
     *
     * @return
     */
    private Bitmap getBitmap() {
        float[] points = cropImageFrameView.getRectPoint();
        return ViewUtil.screenShot(this, (int) points[0], (int) points[1], (int) points[2], (int) points[3]);
    }


    /**
     * 上传照片
     * <p>成功后删除头像缓存</p>
     */
    private void uploadFile(final Bitmap bitmap) {
        showWaitDialogs(Constant.UPLOAD_WAIT, false);

        userService.uploadUserImage(SharedPreferencesUtil.getUserId(this), bitmap,
                new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                userService.clearUserImgCache(imageLoader);
                onFinishActivity();
            }

            @Override
            public <T> void failer(T data) {
                ToastUtil.showMessage(CropImageActivity.this, data.toString());
            }
        });
    }

}
