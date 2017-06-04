package com.iilu.fendou.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iilu.fendou.R;

class SimpleHUDDialog extends Dialog {

    public SimpleHUDDialog(Context context, int theme) {
        super(context, theme);
    }

    public static SimpleHUDDialog createDialog(Context context, int layoutId) {
        SimpleHUDDialog dialog = new SimpleHUDDialog(context, R.style.SimpleHUD);
        dialog.setContentView(layoutId);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        return dialog;
    }

    public void setMessage(String message) {
        TextView msgView = (TextView) findViewById(R.id.simplehud_message);
        msgView.setText(message);
    }

    public void setMessage(String message1, String message2) {
        TextView msgView1 = (TextView) findViewById(R.id.simplehud_message);
        TextView msgView2 = (TextView) findViewById(R.id.simplehud_message1);
        msgView1.setText(message1);
        msgView2.setVisibility(View.VISIBLE);
        msgView2.setText(message2);
    }


    public void setImage(Context ctx, int resId) {
        ImageView image = (ImageView) findViewById(R.id.simplehud_image);
        if (resId == R.anim.anim_loading) {
            AnimationDrawable d = (AnimationDrawable) image.getDrawable();
            d.start();
        } else {
            image.setImageResource(resId);
        }
    }
}
