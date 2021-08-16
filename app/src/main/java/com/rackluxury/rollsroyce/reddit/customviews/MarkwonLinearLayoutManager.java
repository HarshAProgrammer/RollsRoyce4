package com.rackluxury.rollsroyce.reddit.customviews;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;

import androidx.recyclerview.widget.LinearLayoutManager;

public class MarkwonLinearLayoutManager extends LinearLayoutManager {
    public interface HorizontalScrollViewScrolledListener {
        void onScrolledLeft();
        void onScrolledRight();
    }

    private HorizontalScrollViewScrolledListener horizontalScrollViewScrolledListener;

    public MarkwonLinearLayoutManager(Context context, HorizontalScrollViewScrolledListener horizontalScrollViewScrolledListener) {
        super(context);
        this.horizontalScrollViewScrolledListener = horizontalScrollViewScrolledListener;
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        if (child instanceof HorizontalScrollView) {
            child.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                private int x = 0;
                @Override
                public void onScrollChanged() {
                    if (child.getScrollX() < x) {
                        horizontalScrollViewScrolledListener.onScrolledLeft();
                    } else {
                        horizontalScrollViewScrolledListener.onScrolledRight();
                    }

                    x = child.getScrollX();
                }
            });
        }
    }
}
