package com.estyle.teabaike.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

public class RecyclerView extends android.support.v7.widget.RecyclerView {

    private boolean isScrollToBottom;
    public boolean isLoading;

    private LinearLayoutManager layoutManager;
    private OnLoadMoreListener onLoadMoreListener;

    public RecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        layoutManager = new LinearLayoutManager(context);
        setLayoutManager(layoutManager);
    }

    private OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView,
                                         int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == SCROLL_STATE_IDLE && isScrollToBottom && !isLoading) {
                isLoading = true;
                onLoadMoreListener.onLoadMore();
            }
        }

        @Override
        public void onScrolled(android.support.v7.widget.RecyclerView recyclerView,
                               int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            int itemCount = layoutManager.getItemCount();
            isScrollToBottom = lastVisibleItemPosition == itemCount - 1;
        }
    };

    public void removeOnScrollListener() {
        removeOnScrollListener(onScrollListener);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        if (onLoadMoreListener != null) {
            addOnScrollListener(onScrollListener);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

}
