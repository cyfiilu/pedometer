package com.iilu.fendou.modules.myself.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iilu.fendou.R;
import com.iilu.fendou.adapters.wheel.AbstractWheelTextAdapter;
import com.iilu.fendou.utils.SystemUtil;
import com.iilu.fendou.views.wheel.WheelView;

public class PersonalInfoSettingDialog extends Dialog {

    private static int mTheme = R.style.NewDialogSytle; // 主题
    private Activity mContext;
    // 取消
    private TextView mCancel;
    // 确定
    private TextView mConfirm;
    private TextView mUnit_1, mUnit_2, mUnit_3;
    private TextView mDescription;

    private WheelView mParentWheel;
    private WheelView mChildWheel;
    private WheelView mSubChildWheel;

    private ParentAdapter parentAdaper;
    private ChildAdapter childAdapter;
    private SubChildAdapter subChildAdapter;
    private View.OnClickListener mListener;

    private String[] mParent = null;
    private String[] mChild = null;
    private String[] mSubChild = null;
    private int COLOR_TOP = 0xefFFFFFF;
    private int COLOR_CENTER = 0xcfFFFFFF;
    private int COLOR_BOTTOM = 0x3fFFFFFF;
    private int mParentResId, mChildResId, mSubChildResId;

    public PersonalInfoSettingDialog(Activity context, View.OnClickListener listener, String[] parent, String[] child, String[] subChild) {
        super(context, mTheme);
        mContext = context;
        mListener = listener;
        this.mParent = parent;
        this.mChild = child;
        this.mSubChild = subChild;

        setContentView(R.layout.dialog_sport_setting);

        initView();

        if (mParent == null) {
            mParentWheel.setVisibility(View.GONE);
            mUnit_1.setVisibility(View.GONE);
        }
        if (mChild == null) {
            mChildWheel.setVisibility(View.GONE);
            mUnit_2.setVisibility(View.GONE);
        }
        if (mSubChild == null) {
            mSubChildWheel.setVisibility(View.GONE);
            mUnit_3.setVisibility(View.GONE);
        }

        initData();

        Point screenParams = SystemUtil.getScreenParams(mContext);
        LinearLayout city_select_layout = (LinearLayout) findViewById(R.id.wheel_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenParams.x, screenParams.y / 3);
        city_select_layout.setLayoutParams(params);

        setListener();
    }

    /**
     * @param context
     * @param listener
     * @param parentResId
     * @param childResId
     * @param subChildResId
     */
    public PersonalInfoSettingDialog(Activity context, View.OnClickListener listener, int parentResId, int childResId, int subChildResId) {
        super(context, mTheme);
        mContext = context;
        mListener = listener;
        mParentResId = parentResId;
        mChildResId = childResId;
        mSubChildResId = subChildResId;

        setContentView(R.layout.dialog_sport_setting);

        initView();

        getWheelContent(mContext, mParentResId, mChildResId, mSubChildResId);

        initData();

        Point screenParams = SystemUtil.getScreenParams(mContext);
        LinearLayout city_select_layout = (LinearLayout) findViewById(R.id.wheel_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenParams.x, screenParams.y / 3);
        city_select_layout.setLayoutParams(params);

        setListener();
    }

    private void initView() {
        mUnit_1 = (TextView) findViewById(R.id.wheelView_1_unit);
        mUnit_2 = (TextView) findViewById(R.id.wheelView_2_unit);
        mUnit_3 = (TextView) findViewById(R.id.wheelView_3_unit);
        mUnit_1.setBackgroundResource(R.color.white);
        mUnit_2.setBackgroundResource(R.color.white);
        mUnit_3.setBackgroundResource(R.color.white);

        mCancel = (TextView) findViewById(R.id.tv_cancel);
        mConfirm = (TextView) findViewById(R.id.tv_confirm);
        mDescription = (TextView) findViewById(R.id.tv_description);

        mCancel.setClickable(true);
        mCancel.setFocusable(true);
        mConfirm.setClickable(true);
        mConfirm.setFocusable(true);

        mParentWheel = (WheelView) findViewById(R.id.wheelView_1);
        mChildWheel = (WheelView) findViewById(R.id.wheelView_2);
        mSubChildWheel = (WheelView) findViewById(R.id.wheelView_3);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.BottomAnim);
    }

    private void initData() {
        mParentWheel.setShadowColor(COLOR_TOP, COLOR_CENTER, COLOR_BOTTOM);
        mChildWheel.setShadowColor(COLOR_TOP, COLOR_CENTER, COLOR_BOTTOM);
        mSubChildWheel.setShadowColor(COLOR_TOP, COLOR_CENTER, COLOR_BOTTOM);

        parentAdaper = new ParentAdapter();
        mParentWheel.setViewAdapter(parentAdaper);
        mParentWheel.setCurrentItem(0);
        childAdapter = new ChildAdapter();
        mChildWheel.setViewAdapter(childAdapter);
        mChildWheel.setCurrentItem(0);

        subChildAdapter = new SubChildAdapter();
        mSubChildWheel.setViewAdapter(subChildAdapter);
        mSubChildWheel.setCurrentItem(0);
    }

    protected void setListener() {
        mCancel.setOnClickListener(mListener);
        mConfirm.setOnClickListener(mListener);
    }

    /**
     * @param parentIndex
     * @param childindex
     * @param subChildindex
     */
    public void show(int parentIndex, int childindex, int subChildindex) {
        Window w = getWindow();
        w.setBackgroundDrawableResource(R.drawable.wheel_bg);
        w.setGravity(Gravity.BOTTOM);
        mParentWheel.setCurrentItem(parentIndex, false);
        mChildWheel.setCurrentItem(childindex, false);
        mSubChildWheel.setCurrentItem(subChildindex, false);
        show();
    }

    public void setDialogText(String text) {
        mDescription.setText(text);
    }

    public void setUnitForData(String textU0, String textU1, String textU2) {
        mUnit_1.setText(textU0);
        mUnit_2.setText(textU1);
        mUnit_3.setText(textU2);
    }

    /**
     * 根据resId，为不同的修改框填充相应的数据
     *
     * @param context
     */
    public void getWheelContent(Context context, int parentResId, int childResId, int subChildResId) {
        if (parentResId != -1) {
            mParent = context.getResources().getStringArray(parentResId);
        } else {
            mParentWheel.setVisibility(View.GONE);
            mUnit_1.setVisibility(View.GONE);
        }
        if (childResId != -1) {
            mChild = context.getResources().getStringArray(childResId);
        } else {
            mChildWheel.setVisibility(View.GONE);
            mUnit_2.setVisibility(View.GONE);
        }
        if (subChildResId != -1) {
            mSubChild = context.getResources().getStringArray(subChildResId);
        } else {
            mSubChildWheel.setVisibility(View.GONE);
            mUnit_3.setVisibility(View.GONE);
        }
    }

    class ParentAdapter extends AbstractWheelTextAdapter {
        public ParentAdapter() {
            super(mContext, R.layout.wheel_item);
            setItemTextResource(R.id.tv_wheel_item);
        }

        @Override
        public int getItemsCount() {
            return mParent != null ? mParent.length : 0;
        }

        @Override
        protected CharSequence getItemText(int index) {

            return mParent[index];
        }
    }

    class ChildAdapter extends AbstractWheelTextAdapter {
        public ChildAdapter() {
            super(mContext, R.layout.wheel_item);
            setItemTextResource(R.id.tv_wheel_item);
        }

        @Override
        public int getItemsCount() {
            return mChild != null ? mChild.length : 0;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return mChild[index];
        }
    }

    class SubChildAdapter extends AbstractWheelTextAdapter {
        public SubChildAdapter() {
            super(mContext, R.layout.wheel_item);
            setItemTextResource(R.id.tv_wheel_item);
        }

        @Override
        public int getItemsCount() {
            return mSubChild != null ? mSubChild.length : 0;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return mSubChild[index];
        }
    }

    public Object getValue(int type) {
        String parentValue = null;
        String childValue = null;
        String subChildValue = null;
        if (mParent != null) {
            parentValue = mParent[mParentWheel.getCurrentItem()];
        }
        if (mChild != null) {
            childValue = mChild[mChildWheel.getCurrentItem()];
        }
        if (mSubChild != null) {
            subChildValue = mSubChild[mSubChildWheel.getCurrentItem()];
        }
        switch (type) {
            case 1: // 性别
                return parentValue;
            case 2: // 身高
                Integer height = Integer.valueOf(parentValue) * 100 + Integer.valueOf(childValue) * 10 + Integer.valueOf(subChildValue);
                return height;
            case 3: // 体重
                Integer weight = Integer.valueOf(parentValue) * 100 + Integer.valueOf(childValue) * 10 + Integer.valueOf(subChildValue);
                return weight;
        }
        return "";
    }

}
