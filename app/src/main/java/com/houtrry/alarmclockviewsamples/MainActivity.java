package com.houtrry.alarmclockviewsamples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.houtrry.alarmclockview.AlarmClockView;
import com.houtrry.alarmclockview.OnAlarmChangeListener;

/**
 * @author: houtrry
 * @date: ${DATE} ${TIME}
 * @version:
 * @description: ${TODO}
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AlarmClockView mAlarmClockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAlarmClockView = (AlarmClockView) findViewById(R.id.alarmClockView);
        mAlarmClockView.setStartAndEndValue(30, 300);
        mAlarmClockView.setOnAlarmChangeListener(new OnAlarmChangeListener() {
            @Override
            public void onAlarmChange(int startTime, int endTime, int timeGap) {
                Log.d(TAG, "onAlarmChange: startTime: "+startTime+"=("+startTime/+60+":"+startTime%+60+"), "+endTime+"=("+endTime/+60+":"+endTime%+60+"), gap: "+timeGap);
            }
        });
    }
}
