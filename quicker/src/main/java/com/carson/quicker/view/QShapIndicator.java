package com.carson.quicker.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.support.annotation.UiThread;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.carson.quicker.codec.QAESCoder;
import com.carson.quicker.utils.QAndroid;

/**
 * Created by carson2440 on 2018/1/3.
 * may be CIRCLE and RECTANGLE
 */
@UiThread
public class QShapIndicator extends View implements ViewPager.OnPageChangeListener {

    public static final int CIRCLE = 0;
    public static final int SQUARE = 1;
    public static final int RECTANGLE = 2;
    private int shape = CIRCLE;
    private int fillColor = Color.WHITE;
    private int strokeColor = Color.RED;
    private int activeItem = 0;
    private int countItem = 0;
    private int previouslyActiveItem = 99;

    private float radius;
    private float activeRadius, constantRadius, previousRadius;

    private static final float SELECTED_FACTOR = 1.30f;
    private static int SPACING_FACTOR = 1;
    private Paint fillPaint = new Paint();
    private Paint strokePaint = new Paint();
    private ViewPager viewPager;
    PaintFlagsDrawFilter filterPaint;

    public QShapIndicator(Context context) {
        this(context, null);
    }

    public QShapIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        radius = QAndroid.dp2px(context,6);
        fillPaint.setAntiAlias(true);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setStrokeJoin(Paint.Join.ROUND);
        if (strokeColor == 0) {
            strokeColor = fillColor;
        }
        strokePaint.setAntiAlias(true);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(radius * 0.2f);
        activeRadius = radius * 0.6f;
        // size of inactive indicator
        constantRadius = activeRadius;
        if (shape == RECTANGLE) {
            SPACING_FACTOR *= 2;
        }
        filterPaint = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        onPageSelected(0);
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        this.countItem = this.viewPager.getAdapter().getCount();
        this.activeItem = viewPager.getCurrentItem();
        this.invalidate();
        this.requestLayout();
        this.viewPager.addOnPageChangeListener(this);
    }

    public ViewPager getViewPager() {
        return this.viewPager;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int position) {
        if (previouslyActiveItem != 99) {
            activeRadius /= SELECTED_FACTOR;
        }
        // desiredRadius is always size of active indicator
        setActiveItem(position, constantRadius * SELECTED_FACTOR);

    }

    // to set new indicator active when viewpager flipped
    public void setActiveItem(int activeItem, float desiredRadius) {
        this.previouslyActiveItem = this.activeItem;
        this.activeItem = activeItem;

        // setting up animation
        ValueAnimator animation = ValueAnimator.ofFloat(activeRadius, desiredRadius);
        animation.setDuration(300);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // on every animation update effects 2 indicator:
                // active and previously active one
                activeRadius = (float) valueAnimator.getAnimatedValue();
                previousRadius = (constantRadius * SELECTED_FACTOR) - (activeRadius - constantRadius);
                invalidate();
            }
        });
        animation.start();
        this.invalidate();
        this.requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // x(n) = a + d(n-1)
        int n = countItem;
        int measuredWidth = (int) (radius * SELECTED_FACTOR + (radius * (SELECTED_FACTOR + SPACING_FACTOR)) * (n - 1));
        measuredWidth += radius * SELECTED_FACTOR;
        int measuredHeight = (int) (radius * SELECTED_FACTOR) * 2;
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        fillPaint.setColor(fillColor);
        strokePaint.setColor(strokeColor);
        canvas.setDrawFilter(filterPaint);

        float factor = SELECTED_FACTOR, x, y;
        if (shape == RECTANGLE) {
            factor *= 2;
        }
        for (int i = 0; i < countItem; i++) {
            // coordinates of circles
            x = i * (factor + 1) * radius;
            y = radius * factor;

            if (i == activeItem) {
                // if its on initial state
                if (previouslyActiveItem == 99) {
                    drawIndicators(i, canvas, x, y, (activeRadius * factor));
                } else {
                    drawIndicators(i, canvas, x, y, activeRadius);
                }
            } else if (i == previouslyActiveItem) {
                drawIndicators(i, canvas, x, y, previousRadius);
            } else {
                drawIndicators(i, canvas, x, y, constantRadius);
            }
        }
    }

    public void drawIndicators(int position, Canvas canvas, float coordinateX, float coordinateY,
                               float calculatedSize) {
        if (shape == SQUARE) {
            canvas.drawRect(coordinateX - (calculatedSize), coordinateY - (calculatedSize),
                    coordinateX + (calculatedSize), coordinateY + (calculatedSize), fillPaint);
            canvas.drawRect(coordinateX - (calculatedSize), coordinateY - (calculatedSize),
                    coordinateX + (calculatedSize), coordinateY + (calculatedSize), strokePaint);
        } else if (shape == RECTANGLE) {

            RectF oval3 = new RectF(coordinateX - (calculatedSize), coordinateY - (calculatedSize),
                    coordinateX + (calculatedSize) * 3, coordinateY + (calculatedSize));// 设置个新的长方形
            canvas.drawRoundRect(oval3, 4, 6, fillPaint);
            canvas.drawRoundRect(oval3, 4, 6, strokePaint);
        } else {
            canvas.drawCircle(coordinateX, coordinateY, calculatedSize, fillPaint);
            canvas.drawCircle(coordinateX, coordinateY, calculatedSize, strokePaint);
        }
    }

    public void setRadius(float newRadius) {
        this.radius = QAndroid.dp2px(getContext(),newRadius);
        activeRadius = radius  * 0.6f;
        constantRadius = activeRadius;
        this.invalidate();
    }

    public void setFillColor(int color) {
        this.fillColor = color;
        this.invalidate();
    }

    public void setStrokeColor(int color) {
        this.strokeColor = color;
        this.invalidate();
    }

    public void setShape(int shape) {
        this.shape = shape;
        if (shape == RECTANGLE) {
            SPACING_FACTOR *= 2;
        }
        this.invalidate();
    }

}

