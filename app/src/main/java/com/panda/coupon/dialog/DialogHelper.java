package com.panda.coupon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

/**
 * Created by congxiany on 18/3/7.
 */

public class DialogHelper {

    public static Dialog showAlertDialog(final Context mContext, String title, String msg, String leftBtnStr, String rightBtnStr, final ConfirmDialog.IClickListener leftBtnListener, final ConfirmDialog.IClickListener rightBtnListener) {
        return showAlertDialog(mContext,title,msg,leftBtnStr,rightBtnStr,"","",leftBtnListener,rightBtnListener);
    }

    public static Dialog showAlertDialog(final Context mContext, String title, String msg, String leftBtnStr, String rightBtnStr, String leftBtnColor, String rightBtnColor, final ConfirmDialog.IClickListener leftBtnListener, final ConfirmDialog.IClickListener rightBtnListener) {
        final ConfirmDialog.Builder dialogBuilder = new ConfirmDialog.Builder(mContext).setTitle(TextUtils.isEmpty(title) ? "" : title).setMessage(TextUtils.isEmpty(msg) ? "" : msg);
        if (!TextUtils.isEmpty(leftBtnStr)) {
            dialogBuilder.setLeftButtonStr(leftBtnStr);
            if (leftBtnListener != null) dialogBuilder.setLeftClickListener(leftBtnListener);
        }
        if (!TextUtils.isEmpty(leftBtnColor)) {
            dialogBuilder.setLeftButtonColor(leftBtnColor);
        }
        if (!TextUtils.isEmpty(rightBtnStr)) {
            dialogBuilder.setRightButtonStr(rightBtnStr);
            if (rightBtnListener != null) dialogBuilder.setRightClickListener(rightBtnListener);
        }
        if (!TextUtils.isEmpty(rightBtnColor)) {
            dialogBuilder.setRightButtonColor(rightBtnColor);
        }
        final ConfirmDialog dialog = dialogBuilder.create();
        dialog.show();
        return dialog;
    }

}
