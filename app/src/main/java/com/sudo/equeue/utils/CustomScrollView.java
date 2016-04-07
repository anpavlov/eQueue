package com.sudo.equeue.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

class CustomScrollView extends ScrollView {

    // true if we can scroll the ScrollView
    // false if we cannot scroll
    private boolean scrollable = false;

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                // if we can scroll pass the event to the superclass
////                if (scrollable) return super.onTouchEvent(ev);
//                // only continue to handle the touch event if scrolling enabled
//                return false; // scrollable is always false at this point
//            default:
//                return super.onTouchEvent(ev);
//        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
        return false;
    }

}