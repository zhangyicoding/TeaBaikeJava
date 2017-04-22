package com.estyle.teabaike.application;

import android.app.Application;

import com.estyle.teabaike.dagger.component.DaggerTeaBaikeComponent;
import com.estyle.teabaike.dagger.component.TeaBaikeComponent;
import com.estyle.teabaike.dagger.module.DataModule;

public class TeaBaikeApplication extends Application {

    private static TeaBaikeApplication application;
    private TeaBaikeComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;
        initDagger();
    }

    private void initDagger() {
        component = DaggerTeaBaikeComponent
                .builder()
                .dataModule(new DataModule(this))
                .build();
    }

    public static TeaBaikeApplication getApplication() {
        return application;
    }

    public TeaBaikeComponent getTeaBaikeComponent() {
        return component;
    }

}