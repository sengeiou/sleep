package com.szip.sleepee.Bean;

public class DevicePowerBean {
    private String power;

    public DevicePowerBean(byte power, boolean isPower){
        int a = (0|power)&0xff;
        if (isPower){
            this.power = String.format("%d",a)+"%,正在充电";
        }else {
            this.power = String.format("%d",a)+"%";
        }

    }

    public String getPower() {
        return power;
    }
}
