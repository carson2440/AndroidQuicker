package com.carson.quicker.view.pager;


import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ViewPager.setOffscreenPageLimit(4) and android:layout_centerInParent="true" must set to viewpager
 * <p/>
 * pager.setPageTransformer(true, new ZoomOutTransformer());
 * ViewGroup.LayoutParams layoutParams = pager.getLayoutParams();
 * layoutParams.width = ((Activity) pager.getContext()).getWindowManager().getDefaultDisplay().getWidth() / 7 * 5;
 * layoutParams.height = (int) ((layoutParams.width / 0.6));
 * if (pager.getParent() instanceof ViewGroup) {
 * ViewGroup viewParent = ((ViewGroup) pager.getParent());
 * viewParent.setClipChildren(false);
 * pager.setClipChildren(false);
 * }
 */
public class ZoomOutTransformer implements ViewPager.PageTransformer {

    public static final float MAX_SCALE = 0.95f;
    public static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;
    private double alpha;

    public ZoomOutTransformer() {

    }

    /**
     * alpha_min should from 0.0 to 1.0
     */
    public ZoomOutTransformer(double alpha_min) {
        if (alpha_min < MIN_ALPHA || alpha_min > 1) {
            this.alpha = MIN_ALPHA;
        } else {
            this.alpha = alpha_min;
        }
    }

    @Override
    public void transformPage(View view, float position) {
        if (position < -1) { // [-Infinity,-1)
            if (this.alpha > 0) {
                view.setAlpha(MIN_ALPHA);
            }
            view.setScaleX(MIN_SCALE);
            view.setScaleY(MIN_SCALE);
        } else if (position <= 1) { // [-1,1]
            float scaleFactor = MIN_SCALE + (1 - Math.abs(position)) * (MAX_SCALE - MIN_SCALE);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            // Fade the page relative to its size.
            if (this.alpha > 0) {
                float alphaFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            }
        } else { //  [-Infinity,-1)  (1,+Infinity]
            if (this.alpha > 0) {
                view.setAlpha(MIN_ALPHA);
            }
            view.setScaleX(MIN_SCALE);
            view.setScaleY(MIN_SCALE);
        }
    }

}