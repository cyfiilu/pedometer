package com.iilu.fendou.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iilu.fendou.MainApplication;
import com.iilu.fendou.R;
import com.iilu.fendou.configs.PrefsConfig;
import com.iilu.fendou.utils.SPrefUtil_2;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;

public class FaceKeyboard {

    private final Logger mlog = Logger.getLogger(FaceKeyboard.class.getSimpleName());

    private Activity mActivity;
    private InputMethodManager mInputManager;//软键盘管理类
    private View mEmotionLayout;//表情布局
    private View mMoreLayout;//表情布局
    private ImageView mVoiceView;//语音按钮
    private ImageView mFaceView;//表情按钮
    private Button mPressToSay;//表情按钮
    private EditText mEditText;//
    private View mContentView;//内容布局view,即除了表情布局或者软键盘布局以外的布局，用于固定bar的高度，防止跳闪

    private FaceKeyboard() {

    }

    /**
     * 外部静态调用
     *
     * @param activity
     * @return
     */
    public static FaceKeyboard with(Activity activity) {
        FaceKeyboard emotionInputDetector = new FaceKeyboard();
        emotionInputDetector.mActivity = activity;
        emotionInputDetector.mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        return emotionInputDetector;
    }

    /**
     * 绑定内容view，此view用于固定bar的高度，防止跳闪
     *
     * @param contentView
     * @return
     */
    public FaceKeyboard bindToContent(View contentView) {
        mContentView = contentView;
        return this;
    }

    private int getImageViewCurrBgResId(ImageView imageView) {
        int bgResId = -1;
        try {
            Field field = imageView.getClass().getDeclaredField("mResource");
            field.setAccessible(true);
            bgResId = field.getInt(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bgResId;
    }

    public FaceKeyboard bindToVoiceImageView(ImageView faceButton) {
        this.mVoiceView = faceButton;
        faceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSoftInputShown() || mMoreLayout.isShown() || mEmotionLayout.isShown()) {
                    ((ImageView) v).setImageResource(R.drawable.selector_msg_keyboard);
                    mFaceView.setImageResource(R.drawable.selector_msg_face);
                    hideEmotionLayout();
                    hideMoreLayout();
                    hideSoftInput();
                    mEditText.setVisibility(View.GONE);
                    mPressToSay.setVisibility(View.VISIBLE);
                } else {
                    if (getImageViewCurrBgResId((ImageView) v) == R.drawable.selector_msg_keyboard) {
                        ((ImageView) v).setImageResource(R.drawable.selector_msg_voice);
                        mEditText.setVisibility(View.VISIBLE);
                        mEditText.requestFocus();
                        mPressToSay.setVisibility(View.GONE);
                        showSoftInput();
                    } else {
                        ((ImageView) v).setImageResource(R.drawable.selector_msg_keyboard);
                        mEditText.setVisibility(View.GONE);
                        mPressToSay.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        return this;
    }

    /**
     * 绑定编辑框
     *
     * @param editText
     * @return
     */
    public FaceKeyboard bindToEditText(EditText editText) {
        mEditText = editText;
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && (mEmotionLayout.isShown() || mMoreLayout.isShown())) {
                    mFaceView.setImageResource(R.drawable.selector_msg_face);
                    lockContentHeight();// 显示软件盘时，锁定内容高度，防止跳闪。
                    hideEmotionLayout();// 隐藏表情布局
                    hideMoreLayout(); // 隐藏更多面板
                    showSoftInput(); // 显示键盘
                    mEditText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            unlockContentHeightDelayed(); // 软件盘显示后，释放内容高度
                        }
                    }, 200L);
                }
                return false;
            }
        });
        return this;
    }

    /**
     * 绑定表情按钮
     *
     * @param faceButton
     * @return
     */
    public FaceKeyboard bindToFaceImageView(ImageView faceButton) {
        faceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmotionLayout.isShown()) {
                    ((ImageView) v).setImageResource(R.drawable.selector_msg_face);
                    lockContentHeight();
                    hideEmotionLayout();
                    showSoftInput();
                    unlockContentHeightDelayed();
                } else {
                    ((ImageView) v).setImageResource(R.drawable.selector_msg_keyboard);
                    if (isSoftInputShown()) {
                        lockContentHeight();
                        hideMoreLayout();
                        showEmotionLayout();
                        unlockContentHeightDelayed();
                    } else {
                        mVoiceView.setImageResource(R.drawable.selector_msg_voice);
                        mEditText.setVisibility(View.VISIBLE);
                        mEditText.requestFocus();
                        mPressToSay.setVisibility(View.GONE);
                        hideMoreLayout();
                        showEmotionLayout();
                    }
                }
            }
        });
        return this;
    }

    /**
     * 绑定更多按钮
     *
     * @param moreImageView
     * @return
     */
    public FaceKeyboard bindToMoreImageView(final ImageView moreImageView) {
        moreImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoreLayout.isShown()) { // 如果更多面板显示
                    lockContentHeight(); // 锁定高度，防止闪跳
                    hideMoreLayout(); // 隐藏更多面板
                    showSoftInput(); // 显示输入法
                    mEditText.requestFocus(); // 输入框获取焦点
                    unlockContentHeightDelayed(); // 释放高度
                } else { // 如果更多面板不显示
                    // 清楚输入框焦点
                    mEditText.clearFocus();
                    if (isSoftInputShown()) { // 如果输入法显示
                        // 1. 锁定高度
                        lockContentHeight();
                        // 2. 隐藏表情面板
                        hideEmotionLayout();
                        // 3. 显示更多面板
                        showMoreLayout();
                        // 4. 释放高度
                        unlockContentHeightDelayed();
                    } else { // 输入法不显示
                        mVoiceView.setImageResource(R.drawable.selector_msg_voice);
                        mFaceView.setImageResource(R.drawable.selector_msg_face);
                        mEditText.setVisibility(View.VISIBLE);
                        mEditText.requestFocus();
                        mPressToSay.setVisibility(View.GONE);
                        // 1. 隐藏表情面板
                        hideEmotionLayout();
                        // 2. 显示更多面板
                        showMoreLayout();
                    }
                }
            }
        });
        return this;
    }

    /**
     * 设置表情内容布局
     *
     * @param emotionView
     * @return
     */
    public FaceKeyboard setEmotionView(View emotionView) {
        this.mEmotionLayout = emotionView;
        return this;
    }

    /**
     * 设置表情图标
     *
     * @param faceView
     * @return
     */
    public FaceKeyboard setViews(ImageView faceView, Button pressToSay) {
        this.mFaceView = faceView;
        this.mPressToSay = pressToSay;
        return this;
    }

    /**
     * 设置表情内容布局
     *
     * @param moreView
     * @return
     */
    public FaceKeyboard setMoreView(View moreView) {
        this.mMoreLayout = moreView;
        return this;
    }

    public FaceKeyboard build() {
        //设置软件盘的模式：SOFT_INPUT_ADJUST_RESIZE，这个属性表示Activity的主窗口总是会被调整大小，从而保证软键盘显示空间。
        //从而方便我们计算软件盘的高度
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //隐藏软件盘
        hideSoftInput();
        return this;
    }

    /**
     * 点击返回键时先隐藏表情布局
     *
     * @return
     */
    public boolean interceptBackPress() {
        if (mEmotionLayout.isShown() || mMoreLayout.isShown()) {
            hideEmotionLayout();
            return true;
        }
        return false;
    }

    /**
     * 显示表情面板
     */
    private void showEmotionLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = getKeyBoardHeight();
        }
        hideSoftInput();
        mEmotionLayout.getLayoutParams().height = softInputHeight;
        mEmotionLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏表情面板
     */
    public void hideEmotionLayout() {
        if (mEmotionLayout.isShown()) {
            mEmotionLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 显示更多面板
     */
    private void showMoreLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = getKeyBoardHeight();
        }
        hideSoftInput();
        mMoreLayout.getLayoutParams().height = softInputHeight;
        mMoreLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏更多面板
     */
    public void hideMoreLayout() {
        if (mMoreLayout.isShown()) {
            mMoreLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 锁定内容高度，防止跳闪
     */
    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = mContentView.getHeight();
        params.weight = 0.0F;
    }

    /**
     * 释放被锁定的内容高度
     */
    private void unlockContentHeightDelayed() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    private void showSoftInput() {
        mEditText.requestFocus();
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(mEditText, 0);
            }
        });
    }

    /**
     * 隐藏软件盘
     */
    public void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 是否显示软件盘
     *
     * @return
     */
    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    /**
     * 获取软件盘的高度
     *
     * @return
     */
    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;

        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }

        if (softInputHeight < 0) {
            mlog.error("Warning: value of softInputHeight is below zero!");
        }
        //存一份到本地
        if (softInputHeight > 0) {
            SPrefUtil_2.put(MainApplication.getAppContext(), PrefsConfig.MSG_CHAT, "soft_input_height", softInputHeight);
        }
        return softInputHeight;
    }


    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /**
     * 获取软键盘高度，由于第一次直接弹出表情时会出现小问题，787是一个均值，作为临时解决方案
     *
     * @return
     */
    private int getKeyBoardHeight() {
        return SPrefUtil_2.get(MainApplication.getAppContext(), PrefsConfig.MSG_CHAT, "soft_input_height", 787);
    }

}
