package com.panda.coupon.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.panda.coupon.utils.DensityUtils;
import com.panda.coupon.R;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewClientExtension;
import com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewCallbackClient;

/**
 * Des:
 */

public class RefreshWebView extends WebView implements WebViewLoadErrorInterface {
    public ProgressBar progressbar;
    Context context;
    public boolean isLoadError;//是否加载成功
    public RelativeLayout errorLayout;
    TextView retryBtn;
    ImageView errorIV;
    TextView desTv;
    public int onPageStartCount = 0;
    public int onReceivedErrorCount = 0;
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
    public RefreshWebView(Context context) {
        super(context);
        this.context = context;
        progressbar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT, 10, 0, 0));

        Drawable drawable = context.getResources().getDrawable(R.drawable.progress_horizontal);
        progressbar.setProgressDrawable(drawable);

        //setWebChromeClient(new RefreshWebChromeClient());
        getSettings().setSupportZoom(true);
        getSettings().setBuiltInZoomControls(true);

        errorLayout = (RelativeLayout) ((Activity)context).getLayoutInflater().inflate(R.layout.webview_error_layout, null, false);
        retryBtn = errorLayout.findViewById(R.id.retry_btn);
        errorIV = errorLayout.findViewById(R.id.error_img);
        desTv = errorLayout.findViewById(R.id.error_des);
        retryBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isReLoadClick()) {
                    reload();
                }
            }
        });
        errorLayout.setLayoutParams(new ViewGroup.LayoutParams(DensityUtils.getWidth(), ViewGroup.LayoutParams.MATCH_PARENT));
        addView(errorLayout);
        addView(progressbar);
        errorLayout.setVisibility(View.GONE);
        setWebViewCallbackClient(mCallbackClient);
        if(getX5WebViewExtension() != null)
        {
            getX5WebViewExtension().setWebViewClientExtension(mWebViewClientExtension );
        }
    }

    long lastClickTime;

    public boolean isReLoadClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        int btn_click_time = 1000;//转换成毫秒
        if (0 < timeD && timeD < btn_click_time) {
            return true;
        } else {
            lastClickTime = time;
        }
        return false;
    }
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        onPageStartCount++;
        if(onReceivedErrorCount==0) {
            if (isLoadError) {
                setErrorContentVisible(View.GONE);
            }
            isLoadError = false;
        }
    }


    @Override
    public void onPageFinished(WebView view, String url) {
//        if(!isLoadError){
//            errorLayout.setVisibility(View.GONE);
//        }
    }
    private void setErrorContentVisible(int visible){
        retryBtn.setVisibility(visible);
        errorIV.setVisibility(visible);
        desTv.setVisibility(visible);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(request.isForMainFrame()){
                onReceivedErrorCount++;
                isLoadError = true;
                errorLayout.setVisibility(View.VISIBLE);
                setErrorContentVisible(View.VISIBLE);
            }
        }

    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        onReceivedErrorCount++;
        isLoadError = true;
        errorLayout.setVisibility(View.VISIBLE);
        setErrorContentVisible(View.VISIBLE);
    }

    @Override
    public void shouldOverrideUrlLoading(@NonNull WebView view, String url) {
        Log.d("DemoLog","shouldOverrideUrlLoading="+url);
    }

    public class RefreshWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                onPageStartCount = 0;
                onReceivedErrorCount = 0;
                if(!isLoadError){
                    isLoadError = false;
                    errorLayout.setVisibility(View.GONE);
                }
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (this.getScrollY() <= 0) {
                    this.scrollTo(getScrollX(), 1);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(this.getScrollY() == 1) {
                    this.scrollTo(getScrollX(), 0);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
                if(this.getScrollY() == 1) {
                    this.scrollTo(getScrollX(), 0);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
    class CallbackClient implements WebViewCallbackClient {

        public void invalidate()
        {
        }

        @Override
        public boolean onTouchEvent(MotionEvent event, View view) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (getView().getScrollY() <= 0) {
                        getView().scrollTo(getScrollX(), 1);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(getView().getScrollY() == 1) {
                        getView().scrollTo(getScrollX(), 0);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if(getView().getScrollY() == 1) {
                        getView().scrollTo(getScrollX(), 0);
                    }
                    break;
                default:
                    break;
            }
            return super_onTouchEvent(event);
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                    int scrollY, int scrollRangeX, int scrollRangeY,
                                    int maxOverScrollX, int maxOverScrollY,
                                    boolean isTouchEvent, View view) {

            return super_overScrollBy(deltaX, deltaY, scrollX, scrollY,
                    scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
                    isTouchEvent);
        }

        @Override
        public void computeScroll(View view) {
            super_computeScroll();
        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
                                   boolean clampedY, View view) {
            super_onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        }

        @Override
        public void onScrollChanged(int l, int t, int oldl, int oldt, View view) {
            super_onScrollChanged(l, t, oldl, oldt);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev, View view) {
            return super_dispatchTouchEvent(ev);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev, View view) {
            return super_onInterceptTouchEvent(ev);
        }

    };
    private CallbackClient mCallbackClient = new CallbackClient();
    private IX5WebViewClientExtension mWebViewClientExtension = new ProxyWebViewClientExtension() {


        public void invalidate()
        {
        }

        public void onReceivedViewSource(String data) {

        };

        @Override
        public boolean onTouchEvent(MotionEvent event, View view) {

            return mCallbackClient.onTouchEvent(event, view);
        }

        // 1
        public boolean onInterceptTouchEvent(MotionEvent ev, View view) {
            return mCallbackClient.onInterceptTouchEvent(ev, view);
        }

        // 3
        public boolean dispatchTouchEvent(MotionEvent ev, View view) {
            return mCallbackClient.dispatchTouchEvent(ev, view);
        }
        // 4
        public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                    int scrollRangeX, int scrollRangeY,
                                    int maxOverScrollX, int maxOverScrollY,
                                    boolean isTouchEvent, View view) {
            return mCallbackClient.overScrollBy(deltaX, deltaY, scrollX, scrollY,
                    scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent, view);
        }
        // 5
        public void onScrollChanged(int l, int t, int oldl, int oldt, View view) {
            mCallbackClient.onScrollChanged(l, t, oldl, oldt, view);
        }
        // 6
        public void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
                                   boolean clampedY, View view) {
            mCallbackClient.onOverScrolled(scrollX, scrollY, clampedX, clampedY, view);
        }
        // 7
        public void computeScroll(View view) {
            mCallbackClient.computeScroll(view);
        }

        @Override
        public void onResponseReceived(WebResourceRequest request, WebResourceResponse response, int errorCode) {
            super.onResponseReceived(request, response, errorCode);
        }
    };
}
