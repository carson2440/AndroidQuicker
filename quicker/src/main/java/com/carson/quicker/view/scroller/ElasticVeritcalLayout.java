package com.carson.quicker.view.scroller;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * Created by carson on 2017/4/10.
 */


class ElasticVeritcalLayout extends FrameLayout {


    private boolean canChildScrollDown;
    private boolean canChildScrollUp;
    private boolean isDownFlexibly = true;
    private boolean isDragingDown;
    private boolean isDragingUp;
    private float mInitialMotionY;
    protected View mScroller;
    protected ViewDragHelper mViewDragHelper;
    private int sY;

    public ElasticVeritcalLayout(Context context) {
        super(context);
    }

    public ElasticVeritcalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ElasticVeritcalLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getSY() {
        return this.sY;
    }

    private boolean canChildScrollDown() {
        if (this.mScroller instanceof AbsListView) {
            AbsListView listView = (AbsListView) this.mScroller;
            int count = listView.getCount();
            int childCount = listView.getChildCount();
            return childCount > 0 && (listView.getLastVisiblePosition() < count - 1 || listView.getMeasuredHeight() - listView.getPaddingBottom() < listView.getChildAt(childCount - 1).getPaddingBottom());
        } else if (this.mScroller instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) this.mScroller;
            LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
            int count = recyclerView.getAdapter().getItemCount() - 1;
            if (lm.canScrollVertically()) {
                return !(lm.findLastVisibleItemPosition() == count);
            } else {
                return false;
            }
        } else {
            return ViewCompat.canScrollVertically(this.mScroller, 1);//down
        }
    }

    protected boolean canChildScrollUp() {
        if (this.mScroller instanceof AbsListView) {
            final AbsListView absListView = (AbsListView) this.mScroller;
            return absListView.getChildCount() > 0
                    && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                    .getTop() < absListView.getPaddingTop());
        } else if (this.mScroller instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) this.mScroller;
            LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
            if ((lm.findFirstVisibleItemPosition() == 0)) {
                View firstView = lm.findViewByPosition(0);
                return firstView.getTop() < 0;
            } else {
                return true;
            }
        } else {
            return ViewCompat.canScrollVertically(mScroller, -1);
        }
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper != null && mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mViewDragHelper = ViewDragHelper.create(this, 3.0F, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == mScroller;
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return mScroller.getMeasuredHeight();
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                final int topBound = getPaddingTop();
                final int bottomBound = getHeight() - child.getHeight() - topBound;
                if ((canChildScrollDown && !canChildScrollUp && top > 0) || (canChildScrollUp && !canChildScrollDown && top < 0)) {
                    return top;
                } else {
                    return Math.min(Math.max(top, topBound), bottomBound);
                }
//                if ((canChildScrollDown && !canChildScrollUp && top > 0)) {
//                    return top;
//                } else {
//                    return Math.min(Math.max(top, topBound), bottomBound);
//                }
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                sY = top;
                onPositionChanged(top);
                super.onViewPositionChanged(changedView, left, top, dx, dy);
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                onReleased(getSY());
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (this.mScroller == null && getChildCount() > 0) {
            this.mScroller = getChildAt(0);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean shouldIntercept = this.mViewDragHelper.shouldInterceptTouchEvent(ev);//内部
        int action = MotionEventCompat.getActionMasked(ev);
        float f = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                this.mInitialMotionY = f;
                this.isDragingUp = false;
                this.isDragingDown = false;
                this.canChildScrollUp = canChildScrollUp();
                this.canChildScrollDown = canChildScrollDown();
                return false;
            case MotionEvent.ACTION_MOVE:
                f -= this.mInitialMotionY;
//                if (f != 0.0F) {
                action = this.mViewDragHelper.getTouchSlop();
                if ((!this.canChildScrollUp) && f > action) {
                    this.isDragingUp = true;
                } else {
                    if (this.sY > 0) {
                        this.isDragingUp = true;
                    } else if ((!this.canChildScrollDown) && f < -action) {
                        this.isDragingDown = true;
                    } else if (this.sY < 0) {
                        this.isDragingDown = true;
                    }
                }
//                boolean canScrollDown = canScrollVertically(1);
//                boolean canScrollUp = canScrollVertically(-1);
//                System.out.println("down:" + isDragingDown + " up:" + isDragingUp + "  ==== " + shouldIntercept + " - " + canScrollUp + " - " + canScrollDown);
                if (isDragingDown || isDragingUp) {
                    return shouldIntercept;
                }
//                }
                return false;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                this.mViewDragHelper.cancel();
                break;
        }
        return shouldIntercept;

    }

    /**
     * return true: 事件分发给当前View,并由 dispatchTouchEvent 方法进行消费，同时事件会停止向下传递.
     * return false; 父View 的 onTouchEvent 进行消费.
     * return super.dispatchTouchEvent(ev),事件分发给当前 View 的 onInterceptTouchEvent 方法去进行处理
     */

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    public void onPositionChanged(int top) {

    }

    public void onReleased(int top) {
        scrollBackNatural();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mViewDragHelper.getActivePointerId() == -1) {
            this.mViewDragHelper.setActivePointerId(0);
        }
        System.out.println("Action:" + event.getAction());
        try {
            this.mViewDragHelper.processTouchEvent(event);
        } catch (Exception e) {

        }
        return true;
    }

    public void scrollBackNatural() {
        this.mViewDragHelper.settleCapturedViewAt(0, 0);
        invalidate();
    }

    public void scrollBack() {
        if ((this.mViewDragHelper != null) && (this.mViewDragHelper.smoothSlideViewTo(this.mScroller, 0, 0))) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void scrollTo(int paramInt) {
        this.mViewDragHelper.settleCapturedViewAt(0, paramInt);
        invalidate();
    }

    public void setEnablePullUp(boolean paramBoolean) {
    }

}
