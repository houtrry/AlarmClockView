package com.houtrry.alarmclockview;

/**
 * @author: houtrry
 * date: 2018/4/19 10:11
 * @version: $Rev$
 * description:
 */

public interface OnAlarmChangeListener {
    void onAlarmChange(int startTime, int endTime, int timeGap);
}
