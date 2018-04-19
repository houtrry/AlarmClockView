package com.houtrry.alarmclockview;

import android.graphics.PointF;
import android.util.Log;

/**
 * @author: houtrry
 * @date: 2018/4/18 16:46
 * @version: $Rev$
 * @description: ${TODO}
 */

public class ClockPointHelper {

    private static final String TAG = ClockPointHelper.class.getSimpleName();

    private float radius;

    //分钟数
    private int durationTime;
    private float startTime;
    private float endTime;

    private PointF startPoint;
    private PointF endPoint;

    private float startArc;
    private float endArc;

    private PointF centerPoint;
    private OnAlarmChangeListener onAlarmChangeListener;
    private float calculatePositionByTimeDegree;
    private float maxPointAreaRadius = 150;

    public ClockPointHelper() {
    }

    public ClockPointHelper(PointF centerPoint, float radius, float startTime, float endTime) {
        this.centerPoint = centerPoint;
        this.radius = radius;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ClockPointHelper(PointF centerPoint, float radius, float startTime, float endTime, OnAlarmChangeListener onAlarmChangeListener) {
        this.centerPoint = centerPoint;
        this.radius = radius;
        this.startTime = startTime;
        this.endTime = endTime;
        this.onAlarmChangeListener = onAlarmChangeListener;
    }

    public static void main(String... args) {
        double acos = Math.asin(-0.5d);
        float degree = (float) (acos * 180 / Math.PI);
        System.out.println("acos: " + acos + ", degree: " + degree);


    }

    public ClockPointHelper init() {
        startPoint = new PointF();
        endPoint = new PointF();
        return this;
    }

    /**
     * @param calculateTimeByPoint true: 根据位置计算时间；
     *                             false：根据时间计算位置；
     * @return
     */
    public ClockPointHelper calculate(boolean calculateTimeByPoint, boolean updateStartPoint, boolean updateEndPoint) {
        if (calculateTimeByPoint) {
            //根据位置计算时间
            calculateTimeByPoint(updateStartPoint, updateEndPoint);
        } else {
            //根据时间计算位置
            calculatePointByTime(updateStartPoint, updateEndPoint);
        }
        if (this.onAlarmChangeListener != null) {
            this.onAlarmChangeListener.onAlarmChange(startPoint, endPoint, getDurationTime());
        }
        return this;
    }

    /**
     * @param x
     * @param y
     * @param isUpdateStart true:更新start位置；false：更新end位置；
     * @return
     */
    public ClockPointHelper updatePosition(float x, float y, boolean isUpdateStart) {
        if (isUpdateStart) {
            startPoint.set(amendPoint(x, y));
        } else {
            endPoint.set(amendPoint(x, y));
        }
        calculate(true, isUpdateStart, !isUpdateStart);
        Log.d(TAG, "updatePosition: "+(calculateDistance(startPoint.x, startPoint.y, centerPoint))+", "+(calculateDistance(endPoint.x, endPoint.y, centerPoint))+", "+radius);
        return this;
    }

    /**
     * 修正位置， 给的x、y很有可能不是环上的点，修正一下，取环中心线上的点
     *
     * @param x
     * @param y
     * @return
     */
    private PointF amendPoint(float x, float y) {
        float amendX;
        float amendY;
        float distance = calculateDistance(x, y, centerPoint);
        if (distance != radius) {
            amendX = (x - centerPoint.x) * radius / distance + centerPoint.x;
            amendY = (y - centerPoint.y) * radius / distance + centerPoint.y;
            Log.d(TAG, "amendPoint: ("+x+", "+y+"), "+"("+amendX+", "+amendY+")");
        } else {
            amendX = x;
            amendY = y;
        }
        return new PointF(amendX, amendY);
    }

    public PointF getStartPoint() {
        return startPoint;
    }

    public PointF getEndPoint() {
        return endPoint;
    }

    public int getDurationTime() {
        return (int) (endTime - startTime + 0.5f);
    }

    public void setOnAlarmChangeListener(OnAlarmChangeListener onAlarmChangeListener) {
        this.onAlarmChangeListener = onAlarmChangeListener;
    }

    /**
     * 根据当前起始位置计算 当前起始时间
     */
    private void calculateTimeByPoint(boolean updateStartPoint, boolean updateEndPoint) {
        Log.d(TAG, "calculateTimeByPoint, start, startPoint: (" + startPoint.x + ", y: " + startPoint.y + "),  startArc: " + startArc + ", startTime: " + startTime);
        Log.d(TAG, "calculateTimeByPoint, start, endPoint: (" + endPoint.x + ", y: " + endPoint.y + "),  endArc: " + endArc + ", endTime: " + endTime);
        if (updateStartPoint) {
            startArc = calculateTimeByPoint(startPoint);
            startTime = startArc / 360 * 60 * 12;
        }
        if (updateEndPoint) {
            endArc = calculateTimeByPoint(endPoint);
            endTime = endArc / 360 * 60 * 12;
        }

        Log.d(TAG, "calculateTimeByPoint, end, startPoint: (" + startPoint.x + ", y: " + startPoint.y + "),  startArc: " + startArc + ", startTime: " + startTime);
        Log.d(TAG, "calculateTimeByPoint, end, endPoint: (" + endPoint.x + ", y: " + endPoint.y + "),  endArc: " + endArc + ", endTime: " + endTime);
    }

    /**
     * 根据当前起始时间计算当前的起始位置
     */
    private void calculatePointByTime(boolean updateStartPoint, boolean updateEndPoint) {
        if (updateStartPoint) {
            startArc = calculatePositionByTime(startPoint, startTime);
        }
        if (updateEndPoint) {
            endArc = calculatePositionByTime(endPoint, endTime);
        }
    }

    private float calculatePositionByTime(PointF point, float time) {
        time = time % (12 * 60);
        float degree = time / (12 * 60 * 1.0f) * 360;
        point.set((float) (centerPoint.x + radius * Math.sin(degree)), (float) (centerPoint.x - radius * Math.cos(degree)));
        return degree;
    }

    public float calculateTimeByPoint(PointF point) {
        float cos = (centerPoint.y - point.y) / calculateDistance(point, centerPoint);
        double acos = Math.acos(cos);
        float degree = (float) (acos * 180 / Math.PI);

        if (centerPoint.x > point.x) {
            degree += 180;
        }
        Log.d(TAG, "calculateTimeByPoint: degree: " + degree + ", point: (" + point.x + ", " + point.y + "), centerPoint: (" + centerPoint.x + ", " + centerPoint.y + "), radius: " + radius + ", cos: " + cos + ",  acos: " + acos);
        return degree;
    }

    public ClockPointHelper setRadius(float radius) {
        this.radius = radius;
        return this;
    }

    public ClockPointHelper setStartTime(float startTime) {
        this.startTime = startTime;
        return this;
    }

    public ClockPointHelper setEndTime(float endTime) {
        this.endTime = endTime;
        return this;
    }

    public ClockPointHelper setCenterPoint(PointF centerPoint) {
        this.centerPoint = centerPoint;
        return this;
    }

    public float getStartArc() {
        return startArc;
    }

    public float getEndArc() {
        return endArc;
    }

    public boolean isInPointArea(float x, float y) {
        boolean inStartPointArea = isInStartPointArea(x, y);
        boolean inEndPointArea = isInEndPointArea(x, y);
        Log.d(TAG, "isInPointArea: inStartPointArea: " + inStartPointArea + ", inEndPointArea: " + inEndPointArea);
        return inStartPointArea || inEndPointArea;
    }

    public boolean isInStartPointArea(float x, float y) {
        float distance = calculateDistance(x, y, startPoint);
        Log.d(TAG, "isInStartPointArea: distance: " + distance + ", maxPointAreaRadius: " + maxPointAreaRadius);
        return distance < maxPointAreaRadius;
    }

    public boolean isInEndPointArea(float x, float y) {
        float distance = calculateDistance(x, y, endPoint);
        Log.d(TAG, "isInEndPointArea: distance: " + distance + ", maxPointAreaRadius: " + maxPointAreaRadius);
        return distance < maxPointAreaRadius;
    }

    private float calculateDistance(float x, float y, PointF pointF) {
        float distanceX = pointF.x - x;
        float distanceY = pointF.y - y;
        double powx = Math.pow(distanceX, 2);
        double powy = Math.pow(distanceY, 2);
        float sqrt = (float) Math.sqrt(powx + powy);
        Log.d(TAG, "calculateDistance: x: (" + x + ", " + y + "), (" + pointF.x + ", " + pointF.y + "), distanceX: " + distanceX + ", distanceY: " + distanceY + ", powx: " + powx + ", powy: " + powy + ", sqrt: " + sqrt);
        return sqrt;
    }

    private float calculateDistance(PointF startPoint, PointF endPoint) {
        return (float) Math.sqrt(Math.pow(startPoint.x - endPoint.x, 2) + Math.pow(startPoint.y - endPoint.y, 2));
    }
}
