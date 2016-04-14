package com.suntiago.sunandroidframe.util;

/**
 * Created by yu.zai on 2016/4/5.
 */

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public static int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                res.getDisplayMetrics());
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, context.getResources().getDisplayMetrics());

        return (int) scale;
    }

    /**
     * Returns true if the string is null or 0-length.
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }

    public static String timet(long time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        String times = sdr.format(new Date(time));
        return times;

    }
}
