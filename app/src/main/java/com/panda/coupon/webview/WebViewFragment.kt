package com.panda.coupon.webview

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.panda.coupon.*
import com.panda.coupon.utils.DeviceUtil
import com.panda.coupon.utils.LocaleUtils
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import kotlinx.android.synthetic.main.fragment_web_view.*
import kotlin.system.exitProcess

private const val ARG_URL = "url"
private const val ARG_TYPE = "type"
private const val ARG_NUM = "number"


class WebViewFragment : Fragment() {

    private var refreshWebView: RefreshWebView? = null
    private var pdWebViewClient: PDWebViewClient? = null
    private var webView: WebView? = null
    private var url: String?=""
    private var argUrl: String? = null
    private var argType: String? = null
    private var argNum: Int = 0

    companion object {
        @JvmStatic
        fun newInstance(url: String, type: String, num: Int) =
            WebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URL, url)
                    putString(ARG_TYPE, type)
                    putInt(ARG_NUM, num)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            argUrl = it.getString(ARG_URL)
            argType = it.getString(ARG_TYPE)
            argNum = it.getInt(ARG_NUM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPages()
    }

    private fun initPages() {
        //webview 调试
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (BuildConfig.DEBUG) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
        }
        url = arguments?.getString("url")
        val context = context
        webView = context?.let { buildAkWebView(it) }
        webview_container.addView(webView)
        if (pdWebViewClient != null) return

        pdWebViewClient = PDWebViewClient(getContext())
        if (webView is RefreshWebView) {
            pdWebViewClient?.setWebViewLoadErrorInterface(webView as RefreshWebView)
            pdWebViewClient?.jsBridgeUtil?.setAttachFragment(this)
            refreshWebView = webView as RefreshWebView
        }
        pdWebViewClient?.jsBridgeUtil?.number = arguments?.getInt("number")
        webView?.webViewClient = pdWebViewClient
        webView?.webChromeClient = object : WebChromeClient() {

//            //For Android  >= 4.1
//            override fun openFileChooser(
//                valueCallback: ValueCallback<Uri>,
//                acceptType: String?,
//                capture: String?
//            ) {
//                uploadMessage = valueCallback
//                openFileChooserImpl()
//            }
//
//            // For Android >= 5.0
//            override fun onShowFileChooser(
//                webView: WebView?,
//                filePathCallback: ValueCallback<Array<Uri>>?,
//                fileChooserParams: WebChromeClient.FileChooserParams?
//            ): Boolean {
//                uploadMessageAboveL = filePathCallback
//                openFileChooseImplForAndroid()
//                return true
//            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (refreshWebView != null) {
                    if (newProgress == 100) {
                        refreshWebView?.onPageStartCount = 0
                        refreshWebView?.onReceivedErrorCount = 0
                        if (refreshWebView?.isLoadError != true) {
                            refreshWebView?.isLoadError = false
                            refreshWebView?.errorLayout?.visibility = GONE
                        }
                        refreshWebView?.progressbar?.visibility = GONE
                    } else {
                        if (refreshWebView?.progressbar?.visibility == GONE)
                            refreshWebView?.progressbar?.visibility = VISIBLE
                        refreshWebView?.progressbar?.progress = newProgress
                    }
                }
                super.onProgressChanged(view, newProgress)
            }
        }
        if ("default".equals(arguments?.getString("type"), ignoreCase = true)) {
            webView?.loadUrl(url)
        }
    }

    /**
     * 初始化webview
     *
     * @param context
     * @return
     */
    private fun buildAkWebView(context: Context): WebView {
        val webView = RefreshWebView(context)
        //启用支持javascript
        val settings = webView.getSettings()
        var ua = settings.getUserAgentString()
        // 开启DOM缓存。
        settings.setDomStorageEnabled(true)
        settings.setDatabaseEnabled(true)
        settings.setCacheMode(WebSettings.LOAD_DEFAULT)
        settings.setDisplayZoomControls(false)
        settings.databasePath = context.applicationContext.cacheDir.absolutePath
        //允许webview对文件的操作
        settings.setAllowUniversalAccessFromFileURLs(true)
        settings.setAllowFileAccess(true)
        settings.setAllowFileAccessFromFileURLs(true)
        if (TextUtils.isEmpty(ua)) {
            ua = ""
        }
        ua += " device-version/" + Build.VERSION.SDK_INT
        ua += " app_version/" + DeviceUtil.version
        ua += " device-type/android"
        ua += " language-code/" + LocaleUtils.getInstance().language_code
        ua += " script-code/" + LocaleUtils.getInstance().script_code
        ua += " platform/android"
        ua += " country-code/" + LocaleUtils.getInstance().country_code
        ua += " time-zone/" + LocaleUtils.getInstance().time_zone
        settings.setUserAgentString(ua)
        settings.javaScriptEnabled = true
        return webView
    }

    fun getWebView(): WebView? {
        return webView
    }

    fun loadUrl(url: String) {
        if (webView != null) {
            //url="http://172.20.49.66:6012/jsbridge";
            webView?.loadUrl(url)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (pdWebViewClient != null) {
            pdWebViewClient?.destroy()
        }
        handleWithDestroy()
    }


    private fun handleWithDestroy() {
        if (webView != null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            val parent = webView?.getParent()
            if (parent != null) {
                (parent as ViewGroup).removeView(webView)
            }

            webView?.stopLoading()
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView?.settings?.javaScriptEnabled = false
            webView?.clearHistory()
            webView?.removeAllViews()

            try {
                webView?.destroy()
            } catch (ex: Throwable) {

            }

        }
    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
////        mActivity = context as Activity
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        mActivity = null
//    }

    fun onBackPressed() {
        Log.d("DemoLog", "webViewGoBack=" + webView?.canGoBack())
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            exitProcess(0)
        }
//        if (webViewGoBack) {
//            Log.d("DemoLog", "webViewGoBack=" + webView?.canGoBack())
//            if (webView?.canGoBack() == true) {
//                webView?.goBack()
//            } else {
//                System.exit(0)
//            }
//        } else {
//            val homeIntent = Intent(Intent.ACTION_MAIN)
//            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            homeIntent.addCategory(Intent.CATEGORY_HOME)
//            startActivity(homeIntent)
//        }
    }

    fun webViewCanGoBack(): Boolean {
        return webView?.canGoBack() ?: false
    }
}
