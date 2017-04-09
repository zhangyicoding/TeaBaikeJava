package com.estyle.teabaike.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.estyle.teabaike.R;
import com.estyle.teabaike.adapter.HeadlinePagerAdapter;
import com.estyle.teabaike.adapter.MainAdapter;
import com.estyle.teabaike.application.MyApplication;
import com.estyle.teabaike.bean.HeadlineBean;
import com.estyle.teabaike.callback.HeadlineHttpService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HeadlineHeaderView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private ViewPager headlineViewPager;
    private TextView headlineTextView;
    private PointView pointView;

    private int currentPosition;

    private List<HeadlineBean.DataBean> datas;
    private Subscription httpSubscription;
    private Subscription intervalSubscription;

    private MainAdapter parentAdapter;

    public HeadlineHeaderView(@NonNull Context context) {
        super(context);
        HeadlineHeaderView rootView = ((HeadlineHeaderView) inflate(getContext(),
                R.layout.view_headline_header, this));
        headlineViewPager = (ViewPager) rootView.findViewById(R.id.headline_view_pager);
        headlineTextView = (TextView) rootView.findViewById(R.id.headline_tv);
        pointView = (PointView) rootView.findViewById(R.id.point_view);
    }

    public void show(MainAdapter parentAdapter) {
        this.parentAdapter = parentAdapter;
        httpSubscription = ((MyApplication) ((Activity) getContext())
                .getApplication())
                .getRetrofit()
                .create(HeadlineHttpService.class)
                .getObservable()
                .subscribeOn(Schedulers.io())
                .map(func)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext);
    }

    // Databinding和Java8 Lambda冲突
    private Func1<HeadlineBean, List<HeadlineBean.DataBean>> func = new Func1<HeadlineBean,
            List<HeadlineBean.DataBean>>() {
        @Override
        public List<HeadlineBean.DataBean> call(HeadlineBean headlineBean) {
            return headlineBean.getData();
        }
    };

    // Databinding和Java8 Lambda冲突
    private Action1<List<HeadlineBean.DataBean>> onNext =
            new Action1<List<HeadlineBean.DataBean>>() {
                @Override
                public void call(List<HeadlineBean.DataBean> datas) {
                    HeadlineHeaderView.this.datas = datas;
                    pointView.setPointCount(datas.size());

                    headlineTextView.setText(datas.get(0).getTitle());

                    HeadlinePagerAdapter adapter = new HeadlinePagerAdapter(getContext(), datas);
                    headlineViewPager.setAdapter(adapter);
                    headlineViewPager.addOnPageChangeListener(HeadlineHeaderView.this);
                    parentAdapter.addHeaderView(HeadlineHeaderView.this);
                    startAutoPlay();
                }
            };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
        int realPosition = position % pointView.getPointCount();
        headlineTextView.setText(datas.get(realPosition).getTitle());
        pointView.setSelectedPosition(realPosition);
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
        intervalSubscription = Observable.interval(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action1);
    }

    // Databinding和Java8 Lambda冲突
    private Action1<Long> action1 = new Action1<Long>() {
        @Override
        public void call(Long aLong) {
            headlineViewPager.setCurrentItem(++currentPosition);
        }
    };

    public void onDestroy() {
        headlineViewPager.removeOnPageChangeListener(this);
        httpSubscription.unsubscribe();
        intervalSubscription.unsubscribe();
    }

}
