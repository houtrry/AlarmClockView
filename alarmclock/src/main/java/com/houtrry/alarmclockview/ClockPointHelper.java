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
    private float maxPointAreaRadius = 30;

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
        Log.d(TAG, "updatePosition: " + (calculateDistance(startPoint.x, startPoint.y, centerPoint)) + ", " + (calculateDistance(endPoint.x, endPoint.y, centerPoint)) + ", " + radius);
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
            Log.d(TAG, "amendPoint: (" + x + ", " + y + "), " + "(" + amendX + ", " + amendY + ")");
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

    private float preStartArc = 0;
    private float preEndArc = 0;
    private float currentArc = 0;

    /**
     * 根据当前起始位置计算 当前起始时间
     */
    private void calculateTimeByPoint(boolean updateStartPoint, boolean updateEndPoint) {
        if (updateStartPoint) {
            Log.d(TAG, "calculateTimeByPoint: (" + startPoint.x + ", " + startPoint.y + "), startArc: " + startArc + ", " + startTime);
            preStartArc = startArc;
            startArc = calculateTimeByPoint(startPoint);
            if (preStartArc > 350 && startArc < 10) {
                startArc = startArc + 360;
            } else if (preStartArc < 10 && startArc >350) {
                startArc = startArc - 360;
            }
            startTime = startArc / 360 * 60 * 12;
            Log.d(TAG, "calculateTimeByPoint: (" + startPoint.x + ", " + startPoint.y + "), startArc: " + startArc + ", " + startTime);
        }
        if (updateEndPoint) {
            preEndArc = endArc;
            endArc = calculateTimeByPoint(endPoint);
            if (preEndArc > 350 && endArc < 10) {
                endArc = endArc + 360;
            } else if (preEndArc < 10 && endArc >350) {
                endArc = endArc - 360;
            }
            endTime = endArc / 360 * 60 * 12;
        }
    }

    /**
     * 根据当前起始时间计算当前的起始位置
     */
    private void calculatePointByTime(boolean updateStartPoint, boolean updateEndPoint) {
        if (updateStartPoint) {
            Log.d(TAG, "calculatePointByTime: (" + startPoint.x + ", " + startPoint.y + ")");
            startArc = calculatePositionByTime(startPoint, startTime);
            Log.d(TAG, "calculatePointByTime: (" + startPoint.x + ", " + startPoint.y + ")");
        }
        if (updateEndPoint) {
            endArc = calculatePositionByTime(endPoint, endTime);
        }
        Log.d(TAG, "calculatePointByTime: startTime: " + startTime + ", startArc: " + startArc + ", endTime: " + endTime + ", endArc: " + endArc);
    }

    private float calculatePositionByTime(PointF point, float time) {
        time = time % (12 * 60);

        float degree = time / (12 * 60 * 1.0f) * 360;
        double radian = degree * Math.PI / 180;
        Log.d(TAG, "calculatePositionByTime: time: " + time + ", degree: " + degree);
        point.set((float) (centerPoint.x + radius * Math.sin(radian)), (float) (centerPoint.y - radius * Math.cos(radian)));
        return degree;
    }

    public float calculateTimeByPoint(PointF point) {
        float cos = (centerPoint.y - point.y) / calculateDistance(point, centerPoint);
        double acos = Math.acos(cos);
        float degree = (float) (acos * 180 / Math.PI);

        if (centerPoint.x > point.x) {
            degree = 360 - degree;
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
        return inStartPointArea || inEndPointArea;
    }

    public boolean isInStartPointArea(float x, float y) {
        float distance = calculateDistance(x, y, startPoint);
        return distance < maxPointAreaRadius;
    }

    public boolean isInEndPointArea(float x, float y) {
        float distance = calculateDistance(x, y, endPoint);
        return distance < maxPointAreaRadius;
    }

    private float calculateDistance(float x, float y, PointF pointF) {
        return (float) Math.sqrt(Math.pow(pointF.x - x, 2) + Math.pow(pointF.y - y, 2));
    }

    private float calculateDistance(PointF startPoint, PointF endPoint) {
        return (float) Math.sqrt(Math.pow(startPoint.x - endPoint.x, 2) + Math.pow(startPoint.y - endPoint.y, 2));
    }
}
