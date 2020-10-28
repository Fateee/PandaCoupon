package com.panda.coupon.router;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.lzh.nonview.router.Router;
import com.lzh.nonview.router.interceptors.RouteInterceptor;
import com.lzh.nonview.router.route.IActivityRoute;
import com.lzh.nonview.router.route.RouteCallback;
import com.lzh.nonview.router.tools.Utils;
import com.panda.coupon.utils.ConstantUtils;
import com.tencent.smtt.sdk.WebView;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 页面跳转
 */

public final class JMRouter {
    String url;
    Bundle extras = new Bundle();
    int requestCode = -1;
    int inAnimation = -1;
    int outAnimation = -1;
    int flags;
    SoftReference<WebView> webView = null;
    RouteCallback callback;

    ArrayList<RouteInterceptor> interceptors = new ArrayList<>();
    private ActionCallback actionCallback;

    JMRouter() {
    }

    /**
     * 创建一个JMRouter对象。并初始化url。
     *
     * @param url <p>
     *            当url的schema为http/https时。跳转到ImgURLActivity并传递url
     *            当url为非http/https时。根据schema规则匹配本地页面并跳转
     *            </p>
     * @return JMRouter
     */
    public static JMRouter create(String url) {
        JMRouter router = new JMRouter();
        router.url = url;
        return router;
    }

    /**
     * 添加额外的Bundle数据。
     */
    public JMRouter addExtras(Bundle extras) {
        this.extras.putAll(extras);
        return this;
    }

    public JMRouter addFlag(int flag) {
        this.flags |= flag;
        return this;
    }

    /**
     * 用于js回调
     *
     * @param webView
     * @return
     */
    public JMRouter addWebView(WebView webView) {
        this.webView = new SoftReference<WebView>(webView);
        return this;
    }

    public JMRouter requestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    /**
     * 设置转场动画
     */
    public JMRouter setAnim(int inAnimation, int outAnimation) {
        this.inAnimation = inAnimation;
        this.outAnimation = outAnimation;
        return this;
    }

    public JMRouter setCallback(RouteCallback callback) {
        if (callback != null) {
            this.callback = callback;
        }
        return this;
    }

    public JMRouter addInterceptor(RouteInterceptor interceptor) {
        if (interceptor != null) {
            this.interceptors.add(interceptor);
        }
        return this;
    }

    public JMRouter addActionCallback(ActionCallback actionCallback) {
        this.actionCallback = actionCallback;
        return this;
    }
    /**
     * 启动
     */
    public void open(Context context) {
        if (TextUtils.isEmpty(url)) return;

        Uri uri = Uri.parse(url);
        if (Utils.isHttp(uri.getScheme())) {
            if (url.endsWith(".pdf")) {
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                JMRouter.create(RouteSchema.PDF_VIEW).addExtras(bundle).open(context);
            } else {
                String action = extras.getString("replaceUrl");
                if (TextUtils.equals(action, "replaceUrl")) {
                    Intent replaceUrlIntent = new Intent(ConstantUtils.ACTION_REPLACE_URL);
                    replaceUrlIntent.putExtra("url", url);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(replaceUrlIntent);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("url", url);
//                    if (context instanceof ICanReplaceUrl) {
//                        bundle.putBoolean("canReplace", true);
//                    }
                    JMRouter.create(RouteSchema.WEB_VIEW).addExtras(bundle).open(context);
                }
            }
            return;
        }
        if(handleUrl(url)) {
            IActivityRoute route = Router.create(uri).getActivityRoute()
                    .addExtras(extras)
                    .addFlags(flags)
                    .requestCode(requestCode)
                    .setAnim(inAnimation, outAnimation);
            if (!interceptors.isEmpty()) {
                for (RouteInterceptor interceptor : interceptors) {
                    if (interceptor != null) {
                        route.addInterceptor(interceptor);
                    }
                }
            }
            route.open(context);
        }
    }
    public boolean handleUrl(String url){
        //跳转到rn单独页面
//        if("newWorkTasks".equalsIgnoreCase(url)){
//            JMRouter.create(PAGE_TO_RN+"?page=TasksGroup").open(SingleContainer.getApplicationContext());
//            return false;
//        }
        Uri uri = Uri.parse(url);
        final String host = uri.getHost();
        final String action = host + uri.getPath();
        final Map<String, String> params = uriParse(uri);
        boolean result = true;
        switch (action) {
//            case RouteSchema.ACTION_UPDATE://结束当前页面
//                if(!TextUtils.isEmpty(params.get("download_url"))){
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                    intent.setData(Uri.parse(params.get("download_url")));
//                    SingleContainer.getApplicationContext().startActivity(intent);
//                }else{
//                    Beta.checkUpgrade();
//                }
//                result = false;
//                break;
//            case share_action://分享
//                if (actionCallback != null) {
//                    actionCallback.handleWithAction(url,webView);
//                    result = false;
//                }
//                break;
            default:
                break;
        }
        return result;

    }
    public Map<String, String> uriParse(Uri uri) {
        Map<String, String> result = new HashMap<>();
        String query = uri.getEncodedQuery();
        if (TextUtils.isEmpty(query)) return result;
        List<String> splits = splitParam(query);
        for (String param : splits) {
            String[] keyValue = param.split("=");
            if (keyValue.length >= 2) {
                StringBuilder strBuilder = new StringBuilder("");
                for (int index = 1; index < keyValue.length; index++) {
                    if (index > 1) strBuilder.append("=");
                    strBuilder.append(keyValue[index]);
                }

                String value = strBuilder.toString();
                try {
                    value = URLDecoder.decode(value,"UTF-8");
                    result.put(new String(keyValue[0]), value);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    private List<String> splitParam(String str) {
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
                        splits.add(paramStr.toString());
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

    public interface ActionCallback {
        void handleWithAction(String url, SoftReference<WebView> webView);
    }
}
