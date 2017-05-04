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

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RetrofitManager {

    private Retrofit retrofit;

    public RetrofitManager() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Url.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    // 主页数据
    public Observable<List<MainBean.DataBean>> loadMainData(int type, int page) {
        MainHttpService service = retrofit.create(MainHttpService.class);
        Observable<MainBean> observable;
        if (type == 0) {
            observable = service.getHeadlineObservable(page);
        } else {
            observable = service.getMainObservable(type, page);
        }
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<MainBean, List<MainBean.DataBean>>() {
                    @Override
                    public List<MainBean.DataBean> call(MainBean mainBean) {
                        return mainBean.getData();
                    }
                });
    }


    // 头条数据
    public Observable<List<HeadlineBean.DataBean>> loadHeadlineData() {
        return retrofit
                .create(HeadlineHttpService.class)
                .getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<HeadlineBean, List<HeadlineBean.DataBean>>() {
                    @Override
                    public List<HeadlineBean.DataBean> call(HeadlineBean headlineBean) {
                        return headlineBean.getData();
                    }
                });
    }

    // 详情页数据
    public Observable<ContentDataBean> loadContentData(long id) {
        return retrofit
                .create(ContentHttpService.class)
                .getObservable(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ContentBean, ContentDataBean>() {
                    @Override
                    public ContentDataBean call(ContentBean contentBean) {
                        return contentBean.getData();
                    }
                });
    }

    // 搜索页数据
    public Observable<List<MainBean.DataBean>> loadSearchData(String keyword, int page) {
        return retrofit
                .create(SearchHttpService.class)
                .getObservable(keyword, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<MainBean, List<MainBean.DataBean>>() {
                    @Override
                    public List<MainBean.DataBean> call(MainBean mainBean) {
                        return mainBean.getData();
                    }
                });
    }

}
