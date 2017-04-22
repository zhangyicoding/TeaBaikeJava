package com.estyle.teabaike.dagger.module;

import android.content.Context;

import com.estyle.teabaike.manager.TimerManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TimerModule {

    private Context context;

    public TimerModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    public TimerManager provideTimerManager() {
        return new TimerManager(context);
    }

}
