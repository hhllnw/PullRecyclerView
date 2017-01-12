package com.github.hhllnw.pullrecyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hhllnw.pullrecyclerviewlibrary.BaseViewHolder;
import com.github.hhllnw.pullrecyclerviewlibrary.PullRecycler;

/**
 * Created by hhl on 2017/1/11.
 */

public class FooterListActivty extends BaseListActivity<String> {
    private View footerView;

    @Override
    protected void init() {

        footerView = LayoutInflater.from(this).inflate(R.layout.item_list_header, null);
        TextView textView = (TextView) footerView.findViewById(R.id.item_text);
        textView.setText("I'm footer.");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        footerView.setLayoutParams(params);
        mPullRecycler.addFooterView(true, footerView);
        mPullRecycler.setOnClickFooterViewListener(new PullRecycler.OnClickFooterViewListener() {
            @Override
            public void onClickFooterView() {
                Toast.makeText(FooterListActivty.this, "I'm footer.", Toast.LENGTH_SHORT).show();
            }
        });


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
        }

        getData();
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
                    mDataList.add("" + i);
                }

                myHander.sendEmptyMessage(1);
            }
        }.start();
    }

    private class ViewHolder extends BaseViewHolder {
        private TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.item_text);
        }

        @Override
        public void bind(int position) {
            textView.setText(mDataList.get(position));
        }

        @Override
        public void OnItemClick(View view, int position) {
            super.OnItemClick(view, position);
            Toast.makeText(FooterListActivty.this, "" + position, Toast.LENGTH_SHORT).show();
        }
    }
}
