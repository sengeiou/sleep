package com.szip.sleepee.Interface;

import com.szip.sleepee.Bean.HttpBean.BaseApi;

/**
 * Created by Administrator on 2019/7/4.
 */

public interface HttpCallbackWithBase {
    void onCallback(BaseApi baseApi,int id);
}
