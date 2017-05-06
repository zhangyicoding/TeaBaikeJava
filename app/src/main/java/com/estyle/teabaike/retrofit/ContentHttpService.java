package com.estyle.teabaike.retrofit;


import com.estyle.teabaike.bean.ContentBean;
import com.estyle.teabaike.constant.Url;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ContentHttpService {

    @GET(Url.CONTENT_URL)
    Observable<ContentBean> getObservable(@Query(Url.ID) long id);

}
