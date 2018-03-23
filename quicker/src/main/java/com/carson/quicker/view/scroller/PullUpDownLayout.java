package com.carson.quicker.view.scroller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.carson.quicker.R;


/**
 * Created by carson on 2017/4/10.
 */

public class PullUpDownLayout extends ElasticVeritcalLayout {

    public static final int STATE_IDEL = 0;
    public static final int STATE_PULL = 1;
    public static final int STATE_REACH = 2;
    public static final int STATE_LOAD = 3;

    private boolean refreshable = true;
    private HeaderView headerView;
    private int headerInitPos;
    protected int headerOffset;
    private int lastState = STATE_IDEL;
    private int mHeadViewHeight = -1;
    private int nowState = 0;
    private int refreshFlag = 50;
    private int refreshingHeight = 100;
    private OnLoadListener onLoadListener;
    


    public interface OnLoadListener {
        public void onRefresh();

        public void onLoadMore();
    }


    public PullUpDownLayout(Context context) {
        super(context);
        init();
    }

    public PullUpDownLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullUpDownLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void setOnLoadListener(OnLoadListener listener) {
        this.onLoadListener = listener;
    }

    private void init() {
        this.headerView = ((HeaderView) View.inflate(getContext(), R.layout.header_view, null));
    }

    private void checkNowState(int top) {
        if (this.lastState == STATE_LOAD) {
            this.nowState = STATE_LOAD;
        } else {
            if (top <= 0) {
                this.nowState = STATE_IDEL;
            } else if (top < this.refreshFlag) {
                this.nowState = STATE_PULL;
            } else {
                this.nowState = STATE_REACH;
            }
        }
    }


    private void updateHeaderShow() {
        if (this.nowState == this.lastState) {
            return;
        }
        this.lastState = this.nowState;
        switch (this.nowState) {
            case STATE_IDEL:
                headerView.reset();
                if (!(this.mScroller instanceof AbsListView)) {
                    this.mScroller.scrollTo(0, 0);
                }
                break;
            case STATE_PULL:
                headerView.onPullToRefresh();
                break;
            case STATE_REACH:
                headerView.onReleaseToRefresh();
                break;
            case STATE_LOAD:
                headerView.onRefresh();
                break;
        }
    }


    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        onFinishInflate();
        if (getChildCount() == 1) {
            removeView(this.headerView);
            addView(this.headerView, 0, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mHeadViewHeight == -1 || (this.mHeadViewHeight != this.headerView.getMeasuredHeight())) {
            this.mHeadViewHeight = this.headerView.getMeasuredHeight();
            this.headerInitPos = (this.mHeadViewHeight - this.headerOffset);
            this.headerView.setTranslationY(-this.headerInitPos);
            refreshingHeight = this.mHeadViewHeight;
            refreshFlag = refreshingHeight / 2;
        }
        this.mScroller.layout(0, getSY(), mScroller.getMeasuredWidth(), mScroller.getMeasuredHeight() + getSY());
    }


    @Override
    public void onPositionChanged(int top) {
        super.onPositionChanged(top);
        if (this.refreshable) {
            headerView.setTranslationY(top - this.headerInitPos);
            checkNowState(top);
            updateHeaderShow();
        }
    }

    @Override
    public void onReleased(int top) {
        if (this.refreshable) {
            checkNowState(top);
            switch (this.nowState) {
                case STATE_IDEL:
                    headerView.reset();
                    break;
                case 2:
                    if (top > this.refreshingHeight) {
                        scrollTo(this.refreshingHeight);
                        this.nowState = 3;
                        updateHeaderShow();
                        if (this.onLoadListener != null) {
                            this.onLoadListener.onRefresh();
                        }
                        return;
                    }
                case 3:
                    updateHeaderShow();
                    if (top > this.refreshingHeight) {
                        scrollTo(this.refreshingHeight);
                        return;
                    } else {
                        onRefreshComplete();
                    }
            }
        }
        super.onReleased(top);
    }

    public void onRefreshComplete() {
        if (this.nowState == 3) {
            this.nowState = STATE_IDEL;
            updateHeaderShow();
            scrollBack();
        }
    }

    public void setRefreshable(boolean refreshable) {
        this.refreshable = refreshable;
        if (getSY() != 0) {
            this.headerView.setTranslationY(-this.headerInitPos);
            scrollBack();
        }
        if (this.refreshable) {
            this.headerView.setVisibility(VISIBLE);
        } else {
            this.headerView.setVisibility(GONE);
        }
    }

    public void scrollBack() {
        if ((this.mViewDragHelper != null) && (getSY() == this.refreshingHeight)) {
            this.mViewDragHelper.removemIdleRunnable();
        }
        super.scrollBack();
    }

    public void setHeaderOffset(int paramInt) {
        this.headerOffset = paramInt;
        this.headerInitPos = (this.mHeadViewHeight - this.headerOffset);
        this.headerView.setTranslationY(-this.headerInitPos);
    }

}
