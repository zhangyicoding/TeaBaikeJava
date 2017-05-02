package com.estyle.teabaike.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.estyle.teabaike.R;
import com.estyle.teabaike.bean.MainBean;
import com.estyle.teabaike.databinding.ItemMainBinding;
import com.estyle.teabaike.util.EstyleLog;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private Context context;
    private List<MainBean.DataBean> datas;

    private View emptyView;
    private List<View> headerList;
    private List<View> footerList;
    private int headerCount;
    private int footerCount;

    private OnItemClickListener onItemClickListener;

    public MainAdapter(Context context) {
        this.context = context;
        datas = new ArrayList<>();
    }

    // 刷新数据
    public void refreshDatas(List<MainBean.DataBean> datas) {
        this.datas.clear();
        addDatas(datas);

    }

    // 添加数据
    public void addDatas(List<MainBean.DataBean> datas) {
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    // 添加头视图
    public void addHeaderView(View headerView) {
        if (headerList == null) {
            headerList = new ArrayList<>();
        }
        headerList.add(headerView);
        headerCount = headerList.size();
    }

    // 添加w尾视图
    public void addFooterView(View footerView) {
        if (footerList == null) {
            footerList = new ArrayList<>();
        }
        footerList.add(footerView);
        footerCount = footerList.size();
    }

    // 设置空视图
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType < headerCount) {
            View headerView = headerList.get(viewType);
            holder = new HeaderViewHolder(headerView);
        } else if (viewType > headerCount) {
            View footerView = footerList.get(viewType - headerCount - datas.size());
            holder = new FooterViewHolder(footerView);
        } else {
            ItemMainBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                    R.layout.item_main,
                    parent,
                    false);
            View itemView = binding.getRoot();
            initItemViewListener(itemView);
            holder = new ItemViewHolder(itemView);
            ((ItemViewHolder) holder).setBinding(binding);
        }
        return holder;
    }

    private void initItemViewListener(View itemView) {
        itemView.setOnClickListener(this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            MainBean.DataBean data = datas.get(position - headerCount);
            EstyleLog.e("size", datas.size());
            itemViewHolder.getBinding().setBean(data);
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = headerCount + datas.size() + footerCount;
        if (emptyView != null) {
            if (itemCount > footerCount) {
                emptyView.setVisibility(View.INVISIBLE);
            } else {
                emptyView.setVisibility(View.VISIBLE);
            }
        }
        return itemCount;
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(datas.get(position - headerCount).getId());
    }

    @Override
    public int getItemViewType(int position) {
        if (headerCount > 0) {
            if (position < headerCount) {
                return position;
            }
        }

        if (footerCount > 0) {
            if (position > (footerCount + datas.size()) - 1) {
                return position;
            }
        }
        return headerCount;
    }

    @Override
    public void onClick(View view) {
        int position;
        switch (view.getId()) {
            default:
                if (onItemClickListener != null) {
                    position = ((RecyclerView) view.getParent()).getChildLayoutPosition(view);
                    onItemClickListener.onItemClick(position);
                }
                break;
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        private ItemMainBinding binding;

        public ItemViewHolder(View itemView) {
            super(itemView);
        }

        public ItemMainBinding getBinding() {
            return binding;
        }

        public void setBinding(ItemMainBinding binding) {
            this.binding = binding;
        }
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static interface OnItemClickListener {
        public void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}