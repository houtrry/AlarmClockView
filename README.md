# AlarmClockView

## 效果
![](https://raw.githubusercontent.com/houtrry/AlarmClockView/master/img/gif2.gif)


## 实现
1. 绘制部分很简单。难点在时间和位置的计算部分，比如，给定时间，计算起始结束点位置，或者滑动的时候，知道起始结束点位置，去计算当前的角度和时间。计算部分代码从AlarmClockView.java中分离出来，在ClockPointHelper.java中处理。具体可以参见代码。
2. 关于颜色渐变：使用SweepGradient来实现