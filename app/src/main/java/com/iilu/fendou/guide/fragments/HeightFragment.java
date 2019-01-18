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

import org.apache.log4j.Logger;

public class HeightFragment extends GuideBaseFragment {

    private final Logger mlog = Logger.getLogger(HeightFragment.class.getSimpleName());

    private String SETTING_TAG = "身高：";
    private String UNIT = " cm";
    private String mValue;

    protected String[] mArgs_1 = { "0", "1", "2" };
    private String[] mArgs_2 = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
    private String[] mArgs_3 = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

    private int[] CURR_ITEM = new int[]{1, 7, 0};

    private final int COLOR_TOP = 0xefEDEDED;
    private final int COLOR_CENTER = 0xcfEDEDED;
    private final int COLOR_BOTTOM = 0x3fEDEDED;

    private WheelView mWheelView_1;
    private WheelView mWheelView_2;
    private WheelView mWheelView_3;

    private TextView mTextView;

    protected void init(String[] args_1, int[] curr_item, String setting_tag, String unit) {
        this.mArgs_1 = args_1;
        this.CURR_ITEM = curr_item;
        this.SETTING_TAG = setting_tag;
        this.UNIT = unit;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.SETTING_TAG = getResources().getString(R.string.height) + "：";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wheel_1, container, false);

        initViews(view);

        initDatas();

        addChangingListener();

        return view;
    }

    private void initViews(View view) {
        mWheelView_1 = (WheelView) view.findViewById(R.id.wheelView_1);
        mWheelView_2 = (WheelView) view.findViewById(R.id.wheelView_2);
        mWheelView_3 = (WheelView) view.findViewById(R.id.wheelView_3);

        mWheelView_1.setShadowColor(COLOR_TOP, COLOR_CENTER, COLOR_BOTTOM);
        mWheelView_2.setShadowColor(COLOR_TOP, COLOR_CENTER, COLOR_BOTTOM);
        mWheelView_3.setShadowColor(COLOR_TOP, COLOR_CENTER, COLOR_BOTTOM);

        mWheelView_1.setBackgroundResource(R.color.gray_2);
        mWheelView_2.setBackgroundResource(R.color.gray_2);
        mWheelView_3.setBackgroundResource(R.color.gray_2);
    }

    private void initDatas() {
        mWheelAdapter_1.setItemTextResource(R.id.tv_wheel_item);
        mWheelView_1.setViewAdapter(mWheelAdapter_1);
        mWheelView_1.setCurrentItem(CURR_ITEM[0]);

        mWheelAdapter_2.setItemTextResource(R.id.tv_wheel_item);
        mWheelView_2.setViewAdapter(mWheelAdapter_2);
        mWheelView_2.setCurrentItem(CURR_ITEM[1]);

        mWheelAdapter_3.setItemTextResource(R.id.tv_wheel_item);
        mWheelView_3.setViewAdapter(mWheelAdapter_3);
        mWheelView_3.setCurrentItem(CURR_ITEM[2]);
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

    private AbstractWheelTextAdapter mWheelAdapter_3 = new AbstractWheelTextAdapter(mContext, R.layout.wheel_item) {
        @Override
        protected CharSequence getItemText(int index) {
            return mArgs_3[index];
        }

        @Override
        public int getItemsCount() {
            return mArgs_3.length;
        }
    };

    private void addChangingListener() {
        mWheelView_1.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //mlog.debug("mWheelView_1 newValue = " + newValue);
                String arg_1 = mArgs_1[newValue];
                String arg_2 = mArgs_2[mWheelView_2.getCurrentItem()];
                String arg_3 = mArgs_3[mWheelView_3.getCurrentItem()];

                if ("0".contentEquals(arg_1)) {
                    arg_1 = "";
                    if ("0".contentEquals(arg_2)) {
                        arg_2 = "";
                    }
                }

                mValue = arg_1 + arg_2 + arg_3 + UNIT;

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
                String arg_3 = mArgs_3[mWheelView_3.getCurrentItem()];

                if ("0".contentEquals(arg_1)) {
                    arg_1 = "";
                    if ("0".contentEquals(arg_2)) {
                        arg_2 = "";
                    }
                }

                mValue = arg_1 + arg_2 + arg_3 + UNIT;

                if (mTextView != null) {
                    mTextView.setText(SETTING_TAG + mValue);
                }
            }
        });

        mWheelView_3.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                //mlog.debug("mWheelView_3 newValue = " + newValue);
                String arg_1 = mArgs_1[mWheelView_1.getCurrentItem()];
                String arg_2 = mArgs_2[mWheelView_2.getCurrentItem()];
                String arg_3 = mArgs_3[newValue];

                if ("0".contentEquals(arg_1)) {
                    arg_1 = "";
                    if ("0".contentEquals(arg_2)) {
                        arg_2 = "";
                    }
                }

                mValue = arg_1 + arg_2 + arg_3 + UNIT;

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
        String arg_3 = mArgs_3[mWheelView_3.getCurrentItem()];

        if ("0".contentEquals(arg_1)) {
            arg_1 = "";
            if ("0".contentEquals(arg_2)) {
                arg_2 = "";
            }
        }

        mValue = arg_1 + arg_2 + arg_3 + UNIT;

        if (textView != null) {
            textView.setText(SETTING_TAG + mValue);
        }

        this.mTextView = textView;
    }

    @Override
    public int[] getCurrItemIndex() {
        int[] currItem = new int[3];
        currItem[0] = mWheelView_1.getCurrentItem();
        currItem[1] = mWheelView_2.getCurrentItem();
        currItem[2] = mWheelView_3.getCurrentItem();
        return currItem;
    }

    @Override
    public void setCurrItemIndex(int[] currItemIndex) {
        this.CURR_ITEM = currItemIndex;
        mWheelView_1.setCurrentItem(CURR_ITEM[0]);
        mWheelView_2.setCurrentItem(CURR_ITEM[1]);
        mWheelView_3.setCurrentItem(CURR_ITEM[2]);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
