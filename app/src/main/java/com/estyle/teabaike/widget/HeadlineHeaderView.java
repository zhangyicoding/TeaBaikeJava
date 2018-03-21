package com.estyle.teabaike.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.estyle.teabaike.R;
import com.estyle.teabaike.adapter.HeadlinePagerAdapter;
import com.estyle.teabaike.application.TeaBaikeApplication;
import com.estyle.teabaike.bean.HeadlineBean;
import com.estyle.teabaike.databinding.ViewHeadlineHeaderBinding;
import com.estyle.teabaike.manager.RetrofitManager;
import com.estyle.teabaike.manager.TimerManager;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

public class HeadlineHeaderView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private ViewHeadlineHeaderBinding binding;

    private int currentPosition;

    private List<HeadlineBean.DataBean> datas;
    private Subscription httpSubscription;
    private Subscription intervalSubscription;

    @Inject
    RetrofitManager retrofitManager;
    @Inject
    TimerManager timerManager;

    public HeadlineHeaderView(@NonNull Context context) {
        super(context);
        TeaBaikeApplication.getInstance().getTeaBaikeComponent().inject(this);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.view_headline_header, this, true);
    }

    public void loadData() {
        httpSubscription = retrofitManager.loadHeadlineData()
                .subscribe(new Action1<List<HeadlineBean.DataBean>>() {
                    @Override
                    public void call(List<HeadlineBean.DataBean> dataBean) {
                        datas = dataBean;
                        binding.pointView.setPointCount(datas.size());

                        binding.headlineTextView.setText(datas.get(0).getTitle());

                        HeadlinePagerAdapter adapter = new HeadlinePagerAdapter(getContext(),
                                datas);
                        binding.headlineViewPager.setAdapter(adapter);
                        binding.headlineViewPager.addOnPageChangeListener(HeadlineHeaderView.this);
                        startAutoPlay();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
        int realPosition = position % binding.pointView.getPointCount();
        binding.headlineTextView.setText(datas.get(realPosition).getTitle());
        binding.pointView.setSelectedPosition(realPosition);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_DRAGGING:// 拖拽状态，停止自动播放
                intervalSubscription.unsubscribe();
                break;
            case ViewPager.SCROLL_STATE_IDLE:// 松手后恢复自动播放
                if (intervalSubscription.isUnsubscribed()) {
                    startAutoPlay();
                }
                break;
        }

    }

    // 自动循环播放ViewPager
    public void startAutoPlay() {
        intervalSubscription = timerManager.loop()
                .subscribe(action1);
    }

    // Databinding和Java8 Lambda冲突
    private Action1<Long> action1 = new Action1<Long>() {
        @Override
        public void call(Long aLong) {
            binding.headlineViewPager.setCurrentItem(++currentPosition);
        }
    };

    public void onDestroy() {
        binding.headlineViewPager.removeOnPageChangeListener(this);
        httpSubscription.unsubscribe();
        intervalSubscription.unsubscribe();
    }

}
