/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.panda.coupon.zxing.decode;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.panda.coupon.R;
import com.panda.coupon.activity.ScanCodeActivity;
import com.panda.coupon.zxing.camera.CameraManager;
import com.panda.coupon.zxing.camera.PlanarYUVLuminanceSource;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FoundQrCodeListener;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.common.detector.MathUtils;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import static com.panda.coupon.activity.ScanCodeActivity.QRCODE;


final class DecodeHandler extends Handler {
    public static final double W_H_MAX_MISS = 0.8;
    public static final int MIN_QR_WIDTH = 5;
    public static final int FOUND_QR_SMALL_COUNT = 1;
    public final ScanCodeActivity mActivity;
    @NonNull
    final MultiFormatReader mQrCodeReader;
    @NonNull
    final Map<DecodeHintType, Object> mHints;
    byte[] mRotatedData;

    Collection<BarcodeFormat> barcodeFormats;
    static final Set<BarcodeFormat> PRODUCT_FORMATS;
    static final Set<BarcodeFormat> INDUSTRIAL_FORMATS;
    private static final Set<BarcodeFormat> ONE_D_FORMATS;
    private boolean hasQrCode = false;
    private boolean afterZoom = false;
    private int foundQrSmallCount = 0;
    static {
        PRODUCT_FORMATS = EnumSet.of(BarcodeFormat.UPC_A,
                BarcodeFormat.UPC_E,
//                BarcodeFormat.EAN_13,
//                BarcodeFormat.EAN_8,
                BarcodeFormat.RSS_14,
                BarcodeFormat.RSS_EXPANDED);
        INDUSTRIAL_FORMATS = EnumSet.of(BarcodeFormat.CODE_39,
                BarcodeFormat.CODE_93,
                BarcodeFormat.CODE_128,
                BarcodeFormat.ITF,
                BarcodeFormat.CODABAR);
        ONE_D_FORMATS = EnumSet.copyOf(PRODUCT_FORMATS);
        ONE_D_FORMATS.addAll(INDUSTRIAL_FORMATS);
    }
    DecodeHandler(ScanCodeActivity activity, String type) {
        this.mActivity = activity;
        mQrCodeReader = new MultiFormatReader();
        mHints = new Hashtable<>();
        mHints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        mHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        barcodeFormats = EnumSet.noneOf(BarcodeFormat.class);
//        if(ScanCodeActivity.BARCODE.equalsIgnoreCase(type)) {
//            barcodeFormats.addAll(ONE_D_FORMATS);
//        }else{
//            barcodeFormats.add(BarcodeFormat.QR_CODE);
//        }
//      默认都支持
        if(QRCODE.equalsIgnoreCase(type)){
            barcodeFormats.add(BarcodeFormat.QR_CODE);
        }else {
            barcodeFormats.addAll(ONE_D_FORMATS);
            barcodeFormats.add(BarcodeFormat.QR_CODE);
        }
        mHints.put(DecodeHintType.POSSIBLE_FORMATS, barcodeFormats);
    }

    @Override
    public void handleMessage(@NonNull Message message) {
        switch (message.what) {
            case R.id.decode:
                decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case R.id.quit:
                Looper looper = Looper.myLooper();
                if (null != looper) {
                    looper.quit();
                }
                break;
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency, reuse the same reader
     * objects from one decode to the next.
     *
     * @param data The YUV preview frame.
     * @param width The width of the preview frame.
     * @param height The height of the preview frame.
     */
    void decode(@NonNull byte[] data, int width, int height) {
        if (null == mRotatedData) {
            mRotatedData = new byte[width * height];
        } else {
            if (mRotatedData.length < width * height) {
                mRotatedData = new byte[width * height];
            }
        }
        Arrays.fill(mRotatedData, (byte) 0);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x + y * width >= data.length) {
                    break;
                }
                mRotatedData[x * height + height - y - 1] = data[x + y * width];
            }
        }
        int tmp = width; // Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;

        Result rawResult = null;
        hasQrCode = false;
        afterZoom = false;
        try {
            PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(mRotatedData, width, height);
            BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
            rawResult = mQrCodeReader.decode(bitmap1, mHints, new FoundQrCodeListener() {
                @Override
                public void fountQrCode(DetectorResult detectorResult) {
                    handleFoundQrCode(detectorResult);
                }
            });
        } catch (Exception e) {
        } finally {
            mQrCodeReader.reset();
        }
        //Log.d("DemoLog","mid time="+(System.currentTimeMillis()-beginTime));
        if(rawResult == null && hasQrCode == false){
            try{
                PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(mRotatedData, width, height);
                BinaryBitmap bitmap1 = new BinaryBitmap(new GlobalHistogramBinarizer(source));
                rawResult = mQrCodeReader.decode(bitmap1, mHints, new FoundQrCodeListener() {
                    @Override
                    public void fountQrCode(DetectorResult detectorResult) {
                        handleFoundQrCode(detectorResult);
                    }
                });
            }
            catch (Exception e) {
            } finally {
                mQrCodeReader.reset();
            }
        }
        //Log.d("DemoLog","end time="+(System.currentTimeMillis()-beginTime));
        if(rawResult == null&&hasQrCode){
            Image barcode = new Image(width, height, "Y800");
            barcode.setData(mRotatedData);
            Rect rect = CameraManager.get().getFramingRectInPreview();
            if (rect != null) {
                barcode.setCrop(rect.top, rect.left, rect.width(), rect.height());
            }
            ImageScanner mImageScanner = new ImageScanner();
            int result = mImageScanner.scanImage(barcode);
            if (result != 0) {
                String resultQRcode=null;
                SymbolSet symSet = mImageScanner.getResults();
                for (Symbol sym : symSet) {
                    resultQRcode = sym.getData();
                }
                if(resultQRcode!=null){
//                    Log.d("DemoLog","zbar success");
                    rawResult =new Result(resultQRcode,null,null,null);
                }
            }
            //Log.d("DemoLog","zbar end time="+(System.currentTimeMillis()-beginTime));
        }
        if (rawResult != null) {
//            Log.d("DemoLog","decode success");
            foundQrSmallCount = 0;
            Message message = Message.obtain(mActivity.getCaptureActivityHandler(), R.id.decode_succeeded, rawResult);
            message.sendToTarget();
        } else {
            if(afterZoom) {
                CameraManager.get().setZoomLarge();
            }
            Message message = Message.obtain(mActivity.getCaptureActivityHandler(), R.id.decode_failed);
            message.sendToTarget();
        }
    }
    private void handleFoundQrCode(DetectorResult detectorResult){
        if(mActivity!=null&&!mActivity.isFinishing()) {
            ResultPoint[] p = detectorResult.getPoints();
            if(p!=null&&p.length>=3) {
                hasQrCode = true;
                double h = MathUtils.distance(p[0].getX(), p[0].getY(), p[1].getX(), p[1].getY());
                double w = MathUtils.distance(p[1].getX(), p[1].getY(), p[2].getX(), p[2].getY());
                if(h>0&&w>0) {
//                    Log.d("DemoLog", "w=" + w + ",h=" + h);
//                    Log.d("DemoLog", "h/w=" + h/w);
                    if ((w >= h && h / w > W_H_MAX_MISS) || (w < h && w / h > W_H_MAX_MISS)) {
                        if (w < CameraManager.get().getCameraResolution().y/MIN_QR_WIDTH) {
                            foundQrSmallCount++;
                            if(foundQrSmallCount>=FOUND_QR_SMALL_COUNT) {
                                afterZoom = true;
                                foundQrSmallCount = 0;
                            }
                        }
                    }else{
                    }
                }
            }
        }
    }
}
