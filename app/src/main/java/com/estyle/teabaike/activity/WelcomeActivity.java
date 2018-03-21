package com.estyle.teabaike.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.estyle.teabaike.R;
import com.estyle.teabaike.adapter.WelcomePagerAdapter;
import com.estyle.teabaike.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends BaseActivity implements
        WelcomePagerAdapter.OnButtonClickListener, ViewPager.OnPageChangeListener {

    private ActivityWelcomeBinding binding;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, WelcomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);

        initView();
    }

    private void initView() {
        WelcomePagerAdapter adapter = new WelcomePagerAdapter(this);
        adapter.setOnButtonClickListener(this);
        binding.welcomeViewPager.setAdapter(adapter);
        binding.welcomeViewPager.addOnPageChangeListener(this);
        binding.pointView.setPointCount(adapter.getCount());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        binding.pointView.setSelectedPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onButtonClick(View v) {
        MainActivity.startActivity(this);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.welcomeViewPager.removeOnPageChangeListener(this);
    }
}
