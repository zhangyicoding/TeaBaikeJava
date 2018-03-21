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

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class HeadlineHeaderView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private ViewHeadlineHeaderBinding binding;

    private int currentPosition;

    private List<HeadlineBean.DataBean> datas;

    private Disposable mHttpDisposable;
    private Disposable mIntervalDisposable;

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
        mHttpDisposable = retrofitManager.loadHeadlineData()
                .subscribe(new Consumer<List<HeadlineBean.DataBean>>() {
                    @Override
                    public void accept(List<HeadlineBean.DataBean> dataBeans) throws Exception {
                        datas = dataBeans;
                        binding.pointView.setPointCount(datas.size());

                        binding.headlineTextView.setText(datas.get(0).getTitle());

                        HeadlinePagerAdapter adapter = new HeadlinePagerAdapter(getContext(),
                                datas);
                        binding.headlineViewPager.setAdapter(adapter);
                        binding.headlineViewPager.addOnPageChangeListener(HeadlineHeaderView.this);
                        startAutoPlay();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

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
                mIntervalDisposable.dispose();
                break;
            case ViewPager.SCROLL_STATE_IDLE:// 松手后恢复自动播放
                if (mIntervalDisposable.isDisposed()) {
                    startAutoPlay();
                }
                break;
        }

    }

    // 自动循环播放ViewPager
    public void startAutoPlay() {
        mIntervalDisposable = timerManager.loop()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        binding.headlineViewPager.setCurrentItem(++currentPosition);
                    }
                });

    }

    public void onDestroy() {
        binding.headlineViewPager.removeOnPageChangeListener(this);
        mHttpDisposable.dispose();
        mIntervalDisposable.dispose();
    }

}
