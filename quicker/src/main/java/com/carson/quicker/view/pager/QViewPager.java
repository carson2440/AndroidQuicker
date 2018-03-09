package com.carson.quicker.view.pager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.UiThread;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carson on 2018/3/9.
 */


//实现ViewPager滑动到最后一页 继续滑动加载更多，
//Adapter多加一页view,getCount()+1,重写ViewPager的touch事件
//到达最后一项，并且滑动距离超过一定值，并且Event事件为Up。
@UiThread
public class QViewPager extends ViewPager {

    private int touchSlop;
    private boolean mAutoScroll;
    private int intervalInMillis;
    private QPagerAdapter mAdapter;
    private List<OnPageChangeListener> mOnPageChangeListeners;

    private float mInitialMotionX;
    private float mInitialMotionY;
    private float mLastMotionX;
    private float mLastMotionY;
    private FactorScroller mScroller;

    private static final int MSG_AUTO_SCROLL = 0x01;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_AUTO_SCROLL) {
                setCurrentItem(getCurrentItem() + 1);
                sendEmptyMessageDelayed(MSG_AUTO_SCROLL, intervalInMillis);
            } else {
                super.handleMessage(msg);
            }
        }
    };

    public QViewPager(Context context) {
        this(context, null);
    }

    public QViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        super.setOnPageChangeListener(onPageChangeListener);
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        setScrollFactgor(4);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(MSG_AUTO_SCROLL);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (mAutoScroll) {
            if (visibility == View.VISIBLE) {
                startAutoScroll();
            } else if (visibility == View.INVISIBLE) {
                mHandler.removeMessages(MSG_AUTO_SCROLL);
            }
        }
    }

    public void setScrollFactgor(double factor) {
        setScrollerIfNeeded();
        mScroller.setFactor(factor);
    }

    private void setScrollerIfNeeded() {
        if (mScroller != null) {
            return;
        }
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);
            mScroller = new FactorScroller(getContext(), (Interpolator) interpolatorField.get(null));
            scrollerField.set(this, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
//        mOuterPageChangeListener = listener;
        addOnPageChangeListener(listener);
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        if (mOnPageChangeListeners == null) {
            mOnPageChangeListeners = new ArrayList<>();
        }
        mOnPageChangeListeners.add(listener);
    }

    public void startAutoScroll() {
        startAutoScroll(this.intervalInMillis != 0 ? intervalInMillis : 5000);
    }

    public void startAutoScroll(int millis) {
        if (mAdapter != null && mAdapter.getRealCount() > 0) {
            mAutoScroll = true;
            this.intervalInMillis = millis;
            mHandler.removeMessages(MSG_AUTO_SCROLL);
            mHandler.sendEmptyMessageDelayed(MSG_AUTO_SCROLL, millis);
        }
    }

    public void stopAutoScroll() {
        mAutoScroll = false;
        mHandler.removeMessages(MSG_AUTO_SCROLL);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (mAdapter != null) {
            mAdapter = null;
        }
        mAdapter = new QPagerAdapter(adapter);
        super.setAdapter(mAdapter);
        if (mAdapter.getRealCount() > 0) {
            setCurrentItem(0, false);
        }
    }

    @Override
    public PagerAdapter getAdapter() {
        return mAdapter != null ? mAdapter.getRealAdapter() : mAdapter;
    }

    @Override
    public void setCurrentItem(int item) {
        if (getCurrentItem() != item) {
            setCurrentItem(item, true);
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        int realItem = item + 1;
        super.setCurrentItem(realItem, smoothScroll);
    }

    @Override
    public int getCurrentItem() {
        return mAdapter != null ? mAdapter.toRealPosition(super.getCurrentItem()) : 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeMessages(MSG_AUTO_SCROLL);
                mInitialMotionX = ev.getX();
                mInitialMotionY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mHandler.removeMessages(MSG_AUTO_SCROLL);
                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                if ((int) Math.abs(mLastMotionX - mInitialMotionX) > touchSlop || (int) Math.abs(mLastMotionY - mInitialMotionY) > touchSlop) {
                    mInitialMotionX = 0.0f;
                    mInitialMotionY = 0.0f;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mAutoScroll) {
                    startAutoScroll();
                }
                // Manually swipe not affected by scroll factor.
                if (mScroller != null) {
                    final double lastFactor = mScroller.getFactor();
                    mScroller.setFactor(1);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            mScroller.setFactor(lastFactor);
                        }
                    });
                }
                mLastMotionX = ev.getX();
                mLastMotionY = ev.getY();
                if ((int) mInitialMotionX != 0 && (int) mInitialMotionY != 0) {
                    if ((int) Math.abs(mLastMotionX - mInitialMotionX) < touchSlop
                            && (int) Math.abs(mLastMotionY - mInitialMotionY) < touchSlop) {
                        mInitialMotionX = 0.0f;
                        mInitialMotionY = 0.0f;
                        mLastMotionX = 0.0f;
                        mLastMotionY = 0.0f;
//                        if (onPageClickListener != null) {
//                            onPageClickListener.onPageClick(this, getCurrentItem());
//                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        private float mPreviousPosition = -1;

        private void onPageScrolledOuter(int position, float positionOffset, int positionOffsetPixels) {
            if (mOnPageChangeListeners != null) {
                for (OnPageChangeListener listener : mOnPageChangeListeners) {
                    listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mAdapter != null) {
                int realPosition = mAdapter.toRealPosition(position);
                if (realPosition != mAdapter.getRealCount() - 1) {
                    onPageScrolledOuter(realPosition,
                            positionOffset, positionOffsetPixels);
                } else {
                    if (positionOffset > .5) {
                        onPageScrolledOuter(0, 0, 0);
                    } else {
                        onPageScrolledOuter(realPosition,
                                0, 0);
                    }
                }
            }

        }

        @Override
        public void onPageSelected(int position) {
            int realPosition = mAdapter.toRealPosition(position);
            if (mPreviousPosition != realPosition) {
                mPreviousPosition = realPosition;
                if (mOnPageChangeListeners != null) {
                    for (OnPageChangeListener listener : mOnPageChangeListeners) {
                        listener.onPageSelected(realPosition);
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if ((state == ViewPager.SCROLL_STATE_IDLE && mAdapter != null)) {
                int position = QViewPager.super.getCurrentItem();
                int realPosition = mAdapter.toRealPosition(position);
                if (position == 0 || position == mAdapter.getCount() - 1) {
                    setCurrentItem(realPosition, false);
                }
            }
            if (mOnPageChangeListeners != null) {
                for (OnPageChangeListener listener : mOnPageChangeListeners) {
                    listener.onPageScrollStateChanged(state);
                }
            }
        }
    };

    class FactorScroller extends Scroller {

        private double factor = 1;

        public FactorScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public void setFactor(double factor) {
            this.factor = factor;
        }

        public double getFactor() {
            return factor;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, (int) (duration * factor));
        }
    }
}



