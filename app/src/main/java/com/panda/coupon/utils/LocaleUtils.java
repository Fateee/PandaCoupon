package com.panda.coupon.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by yih on 2018/4/17.
 * Des:
 */

public class LocaleUtils {
    private static LocaleUtils INSTANCE;
    public String language_code = "";
    public String country_code = "";
    public String script_code = "";
    public String time_zone = "";
    ViewGroup statusView ;
    public static LocaleUtils getInstance() {
        if (INSTANCE == null) {
            synchronized (LocaleUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocaleUtils();
                }
            }
        }
        return INSTANCE;
    }
    public void init(Context context){
        Locale locale;
        Configuration configuration = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(configuration.getLocales().size()>0) {
                locale = configuration.getLocales().get(0);
            }else{
                locale = configuration.locale;
            }
        } else {
            locale = configuration.locale;
        }
        language_code = locale.getLanguage();
        if(TextUtils.isEmpty(country_code)){
            country_code = locale.getCountry();
        }else{
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                if ("cn".equalsIgnoreCase(country_code)){
                    configuration.setLocale(Locale.SIMPLIFIED_CHINESE);
                }else if("kr".equalsIgnoreCase(country_code)) {
                    configuration.setLocale(Locale.KOREA);
                }else{
                    configuration.setLocale(Locale.ENGLISH);
                }
                context.getResources().updateConfiguration(configuration,displayMetrics);
            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            script_code = locale.getScript();
        }
        TimeZone tz = TimeZone.getDefault();
        time_zone = tz.getDisplayName(false, TimeZone.SHORT);
//        if(!"online".equalsIgnoreCase(RnEnvUtils.getRnApiEnv())) {
//            WindowManager manager = ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
//            WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
//            if(statusView!=null){
//                manager.removeView(statusView);
//                statusView = null;
//            }
//            localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
//            localLayoutParams.gravity = Gravity.TOP;
//            localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
//            localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//            localLayoutParams.height = DensityUtils.dip2px(24);
//            localLayoutParams.format = PixelFormat.TRANSPARENT;
//            statusView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.status_bar_local, null, false);
//            TextView textview = statusView.findViewById(R.id.textview);
//            textview.setText(RnEnvUtils.getRnApiEnv()+" "+country_code);
//            manager.addView(statusView, localLayoutParams);
//        }else{
//            if(statusView!=null){
//                WindowManager manager = ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
//                manager.removeView(statusView);
//                statusView = null;
//            }
//        }
    }
    public boolean isChina(){
        if(country_code!=null&&country_code.length()>=2){
            return "cn".equalsIgnoreCase(country_code.substring(0,2));
        }else{
            return true;
        }

    }
    public boolean notKr(){
        if(country_code!=null&&country_code.length()>=2){
            return !"kr".equalsIgnoreCase(country_code.substring(0,2));
        }else{
            return true;
        }
    }
}
