package com.iilu.fendou.modules.myself.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.iilu.fendou.MainApplication;
import com.iilu.fendou.R;
import com.iilu.fendou.utils.SystemUtil;

public class AppIntroduceDialog extends Dialog {

    private Context mContext;

    public AppIntroduceDialog(Context context) {
        super(context);
    }

    public AppIntroduceDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;

        View view = View.inflate(context, R.layout.dialog_app_introduce, null);
        setContentView(view);

        Window window = this.getWindow();
        window.getDecorView().setPadding(
                SystemUtil.dip2px(mContext, 40),
                SystemUtil.dip2px(mContext, 120),
                SystemUtil.dip2px(mContext, 40),
                SystemUtil.dip2px(mContext, 120));
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);
        window.setGravity(Gravity.CENTER);

        TextView textView = (TextView) view.findViewById(R.id.tv_content);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView.setText(MainApplication.getAppIntroduceContent());

    }

}
