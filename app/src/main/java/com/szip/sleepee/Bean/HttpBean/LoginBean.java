package com.szip.sleepee.Bean.HttpBean;

import com.szip.sleepee.Bean.UserInfo;

public class LoginBean extends BaseApi {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{
        private String token;
        private UserInfo userInfo;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public UserInfo getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
        }
    }
}
