package com.kunbo.app.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

public class AutoScrollRecyclerview extends RecyclerView implements Runnable {
    public AutoScrollRecyclerview(Context context) {
        this(context, null);
    }

    public AutoScrollRecyclerview(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    /**
     * 是否正在滑动
     */
    private boolean mIsRolling = false;

    /**
     * View被添加到界面时触发
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        startRoll();
    }

    /**
     * View从界面移除时触发
     */
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRoll();
    }

    /**
     * 开始滑动
     */
    private void startRoll() {
        //已经滑动时/没有设置适配器时直接返回
        if (mIsRolling)
            return;
        mIsRolling = true;
        postDelayed(this, 100);
    }

    /**
     * 停止滑动
     */
    private void stopRoll() {
        if (!mIsRolling)
            return;
        mIsRolling = false;
        removeCallbacks(this);
    }

    @Override
    public void run() {
        if (this.mIsRolling) {
            scrollBy(0, 2);
            postDelayed(this, 100);
        }
    }


    /**
     * 事件触摸：
     * 1.若让滑动不受用户触摸影响，直接返回false，表示不处理事件
     * 2.若需要在用户触摸时停止，用户离开时开始，只需要根据情况触摸事件进行处理即可
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //1.若让滑动不受用户触摸影响，直接返回false，表示不处理事件
        //return false;
        //2.若需要在用户触摸时停止，用户离开时开始，只需要根据情况触摸事件进行处理即可
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            stopRoll();
        if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL ||
                event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            startRoll();
        }
        return super.onTouchEvent(event);
    }
}


