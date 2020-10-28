package com.panda.coupon.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.panda.coupon.jsbridge.JSBridgeUtil;
import com.panda.coupon.router.JMRouter;
import com.panda.coupon.router.RouteSchema;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.lang.ref.SoftReference;


/**
 * Created by congxiany on 18/3/6.
 */

public class PDWebViewClient extends WebViewClient {
    Context context;
    WebViewLoadErrorInterface webViewLoadErrorInterface;
    public JSBridgeUtil jsBridgeUtil;
    private JMRouter.ActionCallback actionCallback = new JMRouter.ActionCallback() {
        @Override
        public void handleWithAction(String url, SoftReference<WebView> webView) {
            jsBridgeUtil.handleWithJSBridge(url, context, webView);
        }
    };

    public PDWebViewClient(Context context) {
        super();
        this.context = context;
        init(context);
    }

    private void init(Context context) {
        jsBridgeUtil = new JSBridgeUtil();
        jsBridgeUtil.init(context);
    }

    public void destroy() {
        jsBridgeUtil.webCache.clear();
        jsBridgeUtil.callbackCache.clear();
        jsBridgeUtil.onDestroy();
    }

    public void setWebViewLoadErrorInterface(WebViewLoadErrorInterface webViewLoadErrorInterface) {
        this.webViewLoadErrorInterface = webViewLoadErrorInterface;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (webViewLoadErrorInterface != null) {
            webViewLoadErrorInterface.onPageStarted(view, url, favicon);
        }
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        Log.i("demonLog","shouldInterceptRequest url：" + url);
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (webViewLoadErrorInterface != null) {
            webViewLoadErrorInterface.onPageFinished(view, url);
        }
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        Log.d("DemoLog", "onReceivedError error=");
        if (webViewLoadErrorInterface != null) {
            webViewLoadErrorInterface.onReceivedError(view, request, error);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        Log.d("DemoLog", "onReceivedError failingUrl=" + failingUrl);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return;
        }
        if (webViewLoadErrorInterface != null) {
            webViewLoadErrorInterface.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(@NonNull WebView view, String url) {
        if (url.startsWith(RouteSchema.PREFIX_PANDA)) {
            Uri uri = Uri.parse(url);
            if ("jsbridge".equals(uri.getHost())) {
                jsBridgeUtil.handleWithJSBridge(url, context, new SoftReference<>(view));
            } else {
//                JMRouter.create(url).open(context);
            }
            return true;
        } else {
            if (webViewLoadErrorInterface != null) {
                webViewLoadErrorInterface.shouldOverrideUrlLoading(view, url);
                Log.d("DemoLog", "shouldOverrideUrlLoading=" + url);
            }
            WebView.HitTestResult hitTestResult = view.getHitTestResult();
            if (!TextUtils.isEmpty(url) && hitTestResult == null) {//重定向
                view.loadUrl(url);
                return true;
            } else {
                return false;
            }
        }

    }

    @Override
    public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
//        if ((boolean) SharedPreferencesHelper.getInstance().get(TYPE_SETTINGS, KEY_HTTP_CAPTURE, BuildConfig.DEBUG ? true : false))//开发者选项
//        {
//            sslErrorHandler.proceed();
//        }
        super.onReceivedSslError(webView, sslErrorHandler, sslError);
    }
}
