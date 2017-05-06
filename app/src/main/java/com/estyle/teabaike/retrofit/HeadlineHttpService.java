package com.estyle.teabaike.retrofit;


import com.estyle.teabaike.bean.HeadlineBean;
import com.estyle.teabaike.constant.Url;

import retrofit2.http.GET;
import rx.Observable;

public interface HeadlineHttpService {

    @GET(Url.HEADLINE_HEADER_URL)
    Observable<HeadlineBean> getObservable();

}
