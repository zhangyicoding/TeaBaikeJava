package com.estyle.teabaike.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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
import com.estyle.teabaike.widget.HeadlineHeaderView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

public class MainFragment extends Fragment implements
        MainAdapter.OnItemClickListener, PullToRefreshBase.OnRefreshListener2 {

    private FragmentMainBinding binding;

    private int type;
    private int page = 1;
    private MainAdapter adapter;
    private HeadlineHeaderView headerView;
    private View rootView;
    private boolean isViewCreated;
    private Subscription subscription;

    @Inject
    RetrofitManager retrofitManager;

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
        TeaBaikeApplication.getApplication().getTeaBaikeComponent().inject(this);
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
            View footerView = LayoutInflater.from(getContext()).inflate(R.layout.view_footer, null);
            adapter.addFooterView(footerView);

            binding.mainPullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);
            binding.mainPullToRefresh.setOnRefreshListener(this);

            RecyclerView mainRecyclerView = binding.mainPullToRefresh.getRefreshableView();
            mainRecyclerView.setAdapter(adapter);
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
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        loadData(page = 1, true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        loadData(++page, false);
    }

    @Override
    public void onItemClick(int position) {
        ContentActivity.startActivity(getContext(), adapter.getItemId(position), true);
    }

    // 加载网络数据
    private void loadData(int page, final boolean isRefresh) {
        subscription = retrofitManager.loadMainData(type, page)
                .subscribe(new Action1<List<MainBean.DataBean>>() {
                    @Override
                    public void call(List<MainBean.DataBean> datas) {
                        if (isRefresh) {
                            adapter.refreshDatas(datas);
                        } else {
                            adapter.addDatas(datas);
                        }
                        binding.mainPullToRefresh.onRefreshComplete();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        binding.emptyView.setText(R.string.fail_connect);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (type == 0) {
            headerView.onDestroy();
        }
        subscription.unsubscribe();
    }
}
