package com.yslc.ui.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * 咨讯页面ViewPager FragmentAdapter
 * <p>广播重温Activity</p>
 * @author HH
 */
public class MyFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> list;
    private Fragment currentFragment;

    public MyFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyFragmentAdapter(FragmentManager fragmentManager,
                             List<Fragment> list) {
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
    public Fragment getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public int getCount() {
        return list.isEmpty() ? 0 : list.size();
    }

}
