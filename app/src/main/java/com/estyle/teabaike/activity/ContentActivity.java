package com.estyle.teabaike.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.estyle.teabaike.R;
import com.estyle.teabaike.application.TeaBaikeApplication;
import com.estyle.teabaike.bean.ContentDataBean;
import com.estyle.teabaike.databinding.ActivityContentBinding;
import com.estyle.teabaike.manager.GreenDaoManager;
import com.estyle.teabaike.manager.RetrofitManager;

import java.lang.reflect.Method;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

public class ContentActivity extends BaseActivity {

    private Snackbar mSnackbar;

    private ActivityContentBinding binding;

    private ContentDataBean mData;

    @Inject
    RetrofitManager mNetworkProvider;
    @Inject
    GreenDaoManager mDBProvider;

    private Disposable mDisposable;

    public static void startActivity(Context context, long id, boolean isOnline) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("is_online", isOnline);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TeaBaikeApplication.getInstance().getTeaBaikeComponent().inject(this);
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
        boolean isOnline = getIntent().getBooleanExtra("is_online", false);
        if (isOnline) {
            mDisposable = mNetworkProvider.loadContentData(id)
                    .subscribe(contentDataBean -> {
                                mData = contentDataBean;
                                binding.setBean(mData);
                            },
                            throwable -> Toast.makeText(ContentActivity.this,
                                    R.string.fail_connect,
                                    Toast.LENGTH_SHORT).show());
        } else {
            mData = mDBProvider.queryCollectionDataById(id);
            binding.setBean(mData);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_content, menu);

        Class clazz = menu.getClass();
        Method setOptionalIconsVisibleMethod;
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
        if (mData != null) {
            showTip(R.string.share_successful);
        }
    }

    // 收藏文章
    private void collect() {
        if (mData != null) {
            mDBProvider.collectData(mData);
            showTip(R.string.collect_successful);
        }
    }

    // 展示Snackbar
    private void showTip(int resId) {
        if (mSnackbar == null) {
            mSnackbar = Snackbar.make(binding.getRoot(), resId, Snackbar.LENGTH_SHORT);
            mSnackbar.getView().setBackgroundResource(R.color.colorAccent);
        } else {
            mSnackbar.setText(resId);
        }
        mSnackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }

        binding.contentWebView.removeAllViews();
        binding.contentWebView.destroy();
    }
}
