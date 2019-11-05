package com.szip.sleepee.Interface;

import com.szip.sleepee.Bean.HttpBean.LoginBean;

/**
 * Created by Administrator on 2019/7/4.
 */

public interface HttpCallbackWithLogin {
    void onLogin(LoginBean loginBean);
}
