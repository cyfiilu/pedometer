package com.iilu.fendou.modules.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.iilu.fendou.MainFragment;
import com.iilu.fendou.MainPreferenceFragment;
import com.iilu.fendou.R;
import com.iilu.fendou.modules.myself.fragment.MyselfFragment_1;
import com.iilu.fendou.modules.myself.fragment.PersonalCoverFragment;
import com.iilu.fendou.utils.AppUtil;
import com.iilu.fendou.utils.StatusBarUtil;
import com.ybao.pullrefreshview.layout.FlingLayout;

import org.apache.log4j.Logger;

public class MyselfFragment extends MainFragment {

    private final Logger mlog = Logger.getLogger(MyselfFragment.class.getSimpleName());

    private Context mContext;
    private View mView;
    private PersonalCoverFragment mPersonalCoverFragment;
    private MainPreferenceFragment mMyselfFragment_1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }

        try {
            mView = inflater.inflate(R.layout.fragment_myself, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }

        mPersonalCoverFragment = (PersonalCoverFragment) getChildFragmentManager().findFragmentById(R.id.personal_cover_fragment);
        mMyselfFragment_1 = (MyselfFragment_1) getActivity().getFragmentManager().findFragmentById(R.id.myself_fragment_1);

        mView.findViewById(R.id.tv_login_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomDialog(mContext, R.style.NewDialogSytle).show();
            }
        });

        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPersonalCoverFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == 0x002) {
                mMyselfFragment_1.onRequestPermissionsResult(requestCode, permissions, grantResults);
            } else if (requestCode == 0x003) {
                mPersonalCoverFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!getActivity().isDestroyed()) {
            getChildFragmentManager().beginTransaction().remove(mPersonalCoverFragment).commitAllowingStateLoss();
            getActivity().getFragmentManager().beginTransaction().remove(mMyselfFragment_1).commitAllowingStateLoss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class BottomDialog extends Dialog implements View.OnClickListener {

        public BottomDialog(Context context, int theme) {
            super(context, theme);
            View view = View.inflate(context, R.layout.dialog_bottom_logout, null);
            view.findViewById(R.id.tv_logout).setOnClickListener(this);
            view.findViewById(R.id.tv_cancel).setOnClickListener(this);

            setContentView(view);

            Window window = this.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.BottomAnim);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_logout:
                    dismiss();
                    AppUtil.easeLogout(mContext);
                    break;
                case R.id.tv_cancel:
                    dismiss();
                    break;
            }
        }
    }
}
