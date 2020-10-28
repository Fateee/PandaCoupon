package com.panda.coupon.zxing.decode;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import com.panda.coupon.activity.BaseActivity;
import com.panda.coupon.dialog.ConfirmDialog;
import com.panda.coupon.dialog.DialogHelper;
import com.panda.coupon.R;
import com.panda.coupon.utils.JMToast;
import com.yanzhenjie.permission.AndPermission;

/**
 * 二维码解析管理。
 */
public class DecodeManager {
    public Dialog dialog = null;
    public void showCameraPermissionDialog(@NonNull final Context context) {
        if (dialog == null) {
            dialog = DialogHelper.showAlertDialog(context, context.getResources().getString(R.string.qr_code_not_use), context.getResources().getString(R.string.qr_code_permission_dialog), context.getResources().getString(R.string.cancel), context.getResources().getString(R.string.go_open),
                    new ConfirmDialog.IClickListener() {
                        @Override
                        public void click(Dialog dialog) {
                            ((BaseActivity) context).finish();
                        }
                    }, new ConfirmDialog.IClickListener() {
                        @Override
                        public void click(Dialog dialog) {
                            AndPermission.with(context).runtime().setting().start();
                        }
                    }
            );
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    ((BaseActivity) context).finish();
                }
            });
        } else if (!dialog.isShowing()) {
            try {
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleResult(String url) {
        JMToast.show(url);
    }

    public void showCouldNotReadQrCodeFromScanner() {
        JMToast.show("无法识别二维码");
    }

    public void showCouldNotReadQrCodeFromPicture(Context context) {
    }

    public interface OnRefreshCameraListener {
        void refresh();
    }

}
