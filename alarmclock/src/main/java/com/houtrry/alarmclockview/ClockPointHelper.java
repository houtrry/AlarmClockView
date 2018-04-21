package com.houtrry.alarmclockview;

import android.graphics.PointF;
import android.support.annotation.NonNull;
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

    private float startTime;
    private float endTime;

    private int timeGap;

    private PointF startPoint;
    private PointF endPoint;

    private float startArc;
    private float endArc;

    private PointF centerPoint;
    private OnAlarmChangeListener onAlarmChangeListener;
    private float maxPointAreaRadius = 30;

    private int startCylinder = 0;
    private int endCylinder = 0;
    private PointF preStartPoint = null;
    private PointF preEndPoint = null;

    public ClockPointHelper() {
    }

    public static void main(String... args) {
        double acos = Math.asin(-0.5d);
        float degree = (float) (acos * 180 / Math.PI);
        System.out.println("acos: " + acos + ", degree: " + degree);


    }

    public ClockPointHelper init() {
        startPoint = new PointF();
        endPoint = new PointF();
        preStartPoint = new PointF();
        preEndPoint = new PointF();
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
        calculateTimeGap();
        if (this.onAlarmChangeListener != null) {
            this.onAlarmChangeListener.onAlarmChange(formatTime(startTime), formatTime(endTime), timeGap);
        }
        return this;
    }

    private int formatTime(float time) {
        time = time % (24 * 60);
        if (time < 0) {
            time += (24 * 60);
        }
        return (int) time;
    }

    /**
     * @param x
     * @param y
     * @param isUpdateStart true:更新start位置；false：更新end位置；
     * @return
     */
    public ClockPointHelper updatePosition(float x, float y, boolean isUpdateStart) {
        if (isUpdateStart) {
            preStartPoint.set(startPoint.x, startPoint.y);
            startPoint.set(amendPoint(x, y));
            Log.d(TAG, "calculateTimeByPoint: ===>>> 1");
            if (startPoint.y < centerPoint.y) {
                Log.d(TAG, "calculateTimeByPoint: ===>>> 2, (" + startPoint.x + ", " + startPoint.y + "), (" + preStartPoint.x + ", " + preStartPoint.y + ")");
                if (startPoint.x <= centerPoint.x && preStartPoint.x > centerPoint.x) {
                    Log.d(TAG, "calculateTimeByPoint: ===>>> 3");
                    Log.d(TAG, "calculateTimeByPoint: 天阿鲁，起始点， 又减了一圈");
                    startCylinder--;
                } else if (startPoint.x >= centerPoint.x && preStartPoint.x < centerPoint.x) {
                    Log.d(TAG, "calculateTimeByPoint: ===>>> 4");
                    Log.d(TAG, "calculateTimeByPoint: 天阿鲁，起始点， 又加了一圈");
                    startCylinder++;
                }
            }
        } else {
            preEndPoint.set(endPoint.x, endPoint.y);
            endPoint.set(amendPoint(x, y));

            if (endPoint.y < centerPoint.y) {
                if (endPoint.x <= centerPoint.x && preEndPoint.x > centerPoint.x) {
                    Log.d(TAG, "calculateTimeByPoint: 天阿鲁，结束点， 又减了一圈");
                    endCylinder--;
                } else if (endPoint.x >= centerPoint.x && preEndPoint.x < centerPoint.x) {
                    Log.d(TAG, "calculateTimeByPoint: 天阿鲁，结束点， 又加了一圈");
                    endCylinder++;
                }
            }
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

    public void setOnAlarmChangeListener(@NonNull OnAlarmChangeListener onAlarmChangeListener) {
        this.onAlarmChangeListener = onAlarmChangeListener;
    }

    /**
     * 根据当前起始位置计算 当前起始时间
     */
    private void calculateTimeByPoint(boolean updateStartPoint, boolean updateEndPoint) {
        if (updateStartPoint) {
            startArc = calculateTimeByPoint(startPoint) + startCylinder * 360;
            startTime = startArc % 720 / 360 * 60 * 12;
            Log.d(TAG, "===>>>calculateTimeByPoint: startArc: " + startArc + ", startTime: " + startTime + " = " + (int) startTime / 60 + ":" + (int) startTime % 60 + ", startCylinder: " + startCylinder);
        }
        if (updateEndPoint) {
            endArc = calculateTimeByPoint(endPoint) + endCylinder * 360;
            endTime = endArc % 720 / 360 * 60 * 12;
        }
    }

    private void calculateTimeGap() {

        timeGap = (int) (endTime - startTime) % (24 * 60);
        if (timeGap < 0) {
            timeGap += 24 * 60;
        }
        Log.d(TAG, "===>>>calculateTimeByPoint: startTime: " + startTime + ", endTime: " + endTime + ", timeGap: " + timeGap + " = (" + (timeGap / 60) + ":" + (timeGap % 60) + ")");
        Log.d(TAG, "calculateTimeByPoint: startArc: " + startArc + ", endArc: " + endArc + ", startCylinder: " + startCylinder + ", endCylinder: " + endCylinder);
    }

    /**
     * 根据当前起始时间计算当前的起始位置
     */
    private void calculatePointByTime(boolean updateStartPoint, boolean updateEndPoint) {
        if (updateStartPoint) {
            Log.d(TAG, "calculatePointByTime: (" + startPoint.x + ", " + startPoint.y + ")");
            startCylinder = (int) Math.floor(startTime / (12 * 60));
            startArc = calculatePositionByTime(startPoint, startTime);
            Log.d(TAG, "calculatePointByTime: (" + startPoint.x + ", " + startPoint.y + ")");
        }
        if (updateEndPoint) {
            endCylinder = (int) Math.floor(endTime / (12 * 60));
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

    public int getTimeGap() {
        return timeGap;
    }

    public String getTimeGapString() {
        return formatTimeString(timeGap / 60) + ": " + formatTimeString(timeGap % 60);
    }

    private String formatTimeString(int value) {
        if (value < 10) {
            return "0" + value;
        }
        return String.valueOf(value);
    }

    public ClockPointHelper setMaxPointAreaRadius(float maxPointAreaRadius) {
        this.maxPointAreaRadius = maxPointAreaRadius;
        return this;
    }

}
