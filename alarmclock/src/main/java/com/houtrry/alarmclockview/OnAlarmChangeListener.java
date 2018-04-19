package com.houtrry.alarmclockview;

import android.graphics.PointF;

/**
 * @author: houtrry
 * @date: 2018/4/19 10:11
 * @version: $Rev$
 * @description: ${TODO}
 */

public interface OnAlarmChangeListener {
    void onAlarmChange(PointF startPoint, PointF endPoint, int durationTime);
}
