package com.github.hhllnw.pullrecyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.hhllnw.pullrecyclerviewlibrary.BaseViewHolder;
import com.github.hhllnw.pullrecyclerviewlibrary.PullRecycler;
import com.github.hhllnw.pullrecyclerviewlibrary.SectionData;

/**
 * Created by hhl on 2017/1/11.
 */

public class SectionListActivity extends BaseSectionListActivity<String> {
    private int page = 1;
    private int count = 0;

    @Override
    protected void init() {
        mPullRecycler.setIsCanLoadMore(true);
    }

    @Override
    public void onPullRefresh(int action) {
        if (action == PullRecycler.ACTION_PULL_REFRESH) {
            mDataList.clear();
            page = 1;
            count = 0;
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

    @Override
    protected BaseViewHolder onCreateSectionViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected BaseViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_header, parent, false);
        return new HeaderViewHolder(view);
    }

    private class ViewHolder extends BaseViewHolder {
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_text);
        }

        @Override
        public void bind(int position) {

            textView.setText(mDataList.get(position).t);

        }
    }

    private class HeaderViewHolder extends BaseViewHolder {
        private TextView textView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_text);
        }

        @Override
        public void bind(int position) {
            textView.setText(mDataList.get(position).header + mDataList.get(position).headerIndex);
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

                mDataList.add(new SectionData(true, page - 1, "header"));

                for (int i = 0; i < 20; i++) {
                    count++;
                    mDataList.add(new SectionData("" + count));
                }
                myHander.sendEmptyMessage(1);
            }
        }.start();
    }
}
