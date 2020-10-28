package com.panda.coupon.utils;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.panda.coupon.BuildConfig;
import com.panda.coupon.R;


/**
 * Created by congxiany on 18/3/6.
 */

public class JMToast {
    private static final boolean SHOW_DEBUG = BuildConfig.DEBUG;

    private static final int DEFAULT_GRAVITY = Gravity.CENTER;
    private static final int DEFAULT_DURATION = Toast.LENGTH_LONG;
    private static final int DEFAULT_X_OFFSET = 0;
    private static final int DEFAULT_Y_OFFSET = 0;
    private static final float DEFAULT_HORIZONTAL_MARGIN = 0;
    private static final float DEFAULT_VERTICAL_MARGIN = 0;

    private static Toast toast;
    private static CharSequence oldMsg;
    private static long oneTime = 0;
    private static long twoTime = 0;

    public static class ToastParams {
        public int duration = DEFAULT_DURATION;
        public int gravity = DEFAULT_GRAVITY;
        public int xOffset = DEFAULT_X_OFFSET;
        public int yOffset = DEFAULT_Y_OFFSET;
        // margin以横向和纵向的百分比设置显示位置，参数均为float类型(水平位移正右负左，竖直位移正上负下)
        public float horizontalMargin = DEFAULT_HORIZONTAL_MARGIN;
        public float verticalMargin = DEFAULT_VERTICAL_MARGIN;
        public View view;
    }

    /**
     * showDebug  仅用于在开发阶段中弹出的DEBUG信息便于调试查看，正式版本中会去掉 <br>
     * <p>
     * show       在正式版本中弹出的Toast信息给用户展示
     */
    public static void showDebug(CharSequence msg) {
        showDebug(msg, DEFAULT_DURATION);
    }

    public static void showDebug(CharSequence msg, int duration) {
        showDebug(msg, duration, Gravity.BOTTOM);
    }

    public static void showDebug(CharSequence msg, int duration, int gravity) {
        ToastParams params = new ToastParams();
        params.duration = duration;
        params.gravity = gravity;
        params.verticalMargin = 0.1f;
        showDebug(msg, params);
    }

    public static void showDebug(final CharSequence msg, final ToastParams params) {
        if (SHOW_DEBUG) {
            show(msg, params);
        }
    }

    /**
     * showDebug  仅用于在开发阶段中弹出DEBUG信息便于调试查看，正式版本中会去掉 <br>
     * <p>
     * show       在正式版本中弹出给用户展示的Toast信息
     */
    public static void show(CharSequence msg) {
        show(msg, DEFAULT_DURATION);
    }

    public static void show(CharSequence msg, int duration) {
        show(msg, duration, DEFAULT_GRAVITY);
    }

    public static void show(CharSequence msg, int duration, int gravity) {
        ToastParams params = new ToastParams();
        params.duration = duration;
        params.gravity = gravity;
        show(msg, params);
    }

    public static void show(final CharSequence msg, final ToastParams params) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        SingleContainer.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                prepareToast(msg, params);
            }
        });
    }

    private static void prepareToast(CharSequence text, ToastParams params) {
        //判断当同时多次调用toast时会依次逐个显示所有toast的问题
        //改成会及时更新当前最新的text
        if (toast == null) {
            toast = new Toast(SingleContainer.getApplicationContext());
            showToast(text, params);
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (text.equals(oldMsg)) {
                int duration = DEFAULT_DURATION;
                if (params != null) {
                    duration = params.duration;
                }
                if (twoTime - oneTime > duration) {
                    showToast(text, params);
                }
            } else {
                oldMsg = text;
                showToast(text, params);
            }
        }
        oneTime = twoTime;
    }

    private static void showToast(CharSequence text, ToastParams params) {
        if (params != null) {
            if (params.duration == Toast.LENGTH_LONG || params.duration == Toast.LENGTH_SHORT) {
                toast.setDuration(params.duration);
            }
            toast.setGravity(params.gravity, params.xOffset, params.yOffset);
            toast.setMargin(params.horizontalMargin, params.verticalMargin);
            if (params.view != null) {
                toast.setView(params.view);
            } else {
                toast.setView(View.inflate(SingleContainer.getApplicationContext(), R.layout.layout_toast, null));
            }
        }
        toast.setText(text);
        toast.show();
    }
}
