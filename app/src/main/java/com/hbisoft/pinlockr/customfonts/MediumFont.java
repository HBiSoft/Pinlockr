package com.hbisoft.pinlockr.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class MediumFont extends androidx.appcompat.widget.AppCompatTextView {

    /*
     * Caches typefaces based on their file path and name, so that they don't have to be created every time when they are referenced.
     */
    private static Typeface mTypeface;

    public MediumFont(final Context context) {
        this(context, null);
    }

    public MediumFont(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediumFont(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/AgeoMedium.ttf");
        }
        setTypeface(mTypeface);
    }

}