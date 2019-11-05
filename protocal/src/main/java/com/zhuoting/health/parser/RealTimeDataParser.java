package com.zhuoting.health.parser;

import com.zhuoting.health.ProtocolTag;
import com.zhuoting.health.bean.BloodInfo;
import com.zhuoting.health.bean.HeartInfo;
import com.zhuoting.health.bean.SleepInfo;
import com.zhuoting.health.bean.SportInfo;
import com.zhuoting.health.notify.IDataResponse;
import com.zhuoting.health.notify.IErrorCommand;
import com.zhuoting.health.notify.IRequestResponse;
import com.zhuoting.health.util.DataUtil;
import com.zhuoting.health.util.TransUtils;

import java.util.ArrayList;

/**
 * Created by Hqs on 2018/1/16
 */
public class RealTimeDataParser implements IProtocolParser {
    public IRequestResponse mIRequestResponse;
    public IErrorCommand mIErrorCommand;
    public IDataResponse mIDataResponse;
    private byte[] bytes;

    private RealTimeDataParser() {
    }

    private static RealTimeDataParser mRealTimeDataParser;

    public static RealTimeDataParser newInstance() {                     // 单例模式，双重锁
        if (mRealTimeDataParser == null) {
            synchronized (RealTimeDataParser.class) {
                if (mRealTimeDataParser == null) {
                    mRealTimeDataParser = new RealTimeDataParser();
                }
            }
        }
        return mRealTimeDataParser;
    }

    @Override
    public int findDataType(byte[] data) {
        return 0;
    }

    @Override
    public void parseData(byte[] data) {
        String dataStr = TransUtils.bytes2hex(data);
        String tagHead = dataStr.substring(0, 2);
        String tagStr = dataStr.substring(0, 4);
        // 做了些优化

    }

    /**
     * 是否为错误类型
     * 0xFB 到 0xFF
     * @param bytes
     * @return
     */
    public boolean isErrorType(byte[] bytes){
        if( bytes[4] < 0){
            return true;
        }
        return false;
    }
}
