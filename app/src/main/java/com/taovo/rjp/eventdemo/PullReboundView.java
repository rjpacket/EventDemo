package com.taovo.rjp.eventdemo;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;

/**
 * @Author：RJP on 2016/9/22 09:58
 */
public class PullReboundView extends ViewGroup {

    private final int coverDistance = 200;

    private Scroller mScroller;
    private int mMinTouchSlop;    //最小位移像素
    private float mYDown;
    private float mYMove;
    private float mYLastMove;
    private ImageView backImageView;
    private int bottomBorder;

    public PullReboundView(Context context) {
        super(context);
        init(context);
    }

    public PullReboundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullReboundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);

        backImageView = new ImageView(context);
        ViewGroup.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        backImageView.setLayoutParams(params);
        backImageView.setClickable(true);
        backImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        backImageView.setImageResource(R.mipmap.bg_user_center);
        backImageView.setVisibility(INVISIBLE);
        addView(backImageView);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childCount = getChildCount();
            if(childCount == 2){
                backImageView.layout(0,0,backImageView.getMeasuredWidth(),backImageView.getMeasuredHeight());
            }
            View childView = getChildAt(childCount - 1);
            // 为ScrollerLayout中的每一个子控件在水平方向上进行布局
            childView.layout(0, backImageView.getMeasuredHeight() -  coverDistance, childView.getMeasuredWidth(), backImageView.getMeasuredHeight() + childView.getMeasuredHeight() - coverDistance);
            bottomBorder = coverDistance;
//            rightBorder = getChildAt(getChildCount() - 1).getRight();
        }
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d("----------->" , "子 分发---down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("----------->" , "子 分发---move");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("----------->" , "子 分发---up");
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d("----------->" , "子 拦截---down");
                mYDown = ev.getRawY();
                mYLastMove = mYDown;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("----------->" , "子 拦截---move");
                mYMove = ev.getRawY();
                mYLastMove = mYMove;
                float dY = Math.abs(mYMove - mYDown);
//                if(dY > mMinTouchSlop){
                    return true;   // 交给自己的onTouchEvent处理
//                }
            case MotionEvent.ACTION_UP:
                Log.d("----------->" , "子 拦截---up");
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d("----------->" , "子 消费---down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("----------->" , "子 消费---move");
                mYMove = event.getRawY();
                float dY = mYLastMove - mYMove;
                Log.d("----------->" , "子 消费---move---dY = " + dY);
//                Log.d("----------->" , getScrollY() + "");
                if(-getScrollY() - dY > bottomBorder / 2){
                    scrollTo(0, - bottomBorder / 2);
                    return true;
                }
                scrollBy(0,(int) (dY / 2));
                mYLastMove = mYMove;
                break;
            case MotionEvent.ACTION_UP:
                Log.d("----------->" , "子 消费---up");
                mScroller.startScroll(0,0,0,0);
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

}
