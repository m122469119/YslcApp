package com.yslc.ui.activity;

import com.yslc.app.Constant;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import cn.jpush.android.api.JPushInterface;

/**
 * 设置页面Activity
 *
 * @author HH
 */
public class SettingActivity extends BaseActivity implements
        OnCheckedChangeListener, OnClickListener {
    private SharedPreferencesUtil spfUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化页面
        findView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.set);
    }

    /**
     * 初始化页面
     */
    private void findView() {
        spfUtil = new SharedPreferencesUtil(this, Constant.SPF_SET_INFO_NAME);
        //推送选项
        CheckBox isPush = (CheckBox) findViewById(R.id.isPush);
        isPush.setOnCheckedChangeListener(this);
        isPush.setChecked(spfUtil.getBoolean(Constant.SPF_IS_PUSH_KEY));
        //wifi选项
        CheckBox isWifi = (CheckBox) findViewById(R.id.isWifi);
        isWifi.setOnCheckedChangeListener(this);
        isWifi.setChecked(spfUtil.getBoolean(Constant.SPF_IS_WIFI_KEY));
        //检测版本监听
        findViewById(R.id.checkVersion).setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.isPush:
                if (isChecked) {
                    spfUtil.setBoolean(Constant.SPF_IS_PUSH_KEY, true);
                    JPushInterface.resumePush(this);//极光推送
                } else {
                    spfUtil.setBoolean(Constant.SPF_IS_PUSH_KEY, false);
                    JPushInterface.stopPush(this);
                }
                break;

            case R.id.isWifi:
                if (isChecked) {
                    spfUtil.setBoolean(Constant.SPF_IS_WIFI_KEY, true);
                } else {
                    spfUtil.setBoolean(Constant.SPF_IS_WIFI_KEY, false);
                }
                break;

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.checkVersion:
                // 检测新版本
                ToastUtil.showMessage(this, "当前版本已是最新版");
                break;

            default:
                break;
        }
    }

}
