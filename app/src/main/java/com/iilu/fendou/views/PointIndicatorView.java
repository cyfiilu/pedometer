package com.iilu.fendou.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.iilu.fendou.R;
import com.iilu.fendou.utils.SystemUtil;

import java.util.ArrayList;

public class PointIndicatorView extends LinearLayout {

    private Context mContext;
    private ArrayList<View> mImageViews;//所有指示器集合
    private int size = 8;
    private int marginSize = 15;
    private int pointSize; // 指示器的大小
    private int marginLeft; // 间距

    public PointIndicatorView(Context context) {
        this(context, null);
    }

    public PointIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PointIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        pointSize = SystemUtil.dip2px(context, size);
        marginLeft = SystemUtil.dip2px(context, marginSize);
    }

    public ArrayList<View> getImageViews() {
        return mImageViews;
    }

    /**
     * 初始化指示器
     *
     * @param count 指示器的数量
     */
    public void initIndicator(int count) {
        mImageViews = new ArrayList<>();
        this.removeAllViews();
        LayoutParams lp;
        for (int i = 0; i < count; i++) {
            View v = new View(mContext);
            lp = new LayoutParams(pointSize, pointSize);
            if (i != 0)
                lp.leftMargin = marginLeft;
            v.setLayoutParams(lp);
            if (i == 0) {
                v.setBackgroundResource(R.drawable.shape_point_indicator_select);
            } else {
                v.setBackgroundResource(R.drawable.shape_point_indicator_nomal);
            }
            v.setTag(i);
            mImageViews.add(v);
            this.addView(v);
        }
    }

    /**
     * 页面移动时切换指示器
     */
    public void setCurrIndex(int startPosition, int nextPosition) {
        if (startPosition < 0 || nextPosition < 0 || nextPosition == startPosition) {
            startPosition = nextPosition = 0;
        }
        final View viewStrat = mImageViews.get(startPosition);
        final View viewNext = mImageViews.get(nextPosition);
        viewNext.setBackgroundResource(R.drawable.shape_point_indicator_select);
        viewStrat.setBackgroundResource(R.drawable.shape_point_indicator_nomal);
    }

}
