package com.iilu.fendou.modules.register;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.iilu.fendou.MainActivity;
import com.iilu.fendou.R;
import com.iilu.fendou.utils.StatusBarUtil;
import com.iilu.fendou.utils.ToastUtil;

public class RegisterActivity extends MainActivity implements View.OnClickListener {

    private final int MSG_EMPETY = 0x00005;

    private EditText mUsername;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private Button mRegiest;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_EMPETY:
                    ToastUtil.showCenter(RegisterActivity.this, "恭喜，注册成功！");
                    finish();
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StatusBarUtil.compat(this, Color.TRANSPARENT);
        initViews();
    }

    private void initViews() {
        mUsername = (EditText) findViewById(R.id.et_username);
        mPassword = (EditText) findViewById(R.id.et_password);
        mConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        mRegiest = (Button) findViewById(R.id.btn_register);

        mRegiest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                final String username = mUsername.getText().toString();
                final String password = mPassword.getText().toString();
                final String confirmPassword = mConfirmPassword.getText().toString();

                if (TextUtils.isEmpty(username.trim())) {
                    mUsername.setError(setErrorTextColror("用户名不允许为空！"));
                    return;
                } else if (TextUtils.isEmpty(password.trim())) {
                    mPassword.setError(setErrorTextColror("密码不允许为空！"));
                    return;
                } else if (TextUtils.isEmpty(confirmPassword.trim())) {
                    mConfirmPassword.setError(setErrorTextColror("确认密码不允许为空！"));
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    ToastUtil.showCenter(this, "两次输入密码不一致！");
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //注册失败会抛出HyphenateException
                            EMClient.getInstance().createAccount(username, password);//同步方法
                            mHandler.sendEmptyMessageDelayed(MSG_EMPETY, 1000);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }

    private Spanned setErrorTextColror(String text) {
        return Html.fromHtml("<font color=#ee0000>" + text + "</font>");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}