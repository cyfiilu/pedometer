package com.iilu.fendou.modules.sport.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.iilu.fendou.R;
import com.iilu.fendou.modules.sport.entity.DayData;

import org.apache.log4j.Logger;

public class SportView extends View {

    private final Logger mlog = Logger.getLogger(SportView.class);

    /** 屏幕密度 */
    private float mDensity;

    /** 线条范围 */
    private final int LINT_RANGE = 270;
    /** 线条宽度 */
    private final int STROKE_WIDTH = 30;
    /** 旋转角度 */
    private final float ROTATE_DEGREE = 135f;
    /** 线条个数 */
    private final int LINE_COUNT = 55;
    /** 线条粗细 */
    private final int LINE_WIDTH = 4;

    /** 步数 */
    private String mDayStepCount = "0";
    /** 步数单位 */
    private String mStepCountUnit = "步";
    private float mTextSize_1 = 30f;
    private float mTextSize_2 = 15f;

    private float mKmValue = 0.0f;
    private float mKcalValue = 0.0f;

    /** 画表盘的 */
    private Paint mDialPaint = new Paint();
    /** 画太阳、月亮、星星的 */
    private Paint mBitmapPaint = new Paint();
    /** 画文字的 */
    private Paint mTextPaint = new TextPaint();
    private RectF mRectF = new RectF();
    /** 步数边界 */
    private Rect mRectTextBounds_1 = new Rect();
    /** 步数单位边界 */
    private Rect mRectTextBounds_2 = new Rect();

    private Bitmap mZhaozhaoDefault;
    private Bitmap mZhaozhaoLight;
    private Bitmap mMumuDefault;
    private Bitmap mMumuLight;
    private Bitmap mStarDefault;
    private Bitmap mStarLight;

    private DayData mDayData;

    public SportView(Context context) {
        this(context, null);
    }

    public SportView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SportView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mDensity = displayMetrics.density;

        mDialPaint.setAntiAlias(true);
        mDialPaint.setStyle(Paint.Style.STROKE);

        mZhaozhaoDefault = BitmapFactory.decodeResource(getResources(), R.mipmap.sun_default);
        mZhaozhaoLight = BitmapFactory.decodeResource(getResources(), R.mipmap.sun_light);
        mMumuDefault = BitmapFactory.decodeResource(getResources(), R.mipmap.moon_default);
        mMumuLight = BitmapFactory.decodeResource(getResources(), R.mipmap.moon_light);
        mStarDefault = BitmapFactory.decodeResource(getResources(), R.mipmap.star_default);
        mStarLight = BitmapFactory.decodeResource(getResources(), R.mipmap.star_light);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int center = getWidth() / 2;
        int radius = center - STROKE_WIDTH / 2;

        // 旋转画布
        canvas.rotate(ROTATE_DEGREE, center, center);

        mDialPaint.setStrokeWidth(STROKE_WIDTH);

        float itemSize = LINT_RANGE * 1.0f / LINE_COUNT;
        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius);

        // 画表盘默认线条
        drawDefaultDial(canvas, oval, itemSize);

        if (mDayData != null) {
            int dayGoalNum = mDayData.getDayGoalNum() == 0 ? 10000 : mDayData.getDayGoalNum();
            float lightRange = mDayData.getDayStepCount() * 1.0f / dayGoalNum * LINT_RANGE;
            lightRange = lightRange >= LINT_RANGE ? LINT_RANGE : lightRange;
            // 根据步数画圆盘进度线条
            drawLightDial(canvas, lightRange, oval, itemSize);
        }

        canvas.restore();

        // 获取内圆半径
        int innerRadius = radius - STROKE_WIDTH / 2;
        // 设置矩形左边位置  x * x + x * x = innerRadius(等腰直角三角形) --> x = √2 / 2 * innerRadius
        mRectF.left = (float) (innerRadius - Math.sqrt(2) / 2 * innerRadius) + STROKE_WIDTH;
        mRectF.top = (float) (innerRadius - Math.sqrt(2) / 2 * innerRadius) + STROKE_WIDTH;
        mRectF.right = (float) (mRectF.left + Math.sqrt(2) * innerRadius);
        mRectF.bottom = (float) (mRectF.top + Math.sqrt(2) * innerRadius);

        // 画太阳、月亮、星星
        drawBitmap(canvas, center);

        // 画步数
        drawStep(canvas, center);

        // 画步数单位
        drawStepUnit(canvas, center);

        // 画km/kcal
        drawKmKcal(canvas, center);
    }

    /**
     * 画圆盘默认线条
     * @param canvas
     * @param oval
     * @param itemSize
     */
    private void drawDefaultDial(Canvas canvas, RectF oval, float itemSize) {
        mDialPaint.setColor(getResources().getColor(R.color.blue_1));

        for (float start = 0; start < LINT_RANGE; start += itemSize) {
            canvas.drawArc(oval, start, itemSize - LINE_WIDTH, false, mDialPaint);
        }
    }

    /**
     * 根据已走步数，画圆盘进度
     * @param canvas
     * @param lightRange
     * @param oval
     * @param itemSize
     */
    private void drawLightDial(Canvas canvas, float lightRange, RectF oval, float itemSize) {
        mDialPaint.setColor(getResources().getColor(R.color.white));

        for (float start = 0; start < lightRange; start += itemSize) {
            canvas.drawArc(oval, start, itemSize - LINE_WIDTH, false, mDialPaint);
        }
    }

    /**
     * 画太阳(朝朝完成情况)、画月亮(暮暮完成标志)、星星(每天目标步数)
     * @param canvas
     * @param center
     */
    private void drawBitmap(Canvas canvas, int center) {
        if (mDayData != null) {
            int zzGoalNum = mDayData.getZzGoalNum() == 0 ? 3000 : mDayData.getZzGoalNum();
            if (mDayData.getZzStepCount() >= zzGoalNum) {
                canvas.drawBitmap(mZhaozhaoLight, center - mZhaozhaoLight.getWidth() * 2, mRectF.top + 20, mBitmapPaint);
            } else {
                canvas.drawBitmap(mZhaozhaoDefault, center - mZhaozhaoDefault.getWidth() * 2, mRectF.top + 20, mBitmapPaint);
            }
        }

        if (mDayData != null) {
            int mmGoalNum = mDayData.getMmGoalNum() == 0 ? 4000 : mDayData.getMmGoalNum();
            if (mDayData.getMmStepCount() >= mmGoalNum) {
                canvas.drawBitmap(mMumuLight, center - mMumuLight.getWidth() / 2, mRectF.top + 20, mBitmapPaint);
            } else {
                canvas.drawBitmap(mMumuDefault, center - mMumuDefault.getWidth() / 2, mRectF.top + 20, mBitmapPaint);
            }
        }

        if (mDayData != null) {
            int dayGoalNum = mDayData.getDayGoalNum() == 0 ? 10000 : mDayData.getDayGoalNum();
            if (mDayData.getDayStepCount() >= dayGoalNum) {
                canvas.drawBitmap(mStarLight, center + mStarDefault.getWidth(), mRectF.top + 20, mBitmapPaint);
            } else {
                canvas.drawBitmap(mStarDefault, center + mStarLight.getWidth(), mRectF.top + 20, mBitmapPaint);
            }
        }
    }

    /**
     * 画步数
     * @param canvas
     * @param center
     */
    private void drawStep(Canvas canvas, int center) {
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize_1 * mDensity);
        mTextPaint.getTextBounds(mDayStepCount, 0, mDayStepCount.length(), mRectTextBounds_1);
        canvas.drawText(mDayStepCount,
                center - mRectTextBounds_1.width() / 2 - 20,
                center + mRectTextBounds_1.height() / 2,
                mTextPaint);
    }

    /**
     * 画步数单位
     * @param canvas
     * @param center
     */
    private void drawStepUnit(Canvas canvas, int center) {
        mTextPaint.setColor(getResources().getColor(R.color.gray_2));
        mTextPaint.setTextSize(mTextSize_2 * mDensity);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.getTextBounds(mStepCountUnit, 0, mStepCountUnit.length(), mRectTextBounds_2);
        canvas.drawText(mStepCountUnit,
                center + mRectTextBounds_1.width() / 2 + mRectTextBounds_2.width() / 2 - 10,
                center + mRectTextBounds_1.height() / 2,
                mTextPaint);
    }

    /**
     * 画km/kcal
     * @param canvas
     * @param center
     */
    private void drawKmKcal(Canvas canvas, int center) {
        String kmValueStr = mKmValue + "";
        if ("0.0".contentEquals(kmValueStr)) kmValueStr = "0";
        String kcalValue = mKcalValue + "";
        if ("0.0".contentEquals(kcalValue)) kcalValue = "0";

        String kmKcal = kmValueStr + "km" + " / " + kcalValue + "kcal";
        mTextPaint.setColor(getResources().getColor(R.color.gray_2));
        mTextPaint.setTextSize(mTextSize_2 * mDensity);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mRectTextBounds_2.setEmpty();
        mTextPaint.getTextBounds(kmKcal, 0, kmKcal.length(), mRectTextBounds_2);
        canvas.drawText(kmKcal,
                center - mRectTextBounds_2.width() / 2,
                mRectF.bottom - mRectTextBounds_2.height() * 3 / 2,
                mTextPaint);
    }

    public void updateUI(DayData dayData) {
        if (dayData != null) {
            this.mDayStepCount = dayData.getDayStepCount() + "";
            this.mKmValue = dayData.getKm();
            this.mKcalValue = dayData.getKcal();
            this.mDayData = dayData;
        }
        invalidate();
    }

    public DayData getDayData() {
        return mDayData;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

}
