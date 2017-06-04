package com.iilu.fendou.modules.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.iilu.fendou.MainFragment;
import com.iilu.fendou.R;
import com.iilu.fendou.modules.myself.fragment.PersonalCoverFragment;
import com.iilu.fendou.utils.AppUtil;
import com.ybao.pullrefreshview.layout.FlingLayout;

import org.apache.log4j.Logger;

public class MyselfFragment extends MainFragment {

    private final Logger mlog = Logger.getLogger(MyselfFragment.class.getSimpleName());

    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FlingLayout view = (FlingLayout) inflater.inflate(R.layout.fragment_myself, container, false);

        view.findViewById(R.id.btn_login_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomDialog(mContext, R.style.NewDialogSytle).show();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new PersonalCoverFragment().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!((Activity) mContext).isDestroyed()) {
            FragmentManager fragmentManager = getChildFragmentManager();
            Fragment personalCoverFragment = getChildFragmentManager().findFragmentById(R.id.personal_cover_fragment);
            Fragment myselfFragment_1 = getChildFragmentManager().findFragmentById(R.id.myself_fragment_1);

            if (personalCoverFragment != null && !fragmentManager.isDestroyed()) {
                getFragmentManager().beginTransaction().remove(personalCoverFragment).commit();
            }
            if (myselfFragment_1 != null && !fragmentManager.isDestroyed()) {
                getFragmentManager().beginTransaction().remove(myselfFragment_1).commit();
            }
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
            Button logout = (Button) view.findViewById(R.id.btn_logout);
            Button cancel = (Button) view.findViewById(R.id.btn_cancel);

            logout.setOnClickListener(this);
            cancel.setOnClickListener(this);

            setContentView(view);

            Window window = this.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.BottomAnim);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_logout:
                    dismiss();
                    AppUtil.easeLogout(mContext);
                    break;
                case R.id.btn_cancel:
                    dismiss();
                    break;
            }
        }
    }
}
