package com.houtrry.alarmclockviewsamples;

import android.graphics.PointF;

/**
 * @author: houtrry
 * @date: 2018/4/18 16:46
 * @version: $Rev$
 * @description: ${TODO}
 */

public class ClockPoint {

    private float degree;

    private PointF centerPointF;
    private float radius;

    private PointF currentPointF;

    //分钟数
    private int currentTime;


    public ClockPoint() {
    }

    public ClockPoint(PointF centerPointF, float radius) {
        this.centerPointF = centerPointF;
        this.radius = radius;
    }

    public float getDegree() {
        return degree;
    }

    public ClockPoint setDegree(float degree) {
        this.degree = degree;
        return this;
    }

    public PointF getCurrentPointF() {
        return currentPointF;
    }

    public ClockPoint setCurrentPointF(PointF currentPointF) {
        this.currentPointF = currentPointF;
        return this;
    }

    /**
     * @param calculateDegreeByPoint true: 根据位置计算角度；
     *                               false：根据角度计算位置；
     * @return
     */
    public ClockPoint calculate(boolean calculateDegreeByPoint) {

        if (calculateDegreeByPoint) {
            //根据位置计算角度
            degree = (float) (Math.asin((currentPointF.x - centerPointF.x) / radius) * 180 / Math.PI);
            if (degree < 0) {
                degree += 360;
            }
        } else {
            //根据角度计算位置
            currentPointF.set((float) (centerPointF.x + radius * Math.sin(degree * Math.PI / 180)), (float) (centerPointF.y - radius * Math.cos(degree * Math.PI / 180)));
        }

        return this;
    }

    public ClockPoint calculateTime() {
        currentTime = (int) (degree / 360 * 600 + 0.5);
        return this;
    }

    public static void main(String... args) {
        double acos = Math.asin(-0.5d);
        float degree = (float) (acos*180 / Math.PI);
        System.out.println("acos: "+acos+", degree: "+degree);
    }
}
