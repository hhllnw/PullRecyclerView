package com.github.hhllnw.pullrecyclerviewlibrary;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hhl on 2016/6/27.
 */
public abstract class BaselistAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int SHOW_LOAD_MORE_VIEW = 1000;
    private static final int INSERT_SHOW_LAST_VIEW = 1001;
    protected boolean isShowLoadMore = false;
    protected boolean isShowFooterView = false;
    private View footerView;//recycler新添加的最后一个view
    private PullRecycler.OnClickFooterViewListener footerListener;
    private List<View> loadMoreViews = new ArrayList<>();
    private OnClickLoadMoreViewListener onClickLoadMoreViewListener;

    public interface OnClickLoadMoreViewListener {
        void onClickLoadMoreView();
    }

    public void setOnClickLoadMoreViewListener(OnClickLoadMoreViewListener onClickLoadMoreViewListener) {
        this.onClickLoadMoreViewListener = onClickLoadMoreViewListener;
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SHOW_LOAD_MORE_VIEW) {
            return getLoadMoreViewHolder();
        } else if (viewType == INSERT_SHOW_LAST_VIEW) {
            return insertLastViewHolder();
        }
        return onCreateNormalViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (isShowLoadMore && (position == getItemCount() - 1)) {
            if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                params.setFullSpan(true);
            }
        }
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return getDataCount() + (isShowLoadMore ? 1 : 0) + (isShowFooterView ? 1 : 0);
    }


    @Override
    public int getItemViewType(int position) {
        if (isShowLoadMore && position == (getItemCount() - 1)) {
            return SHOW_LOAD_MORE_VIEW;
        } else if (isShowFooterView && position == (getItemCount() - 1)) {
            return INSERT_SHOW_LAST_VIEW;
        }
        return getDataType(position);
    }

    protected int getDataType(int position) {
        return 0;
    }

    public void showLoadMoreView(final boolean isShow) {

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                isShowLoadMore = isShow;
                if (isShow) {
                    notifyItemInserted(getItemCount());
                } else {
                    notifyItemRemoved(getItemCount());
                }
            }
        };
        try {
            handler.post(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加尾部View
     *
     * @param isShow
     * @param view
     */
    public void addFooterView(boolean isShow, View view) {
        isShowFooterView = isShow;
        footerView = view;
        if (isShowFooterView) {
            notifyItemInserted(getDataCount());
        }
    }

    public void setOnClickFooterViewListener(PullRecycler.OnClickFooterViewListener footerViewListener) {
        this.footerListener = footerViewListener;
    }


    public boolean isShowLoadMoreView(int position) {
        if (isShowLoadMore && (position == getItemCount() - 1)) {
            return true;
        }
        return false;
    }

    /**
     * 添加加载更多...
     *
     * @param view
     */
    public void addLoadMoreView(View view) {
        if (view == null) {
            throw new RuntimeException("loadMoreView is null.");
        }
        removeLoadMoreView();
        loadMoreViews.add(view);
    }

    public View getLoadMoreView() {
        return getLoadMoreCounts() > 0 ? loadMoreViews.get(0) : null;
    }

    private void removeLoadMoreView() {
        if (getLoadMoreCounts() > 0) {
            loadMoreViews.remove(0);
        }
    }

    private int getLoadMoreCounts() {
        return loadMoreViews.size();
    }

    private BaseViewHolder getLoadMoreViewHolder() {
        return new createLoadMoreViewHolder(getLoadMoreView());
    }

    private BaseViewHolder insertLastViewHolder() {
        if (footerView == null) {
            Logger.E("error", "in PullRecycler.isShowLastView(true,view),view not null ");
            return null;
        } else {
            return new createLastViewHolder(footerView);
        }
    }

    /**
     * 绑定列表Item布局
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract BaseViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType);

    /**
     * 获取数据个数
     *
     * @return
     */
    protected abstract int getDataCount();

    public boolean isSectionHeader(int position) {
        return false;
    }

    private class createLoadMoreViewHolder extends BaseViewHolder {
        public createLoadMoreViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(int position) {

        }

        @Override
        public void OnItemClick(View view, int position) {
            super.OnItemClick(view, position);
            if (onClickLoadMoreViewListener != null) {
                onClickLoadMoreViewListener.onClickLoadMoreView();
            }
        }
    }

    protected class createLastViewHolder extends BaseViewHolder {

        public createLastViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(int position) {

        }

        @Override
        public void OnItemClick(View view, int position) {
            if (footerListener != null) {
                footerListener.onClickFooterView();
            }
        }
    }
}
