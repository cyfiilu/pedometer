package com.iilu.fendou.modules.myself.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iilu.fendou.MainActivity;
import com.iilu.fendou.MainApplication;
import com.iilu.fendou.R;
import com.iilu.fendou.utils.StatusBarUtil;

public class AppIntroduceActivity extends MainActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_introduce);
        StatusBarUtil.compat(this, Color.TRANSPARENT);

        RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.title_introduce);
        ((TextView) titleLayout.findViewById(R.id.tv_title)).setText("app介绍");
        ImageView imgBack = (ImageView) titleLayout.findViewById(R.id.iv_left);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTextView = (TextView) findViewById(R.id.tv_content);
        mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTextView.setText(MainApplication.getAppIntroduceContent());
    }
}
