package com.iilu.fendou.modules.myself.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.iilu.fendou.MainApplication;
import com.iilu.fendou.R;
import com.iilu.fendou.scancode.EncodingHandler;
import com.iilu.fendou.utils.BitmapUtil;
import com.iilu.fendou.utils.SystemUtil;

public class CodeCardDialog extends Dialog {

    private String mCurrLoginUsername = MainApplication.getCurrLoginUsername();

    private Context mContext;
    private TextView mTip;
    private ImageView mImgCode;

    public CodeCardDialog(Context context) {
        super(context);
    }

    public CodeCardDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;

        View view = View.inflate(context, R.layout.dialog_code_card, null);
        setContentView(view);

        mTip = (TextView) view.findViewById(R.id.tv_tip);
        mImgCode = (ImageView) view.findViewById(R.id.iv_code);

        Window window = this.getWindow();
        window.getDecorView().setPadding(
                0/*SystemUtil.dip2px(mContext, 40)*/,
                0/*SystemUtil.dip2px(mContext, 120)*/,
                0/*SystemUtil.dip2px(mContext, 40)*/,
                0/*SystemUtil.dip2px(mContext, 120)*/);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        //layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.width = SystemUtil.getScreenWidth(mContext) - SystemUtil.dip2px(mContext, 80);
        layoutParams.height = SystemUtil.getScreenHeight(mContext) - SystemUtil.dip2px(mContext, 260);
        window.setAttributes(layoutParams);
        window.setGravity(Gravity.CENTER);

    }

    public void setImgView() {
        mImgCode.setImageBitmap(createCodeBitmap());
    }

    public Bitmap createCodeBitmap() {
        Bitmap bitmap = EncodingHandler.createQRCode(
                mContext,
                "当前登录用户：" + mCurrLoginUsername+ "\n" + "邮箱：cyfiilu@163.com" + "\n" + "designed by fendou" + "\n" + "All Rights Reserved",
                SystemUtil.dip2px(mContext, 300),
                SystemUtil.dip2px(mContext, 300),
                BitmapUtil.toRoundCorner(((BitmapDrawable) mContext.getResources().getDrawable(R.mipmap.head_default)).getBitmap(), 10));
        return bitmap;
    }

}
