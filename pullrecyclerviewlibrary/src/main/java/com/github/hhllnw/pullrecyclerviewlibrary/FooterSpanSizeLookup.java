package com.github.hhllnw.pullrecyclerviewlibrary;

import android.support.v7.widget.GridLayoutManager;

/**
 * Created by Stay on 6/3/16.
 */
public class FooterSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
    private BaselistAdapter adapter;
    private int spanCount;

    public FooterSpanSizeLookup(BaselistAdapter adapter, int spanCount) {
        this.adapter = adapter;
        this.spanCount = spanCount;
    }

    @Override
    public int getSpanSize(int position) {
        if (adapter.isShowLoadMoreView(position) || adapter.isSectionHeader(position)) {
            return spanCount;
        }
        return 1;
    }
}
