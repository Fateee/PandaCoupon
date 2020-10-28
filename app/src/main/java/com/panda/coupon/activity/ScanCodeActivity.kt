package com.panda.coupon.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.content.PermissionChecker
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.panda.coupon.utils.ConstantUtils.*
import com.panda.coupon.utils.JMToast
import com.panda.coupon.utils.PermissionUtils
import com.panda.coupon.zxing.camera.CameraManager
import com.panda.coupon.zxing.decode.CaptureActivityHandler
import com.panda.coupon.zxing.decode.DecodeManager
import kotlinx.android.synthetic.main.activity_qr_code.*
import java.io.IOException
import java.util.regex.Pattern
import com.lzh.nonview.router.anno.RouterRule
import com.panda.coupon.R
import com.panda.coupon.router.RouteSchema

@RouterRule(RouteSchema.PAGE_SCAN)
open class ScanCodeActivity : BaseActivity(),View.OnClickListener,SurfaceHolder.Callback {

    companion object {
        @JvmField
        val QRCODE = "qrcode"
        @JvmField
        val BARCODE = "barcode"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)
        toolbar_title.text = getString(R.string.qr_code_des)
        initPages()
    }

    fun initPages() {
//        if (Build.VERSION.SDK_INT < 19) { // lower api
//            window.decorView.systemUiVisibility = View.GONE
//        } else {
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                //设置导航栏(NavigationBar)颜色透明
//                window.navigationBarColor = Color.TRANSPARENT
//            }
//            val decorView = window.decorView
//            val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_IMMERSIVE)
//            decorView.systemUiVisibility = uiOptions
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
//        }
        initView()
        initData()
    }


    private fun initData() {
        CameraManager.init()
    }

    private fun initView() {
        flash_light_open.setOnClickListener(this)
        toolbar_back.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
        //        if (!mPermissionOk) {
        ////            mDecodeManager.handleResult(getString(R.string.qr_code_permission));
        //            DialogHelper.showCameraPermissionDeniedDialog(this);
        //            return;
        //        }
        val surfaceHolder = qr_code_preview_view.getHolder()
        if (mHasSurface) {
            qr_code_preview_view.postDelayed(Runnable { initCamera(surfaceHolder) }, 100)
        } else {
            surfaceHolder.addCallback(this)
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
    }

    override fun onPause() {
        super.onPause()
        qr_code_preview_view.postDelayed(Runnable {
            if (mCaptureActivityHandler != null) {
                mCaptureActivityHandler?.quitSynchronously()
                mCaptureActivityHandler = null
            }
            CameraManager.get().closeDriver()
        }, 500)

    }

    internal fun checkPermission() {
        val hasHardware = checkCameraHardWare(this)
        if (hasHardware) {
            if (!hasCameraPermission()) {
                qr_code_view_finder.setVisibility(View.GONE)
                mPermissionOk = false
            } else {
                mPermissionOk = true
            }
        } else {
            mPermissionOk = false
            finish()
        }
        if (!mPermissionOk) {
            val intent = Intent(SCAN_RESULT_NOTICE)
            intent.putExtra("code", CODE_CAMERA_NO_PERMISSION)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            //            jsBridgeUtil.handleWithScanCodeResult(CODE_CAMERA_NO_PERMISSION,callback);
        }
    }

    internal fun hasCameraPermission(): Boolean {
        val isCameraPermission = PermissionChecker.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) === PermissionChecker.PERMISSION_GRANTED
        Log.d("DemoLog", "isCameraPermission=$isCameraPermission")
        return isCameraPermission
    }

    fun handleDecode(@Nullable result: Result?) {
        if (null == result) {
            JMToast.show(getString(R.string.qr_code_permission_dialog))
            restartPreview()
        } else {
            if (result.barcodeFormat != BarcodeFormat.QR_CODE && isPureNumber(result.text.toString())) {
                restartPreview()
                return
            }
            Log.d("DemoLog", "result format =" + result.barcodeFormat)
            setFormatType(result)
            val resultString = result.text
            handleResult(resultString)
        }
    }

    private fun setFormatType(result: Result) {
        if (result.barcodeFormat == BarcodeFormat.QR_CODE) {
            scan_type = QRCODE
        } else {
            scan_type = BARCODE
        }
    }

    internal fun initCamera(surfaceHolder: SurfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder)
            if (mDecodeManager.dialog != null) {
                mDecodeManager.dialog.dismiss()
            }
        } catch (e: IOException) {
            val intent = Intent(SCAN_RESULT_NOTICE)
            intent.putExtra("code", CODE_CAMERA_ERROR)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            // 基本不会出现相机不存在的情况
            //            jsBridgeUtil.handleWithScanCodeResult(CODE_CAMERA_ERROR,callback);
            finish()
            return
        } catch (re: RuntimeException) {
            re.printStackTrace()
            //Android6.0以下无法获取到权限是否打开，所以Android6.0以下直接弹权限框
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PermissionUtils.checkPermission(this, object : PermissionUtils.Callback {
                    override fun onGranted() {
                        initCamera(surfaceHolder)
                    }

                    override fun onRationale() {
                        JMToast.show("请开启相机权限才能正常使用")
                    }

                    override fun onDenied(context: Context) {
                        if (this@ScanCodeActivity.isFinishing) {
                            return
                        }
                        mDecodeManager.showCameraPermissionDialog(this@ScanCodeActivity)
                    }

                }, arrayOf<String>(android.Manifest.permission.CAMERA))
            } else {
                mDecodeManager.showCameraPermissionDialog(this)
            }
            return
        }

        qr_code_view_finder.setVisibility(View.VISIBLE)
        qr_code_preview_view.setVisibility(View.VISIBLE)
        if (mCaptureActivityHandler == null) {
            mCaptureActivityHandler = CaptureActivityHandler(this, scan_type)
        }
    }

    fun restartPreview() {
        if (null != mCaptureActivityHandler) {
            mCaptureActivityHandler?.restartPreviewAndDecode()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    /* 检测相机是否存在 */
    internal fun checkCameraHardWare(@NonNull context: Context): Boolean {
        val packageManager = context.packageManager
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (!mHasSurface) {
            mHasSurface = true
            initCamera(holder)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mHasSurface = false
    }

    @Nullable
    fun getCaptureActivityHandler(): Handler? {
        return mCaptureActivityHandler
    }

    internal fun handleResult(resultString: String) {
        if (TextUtils.isEmpty(resultString)) {
            mDecodeManager.showCouldNotReadQrCodeFromScanner()
            restartPreview()
        } else {
            val intent = Intent(SCAN_RESULT_NOTICE)
            intent.putExtra("code", CODE_SUCC)
            intent.putExtra("message", "success")
            intent.putExtra("from", TYPE_SCAN)
            intent.putExtra("type", scan_type)
            intent.putExtra("result", resultString)
            LocalBroadcastManager.getInstance(this@ScanCodeActivity).sendBroadcast(intent)
            postResult(resultString, "scan")
            qr_code_preview_view.postDelayed(Runnable { restartPreview() }, 1000)
        }
    }

    private fun postResult(code: String, type: String) {
        val bundle = Bundle()
        bundle.putString("code", code)
        bundle.putString("type", type)
        val intent = Intent()
        intent.putExtras(bundle)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.toolbar_back -> {
                val intent = Intent(SCAN_RESULT_NOTICE)
                intent.putExtra("code", CODE_CAMERA_CANCEL)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                //                jsBridgeUtil.handleWithScanCodeResult(CODE_CAMERA_CANCEL,callback);
                finish()
            }
            R.id.flash_light_open -> if (mNeedFlashLightOpen) {
                turnFlashlightOn()
            } else {
                turnFlashLightOff()
            }
        }
    }

    fun turnFlashlightOn() {
        val isLightSuccess = CameraManager.get().setFlashLight(true)
        if (isLightSuccess) {
            mNeedFlashLightOpen = false
            flash_light_open_text.setText(resources.getString(R.string.qr_code_close_flash))
        }
    }

    fun turnFlashLightOff() {
        val isLightSuccess = CameraManager.get().setFlashLight(false)
        if (isLightSuccess) {
            mNeedFlashLightOpen = true
            flash_light_open_text.setText(resources.getString(R.string.qr_code_open_flash))
        }
    }


    private var mDecodeManager = DecodeManager()
    private var mCaptureActivityHandler: CaptureActivityHandler? = null
    private var scan_type: String = QRCODE
    private var mHasSurface: Boolean = false
    private var mNeedFlashLightOpen: Boolean = true
    private var mPermissionOk: Boolean = false


    private fun isPureNumber(str: String): Boolean {
        val regex = "^[0-9]{1,9}$"
        val p = Pattern.compile(regex)
        val m = p.matcher(str)
        if (m.matches()) {
            Log.d("DemoLog", "only number")
            return true
        } else {
            return false
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(SCAN_RESULT_NOTICE)
        intent.putExtra("code", CODE_CAMERA_CANCEL)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    fun isSetImmersiveBar(): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
    }

//    public Context getContext() {
//        return this;
//    }


}
