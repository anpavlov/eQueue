package com.sudo.equeue.utils;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

public class StaticSwipeRefreshLayout extends SwipeRefreshLayout {

    public StaticSwipeRefreshLayout(Context context) {
        super(context);
    }

    public StaticSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        return false;
    }
}