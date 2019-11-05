package com.szip.sleepee.Interface;

import com.szip.sleepee.Bean.HttpBean.ClockDataBean;

/**
 * Created by Administrator on 2019/7/4.
 */

public interface HttpCallbackWithClockData {
    void onClockData(ClockDataBean clockDataBean);
}
