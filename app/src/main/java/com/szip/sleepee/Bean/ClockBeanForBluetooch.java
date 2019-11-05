package com.szip.sleepee.Bean;

public class ClockBeanForBluetooch {
    private int index;
    private boolean isOK;
    private int wordType;

    public ClockBeanForBluetooch(byte index,byte flag,int wordType){
        this.index = index&0xff;
        this.isOK = flag == 0x01?true:false;
        this.wordType = wordType;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isOK() {
        return isOK;
    }

    public void setOK(boolean OK) {
        isOK = OK;
    }

    public int getWordType() {
        return wordType;
    }

    public void setWordType(int wordType) {
        this.wordType = wordType;
    }
}
