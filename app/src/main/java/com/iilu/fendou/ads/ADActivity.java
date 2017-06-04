package com.iilu.fendou.ads;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.iilu.fendou.MainActivity;
import com.iilu.fendou.R;
import com.iilu.fendou.modules.login.LoginActivity;

import org.apache.log4j.Logger;

public class ADActivity extends MainActivity {

    private final Logger mlog = Logger.getLogger(ADActivity.class.getSimpleName());

    private final int MSG_START_GUIDE_ACTIVITY = 0x00001;

    private Button mSkip;
    private TextView mCopyRight;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startActivity();
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        mSkip = (Button) findViewById(R.id.btn_skip);
        mCopyRight = (TextView) findViewById(R.id.tv_copy_right);

        TextView ad_1 = (TextView) findViewById(R.id.tv_ad_1);
        TextView ad_2 = (TextView) findViewById(R.id.tv_ad_2);

        ad_1.setAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_right_in));
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_left_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSkip.setVisibility(View.VISIBLE);
                mCopyRight.setVisibility(View.VISIBLE);

                mHandler.sendEmptyMessageDelayed(MSG_START_GUIDE_ACTIVITY, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ad_2.setAnimation(animation);

        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity();
                mHandler.removeMessages(MSG_START_GUIDE_ACTIVITY);
                finish();
            }
        });

    }

    private void startActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
