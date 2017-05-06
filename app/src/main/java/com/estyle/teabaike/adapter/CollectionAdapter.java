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
import com.estyle.teabaike.eventbus.CheckAllCollectionsEvent;
import com.estyle.teabaike.manager.GreenDaoManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> implements
        View.OnClickListener, View.OnLongClickListener {

    private Context context;
    private List<ContentDataBean> datas;
    private List<Boolean> deleteStateList;
    private List<TempCollectionBean> tempList;
    private View emptyView;
    private boolean isDeleteBoxVisible;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    @Inject
    GreenDaoManager greenDaoManager;

    public CollectionAdapter(Context context) {
        this.context = context;
        TeaBaikeApplication.getApplication().getTeaBaikeComponent().inject(this);
        datas = new ArrayList<>();
    }

    public void addDatas(List<ContentDataBean> datas) {
        this.datas.addAll(datas);
        deleteStateList = new ArrayList<>();
        for (int i = 0; i < this.datas.size(); i++) {
            deleteStateList.add(false);
        }
        notifyDataSetChanged();
    }

    // CheckBox可见
    public void setDeleteBoxVisibility(boolean isDeleteBoxVisible) {
        this.isDeleteBoxVisible = isDeleteBoxVisible;
        if (!isDeleteBoxVisible) {
            for (int i = 0; i < deleteStateList.size(); i++) {
                deleteStateList.set(i, false);
            }
        }
        notifyDataSetChanged();
    }

    // 勾选指定位置Item
    public void invertItemStateAtPosition(int position) {
        deleteStateList.set(position, !deleteStateList.get(position));
        notifyDataSetChanged();
    }

    // 全选/取消全选
    public void setIsCheckedAllItem(boolean isCheckedAll) {
        for (int i = 0; i < deleteStateList.size(); i++) {
            deleteStateList.set(i, isCheckedAll);
        }
        notifyDataSetChanged();
    }

    // 删除选中数据
    public int deleteCheckedItem() {
        if (tempList == null) {
            tempList = new ArrayList<>();
        }
        int count = 0;
        for (int i = deleteStateList.size() - 1; i >= 0; i--) {
            if (deleteStateList.get(i)) {
                tempList.add(new TempCollectionBean(i, datas.get(i)));
                datas.remove(i);
                deleteStateList.remove(i);
                count++;
            }
        }
        return count;
    }

    // 恢复临时数据
    public void restoreTempItem() {
        for (int i = tempList.size() - 1; i >= 0; i--) {
            TempCollectionBean tempCollection = tempList.get(i);
            datas.add(tempCollection.getPosition(), tempCollection.getCollection());
            deleteStateList.add(tempCollection.getPosition(), false);
        }
        notifyDataSetChanged();
        tempList.clear();
    }

    // 删除数据库中的数据
    public void deleteData() {
        greenDaoManager.deleteCollectionData(tempList);
    }

    // 设置空视图
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCollectionBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
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
        ContentDataBean collection = datas.get(position);
        ItemCollectionBinding binding = holder.getBinding();
        binding.setBean(collection);

        int visibility = isDeleteBoxVisible ? View.VISIBLE : View.INVISIBLE;
        binding.deleteBox.setVisibility(visibility);
        binding.deleteBox.setTag(position);
        binding.deleteBox.setChecked(deleteStateList.get(position));
    }

    @Override
    public int getItemCount() {
        if (emptyView != null) {
            if (datas.size() > 0) {
                emptyView.setVisibility(View.INVISIBLE);
            } else {
                emptyView.setVisibility(View.VISIBLE);
            }
        }
        return datas.size();
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(datas.get(position).getId());
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

    @Override
    public boolean onLongClick(View view) {
        int position;
        switch (view.getId()) {
            default:
                if (onItemLongClickListener != null) {
                    position = ((RecyclerView) view.getParent()).getChildLayoutPosition(view);
                    onItemLongClickListener.onItemLongClick(position);
                }
                break;
        }
        return true;
    }

    // 删除Box勾选监听
    public void checkDeleteBox(CompoundButton buttonView, boolean isChecked) {
        int position = (int) buttonView.getTag();
        deleteStateList.set(position, isChecked);

        // 监听是否全选/取消全选，修改Activity中按钮状态
        int count = 0;
        for (boolean isBoxChecked : deleteStateList) {
            if (isBoxChecked) {
                count++;
            }
        }
        if (count == 0) {
            EventBus.getDefault().post(new CheckAllCollectionsEvent(
                    context.getString(R.string.check_all)));
        } else if (count == deleteStateList.size()) {
            EventBus.getDefault().post(new CheckAllCollectionsEvent(
                    context.getString(R.string.uncheck_all)));
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
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

}