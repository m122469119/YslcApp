package com.yslc.ui.activity;

import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.UserModelService;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.util.CommonUtil;
import com.yslc.util.Md5Util;
import com.yslc.util.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
}
