package com.iilu.fendou.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.iilu.fendou.R;
import com.iilu.fendou.configs.PrefsConfig;
import com.iilu.fendou.utils.SPrefUtil_2;
import com.nineoldandroids.view.ViewHelper;

import org.apache.log4j.Logger;

public class SlidingMenuRight extends HorizontalScrollView {

    private static final Logger mlog = Logger.getLogger(SlidingMenuRight.class.getSimpleName());

    private LinearLayout mWrapper;
    private View mMenu;
    private ViewGroup mContent;
    private GestureDetector mGestureDetector;

    private int mScreenWidth;
    private int mMenuWidth;

    private int mTouchSlop;
    private int mSlidingMenuStyle;

    /**
     * 单位为dp
     */
    private int mMenuLeftPadding;
    private boolean once = false;
    private boolean isOpen;

    public SlidingMenuRight(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenuRight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSlidingMenuStyle = SPrefUtil_2.get(context, PrefsConfig.APP_CONST, "sliding_menu_style", 0);

        int defaultMenuRightPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                50,
                context.getResources().getDisplayMetrics());

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenuRight, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.SlidingMenuRight_leftPadding:
                    mMenuLeftPadding = a.getDimensionPixelSize(attr, defaultMenuRightPadding);
                    mlog.debug("mMenuLeftPadding = " + mMenuLeftPadding);
                    break;
            }
        }
        a.recycle();

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();

        mGestureDetector = new GestureDetector(context, new YScrollDetector());

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mScreenWidth = outMetrics.widthPixels;
        mlog.debug("mScreenWidth = " + mScreenWidth);

        /*Point outSize = new Point();
        wm.getDefaultDisplay().getSize(outSize);
        int screenWidth1 = outSize.x;
        Log.d(TAG, "SlidingMenu: screenWidth1 = " + screenWidth1);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth2 = metrics.widthPixels;
        Log.d(TAG, "SlidingMenu: screenWidth2 = " + screenWidth2);*/
    }

    /**
     * 设置子view的宽和高
     * 设置自己的宽和高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once) {
            mWrapper = (LinearLayout) getChildAt(0);
            mMenu = mWrapper.getChildAt(0);
            mContent = (ViewGroup) mWrapper.getChildAt(1);

            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuLeftPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            mlog.debug("mMenuWidth = " + mMenuWidth);
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                if (scrollX >= mMenuWidth / 2) {
                    this.smoothScrollTo(mMenuWidth, 0); //代表menu打开
                    isOpen = true;
                } else {
                    this.smoothScrollTo(0, 0); // 代表隐藏
                    isOpen = false;
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void openMenu() {
        if (isOpen) return;
        this.smoothScrollTo(mMenuWidth, 0);
        isOpen = true;
    }

    public void closeMenu() {
        if (!isOpen) return;
        this.smoothScrollTo(0, 0);
        isOpen = false;
    }

    public void toggle() {
        if (isOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    /**
     * @param l    --> getScrollX : 从1-->0变化
     * @param t
     * @param oldl
     * @param oldt
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mSlidingMenuStyle == 1) {
            //抽屉式侧滑
            ViewHelper.setTranslationX(mMenu, l - mMenuWidth);
        } else if (mSlidingMenuStyle == 2) {
            float scale = l * 1.0f / mMenuWidth; // 0 ~ 1.0f
            float leftScale = 1.0f - 0.3f * scale; // 内容区域1.0 ~ 0.7
            float rightScale = 0.7f + scale * 0.3f; // 菜单区域0.7 ~ 1.0
            float rightAlpha = 0.6f + 0.4f * scale; // 菜单区域透明度0.6 ~ 1.0
            ViewHelper.setTranslationX(mMenu, (l - mMenuWidth) * (1 - scale) * 0.7f);

            //设置menu区域
            ViewHelper.setScaleX(mMenu, rightScale);
            ViewHelper.setScaleY(mMenu, rightScale);
            ViewHelper.setAlpha(mMenu, rightAlpha);

            //设置内容区域
            ViewHelper.setPivotX(mContent, mScreenWidth); // 已内容区域右边界，即屏幕宽度为x轴上缩放点
            ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
            ViewHelper.setScaleX(mContent, leftScale);
            ViewHelper.setScaleY(mContent, leftScale);
        }
    }

    /**
     * 如果竖向滑动距离<横向距离，执行横向滑动，否则竖向。如果是ScrollView，则'<'换成'>'
     */
    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceY) < Math.abs(distanceX)) {
                return true;
            }
            return false;
        }
    }
}
