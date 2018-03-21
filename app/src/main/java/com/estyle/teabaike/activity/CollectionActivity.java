package com.estyle.teabaike.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.estyle.teabaike.R;
import com.estyle.teabaike.adapter.CollectionAdapter;
import com.estyle.teabaike.application.TeaBaikeApplication;
import com.estyle.teabaike.bean.ContentDataBean;
import com.estyle.teabaike.databinding.ActivityCollectionBinding;
import com.estyle.teabaike.bean.eventbus.CheckAllCollectionsEvent;
import com.estyle.teabaike.manager.GreenDaoManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class CollectionActivity extends BaseActivity implements
        CollectionAdapter.OnItemClickListener, CollectionAdapter.OnItemLongClickListener {

    private ActivityCollectionBinding binding;

    private CollectionAdapter mAdapter;

    // 删除功能是否可用
    private boolean mIsDeleteEnabled;

    private Snackbar mSnackbar;

    @Inject
    GreenDaoManager mDBProvider;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, CollectionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TeaBaikeApplication.getInstance().getTeaBaikeComponent().inject(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_collection);
        EventBus.getDefault().register(this);

        initView();
        initData();
    }

    private void initView() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new CollectionAdapter(this);
        mAdapter.setEmptyView(binding.emptyView);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        binding.setAdapter(mAdapter);
    }

    private void initData() {
        List<ContentDataBean> collectionList = mDBProvider.queryCollectionDatas();
        mAdapter.addDatas(collectionList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mIsDeleteEnabled) {
                    setDeleteEnabled(false);
                } else {
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        if (!mIsDeleteEnabled) {
            ContentActivity.startActivity(this, mAdapter.getItemId(position), false);
        } else {
            mAdapter.invertItemStateAtPosition(position);
        }
    }

    @Override
    public void onItemLongClick(int position) {
        if (!mIsDeleteEnabled) {
            mAdapter.invertItemStateAtPosition(position);
            setDeleteEnabled(true);
        }
    }

    // 全选/取消全选按钮
    public void checkAll(View view) {
        TextView checkAllTextView = (TextView) view;
        switch (checkAllTextView.getText().toString()) {
            case "全选":
                mAdapter.setIsCheckedAllItem(true);
                break;
            case "取消":
                mAdapter.setIsCheckedAllItem(false);
                break;
        }
    }

    // 删除选中数据
    public void deleteItem(View view) {
        int deleteCount = mAdapter.deleteCheckedItem();
        String tip = String.format(Locale.getDefault(), getString(R.string.delete_successful), deleteCount);
        setDeleteEnabled(false);
        if (mSnackbar == null) {
            mSnackbar = Snackbar.make(binding.getRoot(), tip, Snackbar.LENGTH_LONG)
                    .setAction(R.string.revoke, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAdapter.restoreTempItem();
                        }
                    })
                    .setActionTextColor(Color.BLACK)
                    .addCallback(snackBarCallback);
            mSnackbar.getView().setBackgroundResource(R.color.colorAccent);
        } else {
            mSnackbar.setText(tip);
        }
        mSnackbar.show();
    }

    // 设置是否可删除
    private void setDeleteEnabled(boolean isDeleteEnabled) {
        this.mIsDeleteEnabled = isDeleteEnabled;
        mAdapter.setDeleteBoxVisibility(isDeleteEnabled);
        int visibility = isDeleteEnabled ? View.VISIBLE : View.INVISIBLE;
        binding.deleteBtn.setVisibility(visibility);
        binding.checkAllTextView.setVisibility(visibility);
    }

    @Override
    public void onBackPressed() {
        if (mIsDeleteEnabled) {
            setDeleteEnabled(false);
        } else {
            super.onBackPressed();
        }
    }

    // 控制全选按钮文字
    @Subscribe
    public void setCheckAllText(CheckAllCollectionsEvent event) {
        binding.checkAllTextView.setText(event.getCheckAllText());
    }


    // Snackbar消失回调
    private Snackbar.Callback snackBarCallback = new Snackbar.Callback() {
        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            super.onDismissed(transientBottomBar, event);
            mAdapter.deleteData();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mSnackbar != null) {
            mSnackbar.removeCallback(snackBarCallback);
        }
    }

}