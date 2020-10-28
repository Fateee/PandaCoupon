package com.panda.coupon.jsbridge;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSON;
import com.panda.coupon.activity.BaseActivity;
import com.panda.coupon.router.RouteSchema;
import com.panda.coupon.utils.ConstantUtils;
import com.panda.coupon.router.JMRouter;
import com.tencent.smtt.sdk.WebView;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.panda.coupon.router.RouteSchema.*;
import static com.panda.coupon.utils.ConstantUtils.*;

/**
 * Created by congxiany on 18/3/7.
 */

public class JSBridgeUtil {
    private static boolean COLD_STARTED = false; //是否已经冷启动
    private static final int MSG_SCAN_RESULT_TIMEOUT = 1;
    public HashMap<String, SoftReference<WebView>> webCache = new HashMap<>();//web缓存
    public HashMap<String, String> callbackCache = new HashMap<>();//callback缓存
    public static ArrayList<SoftReference<JSBridgeUtil>> getUserInfoJsList = new ArrayList<SoftReference<JSBridgeUtil>>();
    public int number = 0;
    private Map<String, String> rightBtnCache = new HashMap<>();//右上角的按钮参数缓存
    WifiManager wifiManager;
    private Fragment attachFragment;

    private static class JSBridgeUtilHolder {
        static JSBridgeUtil instance = new JSBridgeUtil();
    }

    public static JSBridgeUtil getInstance() {
        return JSBridgeUtilHolder.instance;
    }

    private JSCallBack call = new JSCallBack() {
        @Override
        public void jsCall(String callFunc, JSCallbackRsp response, final WebView webView) {
            if (webView != null && webView.getContext() != null && !((Activity) webView.getContext()).isFinishing()) {
                String responseStr = response != null ? JSON.toJSONString(response) : "";
                StringBuilder callStr = new StringBuilder("javascript:window.JSBridge.");
                callStr.append(callFunc).append("('").append(responseStr).append("');");
                final String callUrl = callStr.toString();
                Log.d("jsbridge", "callback url = " + callUrl);
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= 19) {
                            webView.evaluateJavascript(callUrl, null);
                        } else {
                            webView.loadUrl(callUrl);
                        }
                    }
                });
            }
        }
    };
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_SCAN_RESULT_TIMEOUT:
//                    //time out
//                    if (context != null) {
//                        if (webCache.containsKey(JS_GET_WIFI_LIST) && callbackCache.containsKey(JS_GET_WIFI_LIST)) {
//                            jsCall(callbackCache.get(JS_GET_WIFI_LIST), -1, null, webCache.get(JS_GET_WIFI_LIST));
//                        }
//                        try {
//                            context.unregisterReceiver(wifiReceiver);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
//            }
//        }
//    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;

            String action = intent.getAction();
            switch (action) {
//                case LOGIN_NOTICE:
//                    String ssid = intent.getStringExtra("ssid");
//                    String uid = intent.getStringExtra("uid");
//                    handleWithRefreshResult(ssid, uid);
//                    break;
                case SCAN_RESULT_NOTICE:
                    int code = intent.getIntExtra("code", -120);
                    if (code != 0) {
                        handleWithScanCodeResult(code);
                    } else {
                        String from = intent.getStringExtra("from");
                        String type = intent.getStringExtra("type");
                        String result = intent.getStringExtra("result");
                        ArrayList<String> results = intent.getStringArrayListExtra("results");
                        handleWithScanCodeResult(code, from, type, result, results);
                    }
//                case CHOOSE_LOCATION:
//                    code = intent.getIntExtra("code", -1);
//                    String itemJson = intent.getStringExtra("item_json");
//                    handleChooseLocationResult(code, itemJson);
                    break;
            }

        }

    };

    private Context context;

    /**
     * 初始化定位
     */
    public void init(Context context) {
        this.context = context;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LOGIN_NOTICE);
        intentFilter.addAction(SCAN_RESULT_NOTICE);
//        intentFilter.addAction(CHOOSE_LOCATION);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);
    }

    public void setAttachFragment(Fragment attachFragment) {
        this.attachFragment = attachFragment;
    }

//    /**
//     * 初始化定位
//     *
//     * @param applicationContext
//     */
//    public static void initLocation(Context applicationContext) {
//        AMapLocationClient mLocationClient = new AMapLocationClient(applicationContext);//初始化定位
//        mLocationClient.setLocationListener(new AMapLocationListener() {
//            @Override
//            public void onLocationChanged(AMapLocation aMapLocation) {
//                if (aMapLocation != null) {
//                    if (aMapLocation.getErrorCode() == 0) {
//                        double latitude = aMapLocation.getLatitude();
//                        double longitude = aMapLocation.getLongitude();
//                        SharedPreferencesHelper.getInstance().put(TYPE_USER, KEY_LONGITUDE, String.valueOf(longitude)).put(TYPE_USER, KEY_LATITUDE, String.valueOf(latitude));
//                    } else {
//                        String errorMsg = "location Error, ErrCode:"
//                                + aMapLocation.getErrorCode() + ", errInfo:"
//                                + aMapLocation.getErrorInfo();
//                        JLog.d("AmapError", errorMsg);
//                    }
//                }
//            }
//        });//设置定位回调监听
//
//        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
//        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        mLocationOption.setInterval(5000);//5s定位一次
//        mLocationOption.setHttpTimeOut(20000);//定位超时时间 20s
//        mLocationOption.setNeedAddress(false);
//        mLocationClient.setLocationOption(mLocationOption);
//
//        if (!mLocationClient.isStarted()) {
//            mLocationClient.startLocation();
//        }
//    }


    /**
     * 处理js回调
     *
     * @param url
     * @param context
     * @param webViewSoftReference
     * @return
     */
    public void handleWithJSBridge(final String url, final Context context, final SoftReference<WebView> webViewSoftReference) {
        Uri uri = Uri.parse(url);
        Log.d("jsbridge", "url = " + url);
        final String host = uri.getHost();
        final String action = host + uri.getPath();
        final Map<String, String> params = uriParse(url);
        switch (action) {
//            case JS_GET_USER_INFO:
//                handleWithUserInfo(params, context, webViewSoftReference);
//                break;
//            case JS_GET_LOCATION:
//                handleWithLocation(params, context, webViewSoftReference);
//                break;
            case JS_JUMP:
                handleWithJump(params, context, webViewSoftReference);
                break;
//            case JS_MAKE_PHONE:
//                handleWithPhone(params, context);
//                break;
//            case JS_NAVIGATE:
//                handleWithNavigate(params, context);
//                break;
            case JS_SCAN_CODE:
                handleWithScanCode(params, context, webViewSoftReference);
                break;
//            case JS_GET_WIFI_LIST:
//                handleWithGetWifiList(params, context, webViewSoftReference);
//                break;
            case JS_SET_PAGE_TITLE:
                handleWithPageTitle(params, context, webViewSoftReference);
                break;
//            case JS_TOOGLE_BAR:
//                handleWithToggleBar(params, context);
//                break;
//            case JS_OPTION_BUTTON:
//                handleWithOptionBtn(params, context, webViewSoftReference);
//                break;
//            case JS_OPTION_BUTTON_CLICK:
//                handleWithOptionBtnClick(params, context, webViewSoftReference);
//                break;
//            case JS_TOGGLE_BACK_BUTTON:
//                handleWithToggleBackBtn(params, context);
//                break;
//            case JS_TOOGLE_NAVIGATION_BAR:
//                handleWithNavigateBar(params, context);
//                break;
//            case JS_CONNECT_WIFI:
//                handleWithConnectWifi(params, context, webViewSoftReference);
//                break;
//            case JS_DECRYPT:
//                handleWithDecrypt(params, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_BLE_CONNECTION_STATUS:
//                handleWithBleStatusChange(params, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_BLE_DEVICE_CHARACTER:
//                handleWithBleDeviceCharacter(params, context, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_BLE_DEVICE_CHARACTER_CHANGE:
//                handleWithBleDeivceCharacterChange(params, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_BLE_DEVICES:
//                handleWithBleDeivceServices(params, context, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_CLOSE:
//                handleWithBlueToothClose(params, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_CLOSE_BLE_CONNECTION:
//                handleWithBleConnectionClose(params, context, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_DEVICE_FOUND_LISTENER:
//                handleWithDeivceFoundListener(params, context, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_DEVICE_STATUS:
//                handleWithDeviceStatus(params, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_NOTIFY_BLE_DEVICE_CHARACTER:
//                handleWithBleCharacterNotify(params, context, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_OPEN:
//                handleWithBlueToothOpen(params, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_OPEN_BLE_CONNECTION:
//                handleWithBleConnectionOpen(params, context, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_READ_BLE_DEVICE_CHARACTER:
//                handleWithBleReadCharacter(params, context, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_START_DISCOVERY:
//                handleWithStartDiscovery(params, webViewSoftReference);
//                break;
//            case JS_BLE_START_DISCOVERY:
//                handleWithStartBleDiscovery(params, webViewSoftReference);
//                break;
//            case JS_BLE_STOP_DISCOVERY:
//                handleWithStopBleDiscovery(params, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_STATUS:
//                handleWithBlueToothStatus(params, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_WRITE_BLE_DEVICE_CHARACTER:
//                handleWithBleWriteCharacter(params, context, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_STOP_DISCOVERY:
//                handleWithStopDiscovery(params, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_DEVICES:
//                handleWithBlueToothDevices(params, webViewSoftReference);
//                break;
//            case JS_BLUETOOTH_STATUS_LISTENER:
//                handleWithBlueToothStateListener(params, context, webViewSoftReference);
//                break;
//            case JS_GET_APP_INFO:
//                handleWithGetAppInfo(params, context, webViewSoftReference);
//                break;
//            case JS_LOGOUT:
//                handleWithLogout(params, context, webViewSoftReference);
//                break;
//            case JS_CHOOSE_IMAGE:
//                handleWithChooseImage(params, context, webViewSoftReference);
//                break;
//            case JS_ON_CHOOSE_IMAGE_UPLOAD:
//                handleWithOnChooseImageUpload(params, context, webViewSoftReference);
//                break;
//            case JS_ON_IMAGE_UPLOADING:
//                handleWithOnImageUpload(params, context, webViewSoftReference);
//                break;
//            case JS_ON_DELETE_UPLOADING:
//                handleWithDeleteUploading(params, context, webViewSoftReference);
//                break;
//            case JS_ON_RETRY_IMG_UPLOAD:
//                handleWithRetryImgUpload(params, context, webViewSoftReference);
//                break;
//            case JS_UPDATE_APP:
//                handleWithUpdateApp(params, context, webViewSoftReference);
//                break;
//            case JS_PULL_REFRESH:
//                handleWithPullRefresh(params, context, webViewSoftReference);
//            case JS_SET_STORAGE:
//                handleWithSetStorage(params, context, webViewSoftReference);
//                break;
//            case JS_GET_STORAGE:
//                handleWithGetStorage(params, context, webViewSoftReference);
//                break;
//            case JS_REPORT_ERROR:
//                handleWithReportError(params, context, webViewSoftReference);
//                break;
//            case JS_GESTURE_PASSWORD:
//                handleWithGesturePassword(params, context, webViewSoftReference);
//                break;
//            case JS_CLOSE_CONTAINER:
//                handleWithCloseContainer(params, context, webViewSoftReference);
//                break;
//            case JS_CHOOSE_LOCATION:
//                handleWithChooseLocation(params, context, webViewSoftReference);
//                break;
//            case JS_REFRESH_TAB_INDICATION:
//                refreshHomeTabIndication();
//                break;
//            case JS_SET_BUTTON_BACK:
//                handleJsButtonBack(params);
//                break;
//            case JS_LOCAL_NOTIFICATION:
//                handleLocalNotification(params, context, webViewSoftReference);
//                break;
//            case JS_SHOW:
//            case JS_HIDE:
////                jsCall("", ConstantUtils.CODE_SUCC, webViewSoftReference);
//                break;
//            case share_action://分享
//                handleWithShare(context, params, url, webViewSoftReference);
//                break;
//            case share_js://分享监听
//                handleWithShareListener(context, params, webViewSoftReference);
//                break;
//            case PREVIEW_FILES:
//                handleWithPreviewFiles(context, params, webViewSoftReference);
//                break;
            case JS_APP_COLD_START:
                handleWithColdStart(webViewSoftReference, params);
                break;
            default:
                final String callFunc = params.containsKey("callback") ? params.get("callback") : "";
                jsCall(callFunc, ConstantUtils.CODE_COMPATIBLE, webViewSoftReference);
                break;
        }
    }

    private void handleWithColdStart(SoftReference<WebView> webViewSoftReference, Map<String, String> params) {
        if (!COLD_STARTED) {
            final String callFunc = params.containsKey("callback") ? params.get("callback") : "";
            jsCall(callFunc, ConstantUtils.CODE_SUCC, webViewSoftReference);
            COLD_STARTED = true;
        }
    }

//    private void handleLocalNotification(Map<String, String> params, Context context, SoftReference<WebView> webViewSoftReference) {
//        String timeStamp = params.get("timestamp");
//        try {
//            long scheduleTime = Long.parseLong(timeStamp);
//            long currentSeconds = System.currentTimeMillis() / 1000;
//            long delayDuration = scheduleTime - currentSeconds;
//            if (delayDuration < 0) {
//                return;
//            }
//            if (BuildConfig.DEBUG) {
//                Log.e("local_notification", delayDuration + " s ");
//            }
//            String title = params.get("title");
//            String body = params.get("body");
//            String scheme = params.get("scheme");
//            Data data = new Data
//                    .Builder()
//                    .putString("title", title)
//                    .putString("body", body)
//                    .putString("scheme", scheme)
//                    .build();
//            OneTimeWorkRequest notificationWorkRequest = new OneTimeWorkRequest
//                    .Builder(LocalNotificationWorker.class)
//                    .setInitialDelay(delayDuration, TimeUnit.SECONDS)
//                    .setInputData(data)
//                    .build();
//            WorkManager.getInstance().
//                    enqueue(notificationWorkRequest);
//        } catch (NumberFormatException e) {
//            //do nothing
//        }
//        String callFunc = params.get("callback");
//        if (!TextUtils.isEmpty(callFunc))
//            jsCall(callFunc, 0, null, webViewSoftReference);
//    }

//    private void handleJsButtonBack(Map<String, String> params) {
//        final String setButtonBack = params.containsKey("action") ? params.get("action") : "";
//        if (attachFragment != null && attachFragment instanceof WebFragment) {
//            ((WebFragment) attachFragment).mJSSetGoback = "show".equalsIgnoreCase(setButtonBack);
//        }
//    }
//
//    private void refreshHomeTabIndication() {
//        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(REFRESH_TAB_INDICATION));
//    }
//
//    private void handleWithRetryImgUpload(Map<String, String> params, Context context, SoftReference<WebView> webViewSoftReference) {
//        final String key = params.containsKey("key") ? new String(params.get("key")) : "";
//        final String sourceType = params.containsKey("sourceType") ? new String(params.get("sourceType")) : "";
//        if (context instanceof BaseWebActivity) {
//            ((BaseWebActivity) context).retryUpload(key,sourceType);
//        }
//    }
//
//    private void handleWithDeleteUploading(Map<String, String> params, Context context, SoftReference<WebView> webViewSoftReference) {
//        final String key = params.containsKey("key") ? new String(params.get("key")) : "";
//        final String action = params.containsKey("action") ? new String(params.get("action")) : "";
//        final String sourceType = params.containsKey("sourceType") ? new String(params.get("sourceType")) : "";
//        if (context instanceof BaseWebActivity) {
//            ((BaseWebActivity) context).deleteUpload(action, key,sourceType);
//        }
//    }
//
//    private void handleWithChooseLocation(Map<String, String> params, Context context,
//                                          SoftReference<WebView> webViewSoftReference) {
//        Bundle bundle = new Bundle();
//        for (Map.Entry<String, String> entry : params.entrySet()) {
//            String key = entry.getKey();
//            String value = entry.getValue();
//            bundle.putString(key, value);
//            if ("callback".equals(key)) {
//                webCache.put(JS_CHOOSE_LOCATION, webViewSoftReference);
//                callbackCache.put(JS_CHOOSE_LOCATION, value);
//            }
//        }
//        JMRouter.create(RouteSchema.CHOOSE_LOCATION).addExtras(bundle).open(context);
//    }
//
//    private void handleWithCloseContainer(Map<String, String> params, Context context, SoftReference<WebView> webViewSoftReference) {
//        if (context instanceof Activity) {
//            ((Activity) context).finish();
//        }
//    }
//
//    private void handleWithGesturePassword(Map<String, String> params, Context context, SoftReference<WebView> webViewSoftReference) {
//        String eventName = params.containsKey("event_name") ? params.get("event_name") : "";
//        String action = params.containsKey("action") ? params.get("action") : "";
//        JMRouter.create(RouteSchema.LOCK_VIEW_ITEM_MAIN).open(context);
//    }
//
//    private void handleWithReportError(Map<String, String> params, Context context, SoftReference<WebView> webViewSoftReference) {
//        String eventName = params.containsKey("event_name") ? params.get("event_name") : "";
////    String paramJson = params.containsKey("params") ? params.get("params") : "";
////    JSONObject jsonObject = JSON.parseObject(paramJson);
////    Map<String, String> itemMap = JSONObject.toJavaObject(jsonObject, Map.class);
////    Map itemMap = JSON.parseObject(paramJson);
//        SAStatistics.onTrack(eventName, params);
//    }
//
//    private void handleWithGetStorage(Map<String, String> params, Context context, SoftReference<WebView> webViewSoftReference) {
//        String key = params.containsKey("key") ? params.get("key") : "";
//        String callFunc = params.containsKey("callback") ? params.get("callback") : "";
//        if (TextUtils.isEmpty(callFunc)) return;
//        if (!TextUtils.isEmpty(key)) {
//            String data = (String) SharedPreferencesHelper.getInstance().get(REACT_DATA, key, "");
//            Map<String, Object> map = new HashMap<>();
//            map.put("value", data);
//            jsCall(callFunc, ConstantUtils.CODE_SUCC, map, webViewSoftReference);
//        }
//    }
//
//    private void handleWithSetStorage(Map<String, String> params, Context context, SoftReference<WebView> webViewSoftReference) {
//        String key = params.containsKey("key") ? params.get("key") : "";
//        String data = params.containsKey("data") ? params.get("data") : "";
//        if (!TextUtils.isEmpty(key)) {
//            SharedPreferencesHelper.getInstance().put(REACT_DATA, key, data);
//        }
//    }
//
//    private void handleWithPullRefresh(Map<String, String> params, Context context, SoftReference<WebView> webViewSoftReference) {
//        final String refresh = params.containsKey("refresh") ? params.get("refresh") : "1";
//        if (attachFragment != null && attachFragment instanceof WebFragment) {
//            if ("0".equalsIgnoreCase(refresh)) {
//                ((WebFragment) attachFragment).interceptRefresh = true;
//            } else {
//                ((WebFragment) attachFragment).interceptRefresh = false;
//            }
//        }
//
//    }
//
//    private void handleWithUpdateApp(Map<String, String> params, Context context, SoftReference<WebView> webViewSoftReference) {
//        final String downLoadUrl = params.containsKey("download_url") ? params.get("download_url") : "";
//        if (!TextUtils.isEmpty(downLoadUrl)) {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.addCategory(Intent.CATEGORY_BROWSABLE);
//            intent.setData(Uri.parse(downLoadUrl));
//            context.startActivity(intent);
//        } else {
//            Beta.checkUpgrade();
//        }
//    }
//
//    private void handleWithOnImageUpload(Map<String, String> params, Context context, SoftReference<WebView> webViewSoftReference) {
//        final String callFunc = params.containsKey("callback") ? params.get("callback") : "";
//        if (TextUtils.isEmpty(callFunc)) return;
//        webCache.put(JS_ON_IMAGE_UPLOADING, webViewSoftReference);
//        callbackCache.put(JS_ON_IMAGE_UPLOADING, callFunc);
//    }
//
//    private void handleWithOnChooseImageUpload(Map<String, String> params, Context context, SoftReference<WebView> webViewSoftReference) {
//        final String callFunc = params.containsKey("callback") ? params.get("callback") : "";
//        if (TextUtils.isEmpty(callFunc)) return;
//        webCache.put(JS_ON_CHOOSE_IMAGE_UPLOAD, webViewSoftReference);
//        callbackCache.put(JS_ON_CHOOSE_IMAGE_UPLOAD, callFunc);
//    }
//
//    private void handleWithChooseImage(Map<String, String> params, final Context context, final SoftReference<WebView> webViewSoftReference) {
//        final String callFunc = params.containsKey("callback") ? params.get("callback") : "";
//        final String onChooseCallString = callbackCache.get(JS_ON_CHOOSE_IMAGE_UPLOAD);
//        final SoftReference<WebView> onChooseCallWebViewSoftReference = webCache.get(JS_ON_CHOOSE_IMAGE_UPLOAD);
//        final String onUploadingString = callbackCache.get(JS_ON_IMAGE_UPLOADING);
//        final SoftReference<WebView> onUploadingWebViewSoftReference = webCache.get(JS_ON_IMAGE_UPLOADING);
//        final String id = params.containsKey("id") ? new String(params.get("id")) : "";
//        if (TextUtils.isEmpty(callFunc) || TextUtils.isEmpty(onChooseCallString) || onChooseCallWebViewSoftReference == null)
//            return;
//        int count = 1;
//        try {
//            count = Integer.parseInt(params.containsKey("count") ? params.get("count") : "1");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        final String sourceType = params.containsKey("sourceType") ? params.get("sourceType") : "";
//        final String compress = params.containsKey("compress") ? params.get("compress") : "1";
//        //用于app判断获取本地视频的时长如果比该值大 则回调failuare code-3
//        final String maxSeconds = params.containsKey("maxSeconds") ? params.get("maxSeconds") : "";
//        final String path = params.containsKey("path") ? params.get("path") : "";
//        final String version = params.containsKey("version") ? params.get("version") : "";
//        Log.d("DemoLog", "sourceType=" + sourceType);
//        if (context instanceof BaseWebActivity) {
//            ((BaseWebActivity) context).showImagePickDialog(version, path, id, sourceType.split("\\|"), count, compress, null, maxSeconds,new ChooseImageCallBack() {
//
//                @Override
//                public void selectCount(String id, int count, List<LocalMedia> selectList) {
//                    Map<String, Object> data = new HashMap<>();
//                    if (!TextUtils.isEmpty(id)) {
//                        data.put("id", id);
//                    }
//                    if (count >= 0) {
//                        data.put("count", count);
//                    }
//                    if (selectList != null && selectList.size() > 0) {
//                        ArrayList<String> list = new ArrayList<>();
//                        ArrayList<String> localPaths = new ArrayList<>();
//                        for (LocalMedia localMedia : selectList) {
//                            list.add(localMedia.key);
//                            if (localMedia.getMimeType() == PictureMimeType.ofVideo()) {
//                                localPaths.add(LOCAL_FILE_SCHEME+localMedia.thumbPath);
//                            } else {
//                                localPaths.add(LOCAL_FILE_SCHEME+localMedia.getPath());
//                            }
//                        }
//                        data.put("keys", list);
//                        data.put("local_paths", localPaths);
//                    }
//                    jsCall(callFunc, ConstantUtils.CODE_SUCC, data, webViewSoftReference);
//                }
//
//                @Override
//                public void selectUploading(String id, String key, long total, long progress, String compress_status) {
//                    Map<String, Object> data = new HashMap<>();
//                    data.put("id", id);
//                    data.put("file_size", total);
//                    data.put("upload_size", progress);
//                    data.put("key", key);
//                    data.put("compress",compress_status);
//                    jsCall(onUploadingString, 0, data, "", onUploadingWebViewSoftReference);
//                }
//
//                @Override
//                public void postImgInfo(int code, String key, String msgId, String message, String name, String imgUrl, String thumb_url, String orientation, int width, int height, String video_url) {
//                    Map<String, Object> data = new HashMap<>();
//                    data.put("id", msgId);
//                    data.put("name", name);
//                    data.put("imgUrl", imgUrl);
//                    data.put("thumb_url", thumb_url);
//                    data.put("orientation", orientation);
//                    data.put("width", width);
//                    data.put("height", height);
//                    data.put("key", key);
//                    //上传完的视频地址 video_url
//                    data.put("view_url", video_url);
//                    jsCall(onChooseCallString, code, data, message, onChooseCallWebViewSoftReference);
//                }
//            });
//        }
//
//    }
//
//    private void handleWithGetWifiList(Map<String, String> params, Context context, SoftReference<WebView> webViewSoftReference) {
//        final String callFunc = params.containsKey("callback") ? params.get("callback") : "";
//        if (TextUtils.isEmpty(callFunc)) return;
//
//        if (wifiManager == null) {
//            wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        }
//        boolean isSuccess = true;
//        if (!wifiManager.isWifiEnabled()) {
//            isSuccess = wifiManager.setWifiEnabled(true);
//        }
//        if (isSuccess) {
//            wifiManager.startScan();
//            handler.removeCallbacksAndMessages(MSG_SCAN_RESULT_TIMEOUT);
//            handler.sendEmptyMessageDelayed(MSG_SCAN_RESULT_TIMEOUT, 15 * 1000);//15s的扫描时间
//            context.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//            if (!TextUtils.isEmpty(callFunc)) {
//                webCache.put(JS_GET_WIFI_LIST, webViewSoftReference);
//                callbackCache.put(JS_GET_WIFI_LIST, callFunc);
//            }
//        } else {
//            jsCall(callFunc, -1, null, webViewSoftReference);
//        }
//    }
//
//    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            handler.removeMessages(MSG_SCAN_RESULT_TIMEOUT);
//            if (!webCache.containsKey(JS_GET_WIFI_LIST)) return;
//            if (!callbackCache.containsKey(JS_GET_WIFI_LIST)) return;
//            List<ScanResult> result = wifiManager.getScanResults();
//            if (result != null && result.size() > 0) {
//                result = filterScanResults(result);
//                List<WifiScanResult> wifiScanResultList = new ArrayList<>();
//                for (ScanResult scanResult : result) {
//                    WifiScanResult wifiScanResult = new WifiScanResult();
//                    wifiScanResult.ssid = scanResult.SSID;
//                    wifiScanResult.bssid = scanResult.BSSID;
//                    wifiScanResult.secure = scanResult.capabilities;
//                    wifiScanResult.signalStrength = scanResult.level + "";
//                    wifiScanResultList.add(wifiScanResult);
//                }
//                Map<String, Object> data = new HashMap<>();
//                data.put("wifiList", wifiScanResultList);
//                jsCall(callbackCache.get(JS_GET_WIFI_LIST), ConstantUtils.CODE_SUCC, data, webCache.get(JS_GET_WIFI_LIST));
//            }
//            context.unregisterReceiver(this);
//        }
//    };
//
//    /**
//     * 过滤wifi列表
//     *
//     * @param data
//     * @return
//     */
//    private List<ScanResult> filterScanResults(List<ScanResult> data) {
//        List<ScanResult> result = new ArrayList<>();
//        for (ScanResult scanResult : data) {
//            if (TextUtils.isEmpty(scanResult.SSID)) {
//                continue;
//            }
//            boolean hasSsid = false;
//            for (int i = 0; i < result.size(); i++) {
//                if (result.get(i).SSID.equalsIgnoreCase(scanResult.SSID)) {
//                    if (scanResult.level > result.get(i).level) {
//                        result.set(i, scanResult);
//                    }
//                    hasSsid = true;
//                    break;
//                }
//            }
//            if (!hasSsid) {
//                result.add(scanResult);
//            }
//        }
//        return result;
//    }
//
//    private void handleWithGetAppInfo(Map<String, String> params, Context context, SoftReference<WebView> webViewSoftReference) {
//        final String callFunc = params.containsKey("callback") ? params.get("callback") : "";
//        if (TextUtils.isEmpty(callFunc)) return;
//        Map<String, Object> data = new HashMap<>();
//        data.put("env", RnEnvUtils.getRnEnv());
//        data.put("apiEnv", RnEnvUtils.getRnApiEnv());
//        data.put("version", DeviceUtil.version);
//        jsCall(callFunc, ConstantUtils.CODE_SUCC, data, webViewSoftReference);
//
//    }
//
//    private void handleWithLogout(Map<String, String> params, final Context context, SoftReference<WebView> webViewSoftReference) {
//        if (context instanceof BaseActivity && !StringDefaultUtils.isLogining) {
//            ActivityStackHelper.layoutClear();
//            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
//            Intent intent = new Intent(GO_TO_LOGIN);
//            localBroadcastManager.sendBroadcast(intent);
//
//            final String callFunc = params.containsKey("callback") ? params.get("callback") : "";
//            if (TextUtils.isEmpty(callFunc)) return;
//            jsCall(callFunc, ConstantUtils.CODE_SUCC, null, webViewSoftReference);
//            if (webViewSoftReference.get() != null) {
//                webViewSoftReference.get().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("DemoLog", "clearWebviewCache");
//                        WebViewUtils.clearWebviewCache(context);
//                        if (context instanceof WebViewActivity) {
//                            ((Activity) context).finish();
//                        }
//                    }
//                }, 2 * 1000);
//            }
//        }
//    }

    /**
     * js回调
     *
     * @param callFunc
     * @param code
     * @param body
     * @param msg
     * @param webViewSoftReference
     */
    private void jsCall(String callFunc, int code, Map<String, Object> body, String msg, SoftReference<WebView> webViewSoftReference) {
        if (TextUtils.isEmpty(callFunc) || webViewSoftReference == null) return;
        WebView webView = webViewSoftReference.get();
        if (webView == null) return;
        try {
            if (!TextUtils.isEmpty(msg)) msg = URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        call.jsCall(callFunc, new JSCallbackRsp(code, body, msg), webView);
    }

    private void jsCall(String callFunc, int code, Map<String, Object> body, SoftReference<WebView> webViewSoftReference) {
        jsCall(callFunc, code, body, "", webViewSoftReference);
    }

    private void jsCall(String callFunc, int code, SoftReference<WebView> webViewSoftReference) {
        jsCall(callFunc, code, null, webViewSoftReference);
    }




//    /**
//     * 设置右上角按钮
//     *
//     * @param params
//     * @param context
//     */
//    private void handleWithOptionBtn(final Map<String, String> params, final Context context, final SoftReference<WebView> webViewSoftReference) {
//        final String action = params.containsKey("action") ? params.get("action") : "";
//        final String type = params.containsKey("type") ? params.get("type") : "";
//        final String scanType = params.containsKey("scan_type") ? params.get("scan_type") : "";
//        final String imgUrl = params.containsKey("img_url") ? params.get("img_url") : "";
//        final String content = params.containsKey("content") ? params.get("content") : "";
//        rightBtnCache.clear();
//        rightBtnCache.put("type", type);
//
//        if (context instanceof BaseActivity) {
//            ((BaseActivity) context).setToolbarRightIcon("show".equals(action) ? View.VISIBLE : View.GONE, type, imgUrl, content);
//        }
//
//    }
//
//    /**
//     * 设置后退按钮
//     *
//     * @param params
//     * @param context
//     */
//    private void handleWithToggleBackBtn(final Map<String, String> params, final Context context) {
//        final String action = params.containsKey("action") ? params.get("action") : "";
//
//        if (context instanceof BaseWebActivity) {
//            ((BaseWebActivity) context).setToolbarBackVisible("show".equals(action));
//        }
//    }
//
//    /**
//     * 切换导航栏
//     *
//     * @param params
//     * @param context
//     */
//    private void handleWithNavigateBar(final Map<String, String> params, final Context context) {
//        final String action = params.containsKey("action") ? params.get("action") : "";
//        if (context instanceof BaseActivity) {
//            if ("show".equalsIgnoreCase(action)) {
//                ((BaseActivity) context).setToolbarVisible(true);
//            } else {
//                ((BaseActivity) context).setToolbarVisible(false);
//            }
//        }
//    }
//
//    /**
//     * 切换底栏
//     *
//     * @param params
//     * @param context
//     */
//    private void handleWithToggleBar(final Map<String, String> params, final Context context) {
//        final String action = params.containsKey("action") ? params.get("action") : "";
//        Log.d("DemoLog", "handleWithToggleBar=" + action);
//        if (context instanceof BaseActivity) {
//            if ("show".equalsIgnoreCase(action)) {
//                ((BaseActivity) context).changeBottomBarVisibility(View.VISIBLE);
//            } else {
//                ((BaseActivity) context).changeBottomBarVisibility(View.GONE);
//            }
//        }
//
//    }
//
//    /**
//     * 监听右上角按钮点击
//     *
//     * @param params
//     * @param context
//     * @param webViewSoftReference
//     */
//    private void handleWithOptionBtnClick(final Map<String, String> params, final Context context, final SoftReference<WebView> webViewSoftReference) {
//        if (context instanceof BaseActivity) {
//            final String callFunc = params.containsKey("callback") ? params.get("callback") : "";
//            ((BaseActivity) context).setToolbarRightIconListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if ("scan".equalsIgnoreCase(rightBtnCache.get("type"))) {
//                        handleWithScanCode(params, context, webViewSoftReference);
//                    } else {
//                        jsCall(callFunc, ConstantUtils.CODE_SUCC, null, webViewSoftReference);
//                    }
//                }
//            });
//        }
//    }
//
//    /**
//     * 获取用户信息
//     *
//     * @param params
//     * @param webViewSoftReference
//     */
//    private void handleWithUserInfo(final Map<String, String> params, Context context, final SoftReference<WebView> webViewSoftReference) {
//        final String refreshStr = params.containsKey("refresh") ? params.get("refresh") : "";
//        final String callFunc = params.containsKey("callback") ? params.get("callback") : "";
//        if (webViewSoftReference == null) return;
//
//        if (!TextUtils.isEmpty(refreshStr) && !"0".equals(refreshStr)) {
//            //韩国环境
//            if ("kr".equalsIgnoreCase(LocaleUtils.getInstance().country_code)) {
//                Log.d("DemoLog", "h5 refreshStr kr");
//                //多个webview同时调用refresh时先把之前的暂存，登录完成后再都回调
//                if (!StringDefaultUtils.isLogining) {
//                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
//                    Intent intent = new Intent(GO_TO_LOGIN);
//                    intent.putExtra(KEY_LOGIN_REFRESH, "1");
//                    localBroadcastManager.sendBroadcast(intent);
//                } else {
//
//                }
//                if (!TextUtils.isEmpty(callFunc)) {
//                    webCache.put(JS_GET_USER_INFO, webViewSoftReference);
//                    callbackCache.put(JS_GET_USER_INFO, callFunc);
//                    getUserInfoJsList.add(new SoftReference<>(JSBridgeUtil.this));
//                }
//            } else {
//                Log.d("DemoLog", "h5 refreshStr");
//                TokenUtils.refreshToken(context, DataManager.getInstance().refreshToken, new JDHttpRequest.INetworkListener() {
//                    @Override
//                    public void onSuccess(BaseResponseEntity response) {
//                        Log.d("DemoLog", "h5 refreshStr refresh success");
//                        if (!TextUtils.isEmpty(callFunc)) {
//                            Map<String, Object> data = new HashMap<>();
//                            data.put("sid", DataManager.getInstance().accessToken);
//                            data.put("uid", (String) SharedPreferencesHelper.getInstance().get(REACT_DATA, KEY_USER_UID, ""));
//                            jsCall(callFunc, ConstantUtils.CODE_SUCC, data, webViewSoftReference);
//                        }
//                    }
//
//                    @Override
//                    public void onError(int code, ErrorResponseEntity errorMsg) {
//                        Log.d("DemoLog", "h5 refreshStr refresh fail to login");
//                        AkUserManagerModule.realLogout();
//                    }
//                });
//            }
////          if(!(Boolean) SharedPreferencesHelper.getInstance().get(TYPE_SETTINGS, KEY_IS_REFRESH_TOKEN, false)) {
////            TokenUtils.refreshToken(reactContext, DataManager.getInstance().refreshToken, new JDHttpRequest.INetworkListener() {
////              @Override
////              public void onSuccess(BaseResponseEntity response) {
////                Log.d("DemoLog","h5 refreshStr refresh success");
////                if(!TextUtils.isEmpty(callFunc)) {
////                  Map<String, Object> data = new HashMap<>();
////                  data.put("sid", DataManager.getInstance().accessToken);
////                  data.put("uid", (String) SharedPreferencesHelper.getInstance().get(REACT_DATA, KEY_USER_UID, ""));
////                  jsCall(callFunc, ConstantUtils.CODE_SUCC, data, webViewSoftReference);
////                }
////              }
////              @Override
////              public void onError(int code, ErrorResponseEntity errorMsg) {
////                Log.d("DemoLog","h5 refreshStr refresh fail to login");
////                AkUserManagerModule.realLogout();
////              }
////            });
////          }else{
////            Log.d("DemoLog","h5 refreshStr locked");
////            if(DataManager.getInstance().refreshTokenCallBack == null) {
////              DataManager.getInstance().setRefreshCallBack(new DataManager.RefreshTokenCallBack() {
////                @Override
////                public void onChange() {
////                  Log.d("DemoLog","h5 refreshStr locked onchange");
////                  handleWithRefreshResult(DataManager.getInstance().accessToken, (String) SharedPreferencesHelper.getInstance().get(REACT_DATA, KEY_USER_UID, ""));
////                  DataManager.getInstance().refreshTokenCallBack = null;
////                }
////              });
////            }
////            getUserInfoJsList.add(new SoftReference<>(JSBridgeUtil.this));
////          }
////      }
//        } else {
//            SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance();
//            String sid = (String) sharedPreferencesHelper.get(REACT_DATA, KEY_USER_SSID, "");
//            String uid = (String) sharedPreferencesHelper.get(REACT_DATA, KEY_USER_UID, "");
//            if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(sid)) {
//                Map<String, Object> data = new HashMap<>();
//                data.put("sid", sid);
//                data.put("uid", uid);
//                if (!TextUtils.isEmpty(callFunc)) {
//                    jsCall(callFunc, ConstantUtils.CODE_SUCC, data, webViewSoftReference);
//                }
//            } else {
//                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
//                Intent intent = new Intent(GO_TO_LOGIN);
//                localBroadcastManager.sendBroadcast(intent);
//                if (!TextUtils.isEmpty(callFunc)) {
//                    webCache.put(JS_GET_USER_INFO, webViewSoftReference);
//                    callbackCache.put(JS_GET_USER_INFO, callFunc);
//                }
//            }
//        }
//    }
//
//    /**
//     * 获取用户信息回调
//     *
//     * @param ssid
//     * @param uid
//     */
//    public void handleWithRefreshResult(String ssid, String uid) {
//        if (!TextUtils.isEmpty(ssid) && !TextUtils.isEmpty(uid)) {
//            Iterator<SoftReference<JSBridgeUtil>> i = getUserInfoJsList.iterator();
//            while (i.hasNext()) {
//                SoftReference<JSBridgeUtil> jsBridgeUtilSoftReference = i.next(); //必须在remove之前调用
//                JSBridgeUtil tempJsBridge = jsBridgeUtilSoftReference.get();
//                if (tempJsBridge != null) {
//                    if (!tempJsBridge.webCache.containsKey(JS_GET_USER_INFO)) break;
//                    if (!tempJsBridge.callbackCache.containsKey(JS_GET_USER_INFO)) break;
//                    Map<String, Object> data = new HashMap<>();
//                    data.put("sid", ssid);
//                    data.put("uid", uid);
//                    tempJsBridge.jsCall(tempJsBridge.callbackCache.get(JS_GET_USER_INFO), ConstantUtils.CODE_SUCC, data, tempJsBridge.webCache.get(JS_GET_USER_INFO));
//                }
//                i.remove();
//            }
//
//        }
//    }
//
//    /**
//     * 定位模式是否打开
//     *
//     * @return
//     */
//    private static boolean isLocationServiceEnabled(Context context) {
//        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        if (gps || network) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 获取定位
//     *
//     * @param params
//     * @param context
//     * @param webViewSoftReference
//     */
//    public void handleWithLocation(final Map<String, String> params, final Context context, final SoftReference<WebView> webViewSoftReference) {
//        final String type = params.containsKey("type") ? params.get("type") : "";
//        final String callFunc = params.containsKey("callback") ? params.get("callback") : "";
//
//        if (TextUtils.isEmpty(callFunc) || webViewSoftReference == null) return;
//
//        boolean checkFineResult = PermissionChecker.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED;
//        boolean checkCoarseResult = PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED;
//
//        if (isLocationServiceEnabled(context) && (checkCoarseResult || checkFineResult)) {
//            final SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance();
//            if (sharedPreferencesHelper.contains(TYPE_USER, KEY_LATITUDE)) {
//                handleWithLocationResult(true, type, callFunc, webViewSoftReference);
//            } else {
//                Observable.intervalRange(1, 4, 0, 5, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
//                    Disposable disposable;
//
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        disposable = d;
//                    }
//
//                    @Override
//                    public void onNext(Long value) {
//                        if (sharedPreferencesHelper.contains(TYPE_USER, KEY_LATITUDE)) {
//                            if (disposable != null && !disposable.isDisposed())
//                                disposable.dispose();
//                            handleWithLocationResult(true, type, callFunc, webViewSoftReference);
//                        } else {
//                            if (value == 4)
//                                handleWithLocationResult(false, type, callFunc, webViewSoftReference);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        handleWithLocationResult(false, type, callFunc, webViewSoftReference);
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//            }
//        } else {
//            DialogHelper.showAlertDialog(context, "", "定位权限关闭，前往开启？", "取消", "确认", null, new ConfirmDialog.IClickListener() {
//                @Override
//                public void click(Dialog dialog) {
//                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
//                    if (context instanceof Activity)
//                        context.startActivity(intent);
//                }
//            });
//            handleWithLocationResult(false, type, callFunc, webViewSoftReference);
//        }
//    }

//    /**
//     * 连接WIFI
//     *
//     * @param params
//     * @param context
//     * @param webViewSoftReference
//     */
//    private void handleWithConnectWifi(final Map<String, String> params, final Context context, final SoftReference<WebView> webViewSoftReference) {
//        String ssid = params.containsKey("ssid") ? params.get("ssid") : "";
//        String password = params.containsKey("password") ? params.get("password") : "";
//        String timeout = params.containsKey("timeout") ? params.get("timeout") : "";
//
//        final String callFunc = params.containsKey("callback") ? params.get("callback") : "";
//
//        if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(timeout)) return;
//
//        if (WifiMgr.checkIsWifiConnect(ssid, context)) {
//            jsCall(callFunc, ConstantUtils.CODE_SUCC, webViewSoftReference);
//            return;
//        }
//
//        WifiMgr wifiMgr = new WifiMgr();
//
//        wifiMgr.networkConnectTest(context.getApplicationContext(), ssid, password, WifiMgr.TYPE_WPA, StringUtils.parseToLong(timeout), new WifiMgr.TestCallback() {
//            @Override
//            public void fail(int code, String message) {
//                jsCall(callFunc, code, null, message, webViewSoftReference);
//            }
//
//            @Override
//            public void error(int code, Throwable e) {
//                jsCall(callFunc, code, null, e != null ? e.getMessage() : "", webViewSoftReference);
//            }
//
//            @Override
//            public void succ() {
//                jsCall(callFunc, ConstantUtils.CODE_SUCC, webViewSoftReference);
//            }
//        });
//
//    }

//    /**
//     * 导航
//     *
//     * @param params
//     * @param context
//     */
//    private void handleWithNavigate(final Map<String, String> params, final Context context) {
//        String lat = params.containsKey("latitude") ? params.get("latitude") : "";
//        String lon = params.containsKey("longitude") ? params.get("longitude") : "";
//
//        NavigateChain.showNavDlg(lat, lon, (Activity) context);
//    }

//    /**
//     * 处理定位结果
//     *
//     * @param type
//     * @param callFunc
//     * @param webViewSoftReference
//     */
//    private void handleWithLocationResult(final boolean isEnabled, final String type, final String callFunc, final SoftReference<WebView> webViewSoftReference) {
//        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance();
//        String longitude = isEnabled ? (String) sharedPreferencesHelper.get(TYPE_USER, KEY_LONGITUDE, "0") : "0";
//        String latitude = isEnabled ? (String) sharedPreferencesHelper.get(TYPE_USER, KEY_LATITUDE, "0") : "0";
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("longitude", longitude);
//        data.put("latitude", latitude);
//        jsCall(callFunc, isEnabled ? ConstantUtils.CODE_SUCC : ConstantUtils.CODE_LOCATION_ERROR, data, webViewSoftReference);
//    }

    /**
     * 页面跳转
     *
     * @param params
     * @param webViewSoftReference
     */
    private void handleWithJump(final Map<String, String> params, final Context context, final SoftReference<WebView> webViewSoftReference) {
        if (!TextUtils.isEmpty(params.get("scheme"))) {
            JMRouter.create(params.get("scheme")).open(context);
        }
    }

    /**
     * 添加页面标题
     *
     * @param params
     * @param context
     */
    private void handleWithPageTitle(final Map<String, String> params, final Context context, final SoftReference<WebView> webViewSoftReference) {
        String title = params.containsKey("title") ? params.get("title") : "";
        if (TextUtils.isEmpty(title) || webViewSoftReference == null) return;
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).setPageTitle(title);
        }
//    if (reactContext != null) {//设置RN的页面标题
//      WritableMap map = new WritableNativeMap();
//      map.putString("value", title);
//
//      sendEventMsgToRn(reactContext, ConstantUtils.ACTION_SET_PAGETITLE, map, webViewSoftReference);
//    }
    }

//    /**
//     * 打电话
//     *
//     * @param params
//     * @param context
//     */
//    private void handleWithPhone(final Map<String, String> params, final Context context) {
//        String phone = params.containsKey("phone") ? params.get("phone") : "";
//        if (TextUtils.isEmpty(phone)) return;
//        PhoneUtils.callPhone(context, phone);
//    }
//
//    /**
//     * 解密
//     *
//     * @param params
//     * @param webViewSoftReference
//     */
//    private void handleWithDecrypt(final Map<String, String> params, final SoftReference<WebView> webViewSoftReference) {
//        String pwd = params.containsKey("password") ? params.get("password") : "";
//        String index = params.containsKey("key_index") ? params.get("key_index") : "";
//        String callFunc = params.containsKey("callback") ? params.get("callback") : "";
//        if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(index) || TextUtils.isEmpty(callFunc))
//            return;
//        String decodePwd = EncryptUtils.decrypt(pwd, index);
//        Map<String, Object> body = new HashMap<>();
//        body.put("password", decodePwd);
//        jsCall(callFunc, ConstantUtils.CODE_SUCC, body, webViewSoftReference);
//    }

    /**
     * 打开扫码页
     *
     * @param params
     * @param context
     */
    private void handleWithScanCode(final Map<String, String> params, final Context context, final SoftReference<WebView> webViewSoftReference) {
        String type = params.containsKey("scan_type") ? params.get("scan_type") : "";
        String callFunc = params.containsKey("callback") ? params.get("callback") : "";
        String support_multi = params.containsKey("support_multi") ? params.get("support_multi") : "0";
        String limit_num = params.containsKey("limit_num") ? params.get("limit_num") : "1";
        String error_message = params.containsKey("error_message") ? params.get("error_message") : "";
        String scan_title = params.containsKey("scan_title") ? params.get("scan_title") : "";
        String page_title = params.containsKey("page_title") ? params.get("page_title") : "";
        String disable_input = params.containsKey("disable_input") ? params.get("disable_input") : "";
        String enable_album = params.containsKey("enable_album") ? params.get("enable_album") : "";
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(type)) bundle.putString("type", type);
        if (!TextUtils.isEmpty(callFunc)) bundle.putString("callback", callFunc);
        if (!TextUtils.isEmpty(support_multi)) bundle.putString("support_multi", support_multi);
        if (!TextUtils.isEmpty(error_message)) bundle.putString("error_message", error_message);
        if (!TextUtils.isEmpty(limit_num))
            bundle.putInt("limit_num", Integer.parseInt(limit_num));
        if (!TextUtils.isEmpty(scan_title)) bundle.putString("scan_title", scan_title);
        if (!TextUtils.isEmpty(page_title)) bundle.putString("page_title", page_title);
        if (!TextUtils.isEmpty(disable_input)) bundle.putString("disable_input", disable_input);
        if (!TextUtils.isEmpty(enable_album)) bundle.putString("enable_album", enable_album);
        JMRouter router = JMRouter.create(RouteSchema.PAGE_SCAN);
        if (!bundle.isEmpty()) router.addExtras(bundle);
        router.open(context);
//    Intent intent = new Intent();
//    intent.putExtras(bundle);
//    intent.setClass(context,ScanCodeActivity.class);
//    context.startActivity(intent);
        if (!TextUtils.isEmpty(callFunc)) {
            webCache.put(JS_SCAN_CODE, webViewSoftReference);
            callbackCache.put(JS_SCAN_CODE, callFunc);
        }
    }

    /**
     * 处理扫码结果
     *
     * @param code
     * @param from
     * @param type
     * @param result
     */
    public void handleWithScanCodeResult(int code, final String from, final String type, final String result, ArrayList<String> results) {
        if (!webCache.containsKey(JS_SCAN_CODE)) return;
        if (!callbackCache.containsKey(JS_SCAN_CODE)) return;
        if (!TextUtils.isEmpty(from)) {
            Map<String, Object> body = new HashMap<>();
            body.put("from", from);
            if (!TextUtils.isEmpty(type)) body.put("scan_type", type);
            if (!TextUtils.isEmpty(result)) {
                try {
                    body.put("result", URLEncoder.encode(result, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    body.put("results", results);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            jsCall(callbackCache.get(JS_SCAN_CODE), code, body, webCache.get(JS_SCAN_CODE));
        } else {
            jsCall(callbackCache.get(JS_SCAN_CODE), code, webCache.get(JS_SCAN_CODE));
        }
    }

    /**
     * 处理扫码结果
     *
     * @param code
     */
    public void handleWithScanCodeResult(int code) {
        handleWithScanCodeResult(code, "", "", "", null);
    }

//    /**
//     * 地图位置选择结果回调
//     */
//    private void handleChooseLocationResult(int code, String itemJson) {
//        Map<String, Object> body = JSON.parseObject(itemJson, new TypeReference<Map<String, Object>>() {
//        });
//        if (body == null) {
//            body = new HashMap<>();
//        }
//        jsCall(callbackCache.get(JS_CHOOSE_LOCATION), code, body, webCache.get(JS_CHOOSE_LOCATION));
//    }
//
//    /**
//     * 调用RN
//     *
//     * @param reactContext
//     * @param eventName
//     * @param params
//     */
//    public static void sendEventMsgToRn(final ReactContext reactContext, String eventName, @Nullable WritableMap params, @NonNull final SoftReference<WebView> webViewSoftReference) {
//        WebView webView = null;
//        if (reactContext != null && (webView = webViewSoftReference.get()) != null) {
//            reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
//                    webView.getId(),
//                    eventName,
//                    params);
//        }
//    }

    /**
     * uri参数解析
     *
     * @param url
     * @return
     */
    private static Map<String, String> uriParse(String url) {
        Map<String, String> result = new HashMap<>();
        if (url.contains("?")) {
            int beginIndex = url.indexOf("?") + 1;
            if (url.length() == beginIndex) return result;
            String query = url.substring(beginIndex);
            if (query == null || "".equals(query)) return result;
            List<String> splits = splitParam(query);
            for (String param : splits) {
                String[] keyValue = param.split("=");
                if (keyValue.length >= 2) {
                    StringBuilder strBuilder = new StringBuilder("");
                    for (int index = 1; index < keyValue.length; index++) {
                        if (index > 1) strBuilder.append("=");
                        strBuilder.append(keyValue[index]);
                    }
                    result.put(new String(keyValue[0]), strBuilder.toString());
                }
            }
        }
        return result;
    }

    /**
     * 分割参数
     *
     * @param str
     * @return
     */
    private static List<String> splitParam(String str) {
        List<String> splits = new ArrayList<>();
        StringBuilder paramStr = new StringBuilder();
        int braketCount = 0;
        for (int index = 0; index < str.length(); index++) {
            char value = str.charAt(index);
            switch (value) {
                case '[':
                case '{':
                    braketCount++;
                    paramStr.append(value);
                    break;
                case ']':
                case '}':
                    braketCount--;
                    paramStr.append(value);
                    break;
                case '&':
                    if (braketCount == 0) {
                        splits.add(URLDecoder.decode(paramStr.toString()));
                        paramStr = new StringBuilder();
                    } else {
                        paramStr.append(value);
                    }
                    break;
                default:
                    paramStr.append(value);
                    break;
            }
        }
        splits.add(paramStr.toString());
        return splits;
    }

    public void onDestroy() {
        try {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
//            context.unregisterReceiver(wifiReceiver);
        } catch (Exception e) {

        }
    }

    public interface JSCallBack {
        void jsCall(String callFunc, JSCallbackRsp response, WebView webView);
    }

//    /**
//     * 分享
//     */
//
//    void handleWithShare(final Context context, final Map<String, String> params, final String url, final SoftReference<WebView> webViewSoftReference) {
//        final String shareContent = params.containsKey("share_content") ? params.get("share_content") : "";
//        final String sharePlatform = params.containsKey("share_platform") ? params.get("share_platform") : "";
//
//        final ShareContentWebPage content = JSON.parseObject(shareContent, ShareContentWebPage.class);
//
//        String callFunc = context instanceof BaseActivity ? ((BaseActivity) context).getShareCallBackFunc() : "";
//        if (TextUtils.isEmpty(callFunc)) callFunc = "share";
//
//        List<GridDialogBean> beans = buildGridItems(sharePlatform);
//        if (beans.size() == 1) {
//            realShare(beans.get(0).type, content, callFunc, context, webViewSoftReference);
//        } else {
//            String finalCallFunc = callFunc;
//            DialogHelper.showGridDialog(context, beans, (bean, view) -> realShare(bean.type, content, finalCallFunc, context, webViewSoftReference));
//        }
//    }
//
//    /**
//     * 构建GridItem参数
//     *
//     * @param sharePlatform
//     * @return
//     */
//    static List<GridDialogBean> buildGridItems(String sharePlatform) {
//        Map<String, GridDialogBean> map = new HashMap<>();
//        for (int index = 0; index < com.jiedianxia.baselib.ConstantUtils.SHARE_TYPES.length; index++) {
//            GridDialogBean gridDialogBean = new GridDialogBean();
//            gridDialogBean.type = com.jiedianxia.baselib.ConstantUtils.SHARE_TYPES[index];
//            gridDialogBean.text = com.jiedianxia.baselib.ConstantUtils.SHARE_TITLES[index];
//            gridDialogBean.imgRes = com.jiedianxia.baselib.ConstantUtils.SHARE_IMGS[index];
//            map.put(gridDialogBean.type, gridDialogBean);
//        }
//
//        List<GridDialogBean> chooseList = null;
//        if (!TextUtils.isEmpty(sharePlatform)) {
//            sharePlatform = URLDecoder.decode(sharePlatform);
//            chooseList = JSON.parseArray(sharePlatform, GridDialogBean.class);
//        }
//
//        if (chooseList == null || chooseList.isEmpty()) {
//            chooseList = new ArrayList<>();
//            for (int index = 0; index < com.jiedianxia.baselib.ConstantUtils.SHARE_TYPES.length; index++) {
//                chooseList.add(map.get(com.jiedianxia.baselib.ConstantUtils.SHARE_TYPES[index]));
//            }
//        } else {
//            int len = chooseList.size();
//            for (int index = 0; index < len; index++) {
//                GridDialogBean bean = chooseList.get(index);
//                chooseList.set(index, map.get(bean.type));
//            }
//        }
//        return chooseList;
//    }
//
//    /**
//     * 实际分享
//     *
//     * @param type
//     * @param content
//     * @param callFunc
//     */
//    private void realShare(final String type, final ShareContentWebPage content, final String callFunc, final Context context, final SoftReference<WebView> webViewSoftReference) {
//        if (type == SsoShareType.COPY_LINK) {
//            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
//            ClipData clipData = ClipData.newPlainText("share_link", content.getURL());
//            clipboardManager.setPrimaryClip(clipData);
//            JMToast.show("链接已复制到粘贴板");
//        } else {
//            try {
//                final String imageUrl = type.equals(SsoShareType.WEIXIN_FRIEND) && !TextUtils.isEmpty(content.mp_path) ? (!TextUtils.isEmpty(content.mp_image_url) ? content.mp_image_url : content.image_url) : content.image_url;
//                Observable.create(new ObservableOnSubscribe<Bitmap>() {
//                    @Override
//                    public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<Bitmap> emitter) throws Exception {
//                        Bitmap bitmap = Glide.with(context).asBitmap().load(imageUrl).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
//                        emitter.onNext(bitmap);
//
//                    }
//                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Bitmap>() {
//                    @Override
//                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@io.reactivex.annotations.NonNull Bitmap bitmap) {
//                        content.setThumbBmp(bitmap);
//
////                        if (!TextUtils.isEmpty(callFunc) && webViewSoftReference != null) {
////                            call.jsCall(callFunc, new JSCallbackRsp(0, new JSShareResult("click", type), null), webViewSoftReference.get());
////                        }
//                        SsoShareManager.share(context, type, content, new SsoShareManager.ShareStateListener() {
//                            @Override
//                            public void onSuccess(Object response) {
//                                super.onSuccess(response);
//                                if (!TextUtils.isEmpty(callFunc) && webViewSoftReference != null) {
//                                    call.jsCall(callFunc, new JSCallbackRsp(0, new JSShareResult("success", type), null), webViewSoftReference.get());
//                                }
//                                JMToast.show("分享成功");
//                            }
//
//                            @Override
//                            public void onCancel() {
//                                super.onCancel();
//                                JMToast.show("取消分享");
//                            }
//
//                            @Override
//                            public void onError(int code, String msg) {
//                                super.onError(code, msg);
//                                if (!TextUtils.isEmpty(callFunc) && webViewSoftReference != null)
//                                    call.jsCall(callFunc, new JSCallbackRsp(CODE_SHARE_ERROR, null, msg), webViewSoftReference.get());
//                                JMToast.show(msg);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
//
//                        String event = "图片下载抛出异常" + e.getMessage();
//                        JLog.e(event);
//
//                        if (BuildConfig.DEBUG) {
//                            JMToast.show(event);
//                        } else {
//                            JMToast.show("图片下载失败");
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    void handleWithShareListener(final Context context, final Map<String, String> params, final SoftReference<WebView> webViewSoftReference) {
//        final String action_type = params.containsKey("action") ? params.get("action") : "";
//        final String callFunc = params.containsKey("callback") ? params.get("callback") : "";
//        if (TextUtils.isEmpty(action_type)) return;
//        if (context instanceof BaseActivity) {
//            ((BaseActivity) context).setShareCallBackFunc("register".equals(action_type) ? callFunc : "");
//        }
//        if ("destroy".equals(action_type) && webViewSoftReference != null && !TextUtils.isEmpty(callFunc))
//            call.jsCall(callFunc, null, webViewSoftReference.get());
//    }
//
//
//    private void handleWithPreviewFiles(Context context, Map<String, String> params, SoftReference<WebView> webViewSoftReference) {
//        String current = params.containsKey("current") ? params.get("current") : "";
//        String callFunc = params.containsKey("callback") ? params.get("callback") : "";
//        String files = params.containsKey("files") ? params.get("files") : "0";
//        String watermark = params.containsKey("watermark") ? params.get("watermark") : null;
//        Bundle bundle = new Bundle();
//        if (!TextUtils.isEmpty(current)) bundle.putString("current", current);
//        if (!TextUtils.isEmpty(callFunc)) bundle.putString("callback", callFunc);
//        if (!TextUtils.isEmpty(files)) bundle.putString("files", files);
//        bundle.putString("watermark", watermark);//可为null或者""
//        JMRouter router = JMRouter.create(RouteSchema.PAGE_PREVIEW_FILES);
//        if (!bundle.isEmpty()) router.addExtras(bundle);
//        router.open(context);
//        if (!TextUtils.isEmpty(callFunc)) {
////            webCache.put(JS_SCAN_CODE, webViewSoftReference);
////            callbackCache.put(JS_SCAN_CODE, callFunc);
//        }
//    }
}
