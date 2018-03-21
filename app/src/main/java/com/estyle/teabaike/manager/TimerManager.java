package com.estyle.teabaike.manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

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
                .map(new Function<Long, Boolean>() {
                    @Override
                    public Boolean apply(Long aLong) throws Exception {
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
