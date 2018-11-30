package com.iilu.fendou.modules.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iilu.fendou.MainApplication;
import com.iilu.fendou.MainFragment;
import com.iilu.fendou.R;
import com.iilu.fendou.configs.ParseConfig;
import com.iilu.fendou.configs.PrefsConfig;
import com.iilu.fendou.dbs.UserInfoDB;
import com.iilu.fendou.dbs.UserStepDB;
import com.iilu.fendou.dialogs.SimpleHUD;
import com.iilu.fendou.modules.HomeActivity;
import com.iilu.fendou.modules.entity.UserInfo;
import com.iilu.fendou.modules.sport.StepService;
import com.iilu.fendou.modules.sport.adapter.SportAdapter;
import com.iilu.fendou.modules.sport.entity.DayData;
import com.iilu.fendou.modules.sport.listeners.OnStepListener;
import com.iilu.fendou.modules.sport.views.SportView;
import com.iilu.fendou.refreshs.pulltorefresh.PullToRefreshBase;
import com.iilu.fendou.refreshs.pulltorefresh.PullToRefreshScrollView;
import com.iilu.fendou.utils.DateUtil;
import com.iilu.fendou.utils.ParseUtil;
import com.iilu.fendou.utils.SPrefUtil_2;
import com.iilu.fendou.utils.ServiceUtil;
import com.iilu.fendou.utils.StatusBarUtil;
import com.iilu.fendou.views.MainViewPager_1;
import com.iilu.fendou.views.SlidingMenu;
import com.iilu.fendou.views.SlidingMenuRight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class SportFragment extends MainFragment implements View.OnClickListener {

    private final int MSG_PULL_TO_REFRESH = 0x00002;
    private final int MSG_ADD_DATAS = 0x00003;

    private String mCurrLoginUsername = MainApplication.getCurrLoginUsername();

    private Context mContext;

    // 下拉刷新相关
    private ScrollView mScrollView;
    private PullToRefreshScrollView mPullScrollView;

    private TextView mTvTitle;
    private ImageView mImgLeft;
    private ImageView mImgRight;
    private View mChildView;

    private SportView mSportView;
    private TextView mTvDescription;
    private ImageView mImgFlag;
    private TextView mTotalFinishDayCount;
    private TextView mTotalStepCount;

    private MainViewPager_1 mViewPager;
    private SportAdapter mSportAdapter;

    private UserStepDB mDBUserStep = new UserStepDB(MainApplication.getAppContext());

    private Map<String, Integer> mSportInfoMap = new ConcurrentHashMap<>();
    private List<LinearLayout> mDatas = new ArrayList<>();

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PULL_TO_REFRESH:
                    if (mPullScrollView != null) {
                        mPullScrollView.onPullDownRefreshComplete();
                    }
                    notifyAdapter();
                    break;
                case MSG_ADD_DATAS:
                    SimpleHUD.dismiss();
                    SimpleHUD.showSuccessMessage(mContext, "已添加成功！", 1000);
                    loadDatas();
                    notifyAdapter();
                    SPrefUtil_2.put(mContext, PrefsConfig.SPORT, mCurrLoginUsername + "_hasAdd", true);
                    break;
            }
            return false;
        }
    });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sport, container, false);

        StatusBarUtil.compat(getActivity(), Color.TRANSPARENT);

        // 1. 初始化下拉刷新控件
        initPulltoRefreshView(view);

        // 2. 初始化界面
        initViews(view);

        // 3. 下拉刷新获取数据时，默认界面，防止界面内容空
        mTvTitle.setText(mContext.getResources().getString(R.string.today));
        mSportView = createView(null, 0);

        // 4. 下拉刷新，获取数据
        mPullScrollView.doPullRefreshing(true, 0);

        // 通知界面显示数据
        notifyAdapter();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!ServiceUtil.isServiceRunning(mContext, StepService.class.getSimpleName())) {
            bindToService();
            mContext.startService(new Intent(mContext, StepService.class));
        }
    }

    private void initPulltoRefreshView(View view) {
        mPullScrollView = (PullToRefreshScrollView) view.findViewById(R.id.sport_pull_to_refresh);
        mPullScrollView.setHeaderTextColor("#ffffffff");
        mPullScrollView.setLoadingDrawable(getResources().getDrawable(R.drawable.anim_sport_pull_down, null));
        mPullScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                loadDatas();

                int randomInt = new Random().nextInt(11); // [0, 10]随机数
                long delayMillis = randomInt * 100;
                mHandler.sendEmptyMessageDelayed(MSG_PULL_TO_REFRESH, delayMillis);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {

            }
        });
        mScrollView = mPullScrollView.getRefreshableView();
        mScrollView.setVerticalScrollBarEnabled(false);
        mScrollView.setVerticalFadingEdgeEnabled(false);
        mScrollView.setHorizontalFadingEdgeEnabled(false);
        mChildView = LayoutInflater.from(mContext).inflate(R.layout.sport_main, null);
        mScrollView.addView(mChildView);
    }

    private void initViews(View view) {
        RelativeLayout layoutTitle = (RelativeLayout) view.findViewById(R.id.main_title);
        mTvTitle = (TextView) layoutTitle.findViewById(R.id.tv_title);
        mTvTitle.setText(getResources().getString(R.string.sport));

        mImgLeft = (ImageView) layoutTitle.findViewById(R.id.iv_left);
        mImgLeft.setVisibility(View.VISIBLE);
        mImgLeft.setImageResource(R.drawable.selector_menu);
        mImgLeft.setOnClickListener(this);

        mImgRight = (ImageView) layoutTitle.findViewById(R.id.iv_right);
        mImgRight.setVisibility(View.VISIBLE);
        mImgRight.setImageResource(R.mipmap.add_white_normal);
        mImgRight.setOnClickListener(this);

        LinearLayout layoutSportInfo = (LinearLayout) mChildView.findViewById(R.id.sport_info);
        mTotalFinishDayCount = (TextView) layoutSportInfo.findViewById(R.id.tv_total_finish_day_count);
        mTotalStepCount = (TextView) layoutSportInfo.findViewById(R.id.tv_total_step_count);

        mViewPager = (MainViewPager_1) mChildView.findViewById(R.id.sport_view_pager);
        mViewPager.setScanScroll(true);
        mSportAdapter = new SportAdapter();
    }

    private void loadDatas() {
        mSportInfoMap = mDBUserStep.querySportInfo(MainApplication.getCurrLoginUsername());
        mTotalFinishDayCount.setText(mSportInfoMap.get("totalFinishDayCount") + " 天");
        mTotalStepCount.setText(mSportInfoMap.get("totalDayStepCount") + " 步");

        List<DayData> list = mDBUserStep.queryDayData_30(MainApplication.getCurrLoginUsername(), DateUtil.getCurrDayDate());
        if (list.size() > 0) {
            mDatas.clear();
            for (int i = 0; i < list.size(); i++) {
                DayData dayData = list.get(i);
                SportView sportView = createView(dayData, i);
                if (i == 0) {
                    mTvTitle.setText(mContext.getResources().getString(R.string.today));
                    mSportView = sportView;
                }
            }
        }
    }

    private SportView createView(DayData dayData, int i) {
        // 1. 创建view
        LinearLayout layoutSport = (LinearLayout) View.inflate(mContext, R.layout.sport_view, null);
        SportView sportView = (SportView) layoutSport.findViewById(R.id.sport_view);
        TextView description = (TextView) layoutSport.findViewById(R.id.tv_description);
        ImageView imgFlag = (ImageView) layoutSport.findViewById(R.id.iv_flag);
        // 2. 将view添加到集合中
        mDatas.add(0, layoutSport);
        // 3. 更新每个view上的数据
        sportView.updateUI(dayData);
        // 4. 根据卡路里，将运动量转换为文字描述
        if (dayData != null) {
            description.setText(calculateDescription(dayData.getKcal()));
        }
        if (i == 0) {
            mTvDescription = description;
            mImgFlag = imgFlag;
        }
        return sportView;
    }

    private void notifyAdapter() {
        // 1. 设置数据源
        mSportAdapter.setDatas(mDatas);
        // 2. ViewPager重新绑定Adapter
        mViewPager.setAdapter(mSportAdapter);
        // 3. 将ViwePager指向最后一个
        mViewPager.setCurrentItem(mDatas.size() - 1);
        /// 4. 重新设置ViewPager监听
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    public MainViewPager_1 getViewPager() {
        return mViewPager;
    }

    /**
     * 根据卡路里，将运动量转换为文字描述
     * @param calorie
     * @return
     */
    private String calculateDescription(float calorie) {
        if (calorie < 50) {
            return "今天运动量有点少哦~";
        } else if (calorie >= 50 && calorie < 90) {
            return "相当于1个苹果";
        } else if (calorie >= 90 && calorie < 140) {
            return "相当于1碗米饭";
        } else if (calorie >= 140 && calorie < 190) {
            return "相当于1碗米饭和1个苹果";
        } else if (calorie >= 190 && calorie < 240) {
            return "相当于1个鸡腿";
        } else {
            int hNum = (int) (calorie / 200);
            int pRest = (int) (calorie - hNum * 200);
            int pNum = pRest / 50;
            if (pNum == 0) {
                return "相当于" + hNum + "个鸡腿";
            } else {
                return "相当于" + hNum + "个鸡腿和" + pNum + "个苹果";
            }
        }
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == mDatas.size() - 1) {  // 当前天
                mTvTitle.setText(mContext.getResources().getString(R.string.today));
            } else if (position < mDatas.size() - 1) { // 之前天
                LinearLayout layoutSport = mDatas.get(position);
                SportView sportView = (SportView) layoutSport.findViewById(R.id.sport_view);
                DayData dayData = sportView.getDayData();
                mTvTitle.setText(formatSportDate(dayData.getDayDate()));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void bindToService() {
        Intent service = new Intent(mContext, StepService.class);
        mContext.bindService(service, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepService stepService = ((StepService.StepBinder) service).getService();
            stepService.registerListener(mOnStepListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private OnStepListener mOnStepListener = new OnStepListener() {
        @Override
        public void onStepNum() {
            DayData currDayData = mDBUserStep.queryDayData(MainApplication.getCurrLoginUsername(), DateUtil.getCurrDayDate());
            if (currDayData != null) {
                String currDayDate = currDayData.getDayDate();
                if (mSportView != null) {
                    DayData oldDayData = mSportView.getDayData();
                    if (oldDayData != null) {
                        String oldDayDate = oldDayData.getDayDate();
                        if (currDayDate != null && currDayDate.equals(oldDayDate)) {
                            mSportView.updateUI(currDayData);
                            if (mTvDescription != null) {
                                mTvDescription.setText(calculateDescription(currDayData.getKcal()));
                            }
                        } else {
                            loadDatas();
                            notifyAdapter();
                        }
                    } else {
                        loadDatas();
                        notifyAdapter();
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                boolean isLeftSliding = SPrefUtil_2.get(mContext, PrefsConfig.APP_CONST, "isLeftSliding", true);
                if (isLeftSliding) {
                    DrawerLayout drawerLayout = ((HomeActivity) mContext).getDrawerLayout();
                    if (drawerLayout != null) {
                        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                            drawerLayout.closeDrawers();
                        } else {
                            drawerLayout.openDrawer(Gravity.LEFT);
                        }
                    }
                    SlidingMenu slidingMenu = ((HomeActivity) mContext).getSlidingMenu();
                    if (slidingMenu != null) {
                        if (slidingMenu.isOpen()) {
                            slidingMenu.closeMenu();
                        } else {
                            slidingMenu.openMenu();
                        }
                    }
                } else {
                    DrawerLayout drawerLayout = ((HomeActivity) mContext).getDrawerLayout();
                    if (drawerLayout != null) {
                        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                            drawerLayout.closeDrawers();
                        } else {
                            drawerLayout.openDrawer(Gravity.RIGHT);
                        }
                    }
                    SlidingMenuRight slidingMenuRight = ((HomeActivity) mContext).getSlidingMenuRight();
                    if (slidingMenuRight != null) {
                        if (slidingMenuRight.isOpen()) {
                            slidingMenuRight.closeMenu();
                        } else {
                            slidingMenuRight.openMenu();
                        }
                    }
                }
                break;
            case R.id.iv_right:
                if (SPrefUtil_2.get(mContext, PrefsConfig.SPORT, mCurrLoginUsername + "_hasAdd", false)) {
                    SimpleHUD.showInfoMessage(mContext, "已添加过数据，莫再添加啦！", 2000);
                    return;
                }
                SimpleHUD.showLoadingMessage(mContext, "正在添加数据...", false);
                // for test
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        readyDatas();
                        Message.obtain(mHandler, MSG_ADD_DATAS).sendToTarget();
                    }
                }).start();
                break;
        }
    }

    /**
     * just insert data for test
     */
    private void readyDatas() {
        UserStepDB dbUserStep = new UserStepDB(mContext);
        UserInfoDB dbUserInfo = new UserInfoDB(mContext);
        UserInfo userInfo = dbUserInfo.queryUserInfo(mCurrLoginUsername);

        String date = DateUtil.getCurrDayDate();
        for (int i = 0; i < 30; i++) {
            String currDate = DateUtil.offsetDate("yyyyMMdd", date, -i);

            for (int j = 0; j < 24; j++) {
                Random randomHour = new Random();
                int hour = randomHour.nextInt(24);

                Random randomHourStep = new Random();
                int hourStep = randomHourStep.nextInt(20);
                dbUserStep.saveStep(mCurrLoginUsername, currDate, hour, hourStep * 100, userInfo);
            }
        }
    }

    private String formatSportDate(String date) {
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        return ParseUtil.parseValue(ParseConfig.INTEGER, month) + "月"
                + ParseUtil.parseValue(ParseConfig.INTEGER, day) + "日";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatas.clear();
        mContext.unbindService(mConnection);
    }
}
