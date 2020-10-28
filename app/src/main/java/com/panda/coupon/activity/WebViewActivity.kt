package com.panda.coupon.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lzh.nonview.router.anno.RouterRule
import com.panda.coupon.R
import com.panda.coupon.router.RouteSchema
import com.panda.coupon.utils.ConstantUtils
import com.panda.coupon.utils.SpUtil
import com.panda.coupon.webview.WebViewFragment
import kotlinx.android.synthetic.main.activity_main.*


@RouterRule(RouteSchema.WEB_VIEW)
class WebViewActivity : BaseActivity() {

    private var versionClickCount: Int = 0
    private var lastVersionClickTime: Long = 0L
    private var url: String = ConstantUtils.PRODUCT_URL
    private var webViewFragment: WebViewFragment? = null

    //action=replace的时候替换url
    private val mReplaceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val url = intent.getStringExtra("url")
            url?.let {
                webViewFragment?.loadUrl(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var isRd = SpUtil.getInstace().getBoolean(ConstantUtils.KEY_URL_RD,false)
        url = if (isRd) ConstantUtils.RD_URL else ConstantUtils.PRODUCT_URL

        setContentView(R.layout.content_main)
        webViewFragment =
            WebViewFragment.newInstance(url, "default", 0)
        supportFragmentManager.beginTransaction()
            .add(R.id.webview_container, webViewFragment!!).show(webViewFragment!!)
            .commitAllowingStateLoss()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mReplaceReceiver, IntentFilter(ConstantUtils.ACTION_REPLACE_URL))
        setPageTitle("扫码核销")
        toolbar?.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastVersionClickTime <= 1000) {
                versionClickCount++
            } else {
                versionClickCount = 0
            }
            if (versionClickCount == 5) {
                startActivity(Intent(it.context,DeveloperActivity::class.java))
            }
            lastVersionClickTime = currentTime
        }
    }

    override fun hideToolbar() = false

    override fun onBackPressed() {
        if (webViewFragment?.webViewCanGoBack() == true) {
            webViewFragment?.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReplaceReceiver)
    }

}
