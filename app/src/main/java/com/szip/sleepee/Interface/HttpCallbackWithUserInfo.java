package com.szip.sleepee.Interface;

import com.szip.sleepee.Bean.HttpBean.UserInfoBean;

/**
 * Created by Administrator on 2019/7/4.
 */

public interface HttpCallbackWithUserInfo {
    void onUserInfo(UserInfoBean userInfoBean);
}
