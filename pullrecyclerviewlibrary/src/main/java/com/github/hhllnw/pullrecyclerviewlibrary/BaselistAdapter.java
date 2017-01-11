package com.github.hhllnw.pullrecyclerviewlibrary;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * Created by hhl on 2016/6/27.
 */
public abstract class BaselistAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int LOAD_MORE_SHOW_FOOTER = 100;
    private static final int INSERT_SHOW_LAST_VIEW = 101;
    protected boolean isShowLoadMore = false;
    protected boolean isShowInsertLastView = false;
    private View footerView;//recycler新添加的最后一个view
    private PullRecycler.OnClickFooterViewListener footerListener;
    private TextView tv_loadMore;
    private ProgressBar mLoadMoreProgressBar;

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LOAD_MORE_SHOW_FOOTER) {
            return addLoadMoreView(parent);
        } else if (viewType == INSERT_SHOW_LAST_VIEW) {
            return insertLastView();
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
        return getDataCount() + (isShowLoadMore ? 1 : 0) + (isShowInsertLastView ? 1 : 0);
    }


    @Override
    public int getItemViewType(int position) {
        if (isShowLoadMore && position == (getItemCount() - 1)) {
            return LOAD_MORE_SHOW_FOOTER;
        } else if (isShowInsertLastView && position == (getItemCount() - 1)) {
            return INSERT_SHOW_LAST_VIEW;
        }
        return getDataType(position);
    }

    protected int getDataType(int position) {
        return 0;
    }

    public void showLoadMoreFooter(final boolean isShow) {

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

        handler.post(runnable);
    }

    public void addFooterView(boolean isShow, View view) {
        isShowInsertLastView = isShow;
        footerView = view;
        if (isShowInsertLastView) {
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

    protected BaseViewHolder addLoadMoreView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_footer, parent, false);
        tv_loadMore = (TextView) view.findViewById(R.id.tv_footer);
        mLoadMoreProgressBar = (ProgressBar) view.findViewById(R.id.footer_progressBar);
        return new createLoadMoreFooterViewHolder(view);
    }

    /**
     * 设置加载更多提示
     *
     * @param str
     * @param look ProgressBar是否显示
     */
    protected void setLoadMoreTv(String str, int look) {
        if (tv_loadMore == null || mLoadMoreProgressBar == null) return;
        tv_loadMore.setText(str);
        mLoadMoreProgressBar.setVisibility(look);
    }

    private BaseViewHolder insertLastView() {
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

    protected class createLoadMoreFooterViewHolder extends BaseViewHolder {
        public createLoadMoreFooterViewHolder(View view) {
            super(view);
        }

        @Override
        public void bind(int position) {

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
