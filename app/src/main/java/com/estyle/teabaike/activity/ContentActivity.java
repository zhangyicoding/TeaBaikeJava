package com.estyle.teabaike.activity;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.estyle.teabaike.R;
import com.estyle.teabaike.application.MyApplication;
import com.estyle.teabaike.bean.CollectionBean;
import com.estyle.teabaike.bean.CollectionBeanDao;
import com.estyle.teabaike.bean.ContentBean;
import com.estyle.teabaike.callback.ContentHttpService;
import com.estyle.teabaike.databinding.ActivityContentBinding;

import org.greenrobot.greendao.query.Query;

import java.lang.reflect.Method;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ContentActivity extends AppCompatActivity {

    private Snackbar snackbar;

    private ActivityContentBinding binding;

    private ContentBean.DataBean data;
    private Subscription subscription;

    public static void startActivity(Context context, long id) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_content);

        initView();
        initData();
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        long id = getIntent().getLongExtra("id", 0);
        subscription = ((MyApplication) getApplication())
                .getRetrofit()
                .create(ContentHttpService.class)
                .getObservable(id)
                .subscribeOn(Schedulers.io())
                .flatMap(func)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    // Databinding和Java8 Lambda冲突
    private Func1<ContentBean, Observable<ContentBean.DataBean>> func = new Func1<ContentBean, Observable<ContentBean.DataBean>>() {
        @Override
        public Observable<ContentBean.DataBean> call(ContentBean contentBean) {
            return Observable.just(contentBean.getData());
        }
    };

    // Databinding和Java8 Lambda冲突
    private Action1<ContentBean.DataBean> onNext = new Action1<ContentBean.DataBean>() {
        @Override
        public void call(ContentBean.DataBean dataBean) {
            data = dataBean;
            binding.setBean(dataBean);
        }
    };

    // Databinding和Java8 Lambda冲突
    private Action1<Throwable> onError = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Toast.makeText(ContentActivity.this, R.string.fail_connect, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_content, menu);

        Class clazz = menu.getClass();
        Method setOptionalIconsVisibleMethod = null;
        try {
            setOptionalIconsVisibleMethod = clazz.getDeclaredMethod("setOptionalIconsVisible"
                    , boolean.class);
            setOptionalIconsVisibleMethod.setAccessible(true);
            setOptionalIconsVisibleMethod.invoke(menu, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_item:
                share();
                break;
            case R.id.collect_item:
                collect();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 分享文章
    private void share() {
        showTip(R.string.share_successful);
    }

    // 收藏文章
    private void collect() {
        if (data != null) {
            CollectionBeanDao collectionDao = ((MyApplication) getApplication()).getDaoSession()
                    .getCollectionBeanDao();
            Query<CollectionBean> query = collectionDao.queryBuilder()
                    .where(CollectionBeanDao.Properties.Id.eq(data.getId()))
                    .build();
            if (query.unique() == null) {
                CollectionBean collection = new CollectionBean(Long.parseLong(data.getId())
                        , System.currentTimeMillis()
                        , data.getTitle()
                        , data.getSource()
                        , data.getCreate_time()
                        , data.getAuthor());
                collectionDao.insert(collection);
            }
            showTip(R.string.collect_successful);
        }
    }

    // 展示Snackbar
    private void showTip(int resId) {
        if (snackbar == null) {
            snackbar = Snackbar.make(binding.getRoot(), resId, Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundResource(R.color.colorAccent);
        } else {
            snackbar.setText(resId);
        }
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }
}
