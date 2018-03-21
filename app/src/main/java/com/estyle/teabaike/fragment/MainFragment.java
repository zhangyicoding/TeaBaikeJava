package com.estyle.teabaike.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.estyle.teabaike.R;
import com.estyle.teabaike.activity.ContentActivity;
import com.estyle.teabaike.adapter.MainAdapter;
import com.estyle.teabaike.application.TeaBaikeApplication;
import com.estyle.teabaike.bean.MainBean;
import com.estyle.teabaike.databinding.FragmentMainBinding;
import com.estyle.teabaike.manager.RetrofitManager;
import com.estyle.teabaike.widget.FooterView;
import com.estyle.teabaike.widget.HeadlineHeaderView;
import com.estyle.teabaike.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainFragment extends Fragment implements MainAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, RecyclerView.OnLoadMoreListener {

    private FragmentMainBinding binding;

    private int type;
    private int page = 1;
    private MainAdapter adapter;
    private HeadlineHeaderView headerView;
    private View rootView;
    private boolean isViewCreated;

    @Inject
    RetrofitManager retrofitManager;

    private Disposable mDisposable;

    public static MainFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TeaBaikeApplication.getInstance().getTeaBaikeComponent().inject(this);
        if (getArguments() != null) {
            type = getArguments().getInt("type", 0);
        }

        adapter = new MainAdapter(getContext());
        adapter.setOnItemClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (!isViewCreated) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
            rootView = binding.getRoot();
            adapter.setEmptyView(binding.emptyView);

            if (type == 0) {
                headerView = new HeadlineHeaderView(getContext());
                adapter.addHeaderView(headerView);
            }
            FooterView footerView = new FooterView(getContext());
            adapter.addFooterView(footerView);

            binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
            binding.swipeRefreshLayout.setOnRefreshListener(this);

            binding.recyclerView.setOnLoadMoreListener(this);
            binding.recyclerView.setAdapter(adapter);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isViewCreated) {
            if (type == 0) {
                headerView.loadData();
            }
            loadData(page, false);
            isViewCreated = true;
        }
    }

    @Override
    public void onItemClick(int position) {
        ContentActivity.startActivity(getContext(), adapter.getItemId(position), true);
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
//        subscription = retrofitManager.loadMainData(type, page)
        mDisposable = retrofitManager.loadMainData(type, page)
                .subscribe(new Consumer<List<MainBean.DataBean>>() {
                    @Override
                    public void accept(List<MainBean.DataBean> dataBeans) throws Exception {
                        if (isRefresh) {
                            adapter.refreshDatas(dataBeans);
                            binding.swipeRefreshLayout.setRefreshing(false);
                        } else {
                            adapter.addDatas(dataBeans);
                            binding.recyclerView.isLoading = false;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
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
    public void onDestroy() {
        super.onDestroy();
        binding.recyclerView.removeOnScrollListener();
        if (type == 0) {
            headerView.onDestroy();
        }
        mDisposable.dispose();
    }

}
