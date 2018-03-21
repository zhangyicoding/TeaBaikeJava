package com.estyle.teabaike.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.estyle.teabaike.application.TeaBaikeApplication;
import com.estyle.teabaike.manager.TimerManager;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.estyle.teabaike.manager.TimerManager.CONFIG_NAME;


public class SplashActivity extends BaseActivity {

    @Inject
    TimerManager mTimerManager;

    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TeaBaikeApplication.getInstance().getTeaBaikeComponent().inject(this);

        mDisposable = mTimerManager.splash()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isFirstLogin) throws Exception {
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
        mDisposable.dispose();
    }
}
