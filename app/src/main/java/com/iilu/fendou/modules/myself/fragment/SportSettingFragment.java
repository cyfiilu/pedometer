package com.iilu.fendou.modules.myself.fragment;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.iilu.fendou.MainApplication;
import com.iilu.fendou.MainPreferenceFragment;
import com.iilu.fendou.R;
import com.iilu.fendou.dbs.UserInfoDB;
import com.iilu.fendou.dialogs.SimpleHUD;
import com.iilu.fendou.modules.entity.UserInfo;
import com.iilu.fendou.modules.myself.dialog.SportSettingDialog;
import com.iilu.fendou.preference.MainPreference;
import com.iilu.fendou.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

public class SportSettingFragment extends MainPreferenceFragment implements View.OnClickListener {

    private int mType;
    private String mCurrLoginUsername = MainApplication.getCurrLoginUsername();

    private Activity mActivity;

    private MainPreference mDayGoalStepNum;
    private MainPreference mUserWeight;
    private MainPreference mUserStepWidth;
    private MainPreference mZhaozhaoTime;
    private MainPreference mZhaozhaoStepNum;
    private MainPreference mMumuTime;
    private MainPreference mMumuStepNum;

    private UserInfoDB mUserInfoDB;
    private SportSettingDialog mSportSettingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.sport_setting);

        mActivity = getActivity();

        mDayGoalStepNum = (MainPreference) findPreference("day_goal_stepnum");
        mUserWeight = (MainPreference) findPreference("user_weight");
        mUserStepWidth = (MainPreference) findPreference("user_stepwidth");
        mZhaozhaoTime = (MainPreference) findPreference("zhaozhao_time");
        mZhaozhaoStepNum = (MainPreference) findPreference("zhaozhao_goal_stepnum");
        mMumuTime = (MainPreference) findPreference("mumu_time");
        mMumuStepNum = (MainPreference) findPreference("mumu_goal_stepnum");

        mUserInfoDB = new UserInfoDB(mActivity);
        UserInfo userInfo = mUserInfoDB.queryUserInfo(mCurrLoginUsername);

        mDayGoalStepNum.setTvRightText(userInfo.getDayGoalNum() + " 步");
        mUserWeight.setTvRightText(userInfo.getWeight() + " kg");
        mUserStepWidth.setTvRightText(userInfo.getStepWidth() + " cm");
        mZhaozhaoTime.setTvRightText(formatTime(userInfo.getZzStartTime(), userInfo.getZzEndTime()));
        mZhaozhaoStepNum.setTvRightText(userInfo.getZzGoalNum() + " 步");
        mMumuTime.setTvRightText(formatTime(userInfo.getMmStartTime(), userInfo.getMmEndTime()));
        mMumuStepNum.setTvRightText(userInfo.getMmGoalNum() + " 步");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_myself_1, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        ListView list = (ListView) rootView.findViewById(android.R.id.list);
        list.setDivider(new ColorDrawable(getResources().getColor(R.color.gray_D5)));
        list.setDividerHeight(SystemUtil.dip2px(mActivity, 0.5f));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            list.setSelector(R.drawable.selector_ripple_white);
        }
        list.setVerticalScrollBarEnabled(false);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (mDayGoalStepNum == preference) {
            motifyParameter(R.array.zero_to_three, R.array.zero_to_nine, -1,
                    getResources().getString(R.string.day_goal), 1);
        } else if (mUserWeight == preference) {
            motifyParameter(R.array.zero_to_two, R.array.zero_to_nine, R.array.zero_to_nine,
                    getResources().getString(R.string.weight), 2);
        } else if (mUserStepWidth == preference) {
            motifyParameter(R.array.zero_to_one, R.array.zero_to_nine, R.array.zero_to_nine,
                    getResources().getString(R.string.step_width), 3);
        } else if (mZhaozhaoTime == preference) {
            motifyParameter(R.array.zhaozhao_time_range, R.array.zhaozhao_time_range, -1,
                    getResources().getString(R.string.sport_time_1), 4);
        } else if (mZhaozhaoStepNum == preference) {
            motifyParameter(R.array.three_to_eight, R.array.zero_to_nine, -1,
                    getResources().getString(R.string.sport_goal_1), 5);
        } else if (mMumuTime == preference) {
            motifyParameter(R.array.mumu_time_range, R.array.mumu_time_range, -1,
                    getResources().getString(R.string.sport_time_2), 6);
        } else if (mMumuStepNum == preference) {
            motifyParameter(R.array.three_to_eight, R.array.zero_to_nine, -1,
                    getResources().getString(R.string.sport_goal_2), 7);
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private String formatTime(int startTime, int endTime) {
        String resultStartTime = startTime < 10 ? "0" + startTime : startTime + "";
        String resultEndTime = endTime < 10 ? "0" + endTime : endTime + "";
        return resultStartTime + ":00-" + resultEndTime + ":00";
    }

    private void motifyParameter(int parentResId, int childResId, int subChildResId, String dialogText, int type) {
        mSportSettingDialog = new SportSettingDialog(mActivity, this, parentResId, childResId, subChildResId);
        mSportSettingDialog.setDialogText(dialogText);
        setUnitAccordType(type);
        this.mType = type;
    }

    public void setUnitAccordType(int type) {
        switch (type) {
            case 1:// 每日目标步数
                mSportSettingDialog.setUnitForData("万", "千", "");
                showCurrData(type);
                break;
            case 2:// 体重
                mSportSettingDialog.setUnitForData("", "", "");
                showCurrData(type);
                break;
            case 3:// 步幅
                mSportSettingDialog.setUnitForData("", "", "");
                showCurrData(type);
                break;
            case 4:// 朝朝时间
                mSportSettingDialog.setUnitForData("——", "", "");
                showCurrData(type);
                break;
            case 5:// 朝朝步数
                mSportSettingDialog.setUnitForData("千", "百", "");
                showCurrData(type);
                break;
            case 6:// 暮暮时间
                mSportSettingDialog.setUnitForData("——", "", "");
                showCurrData(type);
                break;
            case 7:// 暮暮步数
                mSportSettingDialog.setUnitForData("千", "百", "");
                showCurrData(type);
                break;
        }
    }

    private void showCurrData(int type) {
        List<Integer> currIndex = new ArrayList<>();
        switch (type) {
            case 1:// 每日目标步数
                currIndex.clear();
                String stepAimDayStr = mDayGoalStepNum.getTvRightText().toString().trim();
                stepAimDayStr = stepAimDayStr.substring(0, stepAimDayStr.length() - 1).trim();
                char[] stepAimDayChar = stepAimDayStr.toCharArray();
                if (stepAimDayChar.length < 5) {
                    currIndex.add(0);
                    currIndex.add(Integer.parseInt(stepAimDayChar[0] + ""));
                } else {
                    currIndex.add(Integer.parseInt(stepAimDayChar[0] + ""));
                    currIndex.add(Integer.parseInt(stepAimDayChar[1] + ""));
                }
                mSportSettingDialog.show(currIndex.get(0), currIndex.get(1), -1);
                break;
            case 2:// 体重
                currIndex.clear();
                String tvWeightStr = mUserWeight.getTvRightText().toString().trim();
                tvWeightStr = tvWeightStr.substring(0, tvWeightStr.length() - 3).trim();
                char[] tvWeightChar = tvWeightStr.toCharArray();
                if (tvWeightChar.length < 3) {
                    currIndex.add(0);
                    currIndex.add(Integer.parseInt(tvWeightChar[0] + ""));
                    currIndex.add(Integer.parseInt(tvWeightChar[1] + ""));
                } else {
                    currIndex.add(Integer.parseInt(tvWeightChar[0] + ""));
                    currIndex.add(Integer.parseInt(tvWeightChar[1] + ""));
                    currIndex.add(Integer.parseInt(tvWeightChar[2] + ""));
                }
                mSportSettingDialog.show(currIndex.get(0), currIndex.get(1), currIndex.get(2));
                break;
            case 3:// 步幅
                currIndex.clear();
                String tvStepWidthStr = mUserStepWidth.getTvRightText().toString().trim();
                tvStepWidthStr = tvStepWidthStr.substring(0, tvStepWidthStr.length() - 3).trim();
                char[] tvStepWidthChar = tvStepWidthStr.toCharArray();
                if (tvStepWidthChar.length < 3) {
                    currIndex.add(0);
                    currIndex.add(Integer.parseInt(tvStepWidthChar[0] + ""));
                    currIndex.add(Integer.parseInt(tvStepWidthChar[1] + ""));
                } else {
                    currIndex.add(Integer.parseInt(tvStepWidthChar[0] + ""));
                    currIndex.add(Integer.parseInt(tvStepWidthChar[1] + ""));
                    currIndex.add(Integer.parseInt(tvStepWidthChar[2] + ""));
                }
                mSportSettingDialog.show(currIndex.get(0), currIndex.get(1), currIndex.get(2));
                break;
            case 4:// 朝朝时间
                currIndex.clear();
                String tvMorningTimeStr = mZhaozhaoTime.getTvRightText().toString().trim();
                int tvMorningStartTimeInt = Integer.parseInt(tvMorningTimeStr.substring(0, 2).trim());
                int tvMorningEndTimeInt = Integer.parseInt(tvMorningTimeStr.substring(6, 8).trim());
                currIndex.add(tvMorningStartTimeInt - 5);
                currIndex.add(tvMorningEndTimeInt - 5);
                mSportSettingDialog.show(currIndex.get(0), currIndex.get(1), -1);
                break;
            case 5:// 朝朝步数
                String tvMorningAimStr = mZhaozhaoStepNum.getTvRightText().toString().trim();
                int tvMorningAimInt = Integer.parseInt(tvMorningAimStr.substring(0, tvMorningAimStr.length() - 1).trim());
                int hundrValueMo = (tvMorningAimInt % 1000) / 100;
                int kiloValueMo = (tvMorningAimInt - (tvMorningAimInt % 1000)) / 1000;
                kiloValueMo = kiloValueMo - 3;
                mSportSettingDialog.show(kiloValueMo, hundrValueMo, -1);
                break;
            case 6:// 暮暮时间
                currIndex.clear();
                String tvEveningTimeStr = mMumuTime.getTvRightText().toString().trim();
                int tvEveningStartTimeInt = Integer.parseInt(tvEveningTimeStr.substring(0, 2).trim());
                int tvEveningEndTimeInt = Integer.parseInt(tvEveningTimeStr.substring(6, 8).trim());
                currIndex.add(tvEveningStartTimeInt - 17);
                currIndex.add(tvEveningEndTimeInt - 17);
                mSportSettingDialog.show(currIndex.get(0), currIndex.get(1), -1);
                break;
            case 7:// 暮暮步数
                String tvEveningAimStr = mMumuStepNum.getTvRightText().toString().trim();
                int tvEveningAimInt = Integer.parseInt(tvEveningAimStr.substring(0, tvEveningAimStr.length() - 1).trim());
                int hundrValueEv = (tvEveningAimInt % 1000) / 100;
                int kiloValueEv = (tvEveningAimInt - (tvEveningAimInt % 1000)) / 1000;
                kiloValueEv = kiloValueEv - 3;
                mSportSettingDialog.show(kiloValueEv, hundrValueEv, -1);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                mSportSettingDialog.dismiss();
                mSportSettingDialog = null;
                break;
            case R.id.tv_confirm:
                updateParameter();
                mSportSettingDialog.dismiss();
                mSportSettingDialog = null;
                break;
        }
    }

    private void updateParameter() {
        UserInfo userInfo = new UserInfo();
        switch (mType) {
            case 1:// 每日目标步数
                Integer stepAimDay = (Integer) mSportSettingDialog.getValue(mType);
                if (stepAimDay < 3000 || stepAimDay > 30000) {
                    SimpleHUD.showInfoMessage(mActivity, "设置范围3000~30000", 1000);
                    return;
                }
                mDayGoalStepNum.setTvRightText(stepAimDay + " 步");
                userInfo.setDayGoalNum(stepAimDay);
                mUserInfoDB.saveUserInfo(mCurrLoginUsername, userInfo);
                break;
            case 2:// 体重
                Integer weight = (Integer) mSportSettingDialog.getValue(mType);
                if (weight < 20 || weight > 120) {
                    SimpleHUD.showInfoMessage(mActivity, "设置范围20~120Kg", 1000);
                    return;
                }
                mUserWeight.setTvRightText(weight + " kg");
                userInfo.setWeight(weight);
                mUserInfoDB.saveUserInfo(mCurrLoginUsername, userInfo);
                break;
            case 3:// 步幅
                Integer stepWidth = (Integer) mSportSettingDialog.getValue(mType);
                if (stepWidth < 20 || stepWidth > 120) {
                    SimpleHUD.showInfoMessage(mActivity, "设置范围20~120cm", 1000);
                    return;
                }
                mUserStepWidth.setTvRightText(stepWidth + " cm");
                userInfo.setStepWidth(stepWidth);
                mUserInfoDB.saveUserInfo(mCurrLoginUsername, userInfo);
                break;

            case 4:// 朝三时间
                Integer[] morningTime = (Integer[]) mSportSettingDialog.getValue(mType);
                if (morningTime[0] >= morningTime[1]) {
                    SimpleHUD.showInfoMessage(mActivity, "开始时间需早于结束时间~", 1000);
                    return;
                }
                mZhaozhaoTime.setTvRightText(formatTime(morningTime[0], morningTime[1]));
                userInfo.setZzStartTime(morningTime[0]);
                userInfo.setZzEndTime(morningTime[1]);
                mUserInfoDB.saveUserInfo(mCurrLoginUsername, userInfo);
                break;
            case 5:// 朝三目标
                Integer morningAim = (Integer) mSportSettingDialog.getValue(mType);
                if (morningAim < 3000 || morningAim > 8000) {
                    SimpleHUD.showInfoMessage(mActivity, "设置范围3000~8000", 1000);
                    return;
                }
                mZhaozhaoStepNum.setTvRightText(morningAim + " 步");
                userInfo.setZzGoalNum(morningAim);
                mUserInfoDB.saveUserInfo(mCurrLoginUsername, userInfo);
                break;
            case 6:// 暮四时间
                Integer[] eveningTime = (Integer[]) mSportSettingDialog.getValue(mType);
                if (eveningTime[0] >= eveningTime[1]) {
                    SimpleHUD.showInfoMessage(mActivity, "开始时间需早于结束时间~", 1000);
                    return;
                }
                mMumuTime.setTvRightText(formatTime(eveningTime[0], eveningTime[1]));
                userInfo.setMmStartTime(eveningTime[0]);
                userInfo.setMmEndTime(eveningTime[1]);
                mUserInfoDB.saveUserInfo(mCurrLoginUsername, userInfo);
                break;
            case 7:// 暮四目标
                Integer eveningAim = (Integer) mSportSettingDialog.getValue(mType);
                if (eveningAim < 3000 || eveningAim > 8000) {
                    SimpleHUD.showInfoMessage(mActivity, "设置范围3000~8000", 1000);
                    return;
                }
                mMumuStepNum.setTvRightText(eveningAim + " 步");
                userInfo.setMmGoalNum(eveningAim);
                mUserInfoDB.saveUserInfo(mCurrLoginUsername, userInfo);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
