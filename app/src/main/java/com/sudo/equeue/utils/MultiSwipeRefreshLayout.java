package com.sudo.equeue.utils;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

public class MultiSwipeRefreshLayout extends SwipeRefreshLayout {

    private View recycleView;
    private View defaultView;

    public MultiSwipeRefreshLayout(Context context) {
        super(context);
    }

    public MultiSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Set the children which can trigger a refresh by swiping down when they are visible. These
     * views need to be a descendant of this view.
     */
    public void setSwipeableChildren(final int list, final int defView) {
        recycleView = findViewById(list);
        defaultView = findViewById(defView);
    }

    /**
     * This method controls when the swipe-to-refresh gesture is triggered. By returning false here
     * we are signifying that the view is in a state where a refresh gesture can start.
     *
     * <p>As {@link android.support.v4.widget.SwipeRefreshLayout} only supports one direct child by
     * default, we need to manually iterate through our swipeable children to see if any are in a
     * state to trigger the gesture. If so we return false to start the gesture.
     */
    @Override
    public boolean canChildScrollUp() {
        if (defaultView != null && defaultView.getVisibility() == VISIBLE) {
            return false;
        }

//        if (mSwipeableChildren != null && mSwipeableChildren.length > 0) {
//            // Iterate through the scrollable children and check if any of them can not scroll up
//            for (View view : mSwipeableChildren) {
//                if (view != null && view.isShown() && !canViewScrollUp(view)) {
//                    // If the view is shown, and can not scroll upwards, return false and start the
//                    // gesture.
//                    return false;
//                }
//            }
//        }
        return true;
    }

    /**
     * Utility method to check whether a {@link View} can scroll up from it's current position.
     * Handles platform version differences, providing backwards compatible functionality where
     * needed.
     */
//    private static boolean canViewScrollUp(View view) {
//        if (android.os.Build.VERSION.SDK_INT >= 14) {
//            // For ICS and above we can call canScrollVertically() to determine this
//            return ViewCompat.canScrollVertically(view, -1);
//        } else {
//            if (view instanceof AbsListView) {
//                // Pre-ICS we need to manually check the first visible item and the child view's top
//                // value
//                final AbsListView listView = (AbsListView) view;
//                return listView.getChildCount() > 0 &&
//                        (listView.getFirstVisiblePosition() > 0
//                                || listView.getChildAt(0).getTop() < listView.getPaddingTop());
//            } else {
//                // For all other view types we just check the getScrollY() value
//                return view.getScrollY() > 0;
//            }
//        }
//    }
}