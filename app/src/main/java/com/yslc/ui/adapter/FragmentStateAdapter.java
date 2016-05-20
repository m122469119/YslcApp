package com.yslc.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2016/5/20.
 * 这个适配器跟MyFragmentAdapter基本一样，但是修复了fragment数据不刷新问题
 */
public class FragmentStateAdapter extends FragmentStatePagerAdapter{
    private List<Fragment> list;
    private Fragment currentFragment;

    public FragmentStateAdapter(FragmentManager fm){
        super(fm);
    }

    public FragmentStateAdapter(FragmentManager fragmentManager,
                                List<Fragment> list){
        super(fragmentManager);
        this.list = list;
    }

    public Fragment getCurrentFragment(){
        return currentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentFragment = (Fragment)object;
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
