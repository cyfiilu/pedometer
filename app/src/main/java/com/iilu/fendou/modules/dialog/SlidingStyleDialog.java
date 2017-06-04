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

public class SlidingStyleDialog extends Dialog implements CompoundButton.OnCheckedChangeListener {

    private final Logger mlog = Logger.getLogger(SlidingStyleDialog.class.getSimpleName());

    private String mValue;

    private Context mContext;
    private RadioButton mRadioButton_1;
    private RadioButton mRadioButton_2;
    private RadioButton mRadioButton_3;
    private View mLine_1;

    public SlidingStyleDialog(Context context) {
        this(context, 0);
    }

    public SlidingStyleDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;

        View view = View.inflate(context, R.layout.radio_main, null);
        setContentView(view);

        mRadioButton_1 = (RadioButton) view.findViewById(R.id.radio_button_1);
        mRadioButton_2 = (RadioButton) view.findViewById(R.id.radio_button_2);
        mRadioButton_3 = (RadioButton) view.findViewById(R.id.radio_button_3);
        mLine_1 = view.findViewById(R.id.line_2);
        mRadioButton_3.setVisibility(View.GONE);
        mLine_1.setVisibility(View.GONE);
        mRadioButton_1.setOnCheckedChangeListener(this);
        mRadioButton_2.setOnCheckedChangeListener(this);
        mRadioButton_1.setText("DrawerLayout");
        mRadioButton_2.setText("SlidingMenu");

        boolean isDrawerLayout = SPrefUtil_2.get(mContext, PrefsConfig.APP_CONST, "isDrawerLayout", true);
        if (isDrawerLayout) {
            mRadioButton_1.setChecked(true);
            mRadioButton_2.setChecked(false);
        } else {
            mRadioButton_1.setChecked(false);
            mRadioButton_2.setChecked(true);
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
                    SPrefUtil_2.put(mContext, PrefsConfig.APP_CONST, "isDrawerLayout", true);
                    break;
                case R.id.radio_button_2:
                    mValue = buttonView.getText().toString();
                    SPrefUtil_2.put(mContext, PrefsConfig.APP_CONST, "isDrawerLayout", false);
                    break;
            }
        }
        dismiss();
    }

    public String getValue() {
        return mValue;
    }

}
