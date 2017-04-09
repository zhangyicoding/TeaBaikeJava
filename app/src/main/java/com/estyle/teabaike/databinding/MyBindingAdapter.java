package com.estyle.teabaike.databinding;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.estyle.teabaike.R;

public class MyBindingAdapter {

    @BindingAdapter("imgUrl")// 布局中使用的属性名
    public static void loadImage(ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @BindingAdapter("webData")
    public static void loadWeb(WebView webView, String data) {
        webView.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);
    }

}
