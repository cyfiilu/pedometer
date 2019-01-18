package com.iilu.fendou.modules.sport.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.iilu.fendou.R;
import com.iilu.fendou.modules.sport.entity.DayData;

import org.apache.log4j.Logger;

public class SportView extends View {

    private final Logger mlog = Logger.getLogger(SportView.class);

    /** 旋转角度 */
    private final float ROTATE_DEGREE = 135f;
    /** 线条范围 */
    private final int LINT_RANGE = 270;
    /** 线条宽度 */
    private float STROKE_WIDTH = 3;

    /** 小圆弧里的%比*/
    private String mPercentText = "0%";

    /** 步数 */
    private String mDayStepCount = "0";
    /** 步数单位 */
    private String mStepCountUnit = "步";

    private float mVOffset = 13f;

    private float mTextSize_1 = 32f;
    private float mTextSize_2 = 16f;
    private float mTextSize_3 = 12f;

    private float mKmValue = 0.0f;
    private float mKcalValue = 0.0f;

    /** 画进度弧 */
    private Paint mProgressPaint = new Paint();
    /** 画文字的 */
    private Paint mTextPaint = new TextPaint();
    /** 外切正方形：画大圆弧用 */
    private RectF mOutsideRectF = new RectF();
    /** 内接正方形：画km、kcal用 */
    private RectF mInnerRectF = new RectF();
    /** 左边小正方形：画小圆弧用 */
    private RectF mLeftRectF = new RectF();
    /** 右边小正方形：画小圆弧用 */
    private RectF mRightRectF = new RectF();
    /** 步数边界 */
    private Rect mRectTextBounds_1 = new Rect();
    /** 步数单位边界 */
    private Rect mRectTextBounds_2 = new Rect();
    /** 每日进度百分比，所需path */
    private Path mPath = new Path();

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

        mVOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mVOffset, displayMetrics);

        mTextSize_1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize_1, displayMetrics);
        mTextSize_2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize_2, displayMetrics);
        mTextSize_3 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize_3, displayMetrics);
        STROKE_WIDTH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, STROKE_WIDTH, displayMetrics);

        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(STROKE_WIDTH);
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

        int center = getWidth() / 2;
        float radius = center - STROKE_WIDTH / 2;

        mOutsideRectF.setEmpty();
        mOutsideRectF.left = center - radius;
        mOutsideRectF.top = center - radius;
        mOutsideRectF.right = center + radius;
        mOutsideRectF.bottom = center + radius;

        // 画默认圆弧
        drawDefaultProgress(canvas, mOutsideRectF);

        // 画实际圆弧进度
        drawLightProgress(canvas, mOutsideRectF);

        // 获取内圆半径
        float innerRadius = radius - STROKE_WIDTH / 2;
        // 设置矩形左边位置  x * x + x * x = innerRadius(等腰直角三角形) --> x = √2 / 2 * innerRadius
        mInnerRectF.setEmpty();
        mInnerRectF.left = (float) (innerRadius - Math.sqrt(2) / 2 * innerRadius) + STROKE_WIDTH;
        mInnerRectF.top = (float) (innerRadius - Math.sqrt(2) / 2 * innerRadius) + STROKE_WIDTH;
        mInnerRectF.right = (float) (mInnerRectF.left + Math.sqrt(2) * innerRadius);
        mInnerRectF.bottom = (float) (mInnerRectF.top + Math.sqrt(2) * innerRadius);

        // 画左边小圆弧
        mLeftRectF.setEmpty();
        mLeftRectF.left = (float) (innerRadius - Math.sqrt(2) / 2 * innerRadius) + STROKE_WIDTH;
        mLeftRectF.top = (float) (innerRadius - Math.sqrt(2) / 2 * innerRadius) + STROKE_WIDTH;
        mLeftRectF.right = (float) (mInnerRectF.left + Math.sqrt(2) * innerRadius) / 2;
        mLeftRectF.bottom = (float) (mInnerRectF.top + Math.sqrt(2) * innerRadius) / 2;
        drawLeftTopDefault(canvas, mLeftRectF);
        drawLeftTopLight(canvas, mLeftRectF);

        // 画右边小圆弧
        mRightRectF.setEmpty();
        mRightRectF.right = mInnerRectF.right;
        mRightRectF.left = mRightRectF.right - (mLeftRectF.right - mLeftRectF.left);
        mRightRectF.top = (float) (innerRadius - Math.sqrt(2) / 2 * innerRadius) + STROKE_WIDTH;
        mRightRectF.bottom = (float) (mInnerRectF.top + Math.sqrt(2) * innerRadius) / 2;
        drawRightTopDefault(canvas, mRightRectF);
        drawRightTopLight(canvas, mRightRectF);

        // 画步数
        drawStep(canvas, center);

        // 画步数单位
        drawStepUnit(canvas, center);

        // 画km/kcal
        drawKmKcal(canvas, center);
    }

    /**
     * 画圆弧默认线条
     * @param canvas
     * @param oval
     */
    private void drawDefaultProgress(Canvas canvas, RectF oval) {
        mProgressPaint.setColor(getResources().getColor(R.color.blue_1));
        canvas.drawArc(oval, ROTATE_DEGREE, LINT_RANGE, false, mProgressPaint);
    }

    /**
     * 根据已走步数，画圆弧进度
     * @param canvas
     * @param oval
     */
    private void drawLightProgress(Canvas canvas, RectF oval) {
        mProgressPaint.setColor(getResources().getColor(R.color.white));

        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize_3);
        mTextPaint.setTypeface(Typeface.DEFAULT);

        mPath.reset();
        mPath.addArc(oval, ROTATE_DEGREE, 270f);

        float hOffset = 0f;
        float vOffset = mVOffset;

        if (mDayData != null) {
            int dayGoalNum = mDayData.getDayGoalNum() == 0 ? 10000 : mDayData.getDayGoalNum();
            float percent = mDayData.getDayStepCount() * 1.0f / dayGoalNum;
            float lightRange = percent * LINT_RANGE;
            lightRange = lightRange >= LINT_RANGE ? LINT_RANGE : lightRange;
            // 根据步数画圆盘进度线条
            //canvas.drawArc(oval, 0, lightRange, false, mProgressPaint);
            canvas.drawArc(oval, ROTATE_DEGREE, lightRange, false, mProgressPaint);

            percent = percent * 100;
            int percentInt = (int) percent;
            percentInt = percentInt >= 100 ? 100 : percentInt;
            mPercentText = percentInt + "%";

            int center = getWidth() / 2;
            float radius = center - STROKE_WIDTH / 2;

            float allHOffset = (float) (LINT_RANGE * Math.PI * radius / 180);
            hOffset = (float) (lightRange * Math.PI * radius / 180);

            mRectTextBounds_2.setEmpty();
            mTextPaint.getTextBounds(mPercentText, 0, mPercentText.length(), mRectTextBounds_2);

            if (hOffset > mRectTextBounds_2.width()) {
                hOffset = hOffset - mRectTextBounds_2.width() / 2f;
                if (hOffset + mRectTextBounds_2.width() >= allHOffset) {
                    hOffset = allHOffset - mRectTextBounds_2.width();
                }
            }
        }
        canvas.drawTextOnPath(mPercentText, mPath, hOffset, vOffset, mTextPaint);
    }

    /**
     * 画左边小圆弧默认线条
     * @param canvas
     * @param leftRectF
     */
    private void drawLeftTopDefault(Canvas canvas, RectF leftRectF) {
        // 画默认圆弧
        mProgressPaint.setColor(getResources().getColor(R.color.blue_1));
        canvas.drawArc(leftRectF, ROTATE_DEGREE, LINT_RANGE, false, mProgressPaint);

        // 画“目标一”文字
        float center = leftRectF.left + (leftRectF.right - leftRectF.left) / 2;
        String text = getResources().getString(R.string.sport_goal_1);
        mTextPaint.setColor(getResources().getColor(R.color.gray_2));
        mTextPaint.setTextSize(mTextSize_3);
        mRectTextBounds_2.setEmpty();
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.getTextBounds(text, 0, text.length(), mRectTextBounds_2);
        canvas.drawText(text,
                center - mRectTextBounds_2.width() / 2f,
                leftRectF.bottom,
                mTextPaint);
    }

    /**
     * 画右边小圆弧默认线条
     * @param canvas
     * @param rightRectF
     */
    private void drawRightTopDefault(Canvas canvas, RectF rightRectF) {
        mProgressPaint.setColor(getResources().getColor(R.color.blue_1));
        canvas.drawArc(rightRectF, ROTATE_DEGREE, LINT_RANGE, false, mProgressPaint);

        // 画“目标二”文字
        float center = rightRectF.left + (rightRectF.right - rightRectF.left) / 2;
        String text = getResources().getString(R.string.sport_goal_2);
        mTextPaint.setColor(getResources().getColor(R.color.gray_2));
        mTextPaint.setTextSize(mTextSize_3);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mRectTextBounds_2.setEmpty();
        mTextPaint.getTextBounds(text, 0, text.length(), mRectTextBounds_2);
        canvas.drawText(text,
                center - mRectTextBounds_2.width() / 2f,
                rightRectF.bottom,
                mTextPaint);
    }

    /**
     * 画左边小圆弧实际进度
     * @param canvas
     * @param leftRectF
     */
    private void drawLeftTopLight(Canvas canvas, RectF leftRectF) {
        mProgressPaint.setColor(getResources().getColor(R.color.white));
        if (mDayData != null) {
            int zzGoalNum = mDayData.getZzGoalNum() == 0 ? 3000 : mDayData.getZzGoalNum();
            float percent = mDayData.getZzStepCount() * 1.0f / zzGoalNum;
            float lightRange = percent * LINT_RANGE;
            lightRange = lightRange >= LINT_RANGE ? LINT_RANGE : lightRange;
            canvas.drawArc(leftRectF, ROTATE_DEGREE, lightRange, false, mProgressPaint);

            // 画左边%比进度
            percent = percent * 100;
            int percentInt = (int) percent;
            percentInt = percentInt >= 100 ? 100 : percentInt;
            mPercentText = percentInt + "%";
        }

        float center = leftRectF.left + (leftRectF.right - leftRectF.left) / 2;
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize_2);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mRectTextBounds_2.setEmpty();
        mTextPaint.getTextBounds(mPercentText, 0, mPercentText.length(), mRectTextBounds_2);
        canvas.drawText(mPercentText,
                center - mRectTextBounds_2.width() / 2f,
                leftRectF.bottom - (leftRectF.bottom - leftRectF.top) / 2 + 10,
                mTextPaint);
    }

    /**
     * 画右边小圆弧实际进度
     * @param canvas
     * @param rightRectF
     */
    private void drawRightTopLight(Canvas canvas, RectF rightRectF) {
        mProgressPaint.setColor(getResources().getColor(R.color.white));
        if (mDayData != null) {
            int mmGoalNum = mDayData.getMmGoalNum() == 0 ? 4000 : mDayData.getMmGoalNum();
            float percent = mDayData.getMmStepCount() * 1.0f / mmGoalNum;
            float lightRange = percent * LINT_RANGE;
            lightRange = lightRange >= LINT_RANGE ? LINT_RANGE : lightRange;
            canvas.drawArc(rightRectF, ROTATE_DEGREE, lightRange, false, mProgressPaint);

            // 画右边%比进度
            percent = percent * 100;
            int percentInt = (int) percent;
            percentInt = percentInt >= 100 ? 100 : percentInt;
            mPercentText = percentInt + "%";
        }

        float center = rightRectF.left + (rightRectF.right - rightRectF.left) / 2;
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize_2);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mRectTextBounds_2.setEmpty();
        mTextPaint.getTextBounds(mPercentText, 0, mPercentText.length(), mRectTextBounds_2);
        canvas.drawText(mPercentText,
                center - mRectTextBounds_2.width() / 2f,
                rightRectF.bottom - (rightRectF.bottom - rightRectF.top) / 2 + 10,
                mTextPaint);
    }

    /**
     * 画步数
     * @param canvas
     * @param center
     */
    private void drawStep(Canvas canvas, int center) {
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize_1);
        mTextPaint.getTextBounds(mDayStepCount, 0, mDayStepCount.length(), mRectTextBounds_1);
        canvas.drawText(mDayStepCount,
                center - mRectTextBounds_1.width() / 2 - 20,
                center + mRectTextBounds_1.height() / 2 + 20,
                mTextPaint);
    }

    /**
     * 画步数单位
     * @param canvas
     * @param center
     */
    private void drawStepUnit(Canvas canvas, int center) {
        mTextPaint.setColor(getResources().getColor(R.color.gray_2));
        mTextPaint.setTextSize(mTextSize_2);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mRectTextBounds_2.setEmpty();
        mTextPaint.getTextBounds(mStepCountUnit, 0, mStepCountUnit.length(), mRectTextBounds_2);
        canvas.drawText(mStepCountUnit,
                center + mRectTextBounds_1.width() / 2 + mRectTextBounds_2.width() / 2 - 10,
                center + mRectTextBounds_1.height() / 2 + 20,
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

        String kmKcal = String.format(getResources().getString(R.string.km_cal), kmValueStr, kcalValue);
        mTextPaint.setColor(getResources().getColor(R.color.gray_2));
        mTextPaint.setTextSize(mTextSize_2);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mRectTextBounds_2.setEmpty();
        mTextPaint.getTextBounds(kmKcal, 0, kmKcal.length(), mRectTextBounds_2);
        canvas.drawText(kmKcal,
                center - mRectTextBounds_2.width() / 2f,
                mInnerRectF.bottom - mRectTextBounds_2.height() * 3f / 2,
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
