package com.szip.sleepee.Bean;

public class UserInfoWriteBean {
    private boolean isOk;

    public UserInfoWriteBean(boolean isOk){
        this.isOk = isOk;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }
}
