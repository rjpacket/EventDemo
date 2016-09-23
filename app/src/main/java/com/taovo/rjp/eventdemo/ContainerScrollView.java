package com.taovo.rjp.eventdemo;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * @Author：RJP on 2016/9/23 12:03
 */
public class ContainerScrollView extends ViewGroup {

    private float mYDown;
    private float mYLastMove;
    private float mYMove;
    private Scroller mScroller;
    private int mMinTouchSlop;
    private int topBorder;
    private int bottomBorder;
    private int screenHeight;

    private boolean isChuan = false;

    public ContainerScrollView(Context context) {
        super(context);
        init(context);
    }

    public ContainerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ContainerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        screenHeight = getHeight(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            int childCount = getChildCount();
            View childView0 = getChildAt(0);
            childView0.layout(0,0,childView0.getMeasuredWidth(),childView0.getMeasuredHeight());
            PullReboundView childView1 = (PullReboundView) getChildAt(1);
            // 为ScrollerLayout中的每一个子控件在水平方向上进行布局
            childView1.layout(0, 0, childView1.getMeasuredWidth(), childView0.getMeasuredHeight() + childView1.getChildAt(1).getMeasuredHeight());
            topBorder = 0;
            bottomBorder = childView1.getBottom() - screenHeight;
        }
    }

    /**
     * 获取屏幕的高度
     * @param context
     * @return
     */
    public static int getHeight(Context context) {
        DisplayMetrics dm = context.getApplicationContext()
                .getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            // 为ScrollerLayout中的每一个子控件测量大小
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
//        topBorder = 0;
//        bottomBorder = getChildAt(1).getBottom();
//    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mYDown = ev.getRawY();
                mYLastMove = mYDown;
                break;
            case MotionEvent.ACTION_MOVE:
                mYMove = ev.getRawY();
                float diff = mYMove - mYDown;
                mYLastMove = mYMove;
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diff < 0 || getScrollY() > 0) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                isChuan = false;
                mYMove = event.getRawY();
                int scrolledY = (int) (mYLastMove - mYMove);
                Log.d("------csv----->" , getScrollY() + "..." + scrolledY + "...." + bottomBorder);
                if(getScrollY() + scrolledY < topBorder){
                    scrollTo(0,topBorder);
                    getChildAt(1).dispatchTouchEvent(event);
                    isChuan = true;
                    return true;
                }else if(getScrollY() + scrolledY > bottomBorder){
                    scrollTo(0,bottomBorder);
                    return true;
                }
                scrollBy(0, scrolledY);
                mYLastMove = mYMove;
                break;
            case MotionEvent.ACTION_UP:
                if(isChuan){
                    getChildAt(1).dispatchTouchEvent(event);
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
