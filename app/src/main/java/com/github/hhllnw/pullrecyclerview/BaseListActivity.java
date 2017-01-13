package com.github.hhllnw.pullrecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.hhllnw.pullrecyclerviewlibrary.BaseViewHolder;
import com.github.hhllnw.pullrecyclerviewlibrary.BaselistAdapter;
import com.github.hhllnw.pullrecyclerviewlibrary.DividerDecoration;
import com.github.hhllnw.pullrecyclerviewlibrary.IGridLayoutManager;
import com.github.hhllnw.pullrecyclerviewlibrary.ILayoutManager;
import com.github.hhllnw.pullrecyclerviewlibrary.ILinearLayoutManager;
import com.github.hhllnw.pullrecyclerviewlibrary.IStaggeredGridLayoutManager;
import com.github.hhllnw.pullrecyclerviewlibrary.PullRecycler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hhl on 2017/1/11.
 */

public abstract class BaseListActivity<T> extends AppCompatActivity implements PullRecycler.OnRecyclerRefreshListener {

    protected PullRecycler mPullRecycler;
    protected List<T> mDataList;
    protected BaselistAdapter baselistAdapter;
    protected TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_list);
        mPullRecycler = (PullRecycler) findViewById(R.id.mPullRecycler);
        tv_title = (TextView) findViewById(R.id.tv_title);
        mDataList = new ArrayList<>();
        baselistAdapter = new ListAdapter();
        mPullRecycler.setPullRefreshUI(new int[]{android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light});
        mPullRecycler.setLayoutManager(getLayoutManager());
        mPullRecycler.setOnRecyclerRefreshListener(this);
        mPullRecycler.setAdapter(baselistAdapter);

        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setColorResource(R.color.colorAccent).build();
        mPullRecycler.addItemDecoration(divider);

        init();
        tv_title.setText(getIntent().getStringExtra(Contants.INTENT_KEY_TITLE));

        mPullRecycler.setFirstRefresh();

    }

    protected abstract void init();

    protected ILayoutManager getLayoutManager() {
        return new ILinearLayoutManager(this);
    }


    public class ListAdapter extends BaselistAdapter {
        @Override
        protected BaseViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
            return getViewHolder(parent, viewType);
        }

        @Override
        protected int getDataCount() {
            return mDataList == null ? 0 : mDataList.size();
        }

        @Override
        public boolean isSectionHeader(int position) {
            return this.isSectionHeader(position);
        }

        @Override
        protected int getDataType(int position) {
            return getItemType(position);
        }

    }

    protected abstract BaseViewHolder getViewHolder(ViewGroup parent, int viewType);

    protected boolean isSectionHeader(int position) {
        return false;
    }

    protected int getItemType(int position) {
        return 0;
    }

    protected MyHander myHander = new MyHander();

    public class MyHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            baselistAdapter.notifyDataSetChanged();
            mPullRecycler.onComplete();
        }
    }
}
