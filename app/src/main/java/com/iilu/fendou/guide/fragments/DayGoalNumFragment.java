package com.iilu.fendou.guide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iilu.fendou.R;
import com.iilu.fendou.adapters.wheel.AbstractWheelTextAdapter;
import com.iilu.fendou.views.wheel.OnWheelChangedListener;
import com.iilu.fendou.views.wheel.WheelView;

public class DayGoalNumFragment extends GuideBaseFragment {

    private final int COLOR_TOP = 0xefEDEDED;
    private final int COLOR_CENTER = 0xcfEDEDED;
    private final int COLOR_BOTTOM = 0x3fEDEDED;

    private int mResId_1 = R.array.zero_to_three;
    private int mResId_2 = R.array.zero_to_nine;
    private int mResId_3 = -1;

    private int[] CURR_ITEM = new int[]{1, 0};

    private String[] mArgs_1;
    private String[] mArgs_2;
    private String[] mArgs_3;

    private String SETTING_TAG = "每日目标：";
    private String UNIT = " 步";
    private String mValue;
    private String mFixedSuffix = "000"; // 固定后缀

    private String mUnit_1 = "万";
    private String mUnit_2 = "千";
    private String mUnit_3 = "";

    private boolean isTime = false;

    private WheelView mWheelView_1;
    private WheelView mWheelView_2;
    private WheelView mWheelView_3;

    private TextView mWheelView_1_unit;
    private TextView mWheelView_2_unit;
    private TextView mWheelView_3_unit;

    private TextView mTextView;

    protected void init(int resId_1, int resId_2,
                        int[] curr_item,
                        String unit_1, String unit_2, String unit_3,
                        String setting_tag, String unit, String fixedSuffix,
                        boolean isTime) {
        this.mResId_1 = resId_1;
        this.mResId_2 = resId_2;
        this.CURR_ITEM = curr_item;
        this.mUnit_1 = unit_1;
        this.mUnit_2 = unit_2;
        this.mUnit_3 = unit_3;
        this.SETTING_TAG = setting_tag;
        this.mFixedSuffix = fixedSuffix;
        this.UNIT = unit;
        this.isTime = isTime;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wheel_2, container, false);

        initViews(view);

        initDatas();

        setWheelViewUnit(mUnit_1, mUnit_2, mUnit_3);

        addChangingListener();

        return view;
    }

    private void initViews(View view) {
        mWheelView_1 = (WheelView) view.findViewById(R.id.wheelView_1);
        mWheelView_2 = (WheelView) view.findViewById(R.id.wheelView_2);
        mWheelView_3 = (WheelView) view.findViewById(R.id.wheelView_3);

        mWheelView_1_unit = (TextView) view.findViewById(R.id.wheelView_1_unit);
        mWheelView_2_unit = (TextView) view.findViewById(R.id.wheelView_2_unit);
        mWheelView_3_unit = (TextView) view.findViewById(R.id.wheelView_3_unit);

        mWheelView_1.setShadowColor(COLOR_TOP, COLOR_CENTER, COLOR_BOTTOM);
        mWheelView_2.setShadowColor(COLOR_TOP, COLOR_CENTER, COLOR_BOTTOM);
        mWheelView_3.setShadowColor(COLOR_TOP, COLOR_CENTER, COLOR_BOTTOM);

        mWheelView_1.setBackgroundResource(R.color.gray_2);
        mWheelView_2.setBackgroundResource(R.color.gray_2);
        mWheelView_3.setBackgroundResource(R.color.gray_2);
    }

    private void initDatas() {
        if (mResId_1 != -1) {
            mArgs_1 = getResources().getStringArray(mResId_1);
        } else {
            mWheelView_1.setVisibility(View.GONE);
            mWheelView_1_unit.setVisibility(View.GONE);
        }

        if (mResId_2 != -1) {
            mArgs_2 = getResources().getStringArray(mResId_2);
        } else {
            mWheelView_2.setVisibility(View.GONE);
            mWheelView_2_unit.setVisibility(View.GONE);
        }

        if (mResId_3 != -1) {
            mArgs_3 = getResources().getStringArray(mResId_3);
        } else {
            mWheelView_3.setVisibility(View.GONE);
            mWheelView_3_unit.setVisibility(View.GONE);
        }

        mWheelAdapter_1.setItemTextResource(R.id.tv_wheel_item);
        mWheelView_1.setViewAdapter(mWheelAdapter_1);
        mWheelView_1.setCurrentItem(CURR_ITEM[0]);

        mWheelAdapter_2.setItemTextResource(R.id.tv_wheel_item);
        mWheelView_2.setViewAdapter(mWheelAdapter_2);
        mWheelView_2.setCurrentItem(CURR_ITEM[1]);

    }

    private AbstractWheelTextAdapter mWheelAdapter_1 = new AbstractWheelTextAdapter(mContext, R.layout.wheel_item) {
        @Override
        protected CharSequence getItemText(int index) {
            return mArgs_1[index];
        }

        @Override
        public int getItemsCount() {
            return mArgs_1.length;
        }
    };

    private AbstractWheelTextAdapter mWheelAdapter_2 = new AbstractWheelTextAdapter(mContext, R.layout.wheel_item) {
        @Override
        protected CharSequence getItemText(int index) {
            return mArgs_2[index];
        }

        @Override
        public int getItemsCount() {
            return mArgs_2.length;
        }
    };

    private void setWheelViewUnit(String unit_1, String unit_2, String unit_3) {
        mWheelView_1_unit.setText(unit_1);
        mWheelView_2_unit.setText(unit_2);
        mWheelView_3_unit.setText(unit_3);
    }

    private void addChangingListener() {
        mWheelView_1.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //mlog.debug("mWheelView_1 newValue = " + newValue);
                String arg_1 = mArgs_1[newValue];
                String arg_2 = mArgs_2[mWheelView_2.getCurrentItem()];

                if ("0".contentEquals(arg_1)) {
                    arg_1 = "";
                    arg_2 = "0".contentEquals(arg_2) ? "0" : arg_2 + mFixedSuffix;
                } else {
                    arg_2 = arg_2 + mFixedSuffix;
                }

                if (isTime) {
                    mValue = arg_1 + " - " + arg_2 + UNIT;
                } else {
                    mValue = arg_1 + arg_2 + UNIT;
                }

                if (mTextView != null) {
                    mTextView.setText(SETTING_TAG + mValue);
                }
            }
        });

        mWheelView_2.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //mlog.debug("mWheelView_2 newValue = " + newValue);
                String arg_1 = mArgs_1[mWheelView_1.getCurrentItem()];
                String arg_2 = mArgs_2[newValue];

                if ("0".contentEquals(arg_1)) {
                    arg_1 = "";
                    arg_2 = "0".contentEquals(arg_2) ? "0" : arg_2 + mFixedSuffix;
                } else {
                    arg_2 = arg_2 + mFixedSuffix;
                }

                if (isTime) {
                    mValue = arg_1 + " - " + arg_2 + UNIT;
                } else {
                    mValue = arg_1 + arg_2 + UNIT;
                }

                if (mTextView != null) {
                    mTextView.setText(SETTING_TAG + mValue);
                }
            }
        });
    }

    @Override
    public void setView(TextView textView) {
        String arg_1 = mArgs_1[mWheelView_1.getCurrentItem()];
        String arg_2 = mArgs_2[mWheelView_2.getCurrentItem()];

        if ("0".contentEquals(arg_1)) {
            arg_1 = "";
            arg_2 = "0".contentEquals(arg_2) ? "0" : arg_2 + mFixedSuffix;
        } else {
            arg_2 = arg_2 + mFixedSuffix;
        }

        if (isTime) {
            mValue = arg_1 + " - " + arg_2 + UNIT;
        } else {
            mValue = arg_1 + arg_2 + UNIT;
        }

        if (textView != null) {
            textView.setText(SETTING_TAG + mValue);
        }

        this.mTextView = textView;
    }

    @Override
    public int[] getCurrItemIndex() {
        int[] currItem = new int[2];
        currItem[0] = mWheelView_1.getCurrentItem();
        currItem[1] = mWheelView_2.getCurrentItem();
        return currItem;
    }

    @Override
    public void setCurrItemIndex(int[] currItemIndex) {
        this.CURR_ITEM = currItemIndex;
        mWheelView_1.setCurrentItem(CURR_ITEM[0]);
        mWheelView_2.setCurrentItem(CURR_ITEM[1]);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
