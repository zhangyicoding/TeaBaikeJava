package com.estyle.teabaike.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.widget.Toast;

import com.estyle.teabaike.R;
import com.estyle.teabaike.adapter.MainPagerAdapter;
import com.estyle.teabaike.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    private ActionBarDrawerToggle toggle;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initView();
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);

        toggle = new ActionBarDrawerToggle(this, binding.mainDrawerLayout, binding.toolbar, 0, 0);
        toggle.syncState();
        binding.mainDrawerLayout.addDrawerListener(toggle);

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        binding.mainViewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.mainViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.mainDrawerLayout.removeDrawerListener(toggle);
        toggle = null;
    }

    @Override
    public void onBackPressed() {
        if (binding.mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.mainDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            exitApp();
        }
    }

    // 当前点击返回键的时间

    private long currentBackPressedTime;

    private void exitApp() {
        if (System.currentTimeMillis() - currentBackPressedTime > 2000) {
            currentBackPressedTime = System.currentTimeMillis();
            Toast.makeText(this, R.string.exit_tip, Toast.LENGTH_SHORT).show();
        } else {
            try {
                super.onBackPressed();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }
}
