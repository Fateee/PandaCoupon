package com.google.zxing;

import com.google.zxing.common.DetectorResult;
import com.google.zxing.qrcode.detector.FinderPattern;

/**
 * Created by yih on 2018/12/10.
 * Des:
 */

public interface FoundQrCodeListener {
    void fountQrCode(DetectorResult detectorResult);
}
