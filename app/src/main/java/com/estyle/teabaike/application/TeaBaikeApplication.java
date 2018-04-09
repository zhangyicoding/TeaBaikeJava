package com.estyle.teabaike.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.estyle.teabaike.dagger.component.DaggerTeaBaikeComponent;
import com.estyle.teabaike.dagger.component.TeaBaikeComponent;
import com.estyle.teabaike.dagger.module.DataModule;
import com.estyle.teabaike.dagger.module.TimerModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class TeaBaikeApplication extends Application {

    private static TeaBaikeApplication sApplication;
    private RefWatcher mRefWatcher;
    private TeaBaikeComponent mComponent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(this);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sApplication = this;
        initLeakCanary();
        initDagger();
    }

    private void initLeakCanary() {
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            mRefWatcher = LeakCanary.install(this);
        }
    }

    private void initDagger() {
        mComponent = DaggerTeaBaikeComponent
                .builder()
                .dataModule(new DataModule(this))
                .timerModule(new TimerModule(this))
                .build();
    }

    public static TeaBaikeApplication getInstance() {
        return sApplication;
    }

    public TeaBaikeComponent getTeaBaikeComponent() {
        return mComponent;
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }
}