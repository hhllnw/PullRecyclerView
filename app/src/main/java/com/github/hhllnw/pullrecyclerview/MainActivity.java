package com.github.hhllnw.pullrecyclerview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.hhllnw.pullrecyclerviewlibrary.BaseViewHolder;
import com.github.hhllnw.pullrecyclerviewlibrary.BaselistAdapter;
import com.github.hhllnw.pullrecyclerviewlibrary.ILinearLayoutManager;
import com.github.hhllnw.pullrecyclerviewlibrary.PullRecycler;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PullRecycler.OnRecyclerRefreshListener {

    private PullRecycler mPullRecycler;
    private List<String> data;
    private ListAdapter listAdapter;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPullRecycler = (PullRecycler) findViewById(R.id.mPullRecycler);

        data = new ArrayList<>();
        listAdapter = new ListAdapter();
        mPullRecycler.setLayoutManager(new ILinearLayoutManager(this));
        mPullRecycler.setOnRecyclerRefreshListener(this);
        mPullRecycler.setAdapter(listAdapter);

        mPullRecycler.setFirstRefresh();
        mPullRecycler.setIsCanLoadMore(true);

    }

    @Override
    public void onPullRefresh(int action) {

        if (action == PullRecycler.ACTION_PULL_REFRESH) {
            data.clear();
            page = 1;
            getData();
        } else {
            page++;
            if (page < 5) {
                getData();
            } else {
                mPullRecycler.onComplete(PullRecycler.LOAD_MORE_END);
            }
        }


    }

    private void getData() {

        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < 25; i++) {
                    data.add("" + (data.size() + 1));
                }

                myHander.sendEmptyMessage(1);
            }
        }.start();
    }

    private MyHander myHander = new MyHander();

    private class MyHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            listAdapter.notifyDataSetChanged();
            mPullRecycler.onComplete();
        }
    }


    private class ListAdapter extends BaselistAdapter {

        @Override
        protected BaseViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        protected int getDataCount() {
            return data == null ? 0 : data.size();
        }
    }

    private class ViewHolder extends BaseViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_text);
        }

        @Override
        public void bind(int position) {

            textView.setText(data.get(position));

        }
    }
}
