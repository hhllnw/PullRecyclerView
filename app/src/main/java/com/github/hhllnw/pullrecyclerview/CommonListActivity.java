package com.github.hhllnw.pullrecyclerview;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.hhllnw.pullrecyclerviewlibrary.BaseViewHolder;
import com.github.hhllnw.pullrecyclerviewlibrary.PullRecycler;

/**
 * Created by hhl on 2017/1/11.
 */

public class CommonListActivity extends BaseListActivity<String> {

    private int page = 1;

    @Override
    protected void init() {
        mPullRecycler.setIsCanLoadMore(true);
    }

    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onPullRefresh(int action) {
        if (action == PullRecycler.ACTION_PULL_REFRESH) {
            mDataList.clear();
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

    private class ViewHolder extends BaseViewHolder {
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_text);
        }

        @Override
        public void bind(int position) {
            textView.setText(mDataList.get(position));
        }
    }

    private void getData() {

        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < 20; i++) {
                    mDataList.add("" + (mDataList.size() + 1));
                }

                myHander.sendEmptyMessage(1);
            }
        }.start();
    }


}
