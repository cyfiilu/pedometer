package com.iilu.fendou.modules.message.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iilu.fendou.MainPreferenceActivity;
import com.iilu.fendou.R;
import com.iilu.fendou.configs.PrefsConfig;
import com.iilu.fendou.modules.message.dialog.ChatFaceSettingDialog;
import com.iilu.fendou.preference.MainPreference;
import com.iilu.fendou.utils.SPrefUtil_2;

import org.apache.log4j.Logger;

import de.greenrobot.event.EventBus;

public class ChatFaceSettingActivity extends MainPreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    final Logger mlog = Logger.getLogger(ChatFaceSettingActivity.class.getSimpleName());

    private MainPreference mFaceChangeStylePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs_main);
        addPreferencesFromResource(R.xml.chat_face_setting);

        RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.pref_title);
        ((TextView) titleLayout.findViewById(R.id.tv_title)).setText("表情设置");
        ImageView imgBack = (ImageView) titleLayout.findViewById(R.id.iv_left);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        EventBus.getDefault().register(this);
        mFaceChangeStylePref = (MainPreference) findPreference("face_change_style");

        boolean isCanScroll = SPrefUtil_2.get(this, PrefsConfig.MSG_CHAT, "isCanScroll", true);
        if (isCanScroll) {
            mFaceChangeStylePref.setTvRightText("滑动切换");
        } else {
            mFaceChangeStylePref.setTvRightText("点击tab切换");
        }

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        mlog.info("onPreferenceClick()...");

        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if (preference == mFaceChangeStylePref) {
            mlog.info("onPreferenceTreeClick()... key = " + key);
            ChatFaceSettingDialog dialog = new ChatFaceSettingDialog(this, R.style.NewDialogSytle);
            dialog.show();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mFaceChangeStylePref.setTvRightText(((ChatFaceSettingDialog) dialog).getValue());
                    EventBus.getDefault().post("face_change_type_changed");
                }
            });
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        mlog.info("onPreferenceChange()...");

        return false;
    }

    public void onEventMainThread(String event) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
