package com.iilu.fendou.modules.message.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iilu.fendou.MainPreferenceFragment;
import com.iilu.fendou.R;
import com.iilu.fendou.configs.PrefsConfig;
import com.iilu.fendou.modules.message.dialog.ChatFaceSettingDialog;
import com.iilu.fendou.preference.MainPreference;
import com.iilu.fendou.utils.SPrefUtil_2;

import org.apache.log4j.Logger;

import de.greenrobot.event.EventBus;

public class ChatFaceSettingFragment extends MainPreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    final Logger mlog = Logger.getLogger(ChatFaceSettingFragment.class.getSimpleName());

    private Activity mActivity;
    private MainPreference mFaceChangeStylePref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.chat_face_setting);

        mActivity = getActivity();

        EventBus.getDefault().register(this);

        mFaceChangeStylePref = (MainPreference) findPreference("face_change_style");

        boolean isCanScroll = SPrefUtil_2.get(mActivity, PrefsConfig.MSG_CHAT, "isCanScroll", true);
        if (isCanScroll) {
            mFaceChangeStylePref.setTvRightText("滑动切换");
        } else {
            mFaceChangeStylePref.setTvRightText("点击tab切换");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_myself_1, container, false);
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
            ChatFaceSettingDialog dialog = new ChatFaceSettingDialog(mActivity, R.style.NewDialogSytle);
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
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
