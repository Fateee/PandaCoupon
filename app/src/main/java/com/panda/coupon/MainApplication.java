package com.panda.coupon;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.lzh.nonview.router.RouterConfiguration;
import com.panda.coupon.router.DefaultInterceptor;
import com.panda.coupon.router.JMRouteManager;
import com.panda.coupon.router.RouterRuleCreator;
import com.panda.coupon.utils.DeviceUtil;
import com.panda.coupon.utils.PermissionUtils;
import com.panda.coupon.utils.SingleContainer;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import java.util.HashMap;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        SingleContainer.init(this);
        PermissionUtils.checkPermission(this, new PermissionUtils.Callback() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onRationale() {

            }

            @Override
            public void onDenied(Context context) {

            }
        },new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE});
        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d(TAG, " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
        DeviceUtil.version = BuildConfig.VERSION_NAME;
        JMRouteManager.get().init(context);
        RouterConfiguration.get().addRouteCreator(new RouterRuleCreator());
        RouterConfiguration.get().setInterceptor(DefaultInterceptor.get());
    }
}
