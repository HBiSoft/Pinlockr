package com.hbisoft.pinlockr;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.IntDef;
import androidx.core.view.ViewCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class IndicatorDots extends LinearLayout {

    @IntDef({IndicatorType.FIXED, IndicatorType.FILL, IndicatorType.FILL_WITH_ANIMATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IndicatorType {
        int FIXED = 0;
        int FILL = 1;
        int FILL_WITH_ANIMATION = 2;
    }

    private static final int DEFAULT_PIN_LENGTH = 4;

    private int mDotDiameter;
    private int mDotSpacing;
    private int mFillDrawable;
    private int mEmptyDrawable;
    private int mPinLength;
    private int mIndicatorType;

    private int mPreviousLength;

    public IndicatorDots(Context context) {
        this(context, null);
    }

    public IndicatorDots(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorDots(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PinlockrView);

        try {
            mDotDiameter = (int) typedArray.getDimension(R.styleable.PinlockrView_dotDiameter, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_dot_diameter));
            mDotSpacing = (int) typedArray.getDimension(R.styleable.PinlockrView_dotSpacing, ResourceUtils.getDimensionInPx(getContext(), R.dimen.default_dot_spacing));
            mFillDrawable = typedArray.getResourceId(R.styleable.PinlockrView_dotFilledBackground, R.drawable.dot_filled);
            mEmptyDrawable = typedArray.getResourceId(R.styleable.PinlockrView_dotEmptyBackground, R.drawable.dot_empty);
            mPinLength = typedArray.getInt(R.styleable.PinlockrView_pinLength, DEFAULT_PIN_LENGTH);
            mIndicatorType = typedArray.getInt(R.styleable.PinlockrView_indicatorType,
                    IndicatorType.FIXED);
        } finally {
            typedArray.recycle();
        }

        initView(context);
    }

    private void initView(Context context) {
        ViewCompat.setLayoutDirection(this, ViewCompat.LAYOUT_DIRECTION_LTR);
        if (mIndicatorType == 0) {
            for (int i = 0; i < mPinLength; i++) {
                View dot = new View(context);
                emptyDot(dot);

                LayoutParams params = new LayoutParams(mDotDiameter,
                        mDotDiameter);
                params.setMargins(mDotSpacing, 0, mDotSpacing, 0);
                dot.setLayoutParams(params);

                addView(dot);
            }
        } else if (mIndicatorType == 2) {
            setLayoutTransition(new LayoutTransition());
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // If the indicator type is not fixed
        if (mIndicatorType != 0) {
            ViewGroup.LayoutParams params = this.getLayoutParams();
            params.height = mDotDiameter;
            requestLayout();
        }
    }

    void updateDot(int length) {
        if (mIndicatorType == 0) {
            if (length > 0) {
                if (length > mPreviousLength) {
                    fillDot(getChildAt(length - 1));
                } else {
                    emptyDot(getChildAt(length));
                }
                mPreviousLength = length;
            } else {
                // When {@code mPinLength} is 0, we need to reset all the views back to empty
                for (int i = 0; i < getChildCount(); i++) {
                    View v = getChildAt(i);
                    emptyDot(v);
                }
                mPreviousLength = 0;
            }
        } else {
            if (length > 0) {
                if (length > mPreviousLength) {
                    View dot = new View(getContext());
                    fillDot(dot);

                    LayoutParams params = new LayoutParams(mDotDiameter,
                            mDotDiameter);
                    params.setMargins(mDotSpacing, 0, mDotSpacing, 0);
                    dot.setLayoutParams(params);

                    addView(dot, length - 1);
                } else {
                    removeViewAt(length);
                }
                mPreviousLength = length;
            } else {
                removeAllViews();
                mPreviousLength = 0;
            }
        }
    }

    private void emptyDot(View dot) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        // Extract color from the default empty drawable
        Drawable originalDrawable = getResources().getDrawable(mEmptyDrawable, getContext().getTheme());
        if (originalDrawable instanceof GradientDrawable) {
            int defaultColor = ((GradientDrawable) originalDrawable).getColor().getDefaultColor();
            drawable.setColor(defaultColor);
        } else {
            // Fallback to greyish if the drawable type is unexpected
            drawable.setColor(getResources().getColor(android.R.color.darker_gray, getContext().getTheme()));
        }
        drawable.setSize(mDotDiameter, mDotDiameter);
        dot.setBackground(drawable);
    }

    private void fillDot(View dot) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        if (wasFillSet && mCustomFillColor != 0) {
            drawable.setColor(mCustomFillColor);
        } else {
            // Extract color from the default filled drawable
            Drawable originalDrawable = getResources().getDrawable(mFillDrawable, getContext().getTheme());
            if (originalDrawable instanceof GradientDrawable) {
                int defaultColor = ((GradientDrawable) originalDrawable).getColor().getDefaultColor();
                drawable.setColor(defaultColor);
            } else {
                // Fallback to black if the drawable type is unexpected
                drawable.setColor(getResources().getColor(android.R.color.black, getContext().getTheme()));
            }
        }
        drawable.setSize(mDotDiameter, mDotDiameter);
        dot.setBackground(drawable);
    }

    public int getPinLength() {
        return mPinLength;
    }

    public void setPinLength(int pinLength) {
        this.mPinLength = pinLength;
        removeAllViews();
        initView(getContext());
    }

    public
    @IndicatorType
    int getIndicatorType() {
        return mIndicatorType;
    }

    public void setIndicatorType(@IndicatorType int type) {
        this.mIndicatorType = type;
        removeAllViews();
        initView(getContext());
    }

    public void setEmptyDotColor(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        drawable.setSize(mDotDiameter, mDotDiameter);
        for (int i = 0; i < getChildCount(); i++) {
            View dot = getChildAt(i);
            if (i >= mPreviousLength) { // Empty dots are those beyond the current length
                dot.setBackground(drawable);
            }
        }
    }

    private int mCustomFillColor = 0; // Store the last set fill color
    private boolean wasFillSet = false;
    public void setFillDotColor(int color) {
        wasFillSet = true;
        mCustomFillColor = color;
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        drawable.setSize(mDotDiameter, mDotDiameter);
        Log.e("IndicatorDots", "Setting fill color to " + Integer.toHexString(color) + ", mPreviousLength: " + mPreviousLength + ", childCount: " + getChildCount());
        for (int i = 0; i < mPreviousLength && i < getChildCount(); i++) {
            View dot = getChildAt(i);
            if (dot != null) {
                dot.setBackground(drawable);
                Log.e("IndicatorDots", "Applied fill color to dot at index " + i);
            }
        }
    }
}
