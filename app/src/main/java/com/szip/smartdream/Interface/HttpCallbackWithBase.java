package com.szip.smartdream.Interface;

import com.szip.smartdream.Bean.HttpBean.BaseApi;

/**
 * Created by Administrator on 2019/7/4.
 */

public interface HttpCallbackWithBase {
    void onCallback(BaseApi baseApi,int id);
}
