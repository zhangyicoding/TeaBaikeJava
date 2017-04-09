package com.estyle.teabaike.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.estyle.teabaike.R;

import java.util.ArrayList;
import java.util.List;

public class WelcomePagerAdapter extends PagerAdapter implements View.OnClickListener {

    private int[] imgIds = new int[]{R.drawable.welcome1, R.drawable.welcome2};
    private List<View> viewList;
    private OnButtonClickListener listener;

    public WelcomePagerAdapter(Context context) {
        viewList = new ArrayList<>();
        View view;
        for (int i = 0; i < imgIds.length; i++) {
            view = new View(context);
            view.setBackgroundResource(imgIds[i]);
            viewList.add(view);
        }
        view = LayoutInflater.from(context).inflate(R.layout.view_welcome3, null);
        viewList.add(view);
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public void onClick(View v) {
        listener.onButtonClick(v);
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;
        viewList
                .get(viewList.size() - 1)
                .findViewById(R.id.main_btn)
                .setOnClickListener(this);
    }

    public static interface OnButtonClickListener {
        public void onButtonClick(View v);
    }

}
