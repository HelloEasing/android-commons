package com.easing.commons.android.value.time;

import com.easing.commons.android.time.Times;
import com.easing.commons.android.helper.exception.BizException;
import com.easing.commons.android.struct.Collections;

import java.util.List;

import lombok.Data;

@Data
public class TimeWindow {

    private String startTime = "00:00";
    private String endTime = "23:59";

    public static TimeWindow build(String startTime, String endTime) {
        TimeWindow timeWindow = new TimeWindow();
        timeWindow.startTime = startTime;
        timeWindow.endTime = endTime;
        if (!Times.isHMTime(startTime))
            throw BizException.of("invalid time window");
        if (!Times.isHMTime(endTime))
            throw BizException.of("invalid time window");
        if (endTime.compareTo(startTime) <= 0)
            throw BizException.of("invalid time window");
        return timeWindow;
    }

    public static void sort(List<TimeWindow> timeWindowList) {
        Collections.sort(timeWindowList, (L, R) -> L.startTime.compareTo(R.startTime));
        for (int i = 1; i < timeWindowList.size(); i++) {
            TimeWindow currentItem = timeWindowList.get(i);
            TimeWindow lastItem = timeWindowList.get(i);
            if (currentItem.startTime.compareTo(lastItem.endTime) <= 0)
                throw BizException.of("time window overlaps");
        }
    }

}
