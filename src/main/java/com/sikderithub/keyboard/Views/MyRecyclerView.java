package com.sikderithub.keyboard.Views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sikderithub.keyboard.Adapter.ThemeAdapter;

public class MyRecyclerView extends RecyclerView {
    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int spanCount = ((GridLayoutManager) getLayoutManager()).getSpanCount();
        int availableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int columnWidth = availableWidth / spanCount;

        Adapter<?> adapter = getAdapter();
        if (adapter instanceof ThemeAdapter) {
            ((ThemeAdapter) adapter).setColumnWidth(columnWidth);
        }
    }


}
