package com.panda.coupon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.panda.coupon.R;

/**
 * Created by congxiany on 18/3/7.
 */

public class ConfirmDialog extends Dialog {
    DelayCallBack delayCallBack;
    private Context mContext;

    public interface DelayCallBack {
        void callBack();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (delayCallBack != null) {
                        delayCallBack.callBack();
                    }
                    break;
            }
        }
    };

    public void doDelayCallBack(DelayCallBack delayoutCallBack, int timeout) {
        this.delayCallBack = delayoutCallBack;
        handler.sendEmptyMessageDelayed(0, timeout);
    }

    public ConfirmDialog(Context context) {
        super(context);
    }

    public ConfirmDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ConfirmDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        handler.removeCallbacksAndMessages(null);
    }

    public static class AlertParams {

        Context mContext;
        CharSequence title;
        CharSequence message;
        CharSequence leftButtonStr;
        CharSequence rightButtonStr;
        IClickListener leftButtonClickListener;
        IClickListener rightButtonClickListener;
        DialogInterface.OnCancelListener onCancelListener;
        DialogInterface.OnDismissListener onDismissListener;
        String leftButtonColor;
        String rightButtonColor;
        View customView;
        boolean mCancelable = true;//默认响应back按钮。

        public AlertParams(Context mContext) {
            this.mContext = mContext;
        }

        public void apply(final ConfirmDialog dialog) {
            View view = View.inflate(this.mContext, R.layout.dialog_alert, null);
            dialog.setContentView(view);
            if(!TextUtils.isEmpty(title)) {
                ((TextView) view.findViewById(R.id.title)).setText(title);
            }else{
                ((TextView) view.findViewById(R.id.title)).setTextSize(0);
            }
            LinearLayout customViewGroup = (LinearLayout) view.findViewById(R.id.custom_view);
            TextView tvMessage = (TextView) view.findViewById(R.id.message);
            if (customView != null) {
                tvMessage.setVisibility(View.GONE);
                customViewGroup.setVisibility(View.VISIBLE);
                customViewGroup.addView(customView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                customViewGroup.setVisibility(View.GONE);
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(message);
            }
            if (TextUtils.isEmpty(leftButtonStr) && TextUtils.isEmpty(rightButtonStr)) {
                view.findViewById(R.id.action_layout).setVisibility(View.GONE);
            } else {
                if (!TextUtils.isEmpty(leftButtonStr) && TextUtils.isEmpty(rightButtonStr)) {
                    TextView singleView = (TextView) view.findViewById(R.id.single_button);
                    TextView okView = (TextView) view.findViewById(R.id.ok);
                    TextView cancelView = (TextView) view.findViewById(R.id.cancel);
                    singleView.setVisibility(View.VISIBLE);
                    okView.setVisibility(View.GONE);
                    view.findViewById(R.id.action_line).setVisibility(View.GONE);
                    cancelView.setVisibility(View.GONE);
                    singleView.setText(leftButtonStr);
                    if (!TextUtils.isEmpty(leftButtonColor)) {
                        try {
                            singleView.setTextColor(Color.parseColor(leftButtonColor));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    singleView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (leftButtonClickListener != null) {
                                leftButtonClickListener.click(dialog);
                            }
                            if (dialog != null && dialog.isShowing()) dialog.dismiss();
                        }
                    });
                } else {
                    TextView cancelView = (TextView) view.findViewById(R.id.cancel);
                    if (TextUtils.isEmpty(leftButtonStr)) {
                        cancelView.setVisibility(View.GONE);
                    } else {
                        cancelView.setText(leftButtonStr);
                        if (!TextUtils.isEmpty(leftButtonColor)) {
                            try {
                                cancelView.setTextColor(Color.parseColor(leftButtonColor));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        cancelView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (leftButtonClickListener != null) {
                                    leftButtonClickListener.click(dialog);
                                }
                                if (dialog != null && dialog.isShowing()) dialog.dismiss();
                            }
                        });
                    }
                    TextView okView = (TextView) view.findViewById(R.id.ok);
                    if (TextUtils.isEmpty(rightButtonStr)) {
                        okView.setVisibility(View.GONE);
                    } else {
                        okView.setText(rightButtonStr);
                        if (!TextUtils.isEmpty(rightButtonColor)) {
                            try {
                                okView.setTextColor(Color.parseColor(rightButtonColor));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        okView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (rightButtonClickListener != null) {
                                    rightButtonClickListener.click(dialog);
                                }
                                if (dialog != null && dialog.isShowing()) dialog.dismiss();
                            }
                        });
                    }
                }
            }
        }
    }

    public static class Builder {
        AlertParams alertParams;

        public Builder(Context mContext) {
            alertParams = new AlertParams(mContext);
        }

        public Builder setTitle(CharSequence title) {
            alertParams.title = title;
            return this;
        }

        public Builder setMessage(CharSequence message) {
            alertParams.message = message;
            return this;
        }

        public Builder setLeftButtonColor(String leftButtonColor) {
            alertParams.leftButtonColor = leftButtonColor;
            return this;
        }

        public Builder setRightButtonColor(String rightButtonColor) {
            alertParams.rightButtonColor = rightButtonColor;
            return this;
        }

        public Builder setLeftButtonStr(CharSequence leftButtonStr) {
            alertParams.leftButtonStr = leftButtonStr;
            return this;
        }

        public Builder setRightButtonStr(CharSequence rightButtonStr) {
            alertParams.rightButtonStr = rightButtonStr;
            return this;
        }

        public Builder setLeftClickListener(IClickListener leftClickListener) {
            alertParams.leftButtonClickListener = leftClickListener;
            return this;
        }

        public Builder setRightClickListener(IClickListener rightClickListener) {
            alertParams.rightButtonClickListener = rightClickListener;
            return this;
        }

        public Builder setCancleable(boolean isCancelable) {
            alertParams.mCancelable = isCancelable;
            return this;
        }

        public Builder setOnDissmissListener(DialogInterface.OnDismissListener dissmissListener) {
            alertParams.onDismissListener = dissmissListener;
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener cancelListener) {
            alertParams.onCancelListener = cancelListener;
            return this;
        }

        public Builder setCustomView(View view) {
            alertParams.customView = view;
            return this;
        }

        public ConfirmDialog create() {
            // Context has already been wrapped with the appropriate theme.
            final ConfirmDialog dialog = new ConfirmDialog(alertParams.mContext, R.style.alert_dialog);
            dialog.setContext(alertParams.mContext);
            alertParams.apply(dialog);
            dialog.setCancelable(alertParams.mCancelable);
            if (alertParams.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(alertParams.onCancelListener);
            dialog.setOnDismissListener(alertParams.onDismissListener);

            return dialog;
        }

        public ConfirmDialog show() {
            final ConfirmDialog dialog = create();
            dialog.show();
            return dialog;
        }

    }

    private void setContext(Context context) {
        mContext = context;
    }


    public Context getDialogContext() {
        return mContext;
    }

    public interface IClickListener {
        void click(Dialog dialog);
    }

//    public interface IClickListener4Api {
//        void click(Dialog dialog, CommonDialogBean.BtnsBean btnBean);
//    }
}
