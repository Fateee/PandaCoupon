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

package com.panda.coupon.zxing.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.panda.coupon.utils.DensityUtils;


final class PreviewCallback implements Camera.PreviewCallback {
    static final String TAG = PreviewCallback.class.getName();
    final CameraConfigurationManager mConfigManager;
    public static final int MaxAppearTimes = 8;
    public static final int MinAppearTimes = 3;
    public static int exposureCompensationStep = 2;
    public static int MaxPowerLight = 230;
    public static int MinPowerLight = 30;
    public static int YUVWidth = 400;
    public static int marginTop = 60;
    @Nullable
    Handler mPreviewHandler;
    int mPreviewMessage;
    int nowExposureCompensation = 0;
    int addTimes = 0;
    int minLightTimes = 0;
    PreviewCallback(CameraConfigurationManager configManager) {
        this.mConfigManager = configManager;
    }

    void setHandler(Handler previewHandler, int previewMessage) {
        this.mPreviewHandler = previewHandler;
        this.mPreviewMessage = previewMessage;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Point cameraResolution = mConfigManager.getCameraResolution();
        if (mPreviewHandler != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                String formatString = parameters.get("preview-format");
                float cameraMax = cameraResolution.x>cameraResolution.y?cameraResolution.x:cameraResolution.y;
                float screenMax = mConfigManager.getScreenResolution().x>mConfigManager.getScreenResolution().y?mConfigManager.getScreenResolution().x:mConfigManager.getScreenResolution().y;
                float scaleCamera = cameraMax/screenMax;
                if(!TextUtils.isEmpty(formatString)&&formatString.toLowerCase().startsWith("yuv420")) {
                    YUVWidth = (int)(DensityUtils.dip2px(200)*scaleCamera);
                    if (YUVWidth > cameraResolution.y) {
                        YUVWidth = cameraResolution.y;
                    }
                    int beginWidth = cameraResolution.x / 2 - YUVWidth / 2 - (int)(marginTop*scaleCamera);
                    int endWidth = cameraResolution.x / 2 + YUVWidth / 2 - (int)(marginTop*scaleCamera);
                    if (beginWidth < 0) {
                        endWidth = endWidth - beginWidth;
                        beginWidth = 0;
                    }
                    int beginHeight = cameraResolution.y / 2 - YUVWidth / 2;
                    int endHeight = cameraResolution.y / 2 + YUVWidth / 2;
                    long sumY = 0;
                    for (int j = beginWidth; j < endWidth; j++) {
                        for (int i = beginHeight; i < endHeight; i++) {
                            int y = (0xff & (data[j+i*cameraResolution.x]));
                            if (y < 0) {
                                y = 0;
                            }
                            sumY += y;
                        }
                    }
                    int bp = (int) sumY / (YUVWidth * YUVWidth);
                    if (bp > MaxPowerLight) {
                        addTimes++;
                        if (addTimes > MaxAppearTimes) {
                            Log.d("DemoLog","降低曝光补偿,nowExposureCompensation="+nowExposureCompensation+",parameters.getMinExposureCompensation()="+parameters.getMinExposureCompensation());
                            if (nowExposureCompensation - exposureCompensationStep >= parameters.getMinExposureCompensation()) {
                                parameters.setExposureCompensation(nowExposureCompensation - exposureCompensationStep);
                                camera.setParameters(parameters);
                                nowExposureCompensation = parameters.getExposureCompensation();
                            }
                            addTimes = 0;
                        }
                        minLightTimes = 0;
                    }else if(bp < MinPowerLight){
                        minLightTimes++;
                        if(minLightTimes>MinAppearTimes) {
                        }
                        addTimes = 0;
                    }else {
                        addTimes = 0;
                        minLightTimes = 0;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            Message message =
                    mPreviewHandler.obtainMessage(mPreviewMessage, cameraResolution.x, cameraResolution.y, data);
            message.sendToTarget();
            mPreviewHandler = null;
        } else {
            Log.v(TAG, "no handler callback.");
        }
    }

}
