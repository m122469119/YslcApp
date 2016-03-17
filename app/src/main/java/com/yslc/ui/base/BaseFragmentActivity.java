package com.yslc.ui.base;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.yslc.R;
import com.yslc.inf.IFragmentActivityCallBack;

/**
 * 自定义的FragmentActivity基类
 * ● 实现初始化FragmentManager
 * ● 实现添加Fragment
 * ● 实现设置显示Fragment
 *
 * @author HH
 */
public abstract class BaseFragmentActivity extends BaseActivity implements
        IFragmentActivityCallBack, RadioGroup.OnCheckedChangeListener {
    protected FragmentManager fm = null;
    protected ArrayList<Fragment> mFragmentList = new ArrayList<>();
    protected ArrayList<String> mTitleList = new ArrayList<>();
    private int currFragmentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            //取得上次显示的fragment，activity销毁时可以恢复界面状态
            currFragmentIndex = savedInstanceState.getInt("position");
        }

        fm = getSupportFragmentManager();
    }

    protected void setmRadioGroup(RadioGroup mRadioGroup) {
        mRadioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0, len = group.getChildCount(); i < len; i++) {
            if (group.getChildAt(i).getId() == checkedId) {
                // 设置标题
                onChanceFragment(mTitleList.get(i));

                Fragment fragment = mFragmentList.get(i);
                FragmentTransaction ft = fm.beginTransaction();
                //将上一个Fragment设置为OnPause 将目标Fragment设置为OnResume，若目标Fragment不在，则添加
                mFragmentList.get(currFragmentIndex).onPause();
                if (fragment.isAdded()) {
                    fragment.onResume();
                } else {
                    ft.add(R.id.fragment, fragment);
                }

                // 显示目标Fragment
                setFragment(i);
                currFragmentIndex = i;
                ft.commitAllowingStateLoss();
                break;
            }
        }
    }

    protected void onChanceFragment(String title) {

    }

    /**
     * 添加Fragment
     */
    @Override
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mTitleList.add(title);
    }

    @Override
    public void showFragment(int id) {
        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = mFragmentList.get(currFragmentIndex);
        if (fragment.isAdded()) {
            fragment.onResume();
        } else {
            ft.add(id, fragment);
        }
        ft.commit();
    }

    /**
     * 设置Fragment 隐藏上一个Fragment，显示下一个Fragment
     */
    @Override
    public void setFragment(int position) {
        Fragment fragment;
        for (int i = 0, len = mFragmentList.size(); i < len; i++) {
            fragment = mFragmentList.get(i);
            FragmentTransaction ft = fm.beginTransaction();
            if (position == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //保存当前显示的fragment下标
        outState.putInt("position", currFragmentIndex);
        super.onSaveInstanceState(outState);
    }

}
