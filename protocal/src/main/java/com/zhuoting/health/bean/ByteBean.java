package com.zhuoting.health.bean;

/**
 * Description:
 * Author: lixd
 * Create Time: 2018/4/17 10:59
 */
public class ByteBean {
    byte[] data;

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
    public ByteBean(byte[] b){
        this.data = b;
    }
}
