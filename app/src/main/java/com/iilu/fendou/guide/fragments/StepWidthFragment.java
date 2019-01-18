package com.iilu.fendou.guide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iilu.fendou.R;

public class StepWidthFragment extends HeightFragment {

    private String SETTING_TAG = "步幅：";
    private String UNIT = " cm";

    protected String[] mArgs_1 = { "0", "1"};

    private int[] CURR_ITEM = new int[] {0, 7, 0};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SETTING_TAG = getResources().getString(R.string.step_width) + "：";
        init(mArgs_1, CURR_ITEM, SETTING_TAG, UNIT);
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
