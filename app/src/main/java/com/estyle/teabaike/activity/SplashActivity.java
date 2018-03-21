package com.estyle.teabaike.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.estyle.teabaike.application.TeaBaikeApplication;
import com.estyle.teabaike.manager.TimerManager;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

import static com.estyle.teabaike.manager.TimerManager.CONFIG_NAME;


public class SplashActivity extends BaseActivity {

    private Subscription subscription;

    @Inject
    TimerManager timerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TeaBaikeApplication.getInstance().getTeaBaikeComponent().inject(this);

        subscription = timerManager.splash()
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean isFirstLogin) {
                        if (isFirstLogin) {
                            WelcomeActivity.startActivity(SplashActivity.this);
                            SharedPreferences sharedPreferences = getSharedPreferences(CONFIG_NAME,
                                    Context.MODE_PRIVATE);
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
