package com.hbisoft.pinlockr.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class LightFont extends androidx.appcompat.widget.AppCompatTextView {

    /*
     * Caches typefaces based on their file path and name, so that they don't have to be created every time when they are referenced.
     */
    private static Typeface mTypeface;

    public LightFont(final Context context) {
        this(context, null);
    }

    public LightFont(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LightFont(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/AgeoThin.ttf");
        }
        setTypeface(mTypeface);
    }

}
