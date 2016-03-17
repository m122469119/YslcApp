package com.yslc.ui.base;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;

import com.yslc.R;
import com.yslc.app.ActivityManager;
import com.yslc.ui.dialog.LoadingDialog;
import com.yslc.inf.ILoadingDialogCallBack;
import com.yslc.util.CommonUtil;
import com.yslc.util.HttpUtil;
import com.yslc.util.ViewUtil;

/**
 * 基类activity
 *
 * @author HH
 */
public abstract class BaseActivity extends AppCompatActivity implements
        ILoadingDialogCallBack {
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstence().pushActivity(this);

        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
            if (null != findViewById(R.id.toolbar)) {
                initToolbar((Toolbar) findViewById(R.id.toolbar));
            }
        }

        initView();
    }

    /**
     * 设置状态栏是否透明
     *
     * @param isTranslucent 是否透明（默认不透明）
     */
    protected void setTranslucentStatusBar(boolean isTranslucent) {
        if (isTranslucent) {
            //开启状态栏透明
            ViewUtil.TranslucentStatusBar(this);
        }
    }

    /**
     * 设置布局文件
     *
     * @return
     */
    protected int getLayoutId() {
        return 0;
    }

    /**
     * 设置toolbar标题
     *
     * @return
     */
    protected String getToolbarTitle() {
        return "";
    }

    /**
     * 初始化toolbar
     *
     * @param toolbar
     */
    private void initToolbar(Toolbar toolbar) {
        if (null != toolbar) {
            toolbar.setTitle("");
            ((TextView) toolbar.findViewById(R.id.titleText))
                    .setText(getToolbarTitle());
            setSupportActionBar(toolbar);
            setNavigationIcon(toolbar);
        }
    }

    /**
     * 设置toolbar Navigation
     */
    protected void setNavigationIcon(Toolbar toolbar) {
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this,
                R.drawable.rollback));
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinishActivity();
            }
        });
    }

    /**
     * 初始化布局组件
     */
    protected void initView() {

    }

    protected void onFinishActivity() {
        ActivityManager.getInstence().finishActivity(this);
    }

    @Override
    public void onBackPressed() {
        onFinishActivity();
    }

    @Override
    public void hideWaitDialog() {
        if (null != dialog) {
            dialog.cancel();
        }
    }

    @Override
    public void showWaitDialogs(int resId, boolean isCancle) {
        showWaitDialogs(getString(resId), isCancle);
    }

    @Override
    public void showWaitDialogs(String info, boolean isCancle) {
        if (!CommonUtil.isNetworkAvalible(this)) {
            // 没有网络连接
            return;
        }

        if (null == dialog) {
            dialog = new LoadingDialog(this, info);
            dialog.setCancelable(isCancle);
        }

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
