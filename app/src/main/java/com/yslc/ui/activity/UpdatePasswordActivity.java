package com.yslc.ui.activity;

import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.UserModelService;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.util.CommonUtil;
import com.yslc.util.Md5Util;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 修改用户密码
 *
 * @author HH
 */
public class UpdatePasswordActivity extends BaseActivity implements
        OnClickListener {
    private UserModelService userService;
    private EditText oldPass, newPass, newPasss;
    private Button confirm;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_update_password;
    }

    @Override
    protected String getToolbarTitle() {
        return getText(R.string.updatePassword).toString();
    }

    @Override
    protected void initView() {
        oldPass = (EditText) findViewById(R.id.inputOldPassword);
        onTextLengthChange(oldPass);
        newPass = (EditText) findViewById(R.id.inputPasswordOne);
        onTextLengthChange(newPass);
        newPasss = (EditText) findViewById(R.id.inputPasswordTwo);
        onTextLengthChange(newPasss);
        confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        findViewById(R.id.closeKey).setOnClickListener(this);
        userService = new UserModelService(this);
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
                if (CommonUtil.isInputEmpty(oldPass, newPass, newPasss)) {
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
            case R.id.closeKey:
                // 隐藏软键盘
                CommonUtil.hiddenSoftInput(this);
                break;

            case R.id.confirm:
                // 确认修改密码
                if (CommonUtil.checkPasswordFromat(this, newPass, newPasss)) {
                    updatePass();
                }
                break;

        }
    }

    /**
     * 确认修改密码
     */
    private void updatePass() {
        showWaitDialogs(R.string.updateing, true);

        userService.userUpdatePass(SharedPreferencesUtil.getUserId(this), Md5Util.getMD5(CommonUtil.inputFilter(oldPass).getBytes()),
                Md5Util.getMD5(CommonUtil.inputFilter(newPass).getBytes()), new GetDataCallback() {
                    @Override
                    public <T> void success(T data) {
                        hideWaitDialog();
                        ToastUtil.showMessage(UpdatePasswordActivity.this,
                                data.toString());
                        onFinishActivity();
                    }

                    @Override
                    public <T> void failer(T data) {
                        hideWaitDialog();
                        ToastUtil.showMessage(UpdatePasswordActivity.this,
                                data.toString());
                    }
                });
    }
}
