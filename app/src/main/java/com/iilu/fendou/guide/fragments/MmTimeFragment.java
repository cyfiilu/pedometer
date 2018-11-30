package com.iilu.fendou.guide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iilu.fendou.R;

public class MmTimeFragment extends DayGoalNumFragment {

    private final int mResId_1 = R.array.mumu_time_range;
    private final int mResId_2 = R.array.mumu_time_range;

    private int[] CURR_ITEM = new int[]{1, 4};

    private String SETTING_TAG = "傍晚时间：";
    private String UNIT = "";
    private String mFixedSuffix = ""; // 固定后缀

    private String mUnit_1 = "——";
    private String mUnit_2 = "";
    private String mUnit_3 = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        init(mResId_1, mResId_2, CURR_ITEM, mUnit_1, mUnit_2, mUnit_3, SETTING_TAG, UNIT, mFixedSuffix, true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
