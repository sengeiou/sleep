package com.szip.sleepee.Interface;

import com.szip.sleepee.Bean.HttpBean.ReportDataBean;

/**
 * Created by Administrator on 2019/7/4.
 */

public interface HttpCallbackWithReport {
    void onReport(boolean isNewData);
}
