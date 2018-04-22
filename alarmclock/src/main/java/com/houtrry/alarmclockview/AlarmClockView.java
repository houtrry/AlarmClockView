package com.houtrry.alarmclockview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author: houtrry
 * date: 2018/4/18 14:06
 * @version: $Rev$
 * description: 自定义闹钟的时间选择控件。计算部分的逻辑都放到ClockPointHelper.java来处理。
 * 在AlarmClockView.java里面，我们只关心绘制。
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
    private float mDialLineWidth = 3;
    private float mDialLineHeight = 10;
    private float mDialLineMargin = 10;
    private Paint mDialLinePaint = null;

    private int mDialTextColor = Color.parseColor("#ffffff");
    private float mDialTextSize = 24;
    private float mDialTextMargin = 6;
    private Paint mDialTextPaint = null;

    private int[] mSliderColors = {Color.parseColor("#1AEBF7"), Color.parseColor("#F7F095"), Color.parseColor("#1AEBF7")};
    private float[] mSliderColorPositions = {0.0f, 0.5f, 1.0f};
    private Paint mSliderPaint = null;

    private int[] mDialTexts = {12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};


    private boolean isShowStartDrawable = true;
    private boolean isShowEndDrawable = true;
    private BitmapDrawable mStartDrawable = null;
    private BitmapDrawable mEndDrawable = null;
    private float mStartBackgroundRadius = 0;
    private float mEndBackgroundRadius = 0;

    private int mStartDrawableBackgroundColor = Color.parseColor("#1E263D");
    private int mEndDrawableBackgroundColor = Color.parseColor("#1E263D");

    private Paint mDrawableBackgroundPaint = null;


    private Paint mCenterTimeGapPaint = null;
    private int mCenterTimeGapTextColor = Color.WHITE;
    private float mCenterTimeGapTextSize = 60;
    private boolean isShowCenterText = true;

    private float mMaxPointAreaRadius = 30;
    private ClockPointHelper mClockPointHelper = null;

    private Paint mDayNightPointPaint = null;
    private boolean mNeedHandleEvent = false;
    private RectF mSlideArcRect = new RectF();
    private boolean mInStartPointArea = false;
    private boolean mInEndPointArea = false;
    private PointF mTempPoint = null;
    private String mGapTimeString = "";
    private Rect mTextRect = new Rect();

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
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AlarmClockView);
        mSliderBackgroundColor = typedArray.getColor(R.styleable.AlarmClockView_slider_background_color, Color.parseColor("#1A2239"));
        mSliderWidth = typedArray.getDimension(R.styleable.AlarmClockView_slider_width, 60);
        mDialPlateBackgroundColor = typedArray.getColor(R.styleable.AlarmClockView_dial_plate_background_color, Color.parseColor("#2E364D"));
        mDialPlateRadius = typedArray.getDimension(R.styleable.AlarmClockView_dial_plate_radius, 200);

        mDialLineColor = typedArray.getColor(R.styleable.AlarmClockView_dial_line_color, Color.parseColor("#ffffff"));
        mDialLineWidth = typedArray.getDimension(R.styleable.AlarmClockView_dial_line_width, 3);
        mDialLineHeight = typedArray.getDimension(R.styleable.AlarmClockView_dial_line_height, 10);
        mDialLineMargin = typedArray.getDimension(R.styleable.AlarmClockView_dial_line_margin, 10);

        mDialTextColor = typedArray.getColor(R.styleable.AlarmClockView_dial_text_color, Color.parseColor("#ffffff"));
        mDialTextSize = typedArray.getDimension(R.styleable.AlarmClockView_dial_text_size, 24);
        mDialTextMargin = typedArray.getDimension(R.styleable.AlarmClockView_dial_text_margin, 6);

        isShowCenterText = typedArray.getBoolean(R.styleable.AlarmClockView_is_show_center_text, true);
        mCenterTimeGapTextColor = typedArray.getColor(R.styleable.AlarmClockView_center_time_gap_text_color, Color.parseColor("#ffffff"));
        mCenterTimeGapTextSize = typedArray.getDimension(R.styleable.AlarmClockView_center_time_gap_text_size, 60);

        isShowStartDrawable = typedArray.getBoolean(R.styleable.AlarmClockView_is_show_start_drawable, true);
        mStartDrawable = (BitmapDrawable) typedArray.getDrawable(R.styleable.AlarmClockView_start_drawable);
        mStartDrawableBackgroundColor = typedArray.getColor(R.styleable.AlarmClockView_start_drawable_background_color, Color.parseColor("#ffffff"));
        mStartBackgroundRadius = typedArray.getDimension(R.styleable.AlarmClockView_start_background_radius, 0);

        isShowEndDrawable = typedArray.getBoolean(R.styleable.AlarmClockView_is_show_end_drawable, true);
        mEndDrawable = (BitmapDrawable) typedArray.getDrawable(R.styleable.AlarmClockView_end_drawable);
        mEndDrawableBackgroundColor = typedArray.getColor(R.styleable.AlarmClockView_end_drawable_background_color, Color.parseColor("#ffffff"));
        mEndBackgroundRadius = typedArray.getDimension(R.styleable.AlarmClockView_end_background_radius, 0);

        mMaxPointAreaRadius = typedArray.getDimension(R.styleable.AlarmClockView_max_point_area_radius, 30);
        typedArray.recycle();
    }

    private void initData() {
        mPreDegree = 360f / mDialTexts.length;

        mClockPointHelper = new ClockPointHelper();
        mClockPointHelper.init()
                .setRadius(mDialPlateRadius + mSliderWidth * 0.5f)
                .setMaxPointAreaRadius(mMaxPointAreaRadius);
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
        mSliderPaint.setStrokeWidth(mSliderWidth + 4);

        mDayNightPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDayNightPointPaint.setStyle(Paint.Style.FILL);

        mCenterTimeGapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterTimeGapPaint.setTextSize(mCenterTimeGapTextSize);
        mCenterTimeGapPaint.setColor(mCenterTimeGapTextColor);

        mDrawableBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDrawableBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = (int) Math.ceil(getPaddingLeft() + mDialPlateRadius * 2 + mSliderWidth * 2 + getPaddingRight());
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = (int) Math.ceil(getPaddingTop() + mDialPlateRadius * 2 + mSliderWidth * 2 + getPaddingBottom());
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenterPoint.set(mWidth * 0.5f, mHeight * 0.5f);
        mSliderPaint.setShader(new SweepGradient(mCenterPoint.x, mCenterPoint.y, mSliderColors, mSliderColorPositions));
        mClockPointHelper.setCenterPoint(mCenterPoint);
        mClockPointHelper.calculate(false, true, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mInStartPointArea = mClockPointHelper.isInStartPointArea(x, y);
                mInEndPointArea = mClockPointHelper.isInEndPointArea(x, y);
                mNeedHandleEvent = mInStartPointArea || mInEndPointArea;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mClockPointHelper.updatePosition(x, y, mInStartPointArea);
//                ViewCompat.postInvalidateOnAnimation(this);
                postInvalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                break;
            }
            default:
                break;
        }
        return mNeedHandleEvent;
        //如果不调用super.onTouchEvent(event)， 那么，这个View就不支持onClick 和 onLongClick
//        return super.onTouchEvent(event) || mNeedHandleEvent;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawDialPlate(canvas);
        drawSlider(canvas);
        drawStartAndEndDrawable(canvas);
        drawCenterGapTime(canvas);
    }

    /**
     * 绘制背景
     *
     * @param canvas 画布
     */
    private void drawBackground(Canvas canvas) {
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mDialPlateRadius, mDialPlateBackgroundPaint);
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mDialPlateRadius + mSliderWidth * 0.5f, mSliderBackgroundPaint);
    }

    /**
     * 绘制刻度盘
     *
     * @param canvas 画布
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
            canvas.rotate(mPreDegree, mCenterPoint.x, mCenterPoint.y);
        }
        canvas.restore();
    }

    /**
     * 绘制可以滑动的部分
     *
     * @param canvas 画布
     */
    private void drawSlider(Canvas canvas) {
        mSlideArcRect.set(mCenterPoint.x - mDialPlateRadius - mSliderWidth * 0.5f, mCenterPoint.y - mDialPlateRadius - mSliderWidth * 0.5f,
                mCenterPoint.x + mDialPlateRadius + mSliderWidth * 0.5f, mCenterPoint.y + mDialPlateRadius + mSliderWidth * 0.5f);
        canvas.drawArc(mSlideArcRect, mClockPointHelper.getStartArc() - 90, (mClockPointHelper.getEndArc() - mClockPointHelper.getStartArc()) % 360, false, mSliderPaint);
    }

    private void drawStartAndEndDrawable(Canvas canvas) {
        if (isShowStartDrawable) {
            mTempPoint = mClockPointHelper.getStartPoint();

            if (mStartBackgroundRadius > 0) {
                mDrawableBackgroundPaint.setColor(mStartDrawableBackgroundColor);
                canvas.drawCircle(mTempPoint.x, mTempPoint.y, mStartBackgroundRadius, mDrawableBackgroundPaint);
            }
            if (mStartDrawable != null) {
                mStartDrawable.setBounds((int) (mTempPoint.x - mStartDrawable.getIntrinsicWidth() * 0.5f), (int) (mTempPoint.y - mStartDrawable.getIntrinsicHeight() * 0.5f),
                        (int) (mTempPoint.x + mStartDrawable.getIntrinsicWidth() * 0.5f), (int) (mTempPoint.y + mStartDrawable.getIntrinsicHeight() * 0.5f));
                mStartDrawable.draw(canvas);
            }
        }
        if (isShowEndDrawable) {
            mTempPoint = mClockPointHelper.getEndPoint();
            if (mEndBackgroundRadius > 0) {
                mDrawableBackgroundPaint.setColor(mEndDrawableBackgroundColor);
                canvas.drawCircle(mTempPoint.x, mTempPoint.y, mEndBackgroundRadius, mDrawableBackgroundPaint);
            }
            if (mEndDrawable != null) {
                mEndDrawable.setBounds((int) (mTempPoint.x - mEndDrawable.getIntrinsicWidth() * 0.5f), (int) (mTempPoint.y - mEndDrawable.getIntrinsicHeight() * 0.5f),
                        (int) (mTempPoint.x + mEndDrawable.getIntrinsicWidth() * 0.5f), (int) (mTempPoint.y + mEndDrawable.getIntrinsicHeight() * 0.5f));
                mEndDrawable.draw(canvas);
            }
        }
    }

    private void drawCenterGapTime(Canvas canvas) {
        if (!isShowCenterText) {
            return;
        }
        mGapTimeString = mClockPointHelper.getTimeGapString();
        if (TextUtils.isEmpty(mGapTimeString)) {
            return;
        }
        mCenterTimeGapPaint.getTextBounds(mGapTimeString, 0, mGapTimeString.length(), mTextRect);
        canvas.drawText(mGapTimeString, mCenterPoint.x - mCenterTimeGapPaint.measureText(mGapTimeString) * 0.5f, mCenterPoint.y + mTextRect.height() * 0.5f, mCenterTimeGapPaint);
    }

    public void setOnAlarmChangeListener(@NonNull OnAlarmChangeListener onAlarmChangeListener) {
        if (mClockPointHelper != null) {
            mClockPointHelper.setOnAlarmChangeListener(onAlarmChangeListener);
        }
    }

    public void setStartAndEndValue(float startTime, float endTime) {
        if (mClockPointHelper != null) {
            mClockPointHelper.setStartTime(startTime)
                    .setEndTime(endTime);
            mClockPointHelper.calculate(false, true, true);
//            ViewCompat.postInvalidateOnAnimation(this);
            postInvalidate();
        }
    }
}
