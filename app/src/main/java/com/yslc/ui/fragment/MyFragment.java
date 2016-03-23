package com.yslc.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yslc.ui.base.BaseFragment;
import com.yslc.R;
import com.yslc.ui.activity.LoginActivity;
import com.yslc.ui.activity.SettingActivity;
import com.yslc.ui.activity.ShowSdImgActivity;
import com.yslc.ui.activity.UpdatePasswordActivity;
import com.yslc.app.Constant;
import com.yslc.ui.dialog.ShareDialog;
import com.yslc.util.FileUtil;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;

/**
 * 我Fragment
 *
 * @author HH
 */
public class MyFragment extends BaseFragment implements OnClickListener {
    private Button exitLogin;//退出登录
    private ImageView img;//用户头像
    private TextView account;//用户状态
    private Context context;
    private ImageLoader imageLoader;
    private SharedPreferencesUtil spfUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    /**
     * 初始化布局
     * <p>获取上下文、实例化加载网络图片工具类</p>
     * <p>实例化sharePreferencesUtil工具类</p>
     * @param views
     */
    @Override
    protected void findView(View views) {
        super.findView(views);

        context = this.getActivity();
        imageLoader = ImageLoader.getInstance();
        spfUtil = new SharedPreferencesUtil(context, Constant.SPF_USER_INFO_NAME);

        exitLogin = (Button) views.findViewById(R.id.exitLogin);
        account = (TextView) views.findViewById(R.id.account);
        account.setOnClickListener(this);
        img = (ImageView) views.findViewById(R.id.personImg);
        //设置其他监听事件
        views.findViewById(R.id.set).setOnClickListener(this);
        views.findViewById(R.id.about).setOnClickListener(this);
        views.findViewById(R.id.updatePass).setOnClickListener(this);
        views.findViewById(R.id.valFriend).setOnClickListener(this);
        views.findViewById(R.id.personImg).setOnClickListener(this);
        exitLogin.setOnClickListener(this);
    }

    /**
     * 判断是否登录
     * <p>根据是否登录，显示不同界面</p>
     */
    @Override
    public void onResume() {
        super.onResume();

        // 判断是否登录
        if (SharedPreferencesUtil.isLogin(context)) {
            initIsLogin();
        } else {
            initNoLogin();
        }
    }

    /**
     * 初始化登录状态
     */
    private void initIsLogin() {
        exitLogin.setVisibility(View.VISIBLE);//显示退出登录按钮
        account.setText(spfUtil.getString(Constant.SPF_USER_PHONE_KEY));//设置用户电话

        //用户头像是否保存到了sd卡，没有则保存到sd卡
        Bitmap bitmap = BitmapFactory.decodeFile(FileUtil.getSdCardPath() + Constant.FILES_USERIMG);
        if (null == bitmap) {//sd卡没有头像图片，下载图片
            imageLoader.loadImage(spfUtil.getString(Constant.SPF_USER_IMGURL_KEY), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap b) {
                    if (b != null) {//下载成功，保存头像到sd卡
                        FileUtil.saveFile(b, Constant.FILES_USERIMG);
                    }
                    img.setImageBitmap(b);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        } else {//sd卡有图片，设置图片
            img.setImageBitmap(bitmap);
        }
    }

    /**
     * 初始化未登录状态
     */
    private void initNoLogin() {
        exitLogin.setVisibility(View.GONE);//去掉退出登录按钮
        //未登录默认图片
        img.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.login_login));
        account.setText(getString(R.string.goLogin));//未登录默认状态
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account:
                //去登陆
                if (!SharedPreferencesUtil.isLogin(context)) {
                    startActivity(new Intent(context, LoginActivity.class));
                }
                break;

            case R.id.personImg:
                //用户更改头像
                if (SharedPreferencesUtil.isLogin(context)) {
                    startActivity(new Intent(context, ShowSdImgActivity.class));
                }
                break;

            case R.id.valFriend:
                // 分享(邀请好友)
                new ShareDialog(context, ShareDialog.SHARE_APP);
                break;

            case R.id.set://设置
                startActivity(new Intent(context, SettingActivity.class));
                break;

            case R.id.about://关于
                ToastUtil.showMessage(context, "关于");
                break;

            case R.id.updatePass://修改密码
                // 判断是否登录
                if (!SharedPreferencesUtil.isLogin(context, Constant.PAEASE_LOGIN)) {
                    startActivityForResult(//没有登录，先登录
                            new Intent(context, LoginActivity.class), 1);
                    return;
                }
                startActivity(new Intent(context, UpdatePasswordActivity.class));
                break;

            case R.id.exitLogin://退出登录
                exitLogin();
                break;
        }
    }

    /**
     * 退出登录操作
     * <p>回到未登录界面</p>
     * <p>清除用户数据</p>
     */
    private void exitLogin() {
        // 未登录界面初始化
        initNoLogin();

        // 清除登录数据
        spfUtil.clearAll();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            //登录成功后打开修改密码页面
            startActivity(new Intent(context, UpdatePasswordActivity.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 清除用户头像
        FileUtil.deleteAllFile(Constant.FILES_USER);
    }

}
