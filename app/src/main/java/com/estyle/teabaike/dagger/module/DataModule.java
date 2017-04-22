package com.estyle.teabaike.dagger.module;

import android.content.Context;

import com.estyle.teabaike.manager.GreenDaoManager;
import com.estyle.teabaike.manager.RetrofitManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    private Context context;

    public DataModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    public RetrofitManager provideRetrofitManager() {
        return new RetrofitManager();
    }

    @Singleton
    @Provides
    public GreenDaoManager provideGreenDaoManager() {
        return new GreenDaoManager(context);
    }

}
