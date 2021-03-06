package com.yslc.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.app.Constant;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.ui.dialog.AlbumPopupWindow;
import com.yslc.inf.OnItemClick;
import com.yslc.util.AlbumUtil;
import com.yslc.util.FileUtil;
import com.yslc.util.ViewUtil;
import com.yslc.view.PhotoImageView;

/**
 * 显示用户SD卡照片,选择头像
 *
 * @author HH
 */
public class ShowSdImgActivity extends BaseActivity implements OnItemClick {
    private GridView gridView;
    private ImageLoader imageLoader;
    private List<String> imgPathArray;
    private QuickAdapter<String> adapter;
    private AlbumUtil albumService;
    private AlbumPopupWindow albumDialog;
    private TextView albunTV;

    @Override
    protected void initView() {
        super.initView();

        albumService = new AlbumUtil(this);
        albunTV = (TextView) findViewById(R.id.albumTv);
        findViewById(R.id.linear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择相册
                if (null == albumDialog) {
                    albumDialog = new AlbumPopupWindow(ShowSdImgActivity.this, albumService.getAlbums());
                    albumDialog.setOnItemClick(ShowSdImgActivity.this);
                }

                albumDialog.showDialog(v);
            }
        });

        gridView = (GridView) findViewById(R.id.gridView);
        imageLoader = ImageLoader.getInstance();

        getImgPath();
        setAdapter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_sdimg;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.chioseImg);
    }

    @Override
    public void onItemClick(String albumName) {
        albunTV.setText(albumName);
        imgPathArray.clear();
        imgPathArray.add("-1"); //照相机标识
        imgPathArray.addAll(albumService.getAlbum(albumName));
        setAdapter();
    }

    /**
     * 获取所有照片SD卡路径
     */
    private void getImgPath() {
        imgPathArray = new ArrayList<>();
        imgPathArray = albumService.getCurrent();
        imgPathArray.add(0, "-1"); //拍照标识
    }

    private void setAdapter() {
        if (null == adapter) {
            final DisplayImageOptions option = ViewUtil.getVagueOptions();
            adapter = new QuickAdapter<String>(this,
                    R.layout.controls_sd_image, imgPathArray) {
                @Override
                protected void convert(BaseAdapterHelper helper, String item) {
                    PhotoImageView img = helper.getView(R.id.img);
                    if (item.equals("-1")) {
                        img.setImageDrawable(ContextCompat.getDrawable(
                                ShowSdImgActivity.this, R.drawable.ic_camera));
                        img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 使用相机拍照
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (null != intent.resolveActivity(ShowSdImgActivity.this.getPackageManager())) {
                                    Uri imageUri = Uri.fromFile(creatTempPhoto());
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                    startActivityForResult(intent, 100);
                                }
                            }
                        });
                    } else {
                        imageLoader.displayImage("file://" + item, img, option);
                        img.setTag(item);
                        img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                redictedIntent(v.getTag().toString());
                            }
                        });
                    }
                }
            };

            gridView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }


    }

    /**
     * 创建存储用户使用相机拍摄的照片
     *
     * @return 照片文件
     */
    private File creatTempPhoto() {
        return FileUtil.creatMoreFiles(Constant.FILES_TEMPIMG);
    }

    /**
     * 获取用户使用相机拍摄的照片的路径
     *
     * @return 照片路径
     */
    private String getPhotoPath() {
        return FileUtil.getSdCardPath() + Constant.FILES_TEMPIMG;
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);

        if (arg0 == 100) {
            redictedIntent(getPhotoPath());
        }
    }

    /**
     * 跳转到裁剪页
     *
     * @param imgPath
     */
    private void redictedIntent(String imgPath) {
        Intent intent = new Intent(this, CropImageActivity.class);
        intent.putExtra("path", imgPath);
        startActivity(intent);
        onFinishActivity();
    }

}
