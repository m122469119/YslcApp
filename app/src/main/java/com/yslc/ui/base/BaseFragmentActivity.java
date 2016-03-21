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
 * <p>实现了自定义的IFragmentActivityCallBack接口</p>
 * <P>实现OnCheckedChangeListener,点击Radiobutton后，切换对应的Fragment</P>
 * @author HH
 */
public abstract class BaseFragmentActivity extends BaseActivity implements
        IFragmentActivityCallBack, RadioGroup.OnCheckedChangeListener {
    protected FragmentManager fm = null;
    protected ArrayList<Fragment> mFragmentList = new ArrayList<>();
    protected ArrayList<String> mTitleList = new ArrayList<>();
    private int currFragmentIndex = 0;

    /**
     * 获取上传记录的FragmentIndex
     * <p>实例化FragmentManager对象</p>
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            //取得上次显示的fragment，activity销毁时可以恢复界面状态
            currFragmentIndex = savedInstanceState.getInt("position");
        }

        fm = getSupportFragmentManager();
    }

    /**
     * 设置radioGroup监听对象
     * @param mRadioGroup
     */
    protected void setmRadioGroup(RadioGroup mRadioGroup) {
        mRadioGroup.setOnCheckedChangeListener(this);
    }

    /**
     * RadioGroup监听事件
     * <P></P>
     * <p>点击RadioButton或转换相应的Fragment</p>
     * <p>原来的fragment停止，新的fragment（没有就创建）开始显示</p>
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0, len = group.getChildCount(); i < len; i++) {
            if (group.getChildAt(i).getId() == checkedId) {//选中RadioButton事件
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

    /**
     * 此方法给子类重写
     * @param title 标题
     */
    protected void onChanceFragment(String title) {

    }

    /**
     * 添加Fragment
     * <p>添加fragment，添加标题</p>
     */
    @Override
    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mTitleList.add(title);
    }

    /**
     * 添加布局文件fragment
     * @param id fragment在xml文件的id
     */
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
     * <p>使用FragmentTransaction显示position的fragment</p>
     * @param position 要显示的fragment位置
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

    /**
     * 使用Bundle保存fragmentIndex
     * @param outState Bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //保存当前显示的fragment下标
        outState.putInt("position", currFragmentIndex);
        super.onSaveInstanceState(outState);
    }

}
