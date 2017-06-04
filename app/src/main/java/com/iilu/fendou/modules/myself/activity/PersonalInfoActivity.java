package com.iilu.fendou.modules.myself.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iilu.fendou.MainApplication;
import com.iilu.fendou.MainPreferenceActivity;
import com.iilu.fendou.R;
import com.iilu.fendou.dbs.UserInfoDB;
import com.iilu.fendou.dialogs.SimpleHUD;
import com.iilu.fendou.modules.myself.dialog.CityDialog;
import com.iilu.fendou.modules.myself.dialog.CodeCardDialog;
import com.iilu.fendou.modules.myself.dialog.PersonalInfoSettingDialog;
import com.iilu.fendou.modules.entity.UserInfo;
import com.iilu.fendou.preference.MainPreference;
import com.iilu.fendou.utils.SPrefUtil_2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalInfoActivity extends MainPreferenceActivity implements View.OnClickListener {

    private int mProvinceIndex = 0;
    private int mCityIndex = 0;
    private int mType;
    private String mCurrLoginUsername = MainApplication.getCurrLoginUsername();
    private String mCurrCity;

    private MainPreference mUserSex;
    private MainPreference mUserHeight;
    private MainPreference mUserWeight;
    private MainPreference mUserCity;
    private MainPreference mUserCodeCard;

    private UserInfoDB mUserInfoDB;
    private PersonalInfoSettingDialog mSettingDialog;
    private CityDialog mCityDialog;
    private String[] mCities;
    private String[] mProvince;
    private Map<String, String[]> mMapDatas = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.personal_info);
        setContentView(R.layout.activity_prefs_main);

        RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.pref_title);
        ((TextView) titleLayout.findViewById(R.id.tv_title)).setText("个人信息");
        ImageView imgBack = (ImageView) titleLayout.findViewById(R.id.iv_left);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mUserSex = (MainPreference) findPreference("user_sex");
        mUserHeight = (MainPreference) findPreference("user_height");
        mUserWeight = (MainPreference) findPreference("user_weight");
        mUserCity = (MainPreference) findPreference("uesr_city");
        mUserCodeCard = (MainPreference) findPreference("user_code_card");

        mUserInfoDB = new UserInfoDB(this);
        UserInfo userInfo = mUserInfoDB.queryUserInfo(mCurrLoginUsername);

        mUserSex.setTvRightText(userInfo.getSex());
        mUserHeight.setTvRightText(userInfo.getHeight() + " cm");
        mUserWeight.setTvRightText(userInfo.getWeight() + " kg");
        mUserCity.setTvRightText(userInfo.getCity());

        mCurrCity = userInfo.getCity();

        String json = loadCityFromAssert("cityjson.txt");
        initCity(json);
    }

    private String loadCityFromAssert(String fileName) {
        byte[] buffer;
        String result = "";
        try {
            InputStream is = MainApplication.getAppContext().getAssets().open(fileName);
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void initCity(String json) {
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray countries = jsonObj.optJSONArray("countries");
            JSONObject china = (JSONObject) countries.get(0); //获取到国家（中国）
            JSONArray provinces = china.getJSONArray("provinces"); // 获取到中国所有省份
            mProvince = new String[provinces.length()];
            for (int i = 0; i < provinces.length(); i++) {
                JSONObject provObject = (JSONObject) provinces.get(i);
                String provinceName = provObject.optString("pro_name");
                JSONArray cityArray = provObject.optJSONArray("cities");
                mProvince[i] = provinceName;
                mCities = new String[cityArray.length()];
                if (cityArray.length() == 1) { // 直辖市
                    mCities[0] = provinceName;
                    if (mCurrCity.equals(provinceName)) {
                        mProvinceIndex = i;
                        mCityIndex = 0;
                    }
                } else if (cityArray.length() > 1) {
                    for (int j = 0; j < cityArray.length(); j++) {
                        JSONObject cityObject = cityArray.getJSONObject(j);
                        String cityName = cityObject.optString("city_name");
                        mCities[j] = cityName;
                        if (mCurrCity.equals(cityName)) {
                            mProvinceIndex = i;
                            mCityIndex = j;
                        }
                    }
                }
                mMapDatas.put(provinceName, mCities);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (mUserSex == preference) {
            motifyParameter(R.array.user_sex, -1, -1, "性别", 1);
        } else if (mUserHeight == preference) {
            motifyParameter(R.array.zero_to_two, R.array.zero_to_nine, R.array.zero_to_nine, "身高", 2);
        } else if (mUserWeight == preference) {
            motifyParameter(R.array.zero_to_two, R.array.zero_to_nine, R.array.zero_to_nine, "体重", 3);
        } else if (mUserCity == preference) {
            mCityDialog = new CityDialog(this, this, mMapDatas, null, mProvince, mMapDatas.get(mProvince[mProvinceIndex]));
            mCityDialog.setDialogText("城市");
            setUnitAccordType(4);
            this.mType = 4;
        } else if (mUserCodeCard == preference) {
            CodeCardDialog codeCardDialog = new CodeCardDialog(this, R.style.NewDialogSytle);
            codeCardDialog.show();
            codeCardDialog.setImgView();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void motifyParameter(int parentResId, int childResId, int subChildResId, String dialogText, int type) {
        mSettingDialog = new PersonalInfoSettingDialog(this, this, parentResId, childResId, subChildResId);
        mSettingDialog.setDialogText(dialogText);
        setUnitAccordType(type);
        this.mType = type;
    }

    public void setUnitAccordType(int type) {
        switch (type) {
            case 1:// 性别
                mSettingDialog.setUnitForData("", "", "");
                showCurrData(type);
                break;
            case 2:// 身高
                mSettingDialog.setUnitForData("", "", "");
                showCurrData(type);
                break;
            case 3:// 体重
                mSettingDialog.setUnitForData("", "", "");
                showCurrData(type);
                break;
            case 4:// 城市
                mCityDialog.setUnitForData("", "", "");
                showCurrData(type);
                break;
        }
    }

    private void showCurrData(int type) {
        List<Integer> currIndex = new ArrayList<>();
        switch (type) {
            case 1:
                currIndex.clear();
                String sex = mUserSex.getTvRightText().toString().trim();
                if ("男".equals(sex)) {
                    currIndex.add(0);
                } else if ("女".equals(sex)) {
                    currIndex.add(1);
                } else {
                    currIndex.add(2);
                }
                mSettingDialog.show(currIndex.get(0), -1, -1);
                break;
            case 2:
                currIndex.clear();
                String heightStr = mUserHeight.getTvRightText().toString().trim();
                heightStr = heightStr.substring(0, heightStr.length() - 3).trim();
                char[] stepWidthChar = heightStr.toCharArray();
                if (stepWidthChar.length < 3) {
                    currIndex.add(0);
                    currIndex.add(Integer.parseInt(stepWidthChar[0] + ""));
                    currIndex.add(Integer.parseInt(stepWidthChar[1] + ""));
                } else {
                    currIndex.add(Integer.parseInt(stepWidthChar[0] + ""));
                    currIndex.add(Integer.parseInt(stepWidthChar[1] + ""));
                    currIndex.add(Integer.parseInt(stepWidthChar[2] + ""));
                }
                mSettingDialog.show(currIndex.get(0), currIndex.get(1), currIndex.get(2));
                break;
            case 3:
                currIndex.clear();
                String weightStr = mUserWeight.getTvRightText().toString().trim();
                weightStr = weightStr.substring(0, weightStr.length() - 3).trim();
                char[] tvWeightChar = weightStr.toCharArray();
                if (tvWeightChar.length < 3) {
                    currIndex.add(0);
                    currIndex.add(Integer.parseInt(tvWeightChar[0] + ""));
                    currIndex.add(Integer.parseInt(tvWeightChar[1] + ""));
                } else {
                    currIndex.add(Integer.parseInt(tvWeightChar[0] + ""));
                    currIndex.add(Integer.parseInt(tvWeightChar[1] + ""));
                    currIndex.add(Integer.parseInt(tvWeightChar[2] + ""));
                }
                mSettingDialog.show(currIndex.get(0), currIndex.get(1), currIndex.get(2));
                break;
            case 4:
                currIndex.clear();
                String userCity = mUserCity.getTvRightText().toString().trim();
                if ("保密".equals(userCity)) {
                    mCityDialog.show(-1, 0, 0);
                } else {
                    mCityDialog.show(-1, mProvinceIndex, mCityIndex);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                if (mSettingDialog != null){
                    mSettingDialog.dismiss();
                    mSettingDialog = null;
                }
                if (mCityDialog != null) {
                    mCityDialog.dismiss();
                    mCityDialog = null;
                }
                break;
            case R.id.tv_confirm:
                updateParameter();
                if (mSettingDialog != null){
                    mSettingDialog.dismiss();
                    mSettingDialog = null;
                }
                if (mCityDialog != null) {
                    mCityDialog.dismiss();
                    mCityDialog = null;
                }
                break;
        }
    }

    private void updateParameter() {
        UserInfo userInfo = new UserInfo();
        switch (mType) {
            case 1:
                String sex = (String) mSettingDialog.getValue(mType);
                mUserSex.setTvRightText(sex);
                userInfo.setSex(sex);
                mUserInfoDB.saveUserInfo(mCurrLoginUsername, userInfo);
                break;
            case 2:
                Integer height = (Integer) mSettingDialog.getValue(mType);
                if (height < 80 || height > 260) {
                    SimpleHUD.showInfoMessage(this, "设置范围100~260cm", 1000);
                    return;
                }
                mUserHeight.setTvRightText(height + " cm");
                userInfo.setHeight(height);
                mUserInfoDB.saveUserInfo(mCurrLoginUsername, userInfo);
                break;
            case 3:
                Integer weight = (Integer) mSettingDialog.getValue(mType);
                if (weight < 20 || weight > 120) {
                    SimpleHUD.showInfoMessage(this, "设置范围20~120Kg", 1000);
                    return;
                }
                mUserWeight.setTvRightText(weight + " kg");
                userInfo.setWeight(weight);
                mUserInfoDB.saveUserInfo(mCurrLoginUsername, userInfo);
                break;
            case 4:
                Integer[] cityArray = (Integer[]) mCityDialog.getValue(mType);
                Integer provinceIndex = cityArray[0];
                Integer cityIndex = cityArray[1];
                mProvinceIndex = provinceIndex;
                mCityIndex = cityIndex;
                String currCity = mMapDatas.get(mProvince[mProvinceIndex])[cityIndex];
                mUserCity.setTvRightText(currCity);
                userInfo.setCity(currCity);
                mUserInfoDB.saveUserInfo(mCurrLoginUsername, userInfo);
                break;
        }
    }
}
