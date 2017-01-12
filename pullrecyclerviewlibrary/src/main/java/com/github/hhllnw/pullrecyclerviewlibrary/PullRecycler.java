package com.github.hhllnw.pullrecyclerviewlibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by hhl on 2016/6/4.
 * RecyclerView、SwipeRefreshLayout 自定义成一个View
 * 支持下拉刷新、上滑加载更多功能
 */
public class PullRecycler extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private int mCurrentState;//目前状态
    private int ACTION_IDEL = 0;//空闲状态
    public static final int ACTION_PULL_REFRESH = 1;//下拉刷新
    public static final int ACTION_MORE_ACTION = 2;//上滑加载更多
    public static final int LOAD_MORE_CONTIUE = 3;//加载数据，还有数据可以加载
    public static final int LOAD_MORE_END = 4;//加载完所有数据，没有等多数据了
    private boolean isCanLoadMore;//是否可以加载 /默认不可以加载更多
    private boolean isCanRefresh = true;//是否可以刷新  /默认可以刷新
    private boolean isShowFooterView;
    private OnRecyclerRefreshListener listener;
    private ILayoutManager manager;
    private BaselistAdapter adapter;
    private TextView tv_loadMore;
    private ProgressBar mLoadMoreProgressBar;
    private final int DEFAULT_LOADMORE_STYLE = 1000;//默认加载更多UI
    private final int CUSTOMER_LOADMORE_STYLE = 1001;//自定义加载更多UI
    private int LOADMORE_STYLE = 1000;

    public enum LoadMoreModle {scroll, click}

    private LoadMoreModle loadMoreModle = LoadMoreModle.scroll;
    private volatile int DATA_STATUE;

    /**
     * 加载更多时，默认当倒数五个数出现时加载更多
     */
    private int reachBottomCount = 1;

    public void addItemDecoration(RecyclerView.ItemDecoration divider) {
        mRecyclerView.addItemDecoration(divider);
    }

    /**
     * 刷新、加载回调
     */
    public interface OnRecyclerRefreshListener {
        void onPullRefresh(int action);
    }

    public interface OnClickFooterViewListener {
        void onClickFooterView();
    }

    public PullRecycler(Context context) {
        super(context);
        initView();
    }

    public PullRecycler(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PullRecycler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setOnRecyclerRefreshListener(OnRecyclerRefreshListener listener) {
        this.listener = listener;
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_pull_recycler, this, true);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mCurrentState == ACTION_IDEL && isCanLoadMore && reachBottom() && dy > 0) {
                    if (loadMoreModle == LoadMoreModle.scroll) {
                        mCurrentState = ACTION_MORE_ACTION;
                        mSwipeRefreshLayout.setEnabled(false);
                        listener.onPullRefresh(ACTION_MORE_ACTION);
                        adapter.showLoadMoreView(true);
                    } else if (loadMoreModle == LoadMoreModle.click) {
                        if (DATA_STATUE == LOAD_MORE_CONTIUE) {
                            adapter.showLoadMoreView(true);
                            setLoadMoreTv("加载更多", GONE);
                            setOnClickLoadMoreListener();
                        } else if (DATA_STATUE == LOAD_MORE_END) {
                            onComplete(LOAD_MORE_END);
                        }
                    }
                }
            }
        });

        mRecyclerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCurrentState == ACTION_PULL_REFRESH) {
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    /**
     * 设置下拉刷新时缓冲UI颜色
     *
     * @param array
     */
    public void setPullRefreshUI(int[] array) {
        mSwipeRefreshLayout.setColorSchemeResources(array);
    }

    public void setLoadMOreModle(LoadMoreModle modle) {
        loadMoreModle = modle;
    }

    /**
     * 定位到某个item位置
     *
     * @param position
     */
    public void scrollToPosition(int position) {
        mRecyclerView.scrollToPosition(position);
    }

    /**
     * 滑动到某个item位置
     *
     * @param position
     */
    public void smoothToPosition(int position) {
        mRecyclerView.smoothScrollToPosition(position);
    }

    /**
     * 列表滑到底部
     *
     * @return
     */
    private boolean reachBottom() {
        int position = manager.findLastVisiblePosition();
        //getItemCount表示data.size(),getChildCount()表示视图中可见数目
        int totalCount = manager.getLayoutManager().getItemCount();
        return totalCount - position <= reachBottomCount;
    }

    /**
     * 设置列表滑动到当前页还剩几个没显示的时候加载更多，
     * 列如，reachBottomCount = 5，pageSize = 20;当滑动到当前页第16个的时候开始加载下一页数据
     *
     * @param reachBottomCount
     */
    public void setReachBottomViewCount(int reachBottomCount) {
        this.reachBottomCount = reachBottomCount;
    }

    /**
     * 是否可以加载更多
     *
     * @param isCanLoadMore
     */
    public void setIsCanLoadMore(boolean isCanLoadMore) {
        if (!isShowFooterView) {//如果已经添加footerView，加载更多功能将不能使用
            this.isCanLoadMore = isCanLoadMore;
            if (isCanLoadMore) {
                setLoadMoreViewStyle(null);
            }
        }
    }

    /**
     * 是否可以下拉刷新
     *
     * @param isCanRefresh
     */
    public void setIsCanRefresh(boolean isCanRefresh) {
        this.isCanRefresh = isCanRefresh;
        if (isCanRefresh) {
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    /**
     * 首次进入页面自动刷新获取列表数据
     */
    public void setFirstRefresh() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    public void setAdapter(BaselistAdapter adapter) {
        this.adapter = adapter;
        mRecyclerView.setAdapter(adapter);
        manager.setUpAdapter(adapter);
    }

    /**
     * @param view 设置加载更多风格
     */
    public void setLoadMoreViewStyle(View view) {
        if (adapter == null) {
            throw new RuntimeException("adapter is null.");
        }
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_list_footer, this, false);
            tv_loadMore = (TextView) view.findViewById(R.id.tv_footer);
            mLoadMoreProgressBar = (ProgressBar) view.findViewById(R.id.footer_progressBar);
        } else {
            LOADMORE_STYLE = CUSTOMER_LOADMORE_STYLE;
        }
        adapter.addLoadMoreView(view);
    }

    /**
     * @param manager
     */
    public void setLayoutManager(ILayoutManager manager) {
        this.manager = manager;
        mRecyclerView.setLayoutManager(manager.getLayoutManager());
    }

    @Override
    public void onRefresh() {
        mCurrentState = ACTION_PULL_REFRESH;
        listener.onPullRefresh(ACTION_PULL_REFRESH);
        if (isDefaultLoadMoreStyle()) {
            setLoadMoreTv("正在加载...", VISIBLE);
        }

    }

    public void onComplete() {
        onComplete(LOAD_MORE_CONTIUE);
    }

    /**
     * 数据加载完成
     *
     * @param state
     */
    public void onComplete(int state) {
        DATA_STATUE = state;
        switch (mCurrentState) {
            case ACTION_PULL_REFRESH:
                mSwipeRefreshLayout.setRefreshing(false);
                break;
            case ACTION_MORE_ACTION:
                if (state == LOAD_MORE_END) {
                    if (isDefaultLoadMoreStyle()) {
                        setLoadMoreTv("没有更多数据了", GONE);
                    }
                } else if (state == LOAD_MORE_CONTIUE) {
                    adapter.showLoadMoreView(false);
                }
                if (isCanRefresh) {
                    mSwipeRefreshLayout.setEnabled(true);
                }
                break;
        }
        mCurrentState = ACTION_IDEL;
    }

    /**
     * 添加footer View
     *
     * @param isShowFooterView
     * @param view
     */
    public void addFooterView(boolean isShowFooterView, View view) {
        this.isShowFooterView = isShowFooterView;
        if (adapter != null) {
            if (isShowFooterView) {
                isCanLoadMore = false;//添加footerView与加载更多功能不能同时使用
            }
            adapter.addFooterView(isShowFooterView, view);
        } else {
            Logger.E("err", "adapter is null,please init adapter");
        }
    }

    /**
     * 设置加载更多时的加载提示，ProgressBar不显示
     *
     * @param str
     * @param visible VISIBLE,GONE
     */
    public void setLoadMoreTv(String str, int visible) {
        if (tv_loadMore != null && mLoadMoreProgressBar != null) {
            tv_loadMore.setText(str);
            mLoadMoreProgressBar.setVisibility(visible);
        }
    }

    /**
     * 加载更多是否默认模式
     *
     * @return
     */
    private boolean isDefaultLoadMoreStyle() {
        if (LOADMORE_STYLE == DEFAULT_LOADMORE_STYLE) {
            return true;
        }
        return false;
    }

    /**
     * 单击 footer view 监听
     *
     * @param footerViewListener
     */
    public void setOnClickFooterViewListener(OnClickFooterViewListener footerViewListener) {
        if (adapter != null) {
            adapter.setOnClickFooterViewListener(footerViewListener);
        } else {
            Logger.E("err", "adapter is null,please init adapter");
        }
    }

    private void setOnClickLoadMoreListener() {
        if (adapter != null) {
            adapter.setOnClickLoadMoreViewListener(new BaselistAdapter.OnClickLoadMoreViewListener() {
                @Override
                public void onClickLoadMoreView() {
                    mCurrentState = ACTION_MORE_ACTION;
                    mSwipeRefreshLayout.setEnabled(false);
                    setLoadMoreTv("正在加载...", VISIBLE);
                    listener.onPullRefresh(ACTION_MORE_ACTION);
                }
            });
        }
    }
}
