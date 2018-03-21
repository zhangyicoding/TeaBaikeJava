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

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private static final int TYPE_ITEM = -1;

    private Context mContext;
    private List<MainBean.DataBean> mDatas;

    private View mEmptyView;
    private List<View> mHeaderList;
    private List<View> mFooterList;
    private int mHeaderCount;
    private int mFooterCount;

    private OnItemClickListener mOnItemClickListener;

    public MainAdapter(Context context) {
        this.mContext = context;
        mDatas = new ArrayList<>();
    }

    // 刷新数据
    public void refreshDatas(List<MainBean.DataBean> datas) {
        this.mDatas.clear();
        addDatas(datas);

    }

    // 添加数据
    public void addDatas(List<MainBean.DataBean> datas) {
        this.mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    // 添加头视图
    public void addHeaderView(View headerView) {
        if (mHeaderList == null) {
            mHeaderList = new ArrayList<>();
        }
        mHeaderList.add(headerView);
        mHeaderCount = mHeaderList.size();
    }

    // 添加w尾视图
    public void addFooterView(View footerView) {
        if (mFooterList == null) {
            mFooterList = new ArrayList<>();
        }
        mFooterList.add(footerView);
        mFooterCount = mFooterList.size();
    }

    // 设置空视图
    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == TYPE_ITEM) {
            ItemMainBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                    R.layout.item_main,
                    parent,
                    false);
            View itemView = binding.getRoot();
            initItemViewListener(itemView);
            holder = new ItemViewHolder(itemView);
            ((ItemViewHolder) holder).setBinding(binding);
        } else {
            if (viewType < mHeaderCount) {
                View headerView = mHeaderList.get(viewType);
                holder = new HeaderViewHolder(headerView);
            } else {
                View footerView = mFooterList.get(viewType - mHeaderCount - mDatas.size());
                holder = new FooterViewHolder(footerView);
            }
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
            MainBean.DataBean data = mDatas.get(position - mHeaderCount);
            itemViewHolder.getBinding().setBean(data);
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = mHeaderCount + mDatas.size() + mFooterCount;
        if (mDatas.size() > 0) {
            if (mEmptyView != null) {
                mEmptyView.setVisibility(View.GONE);
            }
            if (mFooterCount > 0) {
                for (View footerView : mFooterList) {
                    footerView.setVisibility(View.VISIBLE);
                }
            }
        }
        return itemCount;
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mDatas.get(position - mHeaderCount).getId());
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderCount > 0) {
            if (position < mHeaderCount) {
                return position;
            }
        }

        if (mFooterCount > 0) {
            if (position > (mHeaderCount + mDatas.size()) - 1) {
                return position;
            }
        }
        return TYPE_ITEM;
    }

    @Override
    public void onClick(View view) {
        int position;
        switch (view.getId()) {
            default:
                if (mOnItemClickListener != null) {
                    position = ((RecyclerView) view.getParent()).getChildLayoutPosition(view);
                    mOnItemClickListener.onItemClick(position);
                }
                break;
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        private ItemMainBinding binding;

        private ItemViewHolder(View itemView) {
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

        private FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

}