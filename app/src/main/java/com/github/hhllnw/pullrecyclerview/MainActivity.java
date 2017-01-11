package com.github.hhllnw.pullrecyclerview;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.hhllnw.pullrecyclerviewlibrary.BaseViewHolder;

public class MainActivity extends BaseListActivity<MainActivity.ListItem> {

    private Class<?>[] ACTIVITY = {CommonListActivity.class,FooterListActivty.class,SectionListActivity.class};
    private String[] titles = {CommonListActivity.class.getSimpleName(),FooterListActivty.class.getSimpleName(), SectionListActivity.class.getSimpleName()};

    @Override
    protected void init() {
    }

    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onPullRefresh(int action) {
        mDataList.clear();
        getData();
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

                for (int i = 0; i < ACTIVITY.length; i++) {
                    ListItem item = new ListItem();
                    item.setaClass(ACTIVITY[i]);
                    item.setTitle(titles[i]);
                    mDataList.add(item);
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
            baselistAdapter.notifyDataSetChanged();
            mPullRecycler.onComplete();
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
            textView.setText(mDataList.get(position).getTitle());
        }

        @Override
        public void OnItemClick(View view, int position) {
            super.OnItemClick(view, position);
            startActivity(new Intent(MainActivity.this, mDataList.get(position).getaClass())
                    .putExtra(Contants.INTENT_KEY_TITLE, mDataList.get(position).getTitle()));
        }
    }


    public class ListItem {
        private Class<?> aClass;
        private String title;


        public Class<?> getaClass() {
            return aClass;
        }

        public void setaClass(Class<?> aClass) {
            this.aClass = aClass;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
