package com.estyle.teabaike.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.estyle.teabaike.R;
import com.estyle.teabaike.activity.ContentActivity;
import com.estyle.teabaike.adapter.MainAdapter;
import com.estyle.teabaike.application.TeaBaikeApplication;
import com.estyle.teabaike.databinding.FragmentMainBinding;
import com.estyle.teabaike.manager.RetrofitManager;
import com.estyle.teabaike.widget.FooterView;
import com.estyle.teabaike.widget.HeadlineHeaderView;
import com.estyle.teabaike.widget.RecyclerView;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

public class MainFragment extends Fragment implements MainAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, RecyclerView.OnLoadMoreListener {

    private FragmentMainBinding binding;

    private int mType;
    private int mPage = 1;
    private MainAdapter mAdapter;
    private HeadlineHeaderView mHeaderview;
    private View mRootView;
    private boolean mIsViewCreated;

    @Inject
    RetrofitManager mNetworkProvider;

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
            mType = getArguments().getInt("type", 0);
        }

        mAdapter = new MainAdapter(getContext());
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (!mIsViewCreated) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
            mRootView = binding.getRoot();
            mAdapter.setEmptyView(binding.emptyView);

            if (mType == 0) {
                mHeaderview = new HeadlineHeaderView(getContext());
                mAdapter.addHeaderView(mHeaderview);
            }
            FooterView footerView = new FooterView(getContext());
            mAdapter.addFooterView(footerView);

            binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
            binding.swipeRefreshLayout.setOnRefreshListener(this);

            binding.recyclerView.setOnLoadMoreListener(this);
            binding.recyclerView.setAdapter(mAdapter);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!mIsViewCreated) {
            if (mType == 0) {
                mHeaderview.loadData();
            }
            loadData(mPage, false);
            mIsViewCreated = true;
        }
    }

    @Override
    public void onItemClick(int position) {
        ContentActivity.startActivity(getContext(), mAdapter.getItemId(position), true);
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
        mDisposable = mNetworkProvider.loadMainData(mType, page)
                .subscribe(dataBeans -> {
                            if (isRefresh) {
                                mAdapter.refreshDatas(dataBeans);
                                binding.swipeRefreshLayout.setRefreshing(false);
                            } else {
                                mAdapter.addDatas(dataBeans);
                                binding.recyclerView.mIsLoading = false;
                            }
                        },
                        throwable -> {
                            binding.emptyView.setText(R.string.fail_connect);
                            if (isRefresh) {
                                binding.swipeRefreshLayout.setRefreshing(false);
                            } else {
                                binding.recyclerView.mIsLoading = false;
                            }
                        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.recyclerView.removeOnScrollListener();
        if (mType == 0) {
            mHeaderview.onDestroy();
        }
        mDisposable.dispose();
    }

}
