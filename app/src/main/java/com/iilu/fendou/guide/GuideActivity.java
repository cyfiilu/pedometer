package com.iilu.fendou.guide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iilu.fendou.MainApplication;
import com.iilu.fendou.MainFragmentActivity;
import com.iilu.fendou.R;
import com.iilu.fendou.configs.ParseConfig;
import com.iilu.fendou.configs.PrefsConfig;
import com.iilu.fendou.dbs.UserInfoDB;
import com.iilu.fendou.guide.fragments.DayGoalNumFragment;
import com.iilu.fendou.guide.fragments.GuideBaseFragment;
import com.iilu.fendou.guide.fragments.HeightFragment;
import com.iilu.fendou.guide.fragments.MmGoalNumFragment;
import com.iilu.fendou.guide.fragments.MmTimeFragment;
import com.iilu.fendou.guide.fragments.StepWidthFragment;
import com.iilu.fendou.guide.fragments.WeightFragment;
import com.iilu.fendou.guide.fragments.ZzGoalNumFragment;
import com.iilu.fendou.guide.fragments.ZzTimeFragment;
import com.iilu.fendou.modules.HomeActivity;
import com.iilu.fendou.modules.entity.UserInfo;
import com.iilu.fendou.utils.ParseUtil;
import com.iilu.fendou.utils.SPrefUtil_2;
import com.iilu.fendou.views.MainViewPager;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GuideActivity extends MainFragmentActivity implements View.OnClickListener {

    private final Logger mlog = Logger.getLogger(GuideActivity.class.getSimpleName());

    private int mPosition;
    private String mCurrLoginUsername = MainApplication.getCurrLoginUsername();

    private TextView mTextView;
    private Button mLast;
    private Button mNext;

    private UserInfoDB mDBUserInfo;
    private MainViewPager mViewPager;
    private List<Fragment> mDatas = new ArrayList<>();
    private ConcurrentMap<String, int[]> mConcurrMap = new ConcurrentHashMap<>();
    private ConcurrentMap<String, String> mResultMap = new ConcurrentHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initDatas();
        initViews();

        mViewPager = (MainViewPager) findViewById(R.id.guide_view_pager);
        mViewPager.setAdapter(mAdapter);

        mViewPager.setOnPageChangeListener(mPageChangeListener);

    }

    private void initDatas() {
        HeightFragment heightFragment = new HeightFragment();
        WeightFragment weightFragment = new WeightFragment();
        StepWidthFragment stepWidthFragment = new StepWidthFragment();
        DayGoalNumFragment dayGoalNumFragment = new DayGoalNumFragment();
        ZzGoalNumFragment zzGoalNumFragment = new ZzGoalNumFragment();
        ZzTimeFragment zzTimeFragment = new ZzTimeFragment();
        MmGoalNumFragment mmGoalNumFragment = new MmGoalNumFragment();
        MmTimeFragment mmTimeFragment = new MmTimeFragment();

        mDatas.add(heightFragment);
        mDatas.add(weightFragment);
        mDatas.add(stepWidthFragment);
        mDatas.add(dayGoalNumFragment);
        mDatas.add(zzGoalNumFragment);
        mDatas.add(zzTimeFragment);
        mDatas.add(mmGoalNumFragment);
        mDatas.add(mmTimeFragment);

        mDBUserInfo = new UserInfoDB(this);
    }

    private void initViews() {
        mTextView = (TextView) findViewById(R.id.tv_value);
        mLast = (Button) findViewById(R.id.btn_last);
        mNext = (Button) findViewById(R.id.btn_next);

        mLast.setOnClickListener(this);
        mNext.setOnClickListener(this);

        mLast.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_last:
                // 1. ViewPager指向上一个Fragment
                mViewPager.setCurrentItem(mPosition - 1);

                // 2. 获取Fragment中已经设置的值
                GuideBaseFragment baseFragment_1 = getFragment();
                String clazzName_1 = baseFragment_1.getClass().getSimpleName();
                int[] currItemIndex = mConcurrMap.get(clazzName_1);
                //mlog.debug("clazzName_1 = " + clazzName_1 + ", currItemIndex = " + Arrays.toString(currItemIndex));
                baseFragment_1.setCurrItemIndex(currItemIndex);

                // 3. 非最后一个将下一个按钮设置回“下一个”
                // 第一个时，将mLast按钮隐藏
                if (mPosition < mDatas.size() - 1) {
                    mNext.setText(getResources().getString(R.string.next_1));
                }
                if (mPosition == 0) {
                    mLast.setVisibility(View.INVISIBLE);
                }

                // 4. 将TextView传到Fragment中去
                getFragment().setView(mTextView);
                break;
            case R.id.btn_next:
                // 1. 获取当前Fragment中的值
                String mResult = mTextView.getText().toString();
                GuideBaseFragment baseFragment_2 = getFragment();
                String clazzName_2 = baseFragment_2.getClass().getSimpleName();
                //mlog.info("mResult = " + mResult + ", clazzName_2 = " + clazzName_2 + ", currItem = " + Arrays.toString(baseFragment_2.getCurrItemIndex()));
                mConcurrMap.put(clazzName_2, baseFragment_2.getCurrItemIndex());
                mResultMap.put(clazzName_2, mResult);

                // 5. 设置完成
                if (getResources().getString(R.string.next_2).equals(mNext.getText().toString())) {
                    guideFinish();
                }

                // 2. ViewPager指向下一个Fragment
                mViewPager.setCurrentItem(mPosition + 1);

                // 3. 设置mLast按钮显示，最后一个将下一个按钮设置成“设置完成”
                mLast.setVisibility(View.VISIBLE);
                if (mPosition >= mDatas.size() - 1) {
                    mNext.setText(getResources().getString(R.string.next_2));
                }

                // 4. 将TextView传到Fragment中去
                getFragment().setView(mTextView);
                break;
        }
    }

    private GuideBaseFragment getFragment() {
        Fragment fragment = mAdapter.getItem(mPosition);
        GuideBaseFragment baseFragment = (GuideBaseFragment) fragment;
        return baseFragment;
    }

    private FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }
    };

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //mlog.debug("onPageScrolled() position = " + position);
            getFragment().setView(mTextView);
        }

        @Override
        public void onPageSelected(int position) {
            //mlog.debug("onPageSelected() position = " + position);
            mPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void guideFinish() {
        // 1. 解析数据
        UserInfo userInfo = new UserInfo();
        for (Map.Entry<String, String> entry : mResultMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            mlog.info("key = " + key + ", value = " + value);

            String result;
            if ("HeightFragment".equals(key)) {
                result = value.substring(value.indexOf("：") + 1, value.indexOf(" ")).trim();
                int height = (int) ParseUtil.parseValue(ParseConfig.INTEGER, result);
                userInfo.setHeight(height);
            } else if ("WeightFragment".equals(key)) {
                result = value.substring(value.indexOf("：") + 1, value.indexOf(" ")).trim();
                int weight = (int) ParseUtil.parseValue(ParseConfig.INTEGER, result);
                userInfo.setWeight(weight);
            } else if ("StepWidthFragment".equals(key)) {
                result = value.substring(value.indexOf("：") + 1, value.indexOf(" ")).trim();
                int stepWidth = (int) ParseUtil.parseValue(ParseConfig.INTEGER, result);
                userInfo.setStepWidth(stepWidth);
            } else if ("DayGoalNumFragment".equals(key)) {
                result = value.substring(value.indexOf("：") + 1, value.indexOf(" ")).trim();
                int dayGoalNum = (int) ParseUtil.parseValue(ParseConfig.INTEGER, result);
                userInfo.setDayGoalNum(dayGoalNum);
            } else if ("ZzGoalNumFragment".equals(key)) {
                result = value.substring(value.indexOf("：") + 1, value.indexOf(" ")).trim();
                int zzGoalNum = (int) ParseUtil.parseValue(ParseConfig.INTEGER, result);
                userInfo.setZzGoalNum(zzGoalNum);
            } else if ("MmGoalNumFragment".equals(key)) {
                result = value.substring(value.indexOf("：") + 1, value.indexOf(" ")).trim();
                int mmGoalNum = (int) ParseUtil.parseValue(ParseConfig.INTEGER, result);
                userInfo.setMmGoalNum(mmGoalNum);
            } else if ("ZzTimeFragment".equals(key)) {
                result = value.substring(value.indexOf("：") + 1, value.length()).trim(); // 06:00 - 09:00
                String[] timeArr = result.split(" - ");
                int zzStartTime = 0;
                int zzEndTime = 0;
                Object obj_1 = ParseUtil.parseValue(ParseConfig.INTEGER, timeArr[0].split(":")[0]);
                if (obj_1 != null) {
                    zzStartTime = (int) obj_1;
                }
                Object obj_2 = ParseUtil.parseValue(ParseConfig.INTEGER, timeArr[1].split(":")[0]);
                if (obj_2 != null) {
                    zzEndTime = (int) obj_2;
                }
                userInfo.setZzStartTime(zzStartTime);
                userInfo.setZzEndTime(zzEndTime);
            } else if ("MmTimeFragment".equals(key)) {
                result = value.substring(value.indexOf("：") + 1, value.length()).trim(); // 06:00 - 09:00
                String[] timeArr = result.split(" - ");
                int mmStartTime = 0;
                int mmEndTime = 0;
                Object obj_1 = ParseUtil.parseValue(ParseConfig.INTEGER, timeArr[0].split(":")[0]);
                if (obj_1 != null) {
                    mmStartTime = (int) obj_1;
                }
                Object obj_2 = ParseUtil.parseValue(ParseConfig.INTEGER, timeArr[1].split(":")[0]);
                if (obj_2 != null) {
                    mmEndTime = (int) obj_2;
                }
                userInfo.setMmStartTime(mmStartTime);
                userInfo.setMmEndTime(mmEndTime);
            }
        }
        mlog.debug("userInfo = " + userInfo.toString());

        userInfo.setSex("保密");
        userInfo.setCity("保密");

        // 2. 存入数据库
        mDBUserInfo.saveUserInfo(mCurrLoginUsername, userInfo);

        // 3. 将第一次登录，设置为false
        SPrefUtil_2.put(this, PrefsConfig.USER_LOGIN, mCurrLoginUsername + "_isFirstLogin", false);

        // 4. 跳转页面
        startActivity(new Intent(this, HomeActivity.class));

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatas.clear();
        mConcurrMap.clear();
        mResultMap.clear();
    }

}
