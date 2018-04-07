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

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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
                .map(MainBean::getData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    // 头条数据
    public Observable<List<HeadlineBean.DataBean>> loadHeadlineData() {
        return mRetrofit
                .create(HeadlineHttpService.class)
                .getObservable()
                .map(HeadlineBean::getData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 详情页数据
    public Observable<ContentDataBean> loadContentData(long id) {
        return mRetrofit
                .create(ContentHttpService.class)
                .getObservable(id)
                .map(ContentBean::getData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 搜索页数据
    public Observable<List<MainBean.DataBean>> loadSearchData(String keyword, int page) {
        return mRetrofit
                .create(SearchHttpService.class)
                .getObservable(keyword, page)
                .map(MainBean::getData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
