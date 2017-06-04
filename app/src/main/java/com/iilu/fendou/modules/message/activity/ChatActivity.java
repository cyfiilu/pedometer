package com.iilu.fendou.modules.message.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.iilu.fendou.MainFragmentActivity;
import com.iilu.fendou.R;
import com.iilu.fendou.configs.PrefsConfig;
import com.iilu.fendou.modules.message.adapter.MessageAdapter;
import com.iilu.fendou.modules.message.fragment.FaceMainFragment;
import com.iilu.fendou.utils.SPrefUtil_2;
import com.iilu.fendou.utils.SystemUtil;
import com.iilu.fendou.views.FaceKeyboard;

import java.util.List;

import de.greenrobot.event.EventBus;

public class ChatActivity extends MainFragmentActivity implements View.OnClickListener {

    private String mMsgFrom;
    private String mFromActivity;

    private RelativeLayout mRecordVoiceLayout;
    private TextView mRecordVoiceDuration;
    private TextView mRecordVoiceTip;
    private TextView mTvRight;
    private ListView mListView;
    private FaceMainFragment mFaceMainFragment;
    private MessageAdapter mMessageAdapter;
    private EMConversation mEMConversation;
    private InputMethodManager mInputMethodManager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getIntentData();

        initViews();

        initFaceFragment();

        initDatas();

    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            mMsgFrom = intent.getStringExtra("msgFrom");
            mFromActivity = intent.getStringExtra("fromActivity");
        }
    }

    private void initViews() {
        RelativeLayout titleLayout = (RelativeLayout) findViewById(R.id.msg_chat_title);
        ImageView imgBack = (ImageView) titleLayout.findViewById(R.id.iv_left);
        mTvRight = (TextView) titleLayout.findViewById(R.id.tv_right);
        ((TextView) titleLayout.findViewById(R.id.tv_title)).setText(mMsgFrom);
        imgBack.setVisibility(View.VISIBLE);
        mTvRight.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(this);
        mTvRight.setOnClickListener(this);
        mTvRight.setTextColor(Color.WHITE);
        boolean isSpeaker = SPrefUtil_2.get(this, PrefsConfig.MSG_CHAT, "is_speaker", true);
        if (isSpeaker) {
            mTvRight.setText("听筒");
        } else {
            mTvRight.setText("扬声器");
        }

        mRecordVoiceLayout = (RelativeLayout) findViewById(R.id.record_voice_layout);
        mRecordVoiceDuration = (TextView) findViewById(R.id.tv_record_voice_duration);
        mRecordVoiceTip = (TextView) findViewById(R.id.tv_record_voice_tip);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SystemUtil.hideInputMethod(ChatActivity.this);
                mFaceMainFragment.getFaceKeyboard().hideEmotionLayout();
                mFaceMainFragment.getFaceKeyboard().hideMoreLayout();
                mFaceMainFragment.resetUI();
                return false;
            }
        });

        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        EMClient.getInstance().chatManager().addMessageListener(mEmMessageListener);
        EventBus.getDefault().register(this);
    }

    private void initFaceFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("mMsgFrom", mMsgFrom);
        mFaceMainFragment = FaceMainFragment.newInstance(FaceMainFragment.class, bundle);
        mFaceMainFragment.bindToContentView(mListView);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.face_main_layout, mFaceMainFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        mFaceMainFragment.setViews(mRecordVoiceLayout, mRecordVoiceDuration, mRecordVoiceTip);
    }

    private void initDatas() {
        mEMConversation = EMClient.getInstance().chatManager().getConversation(mMsgFrom);
        if (mEMConversation != null) {
            mMessageAdapter = new MessageAdapter(this, mEMConversation);
            mListView.setAdapter(mMessageAdapter);
            mListView.setSelection(mListView.getCount() - 1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                backPressed();
                break;
            case R.id.tv_right:
                boolean isSpeaker = SPrefUtil_2.get(this, PrefsConfig.MSG_CHAT, "is_speaker", true);
                if (isSpeaker) {
                    mTvRight.setText("扬声器");
                    SPrefUtil_2.put(this, PrefsConfig.MSG_CHAT, "is_speaker", false);
                } else {
                    mTvRight.setText("听筒");
                    SPrefUtil_2.put(this, PrefsConfig.MSG_CHAT, "is_speaker", true);
                }
                break;
        }
    }

    public void refreshUI() {
        if (mMessageAdapter == null) {
            mEMConversation = EMClient.getInstance().chatManager().getConversation(mMsgFrom);
            mMessageAdapter = new MessageAdapter(this, mEMConversation);
            mListView.setAdapter(mMessageAdapter);
            mListView.setSelection(mEMConversation.getAllMessages().size() - 1);
        } else {
            mMessageAdapter.refreshUI();
            mListView.setSelection(mListView.getCount() - 1);
        }
        mEMConversation.markAllMessagesAsRead();
    }

    private EMMessageListener mEmMessageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshUI();
                }
            });
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

    @Override
    public void onBackPressed() {
        // 判断是否拦截返回键操作
        if (mFaceMainFragment.isInterceptBackPress()) {
            FaceKeyboard faceKeyboard = mFaceMainFragment.getFaceKeyboard();
            faceKeyboard.hideEmotionLayout();
            faceKeyboard.hideMoreLayout();
            faceKeyboard.hideSoftInput();
        } else {
            if ("AddFriendActivity".equals(mFromActivity)) {
                EventBus.getDefault().post("from_AddFriendActivity");
            }
            backPressed();
        }
    }

    private void backPressed() {
        setResult(RESULT_OK, null);
        finish();
    }

    public void onEventMainThread(String event) {
        if (TextUtils.isEmpty(event)) return;
        if ("face_change_type_changed".equals(event)) {
            boolean isCanScroll = SPrefUtil_2.get(this, PrefsConfig.MSG_CHAT, "isCanScroll", true);
            mFaceMainFragment.setViewPagerCanScroll(isCanScroll);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(mEmMessageListener);
        EventBus.getDefault().unregister(this);
    }

}
