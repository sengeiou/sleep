package com.szip.smartdream.Bean;

public class SleepTimeBean {
    private String deepTime;
    private String lightTime;
    private String midTime;
    private String awakeTime;
    private String allTime;

    public SleepTimeBean(int deep,int mid,int light,int awake){
        this.allTime = String.format("%dh%02dmin",(deep+mid+light+awake)/3600,(deep+mid+light+awake)/60%60);
        this.awakeTime = String.format("%dh%02dmin",awake/3600,awake/60%60);
        this.lightTime = String.format("%dh%02dmin",light/3600,light/60%60);
        this.midTime = String.format("%dh%02dmin",mid/3600,mid/60%60);
        this.deepTime = String.format("%dh%02dmin",deep/3600,deep/60%60);
    }

    public String getDeepTime() {
        return deepTime;
    }

    public String getLightTime() {
        return lightTime;
    }

    public String getMidTime() {
        return midTime;
    }

    public String getAwakeTime() {
        return awakeTime;
    }

    public String getAllTime() {
        return allTime;
    }
}
