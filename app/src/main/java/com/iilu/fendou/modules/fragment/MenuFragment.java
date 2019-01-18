package com.iilu.fendou.modules.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iilu.fendou.BuildConfig;
import com.iilu.fendou.MainApplication;
import com.iilu.fendou.MainFragment;
import com.iilu.fendou.R;
import com.iilu.fendou.configs.PrefsConfig;
import com.iilu.fendou.configs.UserInfoConfig;
import com.iilu.fendou.modules.dialog.RebootAppDialog;
import com.iilu.fendou.modules.dialog.SlidingDirectionDialog;
import com.iilu.fendou.modules.dialog.SlidingMenuStyleDialog;
import com.iilu.fendou.modules.dialog.SlidingStyleDialog;
import com.iilu.fendou.modules.message.dialog.ChatFaceSettingDialog;
import com.iilu.fendou.modules.myself.dialog.AppIntroduceDialog;
import com.iilu.fendou.utils.BitmapUtil;
import com.iilu.fendou.utils.SPrefUtil_2;
import com.iilu.fendou.utils.StatusBarUtil;
import com.iilu.fendou.utils.SystemUtil;

import java.io.File;

import de.greenrobot.event.EventBus;

public class MenuFragment extends MainFragment implements View.OnClickListener {

    private final String mPath = Environment.getDataDirectory().getPath() + File.separator
            + "data" + File.separator + BuildConfig.APPLICATION_ID + File.separator
            + "pics" + File.separator;

    private static String mCurrLoginUsername;
    private static String mHeadStr = "head_";

    private Context mContext;

    private static ImageView mImgHead;
    private TextView mUsername;
    private TextView mLeftTextView_1;
    private TextView mRightTextView_1;
    private TextView mLeftTextView_2;
    private TextView mRightTextView_2;
    private TextView mLeftTextView_3;
    private TextView mRightTextView_3;
    private TextView mLeftTextView_4;
    private TextView mRightTextView_4;
    private TextView mLeftTextView_5;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        boolean isLeftSliding = SPrefUtil_2.get(mContext, PrefsConfig.APP_CONST, "isLeftSliding", true);
        if (isLeftSliding) {
            view = inflater.inflate(R.layout.fragment_left_menu, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_right_menu, container, false);
        }

        StatusBarUtil.compat(getActivity(), getResources().getColor(R.color.white));
        EventBus.getDefault().register(this);
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        mImgHead = (ImageView) view.findViewById(R.id.iv_head);
        mUsername = (TextView) view.findViewById(R.id.tv_username);
        mUsername.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        mCurrLoginUsername = MainApplication.getCurrLoginUsername();
        mUsername.setText(mCurrLoginUsername);

        setImgHead();

        initSlidingStyle(view);

        initSlidingDirection(view);

        initSlidingMenuStyle(view);

        initFaceChangeStyle(view);

        initAppIntroduce(view);
    }

    public void setImgHead() {
        mHeadStr = "head_";
        mHeadStr = mHeadStr + mCurrLoginUsername;
        if ("head_".equals(mHeadStr)) {
            mHeadStr = mHeadStr + UserInfoConfig.USER_ID;
        }
        String headFileName = mHeadStr + ".jpg";
        String[] fileList = new File(mPath).list();
        if (fileList != null) {
            for (int i = 0; i < fileList.length; i++) {
                String file = fileList[i];
                if (headFileName.equals(file)) {
                    Bitmap head = BitmapUtil.toBitmapThumbnail(new File(mPath + headFileName));
                    mImgHead.setImageBitmap(head);
                }
            }
        }
    }

    private void initSlidingStyle(View view) {
        RelativeLayout slidingLayout = (RelativeLayout) view.findViewById(R.id.sliding_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            slidingLayout.setBackgroundResource(R.drawable.selector_ripple_white);
        } else {
            slidingLayout.setBackgroundResource(R.drawable.selector_item_main);
        }
        slidingLayout.setOnClickListener(this);
        ImageView imgLeft = (ImageView) slidingLayout.findViewById(R.id.iv_left);
        imgLeft.setVisibility(View.GONE);
        mRightTextView_1 = (TextView) slidingLayout.findViewById(R.id.tv_right);
        mLeftTextView_1 = (TextView) slidingLayout.findViewById(R.id.tv_left);
        ((ImageView) slidingLayout.findViewById(R.id.iv_right)).setImageResource(R.mipmap.arrow_right_1);
        mLeftTextView_1.setPadding(20, 0, 0, 0);
        String str_1 = "侧滑样式(重启应用生效)";
        Spannable text_1 = createomplexContent(str_1, 14, 10, 0, 4, str_1.length());
        mLeftTextView_1.setText(text_1);

        boolean isDrawerLayout = SPrefUtil_2.get(mContext, PrefsConfig.APP_CONST, "isDrawerLayout", true);
        if (isDrawerLayout) {
            mRightTextView_1.setText("DrawerLayout");
        } else {
            mRightTextView_1.setText("SlidingMenu");
        }
    }

    private void initSlidingDirection(View view) {
        RelativeLayout slidingDirectionLayout = (RelativeLayout) view.findViewById(R.id.sliding_direction);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            slidingDirectionLayout.setBackgroundResource(R.drawable.selector_ripple_white);
        } else {
            slidingDirectionLayout.setBackgroundResource(R.drawable.selector_item_main);
        }
        slidingDirectionLayout.setOnClickListener(this);
        ImageView imgLeft_2 = (ImageView) slidingDirectionLayout.findViewById(R.id.iv_left);
        imgLeft_2.setVisibility(View.GONE);
        mLeftTextView_2 = (TextView) slidingDirectionLayout.findViewById(R.id.tv_left);
        mRightTextView_2 = (TextView) slidingDirectionLayout.findViewById(R.id.tv_right);
        mLeftTextView_2.setPadding(20, 0, 0, 0);
        String str_2 = "滑动方向(重启应用生效)";
        Spannable text_2 = createomplexContent(str_2, 14, 10, 0, 4, str_2.length());
        mLeftTextView_2.setText(text_2);
        ((ImageView) slidingDirectionLayout.findViewById(R.id.iv_right)).setImageResource(R.mipmap.arrow_right_1);

        boolean isLeftSliding = SPrefUtil_2.get(mContext, PrefsConfig.APP_CONST, "isLeftSliding", true);
        if (isLeftSliding) {
            mRightTextView_2.setText("左侧");
        } else {
            mRightTextView_2.setText("右侧");
        }
    }

    private void initSlidingMenuStyle(View view) {
        RelativeLayout slidingMenuLayout = (RelativeLayout) view.findViewById(R.id.sliding_menu_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            slidingMenuLayout.setBackgroundResource(R.drawable.selector_ripple_white);
        } else {
            slidingMenuLayout.setBackgroundResource(R.drawable.selector_item_main);
        }
        slidingMenuLayout.setOnClickListener(this);
        ImageView imgLeft = (ImageView) slidingMenuLayout.findViewById(R.id.iv_left);
        imgLeft.setVisibility(View.GONE);
        mRightTextView_3 = (TextView) slidingMenuLayout.findViewById(R.id.tv_right);
        mLeftTextView_3 = (TextView) slidingMenuLayout.findViewById(R.id.tv_left);
        ((ImageView) slidingMenuLayout.findViewById(R.id.iv_right)).setImageResource(R.mipmap.arrow_right_1);
        mLeftTextView_3.setPadding(20, 0, 0, 0);
        String str_1 = "SlidingMenu滑动方式(重启应用生效)";
        Spannable text_1 = createomplexContent(str_1, 14, 10, 0, 15, str_1.length());
        mLeftTextView_3.setText(text_1);

        int sliding_menu_style = SPrefUtil_2.get(mContext, PrefsConfig.APP_CONST, "sliding_menu_style", 0);
        if (sliding_menu_style == 0) {
            mRightTextView_3.setText("普通");
        } else if (sliding_menu_style == 1) {
            mRightTextView_3.setText("抽屉");
        } else if (sliding_menu_style == 2) {
            mRightTextView_3.setText("仿qq");
        }
    }

    private void initFaceChangeStyle(View view) {
        RelativeLayout slidingDirectionLayout = (RelativeLayout) view.findViewById(R.id.face_change_style);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            slidingDirectionLayout.setBackgroundResource(R.drawable.selector_ripple_white);
        } else {
            slidingDirectionLayout.setBackgroundResource(R.drawable.selector_item_main);
        }
        slidingDirectionLayout.setOnClickListener(this);
        ImageView imgLeft_2 = (ImageView) slidingDirectionLayout.findViewById(R.id.iv_left);
        imgLeft_2.setVisibility(View.GONE);
        mLeftTextView_4 = (TextView) slidingDirectionLayout.findViewById(R.id.tv_left);
        mRightTextView_4 = (TextView) slidingDirectionLayout.findViewById(R.id.tv_right);
        mLeftTextView_4.setPadding(20, 0, 0, 0);
        mLeftTextView_4.setText("表情样式切换");
        ((ImageView) slidingDirectionLayout.findViewById(R.id.iv_right)).setImageResource(R.mipmap.arrow_right_1);

        setFaceChangeStyle();
    }

    /**
     * 设置TextView不同字体大小
     * @param text 要设置的文字
     * @param bigFontSize 大字体，单位：sp
     * @param smallFontSize 小字体，单位：sp
     * @param start 开始位置
     * @param middle 中间位置
     * @param end 结束位置
     * @return
     */
    private Spannable createomplexContent(String text, int bigFontSize, int smallFontSize,
                                          int start, int middle, int end) {
        int bigFontSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, bigFontSize,
                mContext.getResources().getDisplayMetrics());
        int smallFontSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, smallFontSize,
                mContext.getResources().getDisplayMetrics());
        Spannable spannale = new SpannableString(text);
        spannale.setSpan(new AbsoluteSizeSpan(bigFontSizePx), start, middle, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannale.setSpan(new AbsoluteSizeSpan(smallFontSizePx), middle, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannale;
    }

    private void initAppIntroduce(View view) {
        RelativeLayout slidingMenuLayout = (RelativeLayout) view.findViewById(R.id.app_introduce);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            slidingMenuLayout.setBackgroundResource(R.drawable.selector_ripple_white);
        } else {
            slidingMenuLayout.setBackgroundResource(R.drawable.selector_item_main);
        }
        slidingMenuLayout.setOnClickListener(this);
        ImageView imgLeft = (ImageView) slidingMenuLayout.findViewById(R.id.iv_left);
        imgLeft.setVisibility(View.GONE);
        mLeftTextView_5 = (TextView) slidingMenuLayout.findViewById(R.id.tv_left);
        ((ImageView) slidingMenuLayout.findViewById(R.id.iv_right)).setImageResource(R.mipmap.arrow_right_1);
        mLeftTextView_5.setPadding(20, 0, 0, 0);
        String str_1 = "app介绍";
        mLeftTextView_5.setText(str_1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sliding_style:
                SlidingStyleDialog sytleDialog = new SlidingStyleDialog(mContext, R.style.NewDialogSytle);
                sytleDialog.show();
                sytleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        String oldValue_1 = mRightTextView_1.getText().toString();
                        String newValue_1 = ((SlidingStyleDialog) dialog).getValue();
                        if (!oldValue_1.equals(newValue_1)) {
                            mRightTextView_1.setText(newValue_1);
                            showRebootDialog();
                        }
                    }
                });
                break;
            case R.id.sliding_direction:
                SlidingDirectionDialog directionDialog = new SlidingDirectionDialog(mContext, R.style.NewDialogSytle);
                directionDialog.show();
                directionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        String oldValue_2 = mRightTextView_2.getText().toString();
                        String newValue_2 = ((SlidingDirectionDialog) dialog).getValue();
                        if (!oldValue_2.equals(newValue_2)) {
                            mRightTextView_2.setText(newValue_2);
                            showRebootDialog();
                        }
                    }
                });
                break;
            case R.id.sliding_menu_style:
                SlidingMenuStyleDialog menuStyleDialog = new SlidingMenuStyleDialog(mContext, R.style.NewDialogSytle);
                menuStyleDialog.show();
                menuStyleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        String oldValue_3 = mRightTextView_3.getText().toString();
                        String newValue_3 = ((SlidingMenuStyleDialog) dialog).getValue();
                        if (!oldValue_3.equals(newValue_3)) {
                            mRightTextView_3.setText(newValue_3);
                            showRebootDialog();
                        }
                    }
                });
                break;
            case R.id.face_change_style:
                ChatFaceSettingDialog faceSettingDialog = new ChatFaceSettingDialog(mContext, R.style.NewDialogSytle);
                faceSettingDialog.show();
                faceSettingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        String oldValue_4 = mRightTextView_4.getText().toString();
                        String newValue_4 = ((ChatFaceSettingDialog) dialog).getValue();
                        if (!oldValue_4.equals(newValue_4)) {
                            mRightTextView_4.setText(newValue_4);
                        }
                    }
                });
                break;
            case R.id.app_introduce:
                new AppIntroduceDialog(mContext, R.style.NewDialogSytle).show();
                break;
        }
    }

    private void showRebootDialog() {
        RebootAppDialog rebootAppDialog = new RebootAppDialog(mContext, R.style.Base_Theme_AppCompat_Light_Dialog);
        AlertDialog alertDialog = rebootAppDialog.create();
        alertDialog.show();
        rebootAppDialog.setConfigDialog(alertDialog, SystemUtil.getScreenWidth(mContext));
    }

    public void onEventMainThread(String event) {
        if (TextUtils.isEmpty(event)) return;
        if ("face_change_type_changed".equals(event)) {
            setFaceChangeStyle();
        }
    }

    private void setFaceChangeStyle() {
        boolean isCanScroll = SPrefUtil_2.get(mContext, PrefsConfig.MSG_CHAT, "isCanScroll", true);
        if (isCanScroll) {
            mRightTextView_4.setText("滑动切换");
        } else {
            mRightTextView_4.setText("点击tab切换");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mHeadStr = "head_";
        mCurrLoginUsername = null;
    }
}
