package com.estyle.teabaike.manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class TimerManager {

    public static final String CONFIG_NAME = "config";

    private Context mContext;

    public TimerManager(Context context) {
        this.mContext = context;
    }

    // 引导页休眠
    public Observable<Boolean> splash() {
        return Observable
                .timer(2, TimeUnit.SECONDS)
                .map(aLong -> {
                    SharedPreferences sharedPreferences =
                            mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
                    return sharedPreferences.getBoolean("is_first_login", true);
                });
    }

}
