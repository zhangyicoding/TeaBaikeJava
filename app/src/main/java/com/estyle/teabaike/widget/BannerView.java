package com.estyle.teabaike.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * loadImages：设置图片路径并启动自动轮播，参数ImageCallback指定图片加载方式
 * setCircling：设置是否开启自动轮播，默认开启
 * isCircling：获取当前轮播状态
 * setOnBannerClickListener：设置图片点击监听
 * setOnBannerSelectedListener：设置图片位置监听
 * 在销毁生命周期中需要调用onDestroy()释放内存
 */
public class BannerView extends ViewPager {

    public static final String TAG = BannerView.class.getSimpleName();

    private static final int MSG_AUTO_CIRCLE = 0;

    private int mCurrentPosition;// ViewPager当前位置

    private boolean mIsCircling = true;// 是否正在循环
    private boolean mNeedCircleAgain = true;// 是否需要再次循环，防止重复叠加循环

    private long mDelayMillis = 2000L;// 轮播时间间隔

    private long mDownTime;// 手指按下的时间

    private int mRealImageCount;// 图片真实数量

    private BannerAdapter mAdapter;

    private ImageCallback mImageCallback;

    private OnCircleListener mOnCircleListener;
    private onBannerClickListener mOnBannerClickListener;

    private OnBannerSelectedListener mOnBannerSelectedListener;

    private WeakHandler mHandler;

    public BannerView(Context context) {
        super(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnBannerClickListener(onBannerClickListener onBannerClickListener) {
        mOnBannerClickListener = onBannerClickListener;
    }

    public void setOnBannerSelectedListener(OnBannerSelectedListener onBannerSelectedListener) {
        mOnBannerSelectedListener = onBannerSelectedListener;
    }

    // 获取是否正在轮播的状态
    public boolean isCircling() {
        return mIsCircling;
    }

    // 设置是否启用轮播
    public void setCircling(boolean circling) {
        mIsCircling = circling;
        if (mIsCircling) {
            startTimer();
        } else {
            stopTimer();
        }
    }

    // 设置图片路径和切换时间间隔
    public void loadImages(List<String> imagePathList, ImageCallback callback) {
        if (callback == null || imagePathList == null || imagePathList.size() == 0) return;

        mImageCallback = callback;
        mRealImageCount = imagePathList.size();
        mAdapter = new BannerAdapter(this, imagePathList);
        setAdapter(mAdapter);

        if (imagePathList.size() != 1) initCircleListener();
    }

    // 初始化无限自动轮播监听器
    private void initCircleListener() {
        mHandler = new WeakHandler(this);
        mOnCircleListener = new OnCircleListener(this);
        addOnPageChangeListener(mOnCircleListener);
        startTimer();
    }

    // 启动定时
    private void startTimer() {
        if (mHandler == null) return;

        if (mNeedCircleAgain) {
            mNeedCircleAgain = false;
            mHandler.sendEmptyMessageDelayed(MSG_AUTO_CIRCLE, mDelayMillis);
        }
    }

    // 停止定时
    private void stopTimer() {
        if (mHandler == null) return;

        mNeedCircleAgain = true;
        mHandler.removeMessages(MSG_AUTO_CIRCLE);
    }

    // 销毁生命周期中调用，释放内存
    public void onDestroy() {
        if (mImageCallback != null) mImageCallback = null;
        if (mOnBannerClickListener != null) mOnBannerClickListener = null;
        if (mOnBannerSelectedListener != null) mOnBannerSelectedListener = null;

        if (mAdapter != null) {
            mAdapter.mViewList.clear();
            mAdapter = null;
        }
        if (mHandler != null) {
            mHandler.removeMessages(MSG_AUTO_CIRCLE);
            mHandler = null;

            removeOnPageChangeListener(mOnCircleListener);
            mOnCircleListener = null;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownTime = System.currentTimeMillis();
                stopTimer();
                break;
            case MotionEvent.ACTION_UP:// ViewPager的child可点击时
                long upTime = System.currentTimeMillis();
                startTimer();
                if (upTime - mDownTime >= 500) return true;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:// ViewPager的child不可点击时
                startTimer();
                break;
        }
        return super.onTouchEvent(ev);
    }

    private static class WeakHandler extends Handler {

        private WeakReference<BannerView> mWeakReference;

        WeakHandler(BannerView bannerView) {
            mWeakReference = new WeakReference<>(bannerView);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_AUTO_CIRCLE) {
                BannerView bannerView = mWeakReference.get();
                if (bannerView == null) return;

                bannerView.setCurrentItem(++bannerView.mCurrentPosition);
                sendEmptyMessageDelayed(MSG_AUTO_CIRCLE, bannerView.mDelayMillis);
            }
        }
    }

    private static class OnCircleListener extends SimpleOnPageChangeListener {

        private BannerView mBannerView;

        OnCircleListener(BannerView bannerView) {
            mBannerView = bannerView;
        }

        @Override
        public void onPageSelected(int position) {
            mBannerView.mCurrentPosition = position;
            if (mBannerView.mOnBannerSelectedListener != null) {
                int realPosition = position % mBannerView.mRealImageCount;
                if (mBannerView.mRealImageCount == 2 || mBannerView.mRealImageCount == 3) {
                    realPosition %= mBannerView.mRealImageCount;
                }
                mBannerView.mOnBannerSelectedListener.onBannerSelected(realPosition);
            }
        }

    }

    private static class BannerAdapter extends PagerAdapter implements OnClickListener {

        private List<String> mImagePathList;
        private List<ImageView> mViewList;

        private BannerView mBannerView;

        BannerAdapter(BannerView bannerView, List<String> imagePathList) {
            mBannerView = bannerView;
            mImagePathList = imagePathList;

            // 受ViewPager预加载影响，轮播时备选child数量至少为4，否则报错
            if (mBannerView.mRealImageCount == 2 || mBannerView.mRealImageCount == 3) {
                mImagePathList.addAll(imagePathList);
            }

            mViewList = new ArrayList<>(mImagePathList.size());
            for (int i = 0; i < mImagePathList.size(); i++) {
                ImageView imageView = new ImageView(bannerView.getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                mBannerView.mImageCallback.onLoadImage(mImagePathList.get(i), imageView);

                if (mBannerView.mRealImageCount == 2 || mBannerView.mRealImageCount == 3) {
                    imageView.setId(i % mBannerView.mRealImageCount);
                } else {
                    imageView.setId(i);
                }

                imageView.setOnClickListener(this);
                mViewList.add(imageView);
            }
        }

        @Override
        public void onClick(View v) {
            if (mBannerView.mOnBannerClickListener == null) return;

            mBannerView.mOnBannerClickListener.onBannerClick(v.getId());
        }

        @Override
        public int getCount() {
            return mImagePathList.size() == 1 ? 1 : Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int realPosition = position % mViewList.size();
            container.addView(mViewList.get(realPosition));
            return mViewList.get(realPosition);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            int realPosition = position % mViewList.size();
            container.removeView(mViewList.get(realPosition));
        }

    }

    public interface ImageCallback {
        void onLoadImage(String path, ImageView view);
    }

    public interface onBannerClickListener {
        void onBannerClick(int position);
    }

    public interface OnBannerSelectedListener {
        void onBannerSelected(int position);
    }

}
