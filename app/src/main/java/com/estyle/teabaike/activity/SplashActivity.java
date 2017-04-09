package com.estyle.teabaike.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.estyle.teabaike.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;


public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_splash);

        subscription = Observable
                .timer(2, TimeUnit.SECONDS)
                .map(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        sharedPreferences = getSharedPreferences("app_config",
                                Context.MODE_PRIVATE);
                        return sharedPreferences.getBoolean("is_first_login", true);
                    }
                })
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean isFirstLogin) {
                        if (isFirstLogin) {
                            WelcomeActivity.startActivity(SplashActivity.this);
                            sharedPreferences.edit()
                                    .putBoolean("is_first_login", false)
                                    .commit();
                        } else {
                            MainActivity.startActivity(SplashActivity.this);
                        }
                        finish();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }
}
