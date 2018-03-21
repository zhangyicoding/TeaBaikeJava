package com.estyle.teabaike.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.estyle.teabaike.bean.ChannelBean;
import com.estyle.teabaike.constant.Url;
import com.estyle.teabaike.fragment.MainFragment;

import java.util.ArrayList;
import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<ChannelBean> mChannelList;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        mChannelList = new ArrayList<>();
        for (int i = 0; i < Url.TYPES.length; i++) {
            mChannelList.add(new ChannelBean(Url.TITLES[i], MainFragment.newInstance(Url.TYPES[i])));
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mChannelList.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return mChannelList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mChannelList.get(position).getChannel();
    }
}
