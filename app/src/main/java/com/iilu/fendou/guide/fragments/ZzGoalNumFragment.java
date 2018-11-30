package com.iilu.fendou.guide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iilu.fendou.R;

public class ZzGoalNumFragment extends DayGoalNumFragment {

    private final int mResId_1 = R.array.three_to_eight;
    private final int mResId_2 = R.array.zero_to_nine;

    private int[] CURR_ITEM = new int[]{0, 0};

    private String SETTING_TAG = "早晨目标：";
    private String UNIT = " 步";
    private String mFixedSuffix = "00"; // 固定后缀

    private String mUnit_1 = "千";
    private String mUnit_2 = "百";
    private String mUnit_3 = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        init(mResId_1, mResId_2, CURR_ITEM, mUnit_1, mUnit_2, mUnit_3, SETTING_TAG, UNIT, mFixedSuffix, false);
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
