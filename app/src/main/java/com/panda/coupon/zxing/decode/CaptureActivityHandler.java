/*
 * Copyright (C) 2008 ZXing authors
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

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.panda.coupon.R;
import com.panda.coupon.activity.ScanCodeActivity;
import com.panda.coupon.zxing.camera.CameraManager;
import com.google.zxing.Result;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 */
public final class CaptureActivityHandler extends Handler {
    static final String TAG = CaptureActivityHandler.class.getName();
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    final ScanCodeActivity mActivity;
    @NonNull
    final DecodeThread mDecodeThread;
    volatile State mState;

    enum State {
        PREVIEW, SUCCESS, DONE
    }

    public CaptureActivityHandler(ScanCodeActivity activity, String type) {
        this.mActivity = activity;
        mDecodeThread = new DecodeThread(activity,type);
        mDecodeThread.start();
        mState = State.SUCCESS;
        // Start ourselves capturing previews and decoding.
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(@NonNull Message message) {
        switch (message.what) {
            case R.id.auto_focus:
                // Log.d(TAG, "Got auto-focus message");
                // When one auto focus pass finishes, start another. This is the closest thing to
                // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
                if (mState == State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                }
                break;
            case R.id.decode_succeeded:
                Log.e(TAG, "Got decode succeeded message");
                mState = State.SUCCESS;
                mActivity.handleDecode((Result) message.obj);
                break;
            case R.id.decode_failed:
                // We're decoding as fast as possible, so when one decode fails, start another.
                mState = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), R.id.decode);
                break;
        }
    }

    public void quitSynchronously() {
        mState = State.DONE;
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(mDecodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            mDecodeThread.join();
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    public void restartPreviewAndDecode() {
        if (mState != State.PREVIEW) {
            try {
                executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            //release相机后不能调用该方法
                            if(mState != State.DONE) {
                                try {
                                    CameraManager.get().startPreview();
                                    mState = State.PREVIEW;
                                    CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), R.id.decode);
                                    CameraManager.get().requestAutoFocus(CaptureActivityHandler.this, R.id.auto_focus);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
