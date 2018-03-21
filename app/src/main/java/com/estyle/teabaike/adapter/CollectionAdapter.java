package com.estyle.teabaike.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.estyle.teabaike.R;
import com.estyle.teabaike.application.TeaBaikeApplication;
import com.estyle.teabaike.bean.ContentDataBean;
import com.estyle.teabaike.bean.TempCollectionBean;
import com.estyle.teabaike.databinding.ItemCollectionBinding;
import com.estyle.teabaike.bean.eventbus.CheckAllCollectionsEvent;
import com.estyle.teabaike.manager.GreenDaoManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> implements
        View.OnClickListener, View.OnLongClickListener {

    private Context mContext;
    private List<ContentDataBean> mDatas;
    private List<Boolean> mDeleteStateList;
    private List<TempCollectionBean> mTempList;
    private View mEmptyView;
    private boolean mIsDeleteBoxVisible;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    @Inject
    GreenDaoManager mDBProvider;

    public CollectionAdapter(Context context) {
        this.mContext = context;
        TeaBaikeApplication.getInstance().getTeaBaikeComponent().inject(this);
        mDatas = new ArrayList<>();
    }

    public void addDatas(List<ContentDataBean> datas) {
        this.mDatas.addAll(datas);
        mDeleteStateList = new ArrayList<>();
        for (int i = 0; i < this.mDatas.size(); i++) {
            mDeleteStateList.add(false);
        }
        notifyDataSetChanged();
    }

    // CheckBox可见
    public void setDeleteBoxVisibility(boolean isDeleteBoxVisible) {
        this.mIsDeleteBoxVisible = isDeleteBoxVisible;
        if (!isDeleteBoxVisible) {
            for (int i = 0; i < mDeleteStateList.size(); i++) {
                mDeleteStateList.set(i, false);
            }
        }
        notifyDataSetChanged();
    }

    // 勾选指定位置Item
    public void invertItemStateAtPosition(int position) {
        mDeleteStateList.set(position, !mDeleteStateList.get(position));
        notifyDataSetChanged();
    }

    // 全选/取消全选
    public void setIsCheckedAllItem(boolean isCheckedAll) {
        for (int i = 0; i < mDeleteStateList.size(); i++) {
            mDeleteStateList.set(i, isCheckedAll);
        }
        notifyDataSetChanged();
    }

    // 删除选中数据
    public int deleteCheckedItem() {
        if (mTempList == null) {
            mTempList = new ArrayList<>();
        }
        int count = 0;
        for (int i = mDeleteStateList.size() - 1; i >= 0; i--) {
            if (mDeleteStateList.get(i)) {
                mTempList.add(new TempCollectionBean(i, mDatas.get(i)));
                mDatas.remove(i);
                mDeleteStateList.remove(i);
                count++;
            }
        }
        return count;
    }

    // 恢复临时数据
    public void restoreTempItem() {
        for (int i = mTempList.size() - 1; i >= 0; i--) {
            TempCollectionBean tempCollection = mTempList.get(i);
            mDatas.add(tempCollection.getPosition(), tempCollection.getCollection());
            mDeleteStateList.add(tempCollection.getPosition(), false);
        }
        notifyDataSetChanged();
        mTempList.clear();
    }

    // 删除数据库中的数据
    public void deleteData() {
        mDBProvider.deleteCollectionData(mTempList);
    }

    // 设置空视图
    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCollectionBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.item_collection,
                parent,
                false);
        binding.setAdapter(this);
        View itemView = binding.getRoot();
        ViewHolder holder = new ViewHolder(itemView);
        initItemViewListener(itemView);
        holder.setBinding(binding);
        return holder;
    }

    private void initItemViewListener(View itemView) {
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ContentDataBean collection = mDatas.get(position);
        ItemCollectionBinding binding = holder.getBinding();
        binding.setBean(collection);

        int visibility = mIsDeleteBoxVisible ? View.VISIBLE : View.INVISIBLE;
        binding.deleteBox.setVisibility(visibility);
        binding.deleteBox.setTag(position);
        binding.deleteBox.setChecked(mDeleteStateList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mEmptyView != null) {
            if (mDatas.size() > 0) {
                mEmptyView.setVisibility(View.INVISIBLE);
            } else {
                mEmptyView.setVisibility(View.VISIBLE);
            }
        }
        return mDatas.size();
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mDatas.get(position).getId());
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

    @Override
    public boolean onLongClick(View view) {
        int position;
        switch (view.getId()) {
            default:
                if (mOnItemLongClickListener != null) {
                    position = ((RecyclerView) view.getParent()).getChildLayoutPosition(view);
                    mOnItemLongClickListener.onItemLongClick(position);
                }
                break;
        }
        return true;
    }

    // 删除Box勾选监听
    public void checkDeleteBox(CompoundButton buttonView, boolean isChecked) {
        int position = (int) buttonView.getTag();
        mDeleteStateList.set(position, isChecked);

        // 监听是否全选/取消全选，修改Activity中按钮状态
        int count = 0;
        for (boolean isBoxChecked : mDeleteStateList) {
            if (isBoxChecked) {
                count++;
            }
        }
        if (count == 0) {
            EventBus.getDefault().post(new CheckAllCollectionsEvent(
                    mContext.getString(R.string.check_all)));
        } else if (count == mDeleteStateList.size()) {
            EventBus.getDefault().post(new CheckAllCollectionsEvent(
                    mContext.getString(R.string.uncheck_all)));
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemCollectionBinding binding;

        private ViewHolder(View itemView) {
            super(itemView);
        }

        public ItemCollectionBinding getBinding() {
            return binding;
        }

        public void setBinding(ItemCollectionBinding binding) {
            this.binding = binding;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

}