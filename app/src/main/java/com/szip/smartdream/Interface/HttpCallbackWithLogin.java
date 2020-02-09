package com.szip.smartdream.Interface;

import com.szip.smartdream.Bean.HttpBean.LoginBean;

/**
 * Created by Administrator on 2019/7/4.
 */

public interface HttpCallbackWithLogin {
    void onLogin(LoginBean loginBean);
}
