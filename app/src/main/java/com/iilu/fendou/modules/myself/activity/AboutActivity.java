package com.iilu.fendou.modules.myself.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iilu.fendou.BuildConfig;
import com.iilu.fendou.MainActivity;
import com.iilu.fendou.R;
import com.iilu.fendou.utils.ToastUtil;

public class AboutActivity extends MainActivity implements View.OnClickListener {

    private String mAppVersionStr =  "版本：" + BuildConfig.VERSION_NAME;
    private String mBuildTimestamp =  "打包时间：" + BuildConfig.BUILD_TIMESTAMP;

    private TextView mAppVersion;
    private TextView mGithubAddress;
    private Button mCheckNewVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initViews();

    }

    private void initViews() {
        RelativeLayout layoutTitle = (RelativeLayout) findViewById(R.id.about_title);
        ((TextView) layoutTitle.findViewById(R.id.tv_title)).setText(getResources().getString(R.string.app_about));
        ImageView imgBack = (ImageView) layoutTitle.findViewById(R.id.iv_left);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(this);
        mAppVersion = (TextView) findViewById(R.id.tv_app_version);
        mGithubAddress = (TextView) findViewById(R.id.tv_github_address);
        mCheckNewVersion = (Button) findViewById(R.id.btn_check_new_versin);
        mAppVersion.setOnClickListener(this);
        mGithubAddress.setOnClickListener(this);
        mCheckNewVersion.setOnClickListener(this);

        mAppVersion.setText(mAppVersionStr);
        mGithubAddress.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_app_version:
                ToastUtil.showCenter(this, mBuildTimestamp);
                break;
            case R.id.tv_github_address:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/cyfiilu/pedometer"));
                startActivity(intent);
                break;
            case R.id.btn_check_new_versin:
                ToastUtil.showBottom(this, "已经是最新版本啦！");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
