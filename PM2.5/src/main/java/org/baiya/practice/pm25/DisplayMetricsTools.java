package org.baiya.practice.pm25;

import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DisplayMetricsTools {

    public static int dpToPx(float px, final DisplayMetrics displayMetrics) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, displayMetrics) + 0.5F);
    }

}
