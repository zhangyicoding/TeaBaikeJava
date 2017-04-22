package com.estyle.teabaike.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.estyle.teabaike.R;
import com.estyle.teabaike.adapter.MainAdapter;
import com.estyle.teabaike.application.TeaBaikeApplication;
import com.estyle.teabaike.bean.MainBean;
import com.estyle.teabaike.databinding.ActivitySearchBinding;
import com.estyle.teabaike.manager.RetrofitManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

public class SearchActivity extends AppCompatActivity implements
        MainAdapter.OnItemClickListener, PullToRefreshBase.OnRefreshListener2 {

    private ActivitySearchBinding binding;

    private MainAdapter adapter;

    private int page = 1;
    private String keyword;
    private Subscription subscription;

    @Inject
    RetrofitManager retrofitManager;

    public static void startActivity(Context context, String keyword) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("keyword", keyword);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TeaBaikeApplication.getApplication().getTeaBaikeComponent().inject(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        initView();
        initData();
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView searchRecyclerView = binding.searchPullToRefresh.getRefreshableView();
        binding.searchPullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);
        binding.searchPullToRefresh.setOnRefreshListener(this);

        adapter = new MainAdapter(this);
        adapter.setEmptyView(binding.emptyView);
        adapter.setOnItemClickListener(this);
        searchRecyclerView.setAdapter(adapter);
    }

    private void initData() {
        keyword = getIntent().getStringExtra("keyword");
        getSupportActionBar().setTitle(keyword);

        loadData(page, false);
    }

    // 加载网络数据
    private void loadData(int page, final boolean isRefresh) {
        subscription = retrofitManager.loadSearchData(keyword, page)
                .subscribe(new Action1<List<MainBean.DataBean>>() {
                    @Override
                    public void call(List<MainBean.DataBean> dataBean) {
                        if (isRefresh) {
                            adapter.refreshDatas(dataBean);
                        } else {
                            adapter.addDatas(dataBean);
                        }
                        binding.searchPullToRefresh.onRefreshComplete();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        ContentActivity.startActivity(this, adapter.getItemId(position), true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        loadData(page = 1, true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        loadData(++page, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }
}
