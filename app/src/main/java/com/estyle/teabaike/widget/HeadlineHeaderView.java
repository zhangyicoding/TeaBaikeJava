package com.estyle.teabaike.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.estyle.teabaike.R;
import com.estyle.teabaike.activity.ContentActivity;
import com.estyle.teabaike.application.TeaBaikeApplication;
import com.estyle.teabaike.bean.HeadlineBean;
import com.estyle.teabaike.databinding.ViewHeadlineHeaderBinding;
import com.estyle.teabaike.manager.RetrofitManager;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class HeadlineHeaderView extends FrameLayout {

    private ViewHeadlineHeaderBinding binding;

    private List<HeadlineBean.DataBean> mDatas;

    private Disposable mHttpDisposable;

    @Inject
    RetrofitManager mNetworkProvider;

    public HeadlineHeaderView(@NonNull Context context) {
        super(context);
        TeaBaikeApplication.getInstance().getTeaBaikeComponent().inject(this);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.view_headline_header, this, true);
    }

    public void loadData() {
        mHttpDisposable = mNetworkProvider.loadHeadlineData()
                .doOnNext(dataBeans -> {
                    mDatas = dataBeans;
                    binding.pointView.setPointCount(mDatas.size());
                    binding.headlineTextView.setText(mDatas.get(0).getTitle());
                    binding.bannerView.setOnBannerSelectedListener(position -> {
                        binding.headlineTextView.setText(mDatas.get(position).getTitle());
                        binding.pointView.setSelectedPosition(position);
                    });
                    binding.bannerView.setOnBannerClickListener(position ->
                            ContentActivity.startActivity(getContext(),
                                    Long.parseLong(mDatas.get(position).getId()),
                                    true));
                })
                .concatMap(Observable::fromIterable)
                .map(HeadlineBean.DataBean::getImage)
                .toList()
                .subscribe(imagePathList -> binding.bannerView.loadImages(imagePathList,
                        (path, view) -> Glide.with(getContext())
                                .load(path)
                                .into(view)),
                        throwable -> Toast.makeText(getContext(),
                                R.string.fail_connect,
                                Toast.LENGTH_SHORT).show());
    }

    public void onDestroy() {
        mHttpDisposable.dispose();
        binding.bannerView.onDestroy();
    }

}
