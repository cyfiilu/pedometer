package com.iilu.fendou.modules.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.iilu.fendou.MainActivity;
import com.iilu.fendou.R;
import com.iilu.fendou.configs.PrefsConfig;
import com.iilu.fendou.dbs.LoginDB;
import com.iilu.fendou.guide.GuideActivity;
import com.iilu.fendou.modules.HomeActivity;
import com.iilu.fendou.modules.register.RegisterActivity;
import com.iilu.fendou.utils.SPrefUtil_1;
import com.iilu.fendou.utils.StatusBarUtil;
import com.iilu.fendou.utils.ToastUtil;

import org.apache.log4j.Logger;

public class LoginActivity extends MainActivity implements View.OnClickListener {

    private Logger mlog = Logger.getLogger(LoginActivity.class.getSimpleName());

    private EditText mUserName;
    private EditText mPassword;

    private Button mRegister;
    private Button mLogin;

    private SPrefUtil_1 mSPrefUtil_1;
    private LoginDB mLoginDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarUtil.compat(this, Color.TRANSPARENT);

        mLoginDB = new LoginDB(this);
        mSPrefUtil_1 = new SPrefUtil_1(this, PrefsConfig.USER_LOGIN);

        mUserName = (EditText) findViewById(R.id.et_user_name);
        mPassword = (EditText) findViewById(R.id.et_password);

        mRegister = (Button) findViewById(R.id.btn_register);
        mLogin = (Button) findViewById(R.id.btn_login);

        mRegister.setOnClickListener(this);
        mLogin.setOnClickListener(this);

        mUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPassword.setText(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                    return true;
                }
                return false;
            }
        });

        //login(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }

    private void login() {
        //mLoginDB.queryUserLoginInfo("")

        final String username = mUserName.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            ToastUtil.showCenter(this, getResources().getString(R.string.user_name_not_null_tip));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showCenter(this, getString(R.string.password_not_null_tip));
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.is_logging));
        progressDialog.show();

        EMClient.getInstance().login(username, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                mlog.info("环信登录成功!");
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                saveUserInfo(username, password);

                startActivity(username);

                finish();
            }

            @Override
            public void onError(int code, final String error) {
                mlog.error("环信登录失败 code = " + code + ", error = " + error);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                // 这些方法都在工作线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showCenter(LoginActivity.this, "登录失败：" + error);
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
                mlog.error("环信登录进度 progress = " + progress + ", status = " + status);
            }
        });
    }

    private void saveUserInfo(String username, String password) {
        // 1. 存入数据库
        if (mLoginDB != null) {
            mLoginDB.saveUserLoginInfo(username, password);
        }
        // 2. 存入sp
        mSPrefUtil_1.setParam("curr_login_username", username);
    }

    private void startActivity(String username) {
        boolean isFirstLogin = (boolean) mSPrefUtil_1.getParam(username + "_isFirstLogin", true);
        Intent intent;
        if (isFirstLogin) {
            intent = new Intent(this, GuideActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
