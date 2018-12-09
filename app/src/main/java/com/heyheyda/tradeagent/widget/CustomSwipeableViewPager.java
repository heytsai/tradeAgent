package com.heyheyda.tradeagent.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomSwipeableViewPager extends ViewPager {

    private boolean isSwipePageEnable;

    public CustomSwipeableViewPager(@NonNull Context context) {
        super(context);

        this.isSwipePageEnable = true;
    }

    public CustomSwipeableViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.isSwipePageEnable = true;
    }

    public void setSwipePageEnable(boolean enable) {
        this.isSwipePageEnable = enable;
    }

    /**
     * not allow swiping to switch between pages
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isSwipePageEnable && super.onInterceptTouchEvent(ev);
    }

    /**
     * not allow swiping to switch between pages
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isSwipePageEnable && super.onTouchEvent(ev);
    }
}
