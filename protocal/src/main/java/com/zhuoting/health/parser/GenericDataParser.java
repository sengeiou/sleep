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
 * 通用数据传输
 * UUID为 BE940001-7333-BE46-B7AE-689E71722BD5
 * 该UUID用于接收回复,用于控制、配置、信息查询等（包含了大多数的指令）
 * Created by Hqs on 2018/1/16
 */
public class GenericDataParser implements IProtocolParser {

    public IRequestResponse mIRequestResponse;
    public IErrorCommand mIErrorCommand;
    public IDataResponse mIDataResponse;
    private byte[] bytes;

    private GenericDataParser() {
    }

    private static GenericDataParser mGenericDataParser;

    public static GenericDataParser newInstance() {                     // 单例模式，双重锁
        if (mGenericDataParser == null) {
            synchronized (GenericDataParser.class) {
                if (mGenericDataParser == null) {
                    mGenericDataParser = new GenericDataParser();
                }
            }
        }
        return mGenericDataParser;
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
