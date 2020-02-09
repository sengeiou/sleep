package com.szip.smartdream.Bean;

public class HealthAdcDataBean {
    int[] heartDatas;
    int[] breathDatas;

    public HealthAdcDataBean(int[] heartDatas, int[] breathDatas) {
        this.heartDatas = heartDatas;
        this.breathDatas = breathDatas;
    }

    public int[] getHeartDatas() {
        return heartDatas;
    }

    public int[] getBreathDatas() {
        return breathDatas;
    }
}
