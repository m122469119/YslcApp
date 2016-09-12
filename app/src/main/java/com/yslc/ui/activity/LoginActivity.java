package com.yslc.ui.activity;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yslc.app.Constant;
import com.yslc.bean.UserBean;
import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.UserModelService;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.util.CommonUtil;
import com.yslc.util.Md5Util;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 用户登录Activity
 *
 * @author HH
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
    private Button login;
    private EditText inputUser, inputPass;
    private ImageView clearUser, clearPass;
    private UserModelService userService;
//    private IWXAPI api;//微信

    /**
     * 设置布局
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    /**
     * 设置标题
     * @return
     */
    @Override
    protected String getToolbarTitle() {
        return getString(R.string.login);
    }

    /**
     * 初始化布局
     * <p>实例化业务类</p>
     * <p>关联登录按钮.清框按钮、输入框、返回键等并监听</p>
     */
    @Override
    protected void initView() {
        ShareSDK.initSDK(LoginActivity.this);
//        initWeChat();
        userService = new UserModelService(this);//业务类
        //登录按钮
        login = (Button) findViewById(R.id.loginBtn);
        login.setOnClickListener(this);
        findViewById(R.id.rememberPass).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.closeKey).setOnClickListener(this);//监听返回键
        //清框按钮
        clearUser = (ImageView) findViewById(R.id.clearUser);
        clearUser.setOnClickListener(this);
        clearPass = (ImageView) findViewById(R.id.clearPass);
        clearPass.setOnClickListener(this);
        //输入框
        inputUser = (EditText) findViewById(R.id.inputUsername);
        inputPass = (EditText) findViewById(R.id.inputPassword);
        //输入框监听
        inputUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                clearPass.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (CommonUtil.isInputEmpty(inputUser)) {
                    clearUser.setVisibility(View.GONE);
                } else {
                    clearUser.setVisibility(View.VISIBLE);
                }

                if (CommonUtil.isInputEmpty(inputUser, inputPass)) {
                    login.setEnabled(false);
                } else {
                    login.setEnabled(true);
                }
            }
        });
        inputPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                clearUser.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (CommonUtil.isInputEmpty(inputPass)) {
                    clearPass.setVisibility(View.GONE);
                } else {
                    clearPass.setVisibility(View.VISIBLE);
                }

                if (CommonUtil.isInputEmpty(inputUser, inputPass)) {
                    login.setEnabled(false);
                } else {
                    login.setEnabled(true);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.loginBtn:
                // 隐藏软键盘
                CommonUtil.hiddenSoftInput(this);

                // 进行登录
                if (userService.userLoginValidation(inputUser, inputPass)) {//验证格式
                    doLogin();
                }
                break;

            case R.id.rememberPass:
                // 忘记密码
                startActivity(new Intent(this, FindPasswordActivity.class));
                break;

            case R.id.register:
                // 去注册
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.clearUser:
                // 清除用户名
                inputUser.setText("");
                break;

            case R.id.clearPass:
                // 清除密码
                inputPass.setText("");
                break;

            case R.id.closeKey:
                // 隐藏软键盘并返回（结束当前Activity)
                CommonUtil.hiddenSoftInput(this);//TODO 算是bug吧
                break;
        }
    }

    /**
     * 登录
     */
    private void doLogin() {
        showWaitDialogs(R.string.logining, true);
        userService.userLogin(CommonUtil.inputFilter(inputUser), Md5Util
                .getMD5(CommonUtil.inputFilter(inputPass).getBytes()), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                hideWaitDialog();
                setResult(Activity.RESULT_OK);
                onFinishActivity();
            }

            @Override
            public <T> void failer(T data) {
                hideWaitDialog();
                //用户名或密码错误
                ToastUtil.showMessage(LoginActivity.this, data.toString());
            }
        });
    }

//    /**
//     * 初始化微信登录相关
//     */
//    private void initWeChat() {
//        api = WXAPIFactory.createWXAPI(this, PayActivity.APPID, true);
//        api.registerApp(PayActivity.APPID);
//    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1111:
                    UserBean user = new UserBean();
                    user.setUserId(msg.getData().getString("user_id"));
                    user.setUserImageUrl(msg.getData().getString("user_icon"));

                    //保存
                    // 保存用户Id,用户账号，用户头像,设置登录成功
                    SharedPreferencesUtil share = new SharedPreferencesUtil(LoginActivity.this,
                            Constant.SPF_USER_INFO_NAME);
                    share.setString(Constant.SPF_USER_ID_KEY, user.getUserId());
                    share.setString(Constant.SPF_USER_PHONE_KEY, msg.getData().getString("user_name"));
                    share.setString(Constant.SPF_USER_IMGURL_KEY, user.getUserImageUrl());
                    share.setBoolean(Constant.SPF_USER_ISLOGIN_KEY, true);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 点击微信登录按钮事件
     * @param view
     */
    public void weChatLogin(View view){
        Platform wechat= ShareSDK.getPlatform(this, Wechat.NAME);
        if (wechat.isValid()) {
            wechat.removeAccount();
        }
        wechat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                ToastUtil.showMessage(LoginActivity.this, "成功");
                Message msg = new Message();
                msg.what = 1111;
                Bundle bundle = new Bundle();
                bundle.putString("user_id",platform.getDb().getUserId());
                bundle.putString("user_icon", platform.getDb().getUserIcon());
                bundle.putString("user_name", platform.getDb().getUserName());
                msg.setData(bundle);
                handler.sendMessage(msg);

                setResult(Activity.RESULT_OK);
                onFinishActivity();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                ToastUtil.showMessage(LoginActivity.this, "错误");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                ToastUtil.showMessage(LoginActivity.this, "取消");
            }
        });
        wechat.SSOSetting(false);
        wechat.authorize();

//        ToastUtil.showMessage(LoginActivity.this, "微信登录");
//        final SendAuth.Req req = new SendAuth.Req();
//        //授权域
//        req.scope = "snsapi_userinfo";
//        // 为保存回调状态， 可以设置随机数，非必需参数
//        req.state = "wechat_sdk_wealth";
//        api.sendReq(req);
    }
}
