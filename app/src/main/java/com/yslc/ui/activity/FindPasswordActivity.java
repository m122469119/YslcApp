package com.yslc.ui.activity;

import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.UserModelService;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.util.CommonUtil;
import com.yslc.util.Md5Util;
import com.yslc.util.TimerUtil;
import com.yslc.util.ToastUtil;

import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 找回密码Activity
 *
 * @author HH
 */
public class FindPasswordActivity extends BaseActivity implements
        OnClickListener, TimerUtil.OnTimerCallback {
    private EditText inputPhone, inputCode, inputPass1, inputPass2;
    private Button getCode, confirm;

    private String getCodeNum = "-1";//验证码
    private int lotterTime = 60;
    private UserModelService userService;
    private TimerUtil timerUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_password;
    }

    @Override
    protected String getToolbarTitle() {
        return getText(R.string.findPassword).toString();
    }


    @Override
    protected void initView() {
        //编辑框
        inputPhone = (EditText) findViewById(R.id.inputPhone);
        inputCode = (EditText) findViewById(R.id.inputCode);
        inputPass1 = (EditText) findViewById(R.id.inputPasswordOne);
        inputPass2 = (EditText) findViewById(R.id.inputPasswordTwo);
        findViewById(R.id.closeKey).setOnClickListener(this);//返回键
        getCode = (Button) findViewById(R.id.getCode);//发送验证码按钮
        confirm = (Button) findViewById(R.id.confirm);//确认修改按钮
        //监听事件
        getCode.setOnClickListener(this);
        confirm.setOnClickListener(this);
        onTextLengthChange(inputPhone);
        onTextLengthChange(inputCode);
        onTextLengthChange(inputPass1);
        onTextLengthChange(inputPass2);

        userService = new UserModelService(this);//业务类
        //计时器（一秒钟执行一次）
        timerUtil = new TimerUtil(1000);
        timerUtil.setOnTimerCallback(this);
    }

    /**
     * 输入框文字长度监听
     * 四个框非空，则可以点击确认修改按钮
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
                    confirm.setEnabled(false);
                } else {
                    confirm.setEnabled(true);
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

            case R.id.confirm:
                // 隐藏软键盘
                CommonUtil.hiddenSoftInput(this);

                // 确认修改
                //验证码输入正确，并且密码格式正确，则修改
                if (userService.isValidationCode(getCodeNum, CommonUtil.inputFilter(inputCode))
                        && userService.passwordIsTrue(inputPass1, inputPass2)) {
                    doUpdate();
                }
                break;

            case R.id.closeKey:
                // 隐藏软键盘，并关闭当前activity
                CommonUtil.hiddenSoftInput(this);
                break;

        }
    }

    @Override
    public void onTimer() {
        getCode.setText(String.valueOf(--lotterTime));
        if (lotterTime == 0) {
            timeOut();//超时
        }
    }

    /**
     * 倒计时已到，重新获取验证码
     * 初始化验证码初始状态
     */
    private void timeOut() {
        // 倒计时已到，需要重新获取验证码
        getCode.setText(getText(R.string.getCode));
        getCode.setEnabled(true);
        inputCode.setText("");//清空验证码输入框
        getCodeNum = "-1";
        timerUtil.stopTimer();//停止计时器
    }

    /**
     * 开始计时
     */
    private void getCodeSuccess() {
        lotterTime = 60;
        getCode.setEnabled(false);
        getCode.setText(String.valueOf(lotterTime));
        timerUtil.startTimer();
    }

    /**
     * 获取后台手机验证码
     * <p>成功后开始计时</p>
     */
    private void getCodeHttp() {
        showWaitDialogs(R.string.getCode, true);

        userService.getValidationCode("1", CommonUtil.inputFilter(inputPhone),//TODO "1"
                new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                hideWaitDialog();
                ToastUtil.showMessage(FindPasswordActivity.this, "验证码已发送");//TODO
                getCodeNum = data.toString();
                getCodeSuccess();//计时
            }

            @Override
            public <T> void failer(T data) {
                hideWaitDialog();
                ToastUtil.showMessage(FindPasswordActivity.this,
                        data.toString());
            }
        });
    }

    /**
     * 进行修改
     * <p>修改密码</p>
     */
    private void doUpdate() {
        showWaitDialogs(R.string.updateing, true);

        //手机号，加密密码号
        userService.userForgetPass(CommonUtil.inputFilter(inputPhone), Md5Util
                .getMD5(CommonUtil.inputFilter(inputPass1).getBytes()), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                timeOut();//停止计时器
                hideWaitDialog();
                ToastUtil.showMessage(FindPasswordActivity.this,
                        data.toString());
                onFinishActivity();
            }

            @Override
            public <T> void failer(T data) {
                timeOut();
                hideWaitDialog();
                ToastUtil.showMessage(FindPasswordActivity.this,
                        data.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        timerUtil.destroyTimer();
    }
}
