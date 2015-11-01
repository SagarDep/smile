package me.zsj.smile.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import me.zsj.smile.utils.ScreenUtils;

/**
 * Created by zsj on 2015/11/1 0001.
 */
public class PullBackLayout extends FrameLayout{


    private ViewDragHelper mDragHelper;
    private int mReleasedHeight;
    private PullCallBack pullCallBack;

    public void setPullCallBack(PullCallBack pullCallBack) {
        this.pullCallBack = pullCallBack;
    }

    public PullBackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDragHelper = ViewDragHelper.create(this, 1f / 8f, new DragCallBack());
        mReleasedHeight = ScreenUtils.getHeight(context) / 4;
    }


    class DragCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return 0;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return 0;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return Math.max(0, top);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getHeight();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            if (releasedChild.getTop() >= mReleasedHeight) {
                if (pullCallBack != null) {
                    pullCallBack.onPullCompleted();
                }
            }else {
                mDragHelper.settleCapturedViewAt(0, 0);
                invalidate();
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        //这么做的目的是当图片缩小时应用会发生下标越界异常，
        // 接着捕捉异常返回false，子View可以继续处理事件分发，应用就不会crash了
        try {
            return mDragHelper.shouldInterceptTouchEvent(ev);
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    public interface PullCallBack {

        void onPullCompleted();
    }
}
