package com.yslc.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.yslc.app.Constant;
import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.UserModelService;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.app.ActivityManager;
import com.yslc.util.CommonUtil;
import com.yslc.util.Md5Util;
import com.yslc.util.TimerUtil;
import com.yslc.util.ToastUtil;

/**
 * 用户注册Activity
 *
 * @author HH
 */
public class RegisterActivity extends BaseActivity implements OnClickListener, TimerUtil.OnTimerCallback {
    private EditText inputPhone, inputCode, inputPass1, inputPass2;
    private Button getCode, register;

    private String getCodeNum = "-1";
    private int lotterTime = 60;   //初始化验证码输入时间为60秒
    private UserModelService userService;
    private TimerUtil timerUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected String getToolbarTitle() {
        return getText(R.string.registers).toString();
    }

    @Override
    protected void initView() {
        inputPhone = (EditText) findViewById(R.id.inputPhone);
        inputCode = (EditText) findViewById(R.id.inputCode);
        inputPass1 = (EditText) findViewById(R.id.inputPasswordOne);
        inputPass2 = (EditText) findViewById(R.id.inputPasswordTwo);

        getCode = (Button) findViewById(R.id.getCode);
        register = (Button) findViewById(R.id.registerBtn);
        getCode.setOnClickListener(this);
        register.setOnClickListener(this);
        findViewById(R.id.closeKey).setOnClickListener(this);
        findViewById(R.id.fastLogin).setOnClickListener(this);

        // 输入框文字长度监听
        onTextLengthChange(inputPhone);
        onTextLengthChange(inputCode);
        onTextLengthChange(inputPass1);
        onTextLengthChange(inputPass2);

        userService = new UserModelService(this);
        timerUtil = new TimerUtil(1000);  //倒计时工具，每隔1秒进行回调
        timerUtil.setOnTimerCallback(this);
    }

    /**
     * 输入框文字长度监听
     */
    private void onTextLengthChange(EditText input) {
        // 监听号码
        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (CommonUtil.isInputEmpty(inputPhone, inputCode, inputPass1, inputPass2)) {
                    register.setEnabled(false);
                } else {
                    register.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.getCode:
                // 获取手机验证码
                if (!CommonUtil.checkMobile(CommonUtil.inputFilter(inputPhone))) {
                    ToastUtil.showMessage(this, "请输入正确的手机号码");
                } else {
                    getCodeHttp();
                }
                break;

            case R.id.registerBtn:
                // 隐藏软键盘
                CommonUtil.hiddenSoftInput(this);

                // 注册（需要验证验证码与两次密码填写是否正确）
                if (userService.isValidationCode(getCodeNum, CommonUtil.inputFilter(inputCode)) && userService.passwordIsTrue(inputPass1, inputPass2)) {
                    doRegister();
                }
                break;

            case R.id.fastLogin:
                // 已有账号，快速登录
                onFinishActivity();
                break;

            case R.id.closeKey:
                // 隐藏软键盘
                CommonUtil.hiddenSoftInput(this);
                break;
        }
    }

    @Override
    public void onTimer() {
        getCode.setText(String.valueOf(lotterTime--));
        if (lotterTime <= 0) {
            timeOut();
        }
    }

    /**
     * 倒计时已到，需要重新获取验证码
     * 初始化验证码初始状态
     */
    private void timeOut() {
        getCode.setText(getText(R.string.getCode));
        getCode.setEnabled(true);
        inputCode.setText("");
        getCodeNum = "-1";
        timerUtil.stopTimer();
    }

    /**
     * 验证码获取成功，启动倒计时线程
     */
    private void getCodeSuccess() {
        getCode.setEnabled(false);
        lotterTime = 60;
        getCode.setText(String.valueOf(lotterTime));
        timerUtil.startTimer();
    }

    /**
     * 获取后台手机验证码
     */
    private void getCodeHttp() {
        showWaitDialogs(R.string.getCode, true);

        userService.getValidationCode("0", CommonUtil.inputFilter(inputPhone), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                hideWaitDialog();
                getCodeNum = data.toString();
                getCodeSuccess();
                ToastUtil.showMessage(RegisterActivity.this, "验证码已发送");
            }

            @Override
            public <T> void failer(T data) {
                hideWaitDialog();
                ToastUtil.showMessage(RegisterActivity.this, data.toString());
            }
        });
    }

    /**
     * 进行注册
     */
    private void doRegister() {
        showWaitDialogs(R.string.registering, true);
        userService.userRegister(CommonUtil.inputFilter(inputPhone), Md5Util.getMD5(CommonUtil.inputFilter(inputPass1).getBytes()), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                hideWaitDialog();
                ToastUtil.showMessage(RegisterActivity.this, Constant.REGISTER_SUCCESS);
                ActivityManager.getInstence().killActivity(LoginActivity.class);
                onFinishActivity();
            }

            @Override
            public <T> void failer(T data) {
                timeOut();
                hideWaitDialog();
                ToastUtil.showMessage(RegisterActivity.this, data.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerUtil.destroyTimer();
    }

}

//////////////////////////  15975337560
