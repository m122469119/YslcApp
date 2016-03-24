package com.yslc.ui.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 咨讯页面ViewPager FragmentAdapter
 * <p>广播重温Activity</p>
 * @author HH
 */
public class MyFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> list;

    public MyFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyFragmentAdapter(FragmentManager fragmentManager,
                             List<Fragment> list) {
        super(fragmentManager);
        this.list = list;
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
