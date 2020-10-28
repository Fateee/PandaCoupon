package com.panda.coupon.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;


import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;


/**
 * Created by yih on 2018/3/7.
 * Des:
 */

public final class DeviceUtil {
    public static final String TAG = DeviceUtil.class.getSimpleName();
    public static boolean isMeiZu = false;
    public static boolean isXiaomi = false;
    public static boolean isChuizi = false;
    public static boolean isNexus = false;
    public static boolean isHuawei = false;
    public static boolean isMeitu = false;
    public static boolean isSamsung = false;
    public static boolean isHuaweiG6C00 = false;

    public static boolean isMX3 = false;
    public static boolean isMX2 = false;
    public static boolean isMi2A = false;
    public static boolean isHwP8 = false;
    public static boolean isHwP7 = false;
    public static boolean isX86 = false;
    public static boolean isSumsungS7 = false;

    public static boolean isMeituOSv2 = false;
    public static boolean isMeituOSv3 = false;
    public static boolean isHw_CHMTL00 = false;

    public static boolean isHw_h60_L03 = false;
    public static boolean isHw_PE_TL20 = false;
    public static boolean isCoolpad = false;
    public static boolean isZTE = false;
    public static boolean isViVo = false;
    public static boolean isSony = false;
    public static boolean isOppo = false;
    public static String sIMEI = "";
    public static String version;
    public static  float density = 3.0f;

    static {
         /*检测厂商*/
        String model = Build.MODEL.trim().toLowerCase();
        if (Build.MANUFACTURER.trim().toLowerCase().equals("xiaomi")) {
            isXiaomi = true;
        }
        if (Build.MANUFACTURER.trim().toLowerCase().equals("meizu")) {
            isMeiZu = true;
        }
        if (Build.MODEL.trim().toLowerCase().contains("nexus")) {
            isNexus = true;
        }

        if (Build.MODEL.trim().toLowerCase().contains("zte")) {
            isZTE = true;
        }

        if (Build.MODEL.trim().toLowerCase().contains("vivo")) {
            isViVo = true;
        }

        if (Build.MODEL.trim().toLowerCase().contains("sony")) {
            isSony = true;
        }

        if (Build.MODEL.trim().toLowerCase().contains("g6-c00")) {
            isHuaweiG6C00 = true;
        }


        if (Build.MANUFACTURER.trim().toLowerCase().contains("huawei")) {
            isHuawei = true;
        }
        if (model.contains("coolpad")) {
            isCoolpad = true;
        }
        if (Build.MANUFACTURER.trim().toLowerCase().contains("samsung")) {
            isSamsung = true;
        }

        /*检测机型*/
        if (Build.MANUFACTURER.trim().toLowerCase().contains("meitu")) {
            isMeitu = true;
            String osVer = readVersion("ro.build.version.meios");
            if (!TextUtils.isEmpty(osVer)) {
                osVer = osVer.trim();
                if (osVer.startsWith("2")) {
                    isMeituOSv2 = true;
                } else if (osVer.startsWith("3")) {
                    isMeituOSv3 = true;
                }
            }
        }
        if (model.equalsIgnoreCase("m353") || Build.DEVICE.equalsIgnoreCase("mx3")) {
            isMX3 = true;
        } else if (model.equalsIgnoreCase("m040") || Build.DEVICE.equalsIgnoreCase("mx2")) {
            isMX2 = true;
        } else if (model.contains("2a")) {
            isMi2A = true;
        } else if (model.equalsIgnoreCase("ale-tl00") || Build.DEVICE.equalsIgnoreCase("hwALE-H")) {
            isHwP8 = true;
        } else if (model.equalsIgnoreCase("sm701") || Build.DEVICE.equalsIgnoreCase("msm8974sfo")) {
            isChuizi = true;
        } else if (model.equalsIgnoreCase("HUAWEI P7-L09")) {
            isHwP7 = true;
        } else if (model.equalsIgnoreCase("SM-G9350")) {
            isSumsungS7 = true;
        } else if (model.equalsIgnoreCase("CHM-TL00")) {
            isHw_CHMTL00 = true;
        } else if (model.equalsIgnoreCase("H60-L03")) {
            isHw_h60_L03 = true;
        } else if (model.equalsIgnoreCase("PE-TL20")) {
            isHw_PE_TL20 = true;
        }else if (model.equalsIgnoreCase("OPPO")) {
            isOppo = true;
        }

        /*检测平台 x86 x64*/
        if (Build.CPU_ABI != null && Build.CPU_ABI.toLowerCase().contains("x86")) {
            isX86 = true;
        }
    }

    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    //这个是获取手机系统版本号的
    public static String readVersion(String buildVersion) {
        String version = "";
        try {
            Class mClass = Class.forName("android.os.SystemProperties");
            Method getMethod = mClass.getDeclaredMethod("get", String.class);
            getMethod.setAccessible(true);
            version = (String) getMethod.invoke(null, buildVersion);

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return version;
    }

    public static boolean isMainProcess(Context c) {

        if(c == null)
            return false;

        ActivityManager am = (ActivityManager) c.getSystemService(Context
                .ACTIVITY_SERVICE);
        List processes = am.getRunningAppProcesses();
        String packageName = c.getPackageName();
        int myPid = Process.myPid();
        if (processes != null) {
            Iterator iterator = processes.iterator();

            ActivityManager.RunningAppProcessInfo processInfo;
            do {
                if (!iterator.hasNext()) {
                    return false;
                }
                processInfo = (ActivityManager.RunningAppProcessInfo) iterator.next();
            }
            while (processInfo.pid != myPid || !TextUtils.equals(packageName, processInfo
                    .processName));
        }
        return true;
    }


//    public static String getSsid() {
//        String ssid = DataManager.getInstance().accessToken;
//        if (TextUtils.isEmpty(ssid)) {
//            ssid = (String) SharedPreferencesHelper.getInstance().get(REACT_DATA, KEY_USER_SSID, "");
//        }
//        return ssid;
//    }
//
//    public static String getUtDid() {
//        String utDid;
//        if (SharedPreferencesHelper.getInstance().contains(REACT_DATA, KEY_DEVICE_ID)) {
//            utDid = (String) SharedPreferencesHelper.getInstance().get(REACT_DATA, KEY_DEVICE_ID, "");
//            if (TextUtils.isEmpty(utDid)) {
//                utDid = UTDevice.getUtdid(SingleContainer.getApplicationContext());
//                SharedPreferencesHelper.getInstance().put(REACT_DATA, KEY_DEVICE_ID, utDid);
//            }
//        } else {
//            utDid = UTDevice.getUtdid(SingleContainer.getApplicationContext());
//            SharedPreferencesHelper.getInstance().put(REACT_DATA, KEY_DEVICE_ID, utDid);
//        }
//        if (utDid.length() > 24) {
//            utDid = utDid.substring(0, 23);
//        } else if (utDid.length() < 24) {
//            int length = utDid.length();
//            for (int i = 24; i > length; i--) {
//                utDid = length + "f";
//            }
//        }
//        return utDid;
//    }
//
//    public static String getUid() {
//        return (String) SharedPreferencesHelper.getInstance().get(REACT_DATA, KEY_USER_UID, "");
//    }

}
