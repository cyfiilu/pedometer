package com.iilu.fendou.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.iilu.fendou.R;
import com.iilu.fendou.configs.PrefsConfig;
import com.iilu.fendou.interfaces.SlidingMenuListener;
import com.iilu.fendou.utils.SPrefUtil_2;
import com.nineoldandroids.view.ViewHelper;

import org.apache.log4j.Logger;

public class SlidingMenu extends HorizontalScrollView {

    private static final Logger mlog = Logger.getLogger(SlidingMenu.class.getSimpleName());

    private LinearLayout mWrapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private GestureDetector mGestureDetector;
    private SlidingMenuListener mSlidingMenuListener;

    private int mScreenWidth;
    private int mMenuWidth;

    private int mTouchSlop;
    private int mSlidingMenuStyle;

    /**
     * 单位为dp
     */
    private int mMenuRightPadding;

    private boolean once = false;

    private boolean isOpen;

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSlidingMenuStyle = SPrefUtil_2.get(context, PrefsConfig.APP_CONST, "sliding_menu_style", 0);

        int defaultMenuRightPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                50,
                context.getResources().getDisplayMetrics());

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenu, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.SlidingMenu_rightPadding:
                    mMenuRightPadding = a.getDimensionPixelSize(attr, defaultMenuRightPadding);
                    mlog.debug("mMenuRightPadding = " + mMenuRightPadding);
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
            mMenu = (ViewGroup) mWrapper.getChildAt(0);
            mContent = (ViewGroup) mWrapper.getChildAt(1);

            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mContent.getLayoutParams().width = mScreenWidth;
            mlog.debug("mMenuWidth = " + mMenuWidth);
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 通过设置偏移量，将menu隐藏
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mlog.debug("onLayout changed = " + changed);
        if (changed) {
            mlog.debug("onLayout...");
            this.scrollTo(mMenuWidth, 0);
        }
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                mlog.debug("Touch action moving...");
                break;
            case MotionEvent.ACTION_UP:
                //隐藏在左边的宽度
                int scrollX = getScrollX();

                if (scrollX >= mMenuWidth / 2) {
                    this.smoothScrollTo(mMenuWidth, 0); //代表隐藏
                    isOpen = false;
                    mSlidingMenuListener.close();
                    mlog.info("Touch action up isOpen=false...");
                } else {
                    this.smoothScrollTo(0, 0); // 代表menu打开
                    isOpen = true;
                    mSlidingMenuListener.open();
                    mlog.info("Touch action up isOpen=true...");
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
        this.smoothScrollTo(0, 0);
        isOpen = true;
    }

    public void closeMenu() {
        if (!isOpen) return;
        this.smoothScrollTo(mMenuWidth, 0);
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
        mlog.info("onScrollChanged... l = " + l + ", t = " + t + ", oldl = " + oldl + ", oldt = " + oldt);
        if (mSlidingMenuStyle == 1) {
            //抽屉式侧滑 1
            ViewHelper.setTranslationX(mMenu, l);
        } else if (mSlidingMenuStyle == 2) {
            float scale = l * 1.0f / mMenuWidth;
            /**
             * qq侧滑
             * 区别1：内容区域1.0 ~ 0.7缩放的效果
             * scale：1.0 ~ 0.0
             * 0.7 + 0.3 * scale
             *
             * 区别2：菜单的偏移量需要修改
             *
             * 区别3：菜单显示时，有缩放及透明度的变化
             * 缩放：0.7 ~ 1.0
             * 1.0 - scale * 0.3
             * 透明度：0.6 ~ 1.0
             * 0.6 + 0.4 *（1 - scale）
             */
            float rightScale = 0.7f + 0.3f * scale;
            float leftScale = 1.0f - scale * 0.3f;
            float leftAlpha = 0.6f + 0.4f * (1 - scale);
            ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.7f);

            //设置menu区域
            ViewHelper.setScaleX(mMenu, leftScale);
            ViewHelper.setScaleY(mMenu, leftScale);
            ViewHelper.setAlpha(mMenu, leftAlpha);

            //设置内容区域
            ViewHelper.setPivotX(mContent, 0);
            ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
            ViewHelper.setScaleX(mContent, rightScale);
            ViewHelper.setScaleY(mContent, rightScale);
        }
    }

    public void setSlidingMenuListener(SlidingMenuListener listener){
        this.mSlidingMenuListener = listener;
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
