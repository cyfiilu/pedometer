package com.iilu.fendou.modules.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.iilu.fendou.utils.AppUtil;

import org.apache.log4j.Logger;

public class RebootAppDialog extends AlertDialog.Builder {

    private Logger mlog = Logger.getLogger(RebootAppDialog.class.getSimpleName());

    public RebootAppDialog(Context context) {
        this(context, 0);
    }

    public RebootAppDialog(final Context context, int theme) {
        super(context, theme);

        this.setMessage("是否立即重启？");
        this.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        this.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                AppUtil.easeLogout(context);
            }
        });

    }

    public void setConfigDialog(AlertDialog alertDialog, int screenWidth) {
        Window window = alertDialog.getWindow();
        window.getDecorView().setPadding(40, 30, 40, 30);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        //layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        int marginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                getContext().getResources().getDisplayMetrics());
        mlog.info("marginPx = " + marginPx + ", screenWidth = " + screenWidth);
        layoutParams.width = screenWidth - marginPx;
        window.setAttributes(layoutParams);
        window.setGravity(Gravity.CENTER);
    }

}
