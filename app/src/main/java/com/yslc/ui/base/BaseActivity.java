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
 * <p>实现加载对话框接口</p>
 * <p>极光推送</p>
 * <div><p>子类可重写getLayoutId()设置布局文件(.xml)</p>
 * <p>重写initView()初始化布局文件</p>
 * <p>重写gettoolbarTitle设置标题</p></div>
 * <div>其他方法
 * <p>设置状态栏透明</p></div>
 * @author HH
 */
public abstract class BaseActivity extends AppCompatActivity implements
        ILoadingDialogCallBack {
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加到activity管理类
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
     * 此方法提供给子类设置布局文件
     * @return 返回布局文件的id
     */
    protected int getLayoutId() {
        return 0;
    }

    /**
     * 设置toolbar标题
     * 默认值为空字符
     * 此方法提供给子类重写
     * @return
     */
    protected String getToolbarTitle() {
        return "";
    }

    /**
     * 初始化toolbar（标题栏）
     *
     * <p>1.设置标题</p>
     * <p>2.提供actionBar支持</p>
     * <p>3.导航栏返回键</p>
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
     * <p>设置了导航图标和回调接口（返回，结束本activity）</p>
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
     * 此方法提供给子类重写
     */
    protected void initView() {

    }

    /**
     * 调用Activity管理类结束activity
     */
    protected void onFinishActivity() {
        ActivityManager.getInstence().finishActivity(this);
    }

    /**
     * 返回键结束当前activity
     */
    @Override
    public void onBackPressed() {
        onFinishActivity();
    }

    /**
     * 关闭等待对话框
     */
    @Override
    public void hideWaitDialog() {
        if (null != dialog) {
            dialog.cancel();
        }
    }

    /**
     * 显示加载对话框
     *
     * @param  resId 资源文件String字符串ID
     * @param isCancle 是否返回键取消
     *
     * @see #showWaitDialogs(String, boolean)
     */
    @Override
    public void showWaitDialogs(int resId, boolean isCancle) {
        showWaitDialogs(getString(resId), isCancle);
    }

    /**
     * 显示加载对话框
     * @param info 提示信息（比如正在加载）
     * @param isCancle 是否返回键取消
     *
     * @see #showWaitDialogs(int, boolean)
     */
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

    /**
     * 重启极光推送
     */
    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    /**
     * 暂停极光推送
     */
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
