package com.estyle.teabaike.manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class TimerManager {

    public static final String CONFIG_NAME = "config";

    private Context context;

    public TimerManager(Context context) {
        this.context = context;
    }

    // 引导页休眠
    public Observable<Boolean> splash() {
        return Observable
                .timer(2, TimeUnit.SECONDS)
                .map(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        SharedPreferences sharedPreferences =
                                context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
                        return sharedPreferences.getBoolean("is_first_login", true);
                    }
                });
    }

    // 头条循环播放
    public Observable<Long> loop() {
        return Observable
                .interval(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

}
