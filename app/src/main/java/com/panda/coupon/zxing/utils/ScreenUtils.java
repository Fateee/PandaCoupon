package com.panda.coupon.zxing.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

/**
 * ScreenUtils
 * <ul>
 * <strong>Convert between dp and sp</strong>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-14
 */
public class ScreenUtils {

    ScreenUtils() {
        throw new AssertionError();
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth(@NonNull Context context) {

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight(@NonNull Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenHeight = dm.heightPixels;
        return screenHeight;
    }

}
