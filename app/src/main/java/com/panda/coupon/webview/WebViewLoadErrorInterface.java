package com.panda.coupon.webview;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by yih on 2018/9/6.
 * Des:
 */

public interface WebViewLoadErrorInterface {
    void onPageStarted(WebView view, String url, Bitmap favicon);
    void onPageFinished(WebView view, String url);
    void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error);
    void onReceivedError(WebView view, int errorCode, String description, String failingUrl);
    void shouldOverrideUrlLoading(@NonNull WebView view, String url);
}
