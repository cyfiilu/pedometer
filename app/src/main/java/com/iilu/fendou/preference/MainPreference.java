package com.iilu.fendou.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iilu.fendou.R;

public class MainPreference extends Preference {

    private Bitmap mBitmapLeft;
    private Bitmap mBitmapRight;
    private String mTvLeftStr;
    private String mTvRightStr;
    private int mTvRightColor;
    private int mTvLeftColor;

    private ImageView mImgLeft;
    private ImageView mImgRight;
    private TextView mTvLeft;
    private TextView mTvRight;

    public MainPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MainPreference, defStyleAttr, 0);
        mBitmapLeft = BitmapFactory.decodeResource(context.getResources(), a.getResourceId(R.styleable.MainPreference_img_left, 0));
        mTvLeftStr = a.getString(R.styleable.MainPreference_tv_left);
        mTvRightStr = a.getString(R.styleable.MainPreference_tv_right);
        mTvLeftColor = a.getColor(R.styleable.MainPreference_tv_left_color, Color.BLACK);
        mTvRightColor = a.getColor(R.styleable.MainPreference_tv_right_color, Color.BLACK);
        mBitmapRight = BitmapFactory.decodeResource(context.getResources(), a.getResourceId(R.styleable.MainPreference_img_right, 0));
        a.recycle();
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        return LayoutInflater.from(getContext()).inflate(R.layout.item_main, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mImgLeft = (ImageView) view.findViewById(R.id.iv_left);
        mImgRight = (ImageView) view.findViewById(R.id.iv_right);
        mTvLeft = (TextView) view.findViewById(R.id.tv_left);
        mTvRight = (TextView) view.findViewById(R.id.tv_right);
        mImgLeft.setImageBitmap(mBitmapLeft);
        mImgRight.setImageBitmap(mBitmapRight);
        mTvLeft.setText(mTvLeftStr);
        mTvRight.setText(mTvRightStr);
        mTvLeft.setTextColor(mTvLeftColor);
        mTvRight.setTextColor(mTvRightColor);
        if (mBitmapLeft == null) {
            mImgLeft.setVisibility(View.GONE);
            mTvLeft.setPadding(20, 0, 0, 0);
        }
        if (mBitmapRight == null) {
            mImgRight.setVisibility(View.GONE);
            mTvRight.setPadding(0, 0, 20, 0);
        }
    }

    public void setTvRightText(String tvRightText) {
        this.mTvRightStr = tvRightText;
        notifyChanged();
    }

    public String getTvRightText() {
        return mTvRightStr;
    }

}
