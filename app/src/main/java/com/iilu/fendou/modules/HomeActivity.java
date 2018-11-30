package com.iilu.fendou.modules;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.iilu.fendou.MainApplication;
import com.iilu.fendou.MainFragmentActivity;
import com.iilu.fendou.R;
import com.iilu.fendou.configs.LogConfig;
import com.iilu.fendou.configs.PrefsConfig;
import com.iilu.fendou.dbs.MsgAddFriendDB;
import com.iilu.fendou.interfaces.SlidingMenuListener;
import com.iilu.fendou.modules.fragment.MenuFragment;
import com.iilu.fendou.modules.fragment.MessageFragment;
import com.iilu.fendou.modules.fragment.MyselfFragment;
import com.iilu.fendou.modules.fragment.SportFragment;
import com.iilu.fendou.modules.message.entity.EasemobAddFriend;
import com.iilu.fendou.modules.myself.dialog.AppIntroduceDialog;
import com.iilu.fendou.utils.PermissionUtil;
import com.iilu.fendou.utils.SPrefUtil_2;
import com.iilu.fendou.utils.StatusBarUtil;
import com.iilu.fendou.views.MainViewPager_1;
import com.iilu.fendou.views.SlidingMenu;
import com.iilu.fendou.views.SlidingMenuRight;
import com.iilu.fendou.views.TabRadioButton;

import org.apache.log4j.Logger;

import java.util.List;

import de.greenrobot.event.EventBus;

public class HomeActivity extends MainFragmentActivity implements View.OnClickListener {

    private Logger mlog = Logger.getLogger(HomeActivity.class);

    private String mUserid = MainApplication.getCurrLoginUsername();

    private Activity mContext;
    private SlidingMenu mSlidingMenu;
    private SlidingMenuRight mSlidingMenuRight;
    private DrawerLayout mDrawerLayout;

    private Fragment mCurrFragment;
    private Fragment mSportFragment;
    private Fragment mMessageFragment;
    private Fragment mMyselfFragment;

    private RadioGroup mRadioGroup;
    private TabRadioButton mBtnRadioSport;
    private TabRadioButton mBtnRadioMessage;
    private TabRadioButton mBtnRadioMyself;

    private MsgAddFriendDB mMsgAddFriendDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        StatusBarUtil.compat(mContext, Color.TRANSPARENT);

        if (!PermissionUtil.checkPermission(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            PermissionUtil.requestPermission(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0x001);
        }

        initLayout();

        initViews();

        initDrawerLayout();

        setTabRadioButtonListener();

        setListener();

        EMClient.getInstance().contactManager().setContactListener(mEmContactListener);
        EMClient.getInstance().chatManager().addMessageListener(mEmMessageListener);

        EventBus.getDefault().register(this);
        mMsgAddFriendDB = new MsgAddFriendDB(this);

        boolean isNeedShowAppIntroduce = SPrefUtil_2.get(this, PrefsConfig.APP_CONST, mUserid + "_needshow_appintroduce", true);
        if (isNeedShowAppIntroduce) {
            new AppIntroduceDialog(this, R.style.NewDialogSytle).show();
            SPrefUtil_2.put(this, PrefsConfig.APP_CONST, mUserid + "_needshow_appintroduce", false);
        }
    }

    private void initLayout() {
        boolean isLeftSliding = SPrefUtil_2.get(this, PrefsConfig.APP_CONST, "isLeftSliding", true);
        boolean isDrawerLayout = SPrefUtil_2.get(this, PrefsConfig.APP_CONST, "isDrawerLayout", true);
        if (isLeftSliding) {
            if (isDrawerLayout) {
                setContentView(R.layout.activity_home_left_1);
            } else {
                setContentView(R.layout.activity_home_left_2);
            }
            mSlidingMenu = (SlidingMenu) findViewById(R.id.sliding_menu);
        } else {
            if (isDrawerLayout) {
                setContentView(R.layout.activity_home_right_1);
            } else {
                setContentView(R.layout.activity_home_right_2);
            }
            mSlidingMenuRight = (SlidingMenuRight) findViewById(R.id.sliding_menu_right);
        }
    }

    private void initViews() {
        mSportFragment = new SportFragment();
        mMessageFragment = new MessageFragment();
        mMyselfFragment = new MyselfFragment();

        mFragTransaction = mFragManager.beginTransaction();
        mFragTransaction.add(R.id.home_content, mSportFragment).commitAllowingStateLoss();
        mCurrFragment = mSportFragment;

        mRadioGroup = (RadioGroup) findViewById(R.id.hot_seat);
        mBtnRadioSport = (TabRadioButton) mRadioGroup.findViewById(R.id.btn_radio_sport);
        mBtnRadioMessage = (TabRadioButton) mRadioGroup.findViewById(R.id.btn_radio_message);
        mBtnRadioMyself = (TabRadioButton) mRadioGroup.findViewById(R.id.btn_radio_myself);

        mBtnRadioSport.setChecked(true);
    }

    private void initDrawerLayout() {
        boolean isDrawerLayout = SPrefUtil_2.get(this, PrefsConfig.APP_CONST, "isDrawerLayout", true);
        if (isDrawerLayout) {
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, null, R.string.open, R.string.close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }
            };
            drawerToggle.syncState();
            mDrawerLayout.addDrawerListener(drawerToggle);
        }
    }

    private void setTabRadioButtonListener() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_radio_sport:
                        switchFragment(mSportFragment);
                        break;
                    case R.id.btn_radio_message:
                        switchFragment(mMessageFragment);
                        break;
                    case R.id.btn_radio_myself:
                        switchFragment(mMyselfFragment);
                        break;
                }
            }
        });
    }

    private void setListener() {
        if (mSlidingMenu != null) {
            final MainViewPager_1 viewPager = ((SportFragment) mSportFragment).getViewPager();
            mSlidingMenu.setSlidingMenuListener(new SlidingMenuListener() {
                @Override
                public void open() {
                    if (viewPager != null) {
                        viewPager.setScanScroll(false);
                    }
                }

                @Override
                public void close() {
                    if (viewPager != null) {
                        viewPager.setScanScroll(true);
                    }
                }
            });
        }
        if (mSlidingMenuRight != null) {
            final MainViewPager_1 viewPager = ((SportFragment) mSportFragment).getViewPager();
            mSlidingMenuRight.setSlidingMenuListener(new SlidingMenuListener() {
                @Override
                public void open() {
                    if (viewPager != null) {
                        viewPager.setScanScroll(false);
                    }
                }

                @Override
                public void close() {
                    if (viewPager != null) {
                        viewPager.setScanScroll(true);
                    }
                }
            });
        }
    }

    private void switchFragment(Fragment toFragment) {
        mFragTransaction = mFragManager.beginTransaction();
        if (mCurrFragment != null && mCurrFragment != toFragment) {
            if (!toFragment.isAdded()) {
                mFragTransaction.hide(mCurrFragment).add(R.id.home_content, toFragment).commitAllowingStateLoss();
            } else {
                mFragTransaction.hide(mCurrFragment).show(toFragment).commitAllowingStateLoss();
            }
        }
        mCurrFragment = toFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x001) {
            try {
                LogConfig.configure();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (mCurrFragment != null && mCurrFragment instanceof MyselfFragment) {
                mMyselfFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCurrFragment instanceof MyselfFragment) {
            mCurrFragment.onActivityResult(requestCode, resultCode, data);
            new MenuFragment().setImgHead();
        } else if (mCurrFragment instanceof MessageFragment) {
            mCurrFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mSlidingMenuRight != null && mSlidingMenuRight.isOpen()) {
            mSlidingMenuRight.closeMenu();
            return true;
        } else if (mSlidingMenu != null && mSlidingMenu.isOpen()) {
            mSlidingMenu.closeMenu();
            return true;
        } else if (mDrawerLayout != null && (mDrawerLayout.isDrawerOpen(Gravity.LEFT) || mDrawerLayout.isDrawerOpen(Gravity.RIGHT))) {
            mDrawerLayout.closeDrawers();
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 注意
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().contactManager().removeContactListener(mEmContactListener);
        EMClient.getInstance().chatManager().removeMessageListener(mEmMessageListener);
        EventBus.getDefault().unregister(this);
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public SlidingMenu getSlidingMenu() {
        return mSlidingMenu;
    }

    public SlidingMenuRight getSlidingMenuRight() {
        return mSlidingMenuRight;
    }

    private EMContactListener mEmContactListener = new EMContactListener() {
        @Override
        public void onContactAdded(String s) {
            // 从联系人添加

        }

        @Override
        public void onContactDeleted(String s) {
            // 好友被删除

        }

        @Override
        public void onContactInvited(String s, String s1) {
            // 收到好友邀请
            mlog.info("onContactInvited() s = " + s + ", s1 = " + s1);
            if (mMessageFragment instanceof MessageFragment) {
                EasemobAddFriend easemobAddFriend = dealAddFriend(s, s1);
                mMsgAddFriendDB.saveFriend(mUserid, easemobAddFriend);
                ((MessageFragment) mMessageFragment).onContactInvited(easemobAddFriend);
            }
        }

        @Override
        public void onFriendRequestAccepted(String s) {
            // 好友请求被同意
            mlog.info("onFriendRequestAccepted() s = " + s);

        }

        @Override
        public void onFriendRequestDeclined(String s) {
            // 好友请求被拒绝
            mlog.info("onFriendRequestDeclined() s = " + s);

        }
    };

    private EMMessageListener mEmMessageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            for (EMMessage msg : messages) {
                mlog.info("msg = " + msg.toString());
                if (mMessageFragment instanceof MessageFragment) {
                    ((MessageFragment) mMessageFragment).onMessageReceived(msg);
                }
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    private EasemobAddFriend dealAddFriend(String friendName, String reason) {
        EasemobAddFriend easemobAddFriend = new EasemobAddFriend();
        easemobAddFriend.setUserid(mUserid);
        easemobAddFriend.setFriendName(friendName);
        easemobAddFriend.setReason(reason);
        easemobAddFriend.setAccepte("1");
        easemobAddFriend.setReadTag("1");
        easemobAddFriend.setReceiveTime(System.currentTimeMillis() + "");
        return easemobAddFriend;
    }

    public void onEventMainThread(String event) {
        if (event == null || event.length() == 0) return;
        if ("from_AddFriendActivity".equals(event)) {
            ((MessageFragment) mMessageFragment).onRefresh();
        }
    }
}
