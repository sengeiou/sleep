package com.szip.smartdream.Interface;

import com.szip.smartdream.Bean.HttpBean.ClockDataBean;

/**
 * Created by Administrator on 2019/7/4.
 */

public interface HttpCallbackWithClockData {
    void onClockData(ClockDataBean clockDataBean);
}
