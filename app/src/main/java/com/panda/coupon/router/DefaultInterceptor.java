package com.panda.coupon.router;

import android.content.Context;
import android.net.Uri;

import com.lzh.nonview.router.extras.RouteBundleExtras;
import com.lzh.nonview.router.interceptors.RouteInterceptor;



/**
 * Created by yih on 2017/5/23.
 * Des:
 */

public class DefaultInterceptor implements RouteInterceptor {
    static DefaultInterceptor interceptor = new DefaultInterceptor();

    public static DefaultInterceptor get() {
        return interceptor;
    }

    @Override
    public boolean intercept(Uri uri, RouteBundleExtras extras, Context context) {
        return false;
    }

    @Override
    public void onIntercepted(Uri uri, RouteBundleExtras extras, Context context) {
    }

}
