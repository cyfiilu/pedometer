package com.iilu.fendou.guide.fragments;

import android.content.Context;
import android.widget.TextView;

import com.iilu.fendou.MainApplication;
import com.iilu.fendou.MainFragment;

public class GuideBaseFragment extends MainFragment {

    protected Context mContext = MainApplication.getAppContext();

    public void setView(TextView textView) {}

    public int[] getCurrItemIndex() {
        return null;
    }

    public void setCurrItemIndex(int[] currItemIndex) {}

}
