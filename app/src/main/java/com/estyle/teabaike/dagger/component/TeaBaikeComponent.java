package com.estyle.teabaike.dagger.component;

import com.estyle.teabaike.activity.CollectionActivity;
import com.estyle.teabaike.activity.ContentActivity;
import com.estyle.teabaike.activity.SearchActivity;
import com.estyle.teabaike.adapter.CollectionAdapter;
import com.estyle.teabaike.dagger.module.DataModule;
import com.estyle.teabaike.fragment.MainFragment;
import com.estyle.teabaike.widget.HeadlineHeaderView;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DataModule.class})
public interface TeaBaikeComponent {

    public void inject(MainFragment fragment);

    public void inject(HeadlineHeaderView view);

    public void inject(ContentActivity activity);

    public void inject(SearchActivity activity);

    public void inject(CollectionActivity activity);

    public void inject(CollectionAdapter adapter);

}
