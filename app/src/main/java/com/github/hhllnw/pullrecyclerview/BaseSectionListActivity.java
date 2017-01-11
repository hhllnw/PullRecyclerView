package com.github.hhllnw.pullrecyclerview;

import android.view.ViewGroup;

import com.github.hhllnw.pullrecyclerviewlibrary.BaseViewHolder;
import com.github.hhllnw.pullrecyclerviewlibrary.SectionData;

/**
 * Created by hhl on 2017/1/11.
 */

public abstract class BaseSectionListActivity<T> extends BaseListActivity<SectionData<T>> {

    protected static final int VIEW_TYPE_SECTION_HEADER = 1;
    protected static final int VIEW_TYPE_SECTION_CONTENT = 2;

    @Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SECTION_HEADER) {
            return onCreateSectionHeaderViewHolder(parent);
        }
        return onCreateSectionViewHolder(parent, viewType);
    }


    protected abstract BaseViewHolder onCreateSectionViewHolder(ViewGroup parent, int viewType);

    protected abstract BaseViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent);

    @Override
    protected int getItemType(int position) {
        return isSectionHeader(position) == true ? VIEW_TYPE_SECTION_HEADER : VIEW_TYPE_SECTION_CONTENT;
    }

    @Override
    protected boolean isSectionHeader(int position) {
        return mDataList.get(position).isHeader;
    }


}
