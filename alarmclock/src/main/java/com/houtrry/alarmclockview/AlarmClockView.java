package com.houtrry.alarmclockview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author: houtrry
 * @date: 2018/4/18 14:06
 * @version: $Rev$
 * @description: ${TODO}
 */

public class AlarmClockView extends View {

    private static final String TAG = AlarmClockView.class.getSimpleName();

    private int mWidth;
    private int mHeight;

    private float mSliderWidth = 60;

    private float mDialPlateRadius = 200;

    /**
     * 每格刻度度数
     */
    private float mPreDegree = 0;

    private int mSliderBackgroundColor = Color.parseColor("#1A2239");

    private int mDialPlateBackgroundColor = Color.parseColor("#2E364D");


    private Paint mSliderBackgroundPaint = null;
    private Paint mDialPlateBackgroundPaint = null;

    private PointF mCenterPoint = new PointF();


    private int mDialLineColor = Color.parseColor("#ffffff");
    private int mDialLineWidth = 3;
    private int mDialLineHeight = 10;
    private int mDialLineMargin = 10;
    private Paint mDialLinePaint = null;

    private int mDialTextColor = Color.parseColor("#ffffff");
    private int mDialTextSize = 24;
    private int mDialTextMargin = 6;
    private Paint mDialTextPaint = null;

    private int[] mSliderColors = {Color.parseColor("#1AEBF7"), Color.parseColor("#F7F095")};
    private Paint mSliderPaint = null;

    private int[] mDialTexts = {12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};


    private float mStartDegree = 0;
    private float mEndDegree = 0;

    private PointF mStartPointF = new PointF();
    private PointF mEndPointF = new PointF();

    private Drawable mDayDrawable = null;
    private Drawable mNightDrawable = null;

    private float mDayDrawableWidth = 0;
    private float mNightDrawableWidth = 0;
    private float mDayDrawableHeight = 0;
    private float mNightDrawableHeight = 0;
    private float mDayDrawableBackgroundColor = Color.parseColor("#1E263D");
    private float mNightDrawableBackgroundColor = Color.parseColor("#1E263D");

    private ClockPointHelper mClockPointHelper = null;

    private Paint mDayNightPointPaint = null;
    private boolean mNeedHandleEvent = false;
    private RectF mSlideArcRect = new RectF();

    public AlarmClockView(Context context) {
        this(context, null);
    }

    public AlarmClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlarmClockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        initData();
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {

    }

    private void initData() {
        mPreDegree = 360f / mDialTexts.length;

        mClockPointHelper = new ClockPointHelper();
        mClockPointHelper.init().setStartTime(30)
                .setEndTime(270)
                .setRadius(mDialPlateRadius + mSliderWidth * 0.5f);
    }

    private void initPaint() {
        mSliderBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSliderBackgroundPaint.setColor(mSliderBackgroundColor);
        mSliderBackgroundPaint.setStyle(Paint.Style.STROKE);
        mSliderBackgroundPaint.setStrokeWidth(mSliderWidth + 2);

        mDialPlateBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDialPlateBackgroundPaint.setColor(mDialPlateBackgroundColor);
        mDialPlateBackgroundPaint.setStyle(Paint.Style.FILL);

        mDialLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDialLinePaint.setColor(mDialLineColor);
        mDialLinePaint.setStyle(Paint.Style.STROKE);
        mDialLinePaint.setStrokeWidth(mDialLineWidth);

        mDialTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDialTextPaint.setColor(mDialTextColor);
        mDialTextPaint.setTextSize(mDialTextSize);

        mSliderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSliderPaint.setStyle(Paint.Style.STROKE);
        mSliderPaint.setStrokeCap(Paint.Cap.ROUND);
        mSliderPaint.setStrokeWidth(mSliderWidth + 2);

        mDayNightPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDayNightPointPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenterPoint.set(mWidth * 0.5f, mHeight * 0.5f);
        mSliderPaint.setShader(new SweepGradient(mCenterPoint.x, mCenterPoint.y, mSliderColors, null));
        mClockPointHelper.setCenterPoint(mCenterPoint);
        mClockPointHelper.calculate(false, true, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mNeedHandleEvent = mClockPointHelper.isInPointArea(x, y);
                Log.d(TAG, "onTouchEvent: x: "+x+", y: "+y+", ");
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mClockPointHelper.updatePosition(x, y, mClockPointHelper.isInStartPointArea(x, y));
                ViewCompat.postInvalidateOnAnimation(this);
                break;
            }
            case MotionEvent.ACTION_UP: {

                break;
            }
            default:
                break;
        }
        return mNeedHandleEvent;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);
        drawCurrentTime(canvas);
        drawDialPlate(canvas);
        drawSlider(canvas);
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mDialPlateRadius, mDialPlateBackgroundPaint);
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mDialPlateRadius + mSliderWidth * 0.5f, mSliderBackgroundPaint);
    }

    /**
     * 绘制中心的当前选择时间
     *
     * @param canvas
     */
    private void drawCurrentTime(Canvas canvas) {

    }

    /**
     * 绘制刻度盘
     *
     * @param canvas
     */
    private void drawDialPlate(Canvas canvas) {
        canvas.save();
        String text = "";
        Rect mDialTextRect = new Rect();
        for (int i = 0; i < mDialTexts.length; i++) {
            canvas.drawLine(mCenterPoint.x, mCenterPoint.y - mDialPlateRadius + mDialLineMargin, mCenterPoint.x, mCenterPoint.y - mDialPlateRadius + mDialLineMargin + mDialLineHeight, mDialLinePaint);

            text = String.valueOf(mDialTexts[i]);

            mDialTextPaint.getTextBounds(text, 0, text.length(), mDialTextRect);

            canvas.drawText(text, mCenterPoint.x - mDialTextPaint.measureText(text) * 0.5f, mCenterPoint.y - mDialPlateRadius + mDialLineMargin + mDialLineHeight + mDialTextMargin + mDialTextRect.height(), mDialTextPaint);

            Log.d(TAG, "drawDialPlate: text : " + text + ", x: " + (mCenterPoint.x - mDialTextPaint.measureText(text) * 0.5f) + ", y: " + (mCenterPoint.y - mDialPlateRadius + mDialLineMargin + mDialLineHeight + mDialTextMargin + mDialTextRect.height()));

            canvas.rotate(mPreDegree, mCenterPoint.x, mCenterPoint.y);
        }
        canvas.restore();
    }

    /**
     * 绘制可以滑动的部分
     *
     * @param canvas
     */
    private void drawSlider(Canvas canvas) {
        mSlideArcRect.set(mCenterPoint.x - mDialPlateRadius - mSliderWidth * 0.5f, mCenterPoint.y - mDialPlateRadius - mSliderWidth * 0.5f,
                mCenterPoint.x + mDialPlateRadius + mSliderWidth * 0.5f, mCenterPoint.y + mDialPlateRadius + mSliderWidth * 0.5f);

        float startArc = mClockPointHelper.getStartArc();
        float endArc = mClockPointHelper.getEndArc();
        Log.d(TAG, "drawSlider: startArc: " + startArc + ", endArc: " + endArc);
        canvas.drawArc(mSlideArcRect, startArc, endArc-startArc, false, mSliderPaint);
    }

}
