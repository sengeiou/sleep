package com.szip.sleepee.Bean;

public class HealthBean {
    private String heart;
    private String breath;

    public HealthBean(byte heart, byte breath) {

        this.heart = String.format("%02d",heart&0xff);
        this.breath = String.format("%02d",breath&0xff);
    }

    public String getHeart() {
        return heart;
    }

    public String getBreath() {
        return breath;
    }
}
