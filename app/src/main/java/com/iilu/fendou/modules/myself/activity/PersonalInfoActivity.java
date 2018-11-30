package com.iilu.fendou.modules.myself.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iilu.fendou.MainActivity;
import com.iilu.fendou.R;
import com.iilu.fendou.modules.myself.fragment.PersonalInfoFragment;
import com.iilu.fendou.utils.StatusBarUtil;

/**
 * Created by Administrator on 2018/11/23.
 */
public class PersonalInfoActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs_main);
        StatusBarUtil.compat(this, Color.TRANSPARENT);
        initView();
    }

    private void initView() {
        RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.pref_title);
        ((TextView) titleLayout.findViewById(R.id.tv_title)).setText("个人信息");
        ImageView imgBack = (ImageView) titleLayout.findViewById(R.id.iv_left);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, new PersonalInfoFragment());
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
