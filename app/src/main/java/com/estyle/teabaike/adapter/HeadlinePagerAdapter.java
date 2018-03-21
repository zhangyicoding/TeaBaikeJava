package com.estyle.teabaike.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.estyle.teabaike.activity.ContentActivity;
import com.estyle.teabaike.bean.HeadlineBean;

import java.util.ArrayList;
import java.util.List;

public class HeadlinePagerAdapter extends PagerAdapter implements View.OnClickListener {

    private Context mContext;
    private List<HeadlineBean.DataBean> mDatas;
    private List<ImageView> mViewlist;

    public HeadlinePagerAdapter(Context context, List<HeadlineBean.DataBean> datas) {
        this.mContext = context;
        this.mDatas = datas;
        if (datas.size() < 4) {
            datas.addAll(datas);
        }
        mViewlist = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            ImageView imageView = new ImageView(context);
            imageView.setId(i);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setOnClickListener(this);
            mViewlist.add(imageView);
            Glide.with(context)
                    .load(datas.get(i).getImage())
                    .into(imageView);
        }
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = position % mViewlist.size();
        container.addView(mViewlist.get(realPosition));
        return mViewlist.get(realPosition);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        int realPosition = position % mViewlist.size();
        container.removeView(mViewlist.get(realPosition));
    }

    @Override
    public void onClick(View v) {
        HeadlineBean.DataBean data = mDatas.get(v.getId());
        String id = data.getId();
        if (TextUtils.isEmpty(id)) {
            String link = data.getLink();
            id = link.substring(link.lastIndexOf("/") + 1);
        }
        ContentActivity.startActivity(mContext, Long.parseLong(id), true);
    }
}
