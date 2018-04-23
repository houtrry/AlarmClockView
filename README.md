# AlarmClockView

## 效果
![](https://raw.githubusercontent.com/houtrry/AlarmClockView/master/img/gif2.gif)

## 使用

```
compile 'com.houtrry.alarmclockview:AlarmClockView:1.0.0'
```


可以参考示例layout/activity_main.xml，如下
```
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:acv="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.houtrry.alarmclockviewsamples.MainActivity">

    <com.houtrry.alarmclockview.AlarmClockView
        android:id="@+id/alarmClockView"
        android:layout_width="200dp"
        android:layout_height="200dp"

        acv:slider_background_color="#1A2239"
        acv:slider_width="20dp"

        acv:dial_plate_background_color="#2E364D"
        acv:dial_plate_radius="70dp"

        acv:dial_line_color="@android:color/white"
        acv:dial_line_width="1dp"
        acv:dial_line_height="3dp"
        acv:dial_line_margin="3dp"

        acv:dial_text_color="@android:color/white"
        acv:dial_text_size="8sp"
        acv:dial_text_margin="2dp"

        acv:is_show_center_text="true"
        acv:center_time_gap_text_size="20sp"
        acv:center_time_gap_text_color="@android:color/white"

        acv:is_show_start_drawable="true"
        acv:start_drawable="@mipmap/ic_sleep"
        acv:start_drawable_background_color="#1A2239"
        acv:start_background_radius="9dp"

        acv:is_show_end_drawable="true"
        acv:end_drawable="@mipmap/ic_wakeup"
        acv:end_drawable_background_color="#1A2239"
        acv:end_background_radius="9dp"

        acv:max_point_area_radius="15dp"

        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"/>

</android.support.constraint.ConstraintLayout>
```

```
        AlarmClockView alarmClockView = (AlarmClockView) findViewById(R.id.alarmClockView);
        alarmClockView.setStartAndEndValue(30, 300);
        alarmClockView.setOnAlarmChangeListener(new OnAlarmChangeListener() {
            @Override
            public void onAlarmChange(int startTime, int endTime, int timeGap) {
                Log.d(TAG, "onAlarmChange: startTime: "+startTime+"=("+startTime/+60+":"+startTime%+60+"), "+endTime+"=("+endTime/+60+":"+endTime%+60+"), gap: "+timeGap);
            }
        });
```

关于各个属性的解释如下
```
        <!--外侧圆环的背景色 也就是可滑动区域的背景色-->
        <attr name="slider_background_color" format="color"/>
        <!--外侧圆环的宽度 也就是可滑动区域的宽度-->
        <attr name="slider_width" format="dimension"/>
        <!--中间刻度盘的背景色-->
        <attr name="dial_plate_background_color" format="color"/>
        <!--中间刻度盘的宽度-->
        <attr name="dial_plate_radius" format="dimension"/>

        <!--刻度线的颜色-->
        <attr name="dial_line_color" format="color"/>
        <!--刻度线的宽度-->
        <attr name="dial_line_width" format="dimension"/>
        <!--刻度线的长度-->
        <attr name="dial_line_height" format="dimension"/>
        <!--刻度线的刻度线的顶部跟外侧环的距离-->
        <attr name="dial_line_margin" format="dimension"/>

        <!--刻度线文字颜色-->
        <attr name="dial_text_color" format="color"/>
        <!--刻度线文字大小-->
        <attr name="dial_text_size" format="dimension"/>
        <!--刻度线文字跟刻度线的距离-->
        <attr name="dial_text_margin" format="dimension"/>


        <!--是否显示中间时间-->
        <attr name="is_show_center_text" format="boolean"/>
        <!--中间文字的大小-->
        <attr name="center_time_gap_text_size" format="dimension"/>
        <!--中间文字的颜色-->
        <attr name="center_time_gap_text_color" format="color"/>


        <!--是否显示start位置的图片-->
        <attr name="is_show_start_drawable" format="boolean"/>
        <!--start位置的图片-->
        <attr name="start_drawable" format="reference"/>
        <!--start位置的图片的背景色-->
        <attr name="start_drawable_background_color" format="color"/>
        <!--start位置的图片的背景圆的半径大小-->
        <attr name="start_background_radius" format="dimension"/>


        <!--是否显示end位置的图片-->
        <attr name="is_show_end_drawable" format="boolean"/>
        <!--start位置的图片-->
        <attr name="end_drawable" format="reference"/>
        <!--start位置的图片的背景色-->
        <attr name="end_drawable_background_color" format="color"/>
        <!--start位置的图片的背景圆的半径大小-->
        <attr name="end_background_radius" format="dimension"/>


        <!--起始点 终点 支持响应滑动的区域的半径-->
        <attr name="max_point_area_radius" format="dimension"/>

```


## 实现
1. 绘制部分很简单。难点在时间和位置的计算部分，比如，给定时间，计算起始结束点位置，或者滑动的时候，知道起始结束点位置，去计算当前的角度和时间。计算部分代码从AlarmClockView.java中分离出来，在ClockPointHelper.java中处理。具体可以参见代码。
2. 关于颜色渐变：使用SweepGradient来实现

