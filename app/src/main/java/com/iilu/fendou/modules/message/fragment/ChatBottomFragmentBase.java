package com.iilu.fendou.modules.message.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.VoiceRecorder;
import com.iilu.fendou.MainFragment;
import com.iilu.fendou.R;
import com.iilu.fendou.configs.PrefsConfig;
import com.iilu.fendou.modules.message.activity.ChatActivity;
import com.iilu.fendou.modules.message.adapter.HorizontalRecyclerViewAdapter;
import com.iilu.fendou.modules.message.adapter.ViewPagerAdapter;
import com.iilu.fendou.utils.FileUtil;
import com.iilu.fendou.utils.SPrefUtil_2;
import com.iilu.fendou.utils.SystemUtil;
import com.iilu.fendou.utils.ToastUtil;
import com.iilu.fendou.views.FaceKeyboard;
import com.iilu.fendou.views.MainViewPager;
import com.iilu.fendou.views.PointIndicatorView;

import org.apache.log4j.Logger;

public class ChatBottomFragmentBase extends MainFragment implements View.OnClickListener {

    private final Logger mlog = Logger.getLogger(ChatBottomFragmentBase.class.getSimpleName());

    /**
     * 发送消息给哪个好友
     */
    private String mMsgFrom;
    /**
     * 上下文
     */
    protected ChatActivity mChatActivity;
    /**
     * 语音按钮
     */
    protected ImageView mImgVoice;
    /**
     * 表情面板
     */
    protected ImageView mImgFace;
    /**
     * 更多面板
     */
    protected ImageView mImgMore;
    /**
     * 发送消息按钮
     */
    protected Button mSend;
    /**
     * 输入框
     */
    protected EditText mInput;
    /**
     * 按住说话按钮
     */
    protected Button mPressToSay;
    /**
     * 需要绑定的内容view
     */
    protected View mContentView;
    /**
     * 表情面板
     */
    protected FaceKeyboard mFaceKeyboard;
    /**
     * 表情面板最下边，表情类型所需的RecyclerView
     */
    protected RecyclerView mRecyclerView;
    /**
     * 表情面板最下边，表情类型所需Adapter
     */
    protected HorizontalRecyclerViewAdapter mRecyclerViewAdapter;
    /**
     * 表情类型切换ViewPager
     */
    protected MainViewPager mFaceViewPager;
    /**
     * 表情面板ViewPager所需ViewPagerAdapter
     */
    protected ViewPagerAdapter mFaceViewPagerAdapter;
    /**
     * 更多面板切换ViewPager
     */
    protected MainViewPager mMoreViewPager;
    /**
     * 更多面板ViewPager所需ViewPagerAdapter
     */
    protected ViewPagerAdapter mMoreViewPagerAdapter;
    /**
     * 更多面板对应的点列表
     */
    protected PointIndicatorView mIndicatorView;

    private VoiceRecorder mVoiceRecorder;
    private PowerManager.WakeLock mWakeLock;
    private RelativeLayout mRecordVoiceLayout;
    private TextView mRecordVoiceDuration;
    private TextView mRecordVoiceTip;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChatActivity = (ChatActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.msg_face_main, container, false);

        mMsgFrom = mArgs.getString("mMsgFrom");

        initViews(view);

        mFaceKeyboard = FaceKeyboard.with(mChatActivity)
                .setEmotionView(view.findViewById(R.id.face_layout)) // 绑定表情面板
                .setViews(mImgFace, mPressToSay)
                .bindToContent(mContentView) // 绑定内容view
                .bindToVoiceImageView(mImgVoice)
                .bindToEditText(mInput) // 判断绑定那种EditView
                .bindToFaceImageView(mImgFace) // 绑定表情按钮
                .setMoreView(view.findViewById(R.id.msg_chat_more_layout)) // 绑定更多面板
                .bindToMoreImageView(mImgMore) // 绑定更多按钮
                .build();

        return view;
    }

    private void initViews(View view) {
        PowerManager powerManager = (PowerManager) mChatActivity.getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, ChatBottomFragmentBase.class.getSimpleName());
        mVoiceRecorder = new VoiceRecorder(mHandler);

        mFaceViewPager = (MainViewPager) view.findViewById(R.id.face_view_pager); // 表情面板用
        mRecyclerView = (RecyclerView) view.findViewById(R.id.face_recycler_view); // 表情面板用
        mMoreViewPager = (MainViewPager) view.findViewById(R.id.more_view_pager); // 更多面板用
        mIndicatorView = (PointIndicatorView) view.findViewById(R.id.point_indicator_view); // 更多面板用
        mImgVoice = (ImageView) view.findViewById(R.id.iv_voice);
        mImgFace = (ImageView) view.findViewById(R.id.iv_face);
        mImgMore = (ImageView) view.findViewById(R.id.iv_more);
        mSend = (Button) view.findViewById(R.id.btn_send);
        mInput = (EditText) view.findViewById(R.id.et_input);
        mPressToSay = (Button) view.findViewById(R.id.btn_press_to_say);

        mImgVoice.setOnClickListener(this);
        mSend.setOnClickListener(this);
        mPressToSay.setOnTouchListener(new VoiceRecorderListener());

        mInput.setBackground(getResources().getDrawable(R.mipmap.edittext_bg_normal));
        mInput.clearFocus();

        boolean isCanScroll = SPrefUtil_2.get(mChatActivity, PrefsConfig.MSG_CHAT, "isCanScroll", true);
        mFaceViewPager.setScanScroll(isCanScroll);
        mMoreViewPager.setScanScroll(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                String text = mInput.getText().toString();
                //mlog.info("text = " + text);
                EMMessage emMessage = EMMessage.createTxtSendMessage(text, mMsgFrom);
                EMClient.getInstance().chatManager().sendMessage(emMessage);
                mChatActivity.refreshUI();
                mInput.setText("");
                break;
        }
    }

    public void setViewPagerCanScroll(boolean isCanScroll) {
        mFaceViewPager.setScanScroll(isCanScroll);
    }

    /**
     * 是否拦截返回键操作，如果此时表情布局未隐藏，先隐藏表情布局
     *
     * @return true 则隐藏表情布局，拦截返回键操作；false 则不拦截返回键操作
     */
    public boolean isInterceptBackPress() {
        return mFaceKeyboard.interceptBackPress();
    }

    public void bindToContentView(View contentView) {
        this.mContentView = contentView;
    }

    public void setViews(RelativeLayout recordVoiceLayout, TextView recordVoiceDuration, TextView recordVoiceTip) {
        this.mRecordVoiceLayout = recordVoiceLayout;
        this.mRecordVoiceDuration = recordVoiceDuration;
        this.mRecordVoiceTip = recordVoiceTip;
    }

    public FaceKeyboard getFaceKeyboard() {
        return mFaceKeyboard;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class VoiceRecorderListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!FileUtil.isSdcardMounted()) {
                        ToastUtil.showCenter(mChatActivity, getString(R.string.sdcard_not_mounted));
                        return false;
                    }
                    ((Button) v).setText(getString(R.string.release_to_end));
                    v.setPressed(true);

                    mRecordVoiceLayout.setVisibility(View.VISIBLE);
                    SystemUtil.vibrate(mChatActivity, 50);
                    mDownTimer.start();

                    mWakeLock.acquire();

                    try {
                        mVoiceRecorder.startRecording(null, mMsgFrom, mChatActivity);
                    } catch (Exception e) {
                        resetRecordVoice((Button) v);
                        mVoiceRecorder.discardRecording();
                        ToastUtil.showCenter(mChatActivity, "录音失败，请重试！");
                        return false;
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (event.getY() < -300) {
                        ((Button) v).setText(getString(R.string.release_to_cancel));
                        mRecordVoiceTip.setText(getString(R.string.release_to_cancel));
                        mRecordVoiceTip.setTextColor(mChatActivity.getResources().getColor(R.color.red));
                    } else {
                        ((Button) v).setText(getString(R.string.release_to_end));
                        mRecordVoiceTip.setText(getString(R.string.move_top_to_cancel));
                        mRecordVoiceTip.setTextColor(mChatActivity.getResources().getColor(R.color.white));
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    resetRecordVoice((Button) v);
                    if (event.getY() < -300) {
                        mVoiceRecorder.discardRecording();
                    } else {
                        doEndRecord();
                    }
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    try {
                        // 停止录音
                        if (mVoiceRecorder.isRecording()) {
                            mVoiceRecorder.discardRecording();
                            resetRecordVoice((Button) v);
                        }
                    } catch (Exception e) {
                        mlog.error("cancel exception...");
                        e.printStackTrace();
                    }
                    return true;

            }
            return false;
        }

    }

    private void resetRecordVoice(Button button) {
        button.setText(getString(R.string.press_to_say));
        button.setPressed(false);
        mRecordVoiceLayout.setVisibility(View.GONE);
        if (mWakeLock.isHeld()) mWakeLock.release();
        mDownTimer.cancel();
    }

    private void doEndRecord() {
        int length = mVoiceRecorder.stopRecoding();
        if (length > 0) {
            sendVoice(mVoiceRecorder.getVoiceFilePath(), length);
        } else if (length == EMError.FILE_INVALID) {
            ToastUtil.showCenter(mChatActivity, "录音无权限！");
        } else {
            ToastUtil.showCenter(mChatActivity, "录音时间太短！");
        }
    }

    public CountDownTimer mDownTimer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            int duration = (int) (60 - millisUntilFinished / 1000);
            String format = getString(R.string.record_voice_duraion);
            mRecordVoiceDuration.setText(String.format(format, duration));
        }

        @Override
        public void onFinish() {
            mVoiceRecorder.discardRecording();
            resetRecordVoice(mPressToSay);
            doEndRecord();
        }
    };

    private void sendVoice(String filePath, int length) {
        try {
            EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, mMsgFrom);
            EMClient.getInstance().chatManager().sendMessage(message);
            mChatActivity.refreshUI();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showCenter(mChatActivity, "发送失败！");
        }
    }

}
