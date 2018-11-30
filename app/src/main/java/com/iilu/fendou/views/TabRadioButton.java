package com.iilu.fendou.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.iilu.fendou.R;

public class TabRadioButton extends RadioButton {

    private int mDrawableSize;

    public TabRadioButton(Context context) {
        this(context, null, 0);
    }

    public TabRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Drawable drawableLeft = null;
        Drawable drawableTop = null;
        Drawable drawableRight = null;
        Drawable drawableBottom = null;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabRadioButton);

        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.TabRadioButton_drawableButtonSize:
                    mDrawableSize = a.getDimensionPixelSize(R.styleable.TabRadioButton_drawableButtonSize, 40);
                    break;
                case R.styleable.TabRadioButton_drawableTop:
                    drawableTop = a.getDrawable(attr);
                    break;
                case R.styleable.TabRadioButton_drawableBottom:
                    drawableRight = a.getDrawable(attr);
                    break;
                case R.styleable.TabRadioButton_drawableRight:
                    drawableBottom = a.getDrawable(attr);
                    break;
                case R.styleable.TabRadioButton_drawableLeft:
                    drawableLeft = a.getDrawable(attr);
                    break;
                default:
                    break;
            }
        }
        a.recycle();

        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
        setClickable(true);
    }

    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (left != null) {
            left.setBounds(0, 0, mDrawableSize, mDrawableSize);
        }
        if (right != null) {
            right.setBounds(0, 0, mDrawableSize, mDrawableSize);
        }
        if (top != null) {
            top.setBounds(0, 0, mDrawableSize, mDrawableSize);
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, mDrawableSize, mDrawableSize);
        }
        setCompoundDrawables(left, top, right, bottom);
    }
}
