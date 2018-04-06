package com.estyle.teabaike.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.estyle.teabaike.R;
import com.estyle.teabaike.application.TeaBaikeApplication;
import com.estyle.teabaike.bean.HeadlineBean;
import com.estyle.teabaike.databinding.ViewHeadlineHeaderBinding;
import com.estyle.teabaike.manager.RetrofitManager;
import com.estyle.teabaike.util.EstyleLog;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

//public class HeadlineHeaderView extends FrameLayout implements ViewPager.OnPageChangeListener {
public class HeadlineHeaderView extends FrameLayout {

    private ViewHeadlineHeaderBinding binding;

//    private int mCurrentPosition;

    private List<HeadlineBean.DataBean> mDatas;

    private Disposable mHttpDisposable;
//    private Disposable mIntervalDisposable;

    @Inject
    RetrofitManager mNetworkProvider;
//    @Inject
//    TimerManager mTimerManager;

    public HeadlineHeaderView(@NonNull Context context) {
        super(context);
        TeaBaikeApplication.getInstance().getTeaBaikeComponent().inject(this);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.view_headline_header, this, true);
    }

    public void loadData() {
        mHttpDisposable = mNetworkProvider.loadHeadlineData()
                .doOnNext(new Consumer<List<HeadlineBean.DataBean>>() {
                    @Override
                    public void accept(List<HeadlineBean.DataBean> dataBeans) throws Exception {
                        EstyleLog.e("get datas", Thread.currentThread().getName());
                        mDatas = dataBeans;
                        binding.pointView.setPointCount(mDatas.size());
                        binding.headlineTextView.setText(mDatas.get(0).getTitle());
                    }
                })
                .subscribeOn(Schedulers.io())
                .concatMap(new Function<List<HeadlineBean.DataBean>, ObservableSource<HeadlineBean.DataBean>>() {
                    @Override
                    public ObservableSource<HeadlineBean.DataBean> apply(List<HeadlineBean.DataBean> dataBeans) throws Exception {
                        EstyleLog.e("list to datas", Thread.currentThread().getName());
                        return Observable.fromIterable(dataBeans);
                    }
                })
                .map(new Function<HeadlineBean.DataBean, String>() {
                    @Override
                    public String apply(HeadlineBean.DataBean dataBean) throws Exception {
                        EstyleLog.e("data to string imgurl", Thread.currentThread().getName());
                        return dataBean.getImage();
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                               @Override
                               public void accept(List<String> imagePathList) throws Exception {
                                   EstyleLog.e("create imglist", Thread.currentThread().getName());
                                   binding.bannerView.loadImages(imagePathList,
                                           new BannerView.ImageCallback() {
                                               @Override
                                               public void onLoadImage(String path, ImageView view) {
                                                   Glide.with(view.getContext())
                                                           .load(path)
                                                           .into(view);
                                               }
                                           });
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                EstyleLog.e("error", throwable.getMessage());
                            }
                        });
//                .subscribe(new Consumer<List<HeadlineBean.DataBean>>() {
//                    @Override
//                    public void accept(List<HeadlineBean.DataBean> dataBeans) throws Exception {
//                        mDatas = dataBeans;
//                        binding.pointView.setPointCount(mDatas.size());
//
//                        binding.headlineTextView.setText(mDatas.get(0).getTitle());
//
//
////                        HeadlinePagerAdapter adapter = new HeadlinePagerAdapter(getContext(),
////                                mDatas);
////                        binding.headlineViewPager.setAdapter(adapter);
////                        binding.headlineViewPager.addOnPageChangeListener(HeadlineHeaderView.this);
////                        startAutoPlay();
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//
//                    }
//                });

    }

//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//    }

//    @Override
//    public void onPageSelected(int position) {
//        mCurrentPosition = position;
//        int realPosition = position % binding.pointView.getPointCount();
//        binding.headlineTextView.setText(mDatas.get(realPosition).getTitle());
//        binding.pointView.setSelectedPosition(realPosition);
//    }

//    @Override
//    public void onPageScrollStateChanged(int state) {
//        switch (state) {
//            case ViewPager.SCROLL_STATE_DRAGGING:// 拖拽状态，停止自动播放
//                mIntervalDisposable.dispose();
//                break;
//            case ViewPager.SCROLL_STATE_IDLE:// 松手后恢复自动播放
//                if (mIntervalDisposable.isDisposed()) {
//                    startAutoPlay();
//                }
//                break;
//        }

//    }

    // 自动循环播放ViewPager
//    public void startAutoPlay() {
//        mIntervalDisposable = mTimerManager.loop()
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                        binding.headlineViewPager.setCurrentItem(++mCurrentPosition);
//                    }
//                });
//
//    }

    public void onDestroy() {
//        binding.headlineViewPager.removeOnPageChangeListener(this);
//        mIntervalDisposable.dispose();
        mHttpDisposable.dispose();
    }

}
