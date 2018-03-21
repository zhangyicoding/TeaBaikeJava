package com.estyle.teabaike.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class SearchActivity extends BaseActivity implements MainAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, RecyclerView.OnLoadMoreListener {

    private ActivitySearchBinding binding;

    private MainAdapter mAdapter;

    private int mPage = 1;
    private String mKeyword;

    @Inject
    RetrofitManager mNetworkProvider;

    private Disposable mDisposable;

    public static void startActivity(Context context, String keyword) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("keyword", keyword);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TeaBaikeApplication.getInstance().getTeaBaikeComponent().inject(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        initView();
        initData();
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        binding.swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new MainAdapter(this);
        mAdapter.setEmptyView(binding.emptyView);
        mAdapter.setOnItemClickListener(this);

        FooterView footerView = new FooterView(this);
        mAdapter.addFooterView(footerView);

        binding.recyclerView.setOnLoadMoreListener(this);
        binding.recyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        mKeyword = getIntent().getStringExtra("keyword");
        getSupportActionBar().setTitle(mKeyword);

        loadData(mPage, false);
    }

    @Override
    public void onItemClick(int position) {
        ContentActivity.startActivity(this, mAdapter.getItemId(position), true);
    }

    @Override
    public void onRefresh() {
        loadData(mPage = 1, true);
    }

    @Override
    public void onLoadMore() {
        loadData(++mPage, false);
    }

    // 加载网络数据
    private void loadData(int page, final boolean isRefresh) {
        mDisposable = mNetworkProvider.loadSearchData(mKeyword, page)
                .subscribe(new Consumer<List<MainBean.DataBean>>() {
                    @Override
                    public void accept(List<MainBean.DataBean> dataBeans) throws Exception {
                        if (isRefresh) {
                            mAdapter.refreshDatas(dataBeans);
                            binding.swipeRefreshLayout.setRefreshing(false);
                        } else {
                            mAdapter.addDatas(dataBeans);
                            binding.recyclerView.mIsLoading = false;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        binding.emptyView.setText(R.string.fail_connect);
                        if (isRefresh) {
                            binding.swipeRefreshLayout.setRefreshing(false);
                        } else {
                            binding.recyclerView.mIsLoading = false;
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
        mDisposable.dispose();
    }

}
