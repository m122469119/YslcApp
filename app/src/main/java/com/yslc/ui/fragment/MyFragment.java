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
    private Button exitLogin;
    private ImageView img;
    private TextView account;
    private Context context;
    private ImageLoader imageLoader;
    private SharedPreferencesUtil spfUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

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
        views.findViewById(R.id.set).setOnClickListener(this);
        views.findViewById(R.id.about).setOnClickListener(this);
        views.findViewById(R.id.updatePass).setOnClickListener(this);
        views.findViewById(R.id.valFriend).setOnClickListener(this);
        views.findViewById(R.id.personImg).setOnClickListener(this);
        exitLogin.setOnClickListener(this);
    }

    /**
     * 判断是否登录
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
        exitLogin.setVisibility(View.VISIBLE);
        account.setText(spfUtil.getString(Constant.SPF_USER_PHONE_KEY));

        //用户头像是否保存到了sd卡，没有则保存到sd卡
        Bitmap bitmap = BitmapFactory.decodeFile(FileUtil.getSdCardPath() + Constant.FILES_USERIMG);
        if (null == bitmap) {
            imageLoader.loadImage(spfUtil.getString(Constant.SPF_USER_IMGURL_KEY), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap b) {
                    if (b != null) {
                        FileUtil.saveFile(b, Constant.FILES_USERIMG);
                    }
                    img.setImageBitmap(b);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        } else {
            img.setImageBitmap(bitmap);
        }
    }

    /**
     * 初始化未登录状态
     */
    private void initNoLogin() {
        exitLogin.setVisibility(View.GONE);
        img.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.login_login));
        account.setText(getString(R.string.goLogin));
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

            case R.id.set:
                startActivity(new Intent(context, SettingActivity.class));
                break;

            case R.id.about:
                ToastUtil.showMessage(context, "关于");
                break;

            case R.id.updatePass:
                // 判断是否登录
                if (!SharedPreferencesUtil.isLogin(context, Constant.PLAREN_LOGIN)) {
                    startActivityForResult(
                            new Intent(context, LoginActivity.class), 1);
                    return;
                }
                startActivity(new Intent(context, UpdatePasswordActivity.class));
                break;

            case R.id.exitLogin:
                exitLogin();
                break;
        }
    }

    /**
     * 退出登录操作
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
            //登录成功后进行的操作
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
