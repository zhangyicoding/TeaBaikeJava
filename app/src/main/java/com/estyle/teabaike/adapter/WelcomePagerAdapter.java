package com.estyle.teabaike.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.estyle.teabaike.R;

import java.util.ArrayList;
import java.util.List;

public class WelcomePagerAdapter extends PagerAdapter {

    private List<View> mViewList;
    private OnButtonClickListener mListener;

    public WelcomePagerAdapter(Context context) {
        mViewList = new ArrayList<>();
        int[] imgIds = new int[]{R.drawable.welcome1, R.drawable.welcome2};
        View view;
        for (int imgId : imgIds) {
            view = new View(context);
            view.setBackgroundResource(imgId);
            mViewList.add(view);
        }
        view = LayoutInflater.from(context).inflate(R.layout.view_welcome3, null);
        mViewList.add(view);
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.mListener = listener;
        mViewList.get(mViewList.size() - 1)
                .findViewById(R.id.main_btn)
                .setOnClickListener(v -> mListener.onButtonClick(v));
    }

    public interface OnButtonClickListener {
        void onButtonClick(View v);
    }

}
