package com.iilu.fendou.modules.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.iilu.fendou.R;
import com.iilu.fendou.configs.PrefsConfig;
import com.iilu.fendou.utils.SPrefUtil_2;

import org.apache.log4j.Logger;

public class SlidingMenuStyleDialog extends Dialog implements CompoundButton.OnCheckedChangeListener {

    private final Logger mlog = Logger.getLogger(SlidingMenuStyleDialog.class.getSimpleName());

    private String mValue;

    private Context mContext;
    private RadioButton mRadioButton_1;
    private RadioButton mRadioButton_2;
    private RadioButton mRadioButton_3;

    public SlidingMenuStyleDialog(Context context) {
        this(context, 0);
    }

    public SlidingMenuStyleDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;

        View view = View.inflate(context, R.layout.radio_main, null);
        setContentView(view);

        mRadioButton_1 = (RadioButton) view.findViewById(R.id.radio_button_1);
        mRadioButton_2 = (RadioButton) view.findViewById(R.id.radio_button_2);
        mRadioButton_3 = (RadioButton) view.findViewById(R.id.radio_button_3);
        mRadioButton_1.setOnCheckedChangeListener(this);
        mRadioButton_2.setOnCheckedChangeListener(this);
        mRadioButton_3.setOnCheckedChangeListener(this);
        mRadioButton_1.setText("普通");
        mRadioButton_2.setText("抽屉");
        mRadioButton_3.setText("仿qq");

        int sliding_menu_style = SPrefUtil_2.get(mContext, PrefsConfig.APP_CONST, "sliding_menu_style", 0);
        if (sliding_menu_style == 0) {
            mRadioButton_1.setChecked(true);
            mRadioButton_2.setChecked(false);
            mRadioButton_3.setChecked(false);
        } else if (sliding_menu_style == 1) {
            mRadioButton_1.setChecked(false);
            mRadioButton_2.setChecked(true);
            mRadioButton_3.setChecked(false);
        } else if (sliding_menu_style == 2) {
            mRadioButton_1.setChecked(false);
            mRadioButton_2.setChecked(false);
            mRadioButton_3.setChecked(true);
        }

        Window window = this.getWindow();
        window.getDecorView().setPadding(60, 0, 60, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //mlog.debug("button text = " + buttonView.getText().toString() + ", isChecked = " + isChecked);
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.radio_button_1:
                    mValue = buttonView.getText().toString();
                    SPrefUtil_2.put(mContext, PrefsConfig.APP_CONST, "sliding_menu_style", 0);
                    break;
                case R.id.radio_button_2:
                    mValue = buttonView.getText().toString();
                    SPrefUtil_2.put(mContext, PrefsConfig.APP_CONST, "sliding_menu_style", 1);
                    break;
                case R.id.radio_button_3:
                    mValue = buttonView.getText().toString();
                    SPrefUtil_2.put(mContext, PrefsConfig.APP_CONST, "sliding_menu_style", 2);
                    break;
            }
        }
        dismiss();
    }

    public String getValue() {
        return mValue;
    }

}
