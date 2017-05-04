package com.estyle.teabaike.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.estyle.teabaike.R;
import com.estyle.teabaike.adapter.MainAdapter;
import com.estyle.teabaike.application.TeaBaikeApplication;
import com.estyle.teabaike.bean.MainBean;
import com.estyle.teabaike.databinding.ActivitySearchBinding;
import com.estyle.teabaike.manager.RetrofitManager;
import com.estyle.teabaike.widget.FooterView;
import com.estyle.teabaike.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

public class SearchActivity extends AppCompatActivity implements
        MainAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        RecyclerView.OnLoadMoreListener {

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

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        binding.swipeRefreshLayout.setOnRefreshListener(this);

        adapter = new MainAdapter(this);
        adapter.setEmptyView(binding.emptyView);
        adapter.setOnItemClickListener(this);

        FooterView footerView = new FooterView(this);
        adapter.addFooterView(footerView);

        binding.recyclerView.setOnLoadMoreListener(this);
        binding.recyclerView.setAdapter(adapter);
    }

    private void initData() {
        keyword = getIntent().getStringExtra("keyword");
        getSupportActionBar().setTitle(keyword);

        loadData(page, false);
    }

    @Override
    public void onItemClick(int position) {
        ContentActivity.startActivity(this, adapter.getItemId(position), true);
    }

    @Override
    public void onRefresh() {
        loadData(page = 1, true);
    }

    @Override
    public void onLoadMore() {
        loadData(++page, false);
    }

    // 加载网络数据
    private void loadData(int page, final boolean isRefresh) {
        subscription = retrofitManager.loadSearchData(keyword, page)
                .subscribe(new Action1<List<MainBean.DataBean>>() {
                    @Override
                    public void call(List<MainBean.DataBean> datas) {
                        if (isRefresh) {
                            adapter.refreshDatas(datas);
                            binding.swipeRefreshLayout.setRefreshing(false);
                        } else {
                            adapter.addDatas(datas);
                            binding.recyclerView.isLoading = false;
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        binding.emptyView.setText(R.string.fail_connect);
                        if (isRefresh) {
                            binding.swipeRefreshLayout.setRefreshing(false);
                        } else {
                            binding.recyclerView.isLoading = false;
                        }
                    }
                });
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
    protected void onDestroy() {
        super.onDestroy();
        binding.recyclerView.removeOnScrollListener();
        subscription.unsubscribe();
    }

}
