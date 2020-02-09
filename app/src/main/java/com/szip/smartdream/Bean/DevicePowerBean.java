package com.szip.smartdream.Bean;

public class DevicePowerBean {
    private String power;

    public DevicePowerBean(byte power, boolean isPower){
        int a = (0|power)&0xff;
        if(a == 100 || !isPower){
            this.power = String.format("%d",a)+"%";
        }else if (isPower){
            this.power = "";
        }
    }

    public String getPower() {
        return power;
    }
}
