package com.estyle.teabaike.manager;

import com.estyle.teabaike.bean.ContentBean;
import com.estyle.teabaike.bean.ContentDataBean;
import com.estyle.teabaike.bean.HeadlineBean;
import com.estyle.teabaike.bean.MainBean;
import com.estyle.teabaike.constant.Url;
import com.estyle.teabaike.retrofit.ContentHttpService;
import com.estyle.teabaike.retrofit.HeadlineHttpService;
import com.estyle.teabaike.retrofit.MainHttpService;
import com.estyle.teabaike.retrofit.SearchHttpService;
import com.estyle.teabaike.util.EstyleLog;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private Retrofit mRetrofit;

    public RetrofitManager() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Url.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    // 主页数据
    public Observable<List<MainBean.DataBean>> loadMainData(int type, int page) {
        MainHttpService service = mRetrofit.create(MainHttpService.class);
        Observable<MainBean> observable;
        if (type == 0) {
            observable = service.getHeadlineObservable(page);
        } else {
            observable = service.getMainObservable(type, page);
        }
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<MainBean, List<MainBean.DataBean>>() {
                    @Override
                    public List<MainBean.DataBean> apply(MainBean mainBean) throws Exception {
                        return mainBean.getData();
                    }
                });
    }


    // 头条数据
    public Observable<List<HeadlineBean.DataBean>> loadHeadlineData() {
        return mRetrofit
                .create(HeadlineHttpService.class)
                .getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<HeadlineBean, List<HeadlineBean.DataBean>>() {
                    @Override
                    public List<HeadlineBean.DataBean> apply(HeadlineBean headlineBean) throws Exception {
                        EstyleLog.e("net mgr", Thread.currentThread().getName());
                        return headlineBean.getData();
                    }
                });
    }

    // 详情页数据
    public Observable<ContentDataBean> loadContentData(long id) {
        return mRetrofit
                .create(ContentHttpService.class)
                .getObservable(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ContentBean, ContentDataBean>() {
                    @Override
                    public ContentDataBean apply(ContentBean contentBean) throws Exception {
                        return contentBean.getData();
                    }
                });
    }

    // 搜索页数据
    public Observable<List<MainBean.DataBean>> loadSearchData(String keyword, int page) {
        return mRetrofit
                .create(SearchHttpService.class)
                .getObservable(keyword, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<MainBean, List<MainBean.DataBean>>() {
                    @Override
                    public List<MainBean.DataBean> apply(MainBean mainBean) throws Exception {
                        return mainBean.getData();
                    }
                });
    }

}
