package com.estyle.teabaike.dagger.component;

import com.estyle.teabaike.activity.CollectionActivity;
import com.estyle.teabaike.activity.ContentActivity;
import com.estyle.teabaike.activity.SearchActivity;
import com.estyle.teabaike.activity.SplashActivity;
import com.estyle.teabaike.adapter.CollectionAdapter;
import com.estyle.teabaike.dagger.module.DataModule;
import com.estyle.teabaike.dagger.module.TimerModule;
import com.estyle.teabaike.fragment.MainFragment;
import com.estyle.teabaike.widget.HeadlineHeaderView;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DataModule.class, TimerModule.class})
public interface TeaBaikeComponent {

    void inject(MainFragment fragment);

    void inject(HeadlineHeaderView view);

    void inject(ContentActivity activity);

    void inject(SearchActivity activity);

    void inject(CollectionActivity activity);

    void inject(CollectionAdapter adapter);

    void inject(SplashActivity activity);

}
