package com.panda.coupon.utils;

import android.content.Context;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.List;

/**
 * Created by yih on 2019/6/5.
 * Des:
 */

public class PermissionUtils {
    public interface Callback {

        void onGranted() ;

        void onRationale() ;

        void onDenied(Context context) ;

    }
    public static void checkPermission(final Context context, final Callback callback , final String[] permission) {
        AndPermission.with(context)
                .runtime()
                .permission(permission).onGranted(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                callback.onGranted();
            }
        }).rationale(new Rationale<List<String>>() {
            @Override
            public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                callback.onRationale();
                executor.execute();
            }
        }).onDenied(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                if (AndPermission.hasAlwaysDeniedPermission(context, permission)){
                    callback.onDenied(context);
                }
            }
        }).start();
    }
}
