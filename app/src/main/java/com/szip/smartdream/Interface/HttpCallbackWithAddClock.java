package com.szip.smartdream.Interface;

import com.szip.smartdream.Bean.HttpBean.AddClockBean;

/**
 * Created by Administrator on 2019/7/4.
 */

public interface HttpCallbackWithAddClock {
    void onAddClock(AddClockBean addClockBean,int id);
}
