package com.zhuoting.health.bean;

import android.support.annotation.NonNull;

/**
 * Created by Administrator on 2019/3/1.
 */

public class ByteData implements Comparable<ByteData>{
    byte[] data;

    public ByteData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public int compareTo(@NonNull ByteData o) {
        return this.data[0]-o.data[0];
    }
}
