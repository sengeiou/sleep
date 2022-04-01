package com.zhuoting.health.parser;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.zhuoting.health.bean.ByteData;
import com.zhuoting.health.notify.IDataResponse;
import com.zhuoting.health.notify.IErrorCommand;
import com.zhuoting.health.notify.IRequestResponse;
import com.zhuoting.health.util.DataUtil;
import com.zhuoting.health.util.Tools;
import com.zhuoting.health.util.TransUtils;

import java.util.ArrayList;

/**
 * Created by Hqs on 2018/1/4
 */
public class DataParser {

    public IRequestResponse mIRequestResponse ;
    public IErrorCommand mIErrorCommand ;
    public IDataResponse mIDataResponse ;
    public IOperation mIOperation ;
    private int[] heartDatas = new int[1];
    private int[] breathDatas = new int[1];
    private int flag = 0;
    byte syncType ;                                          // 同步类型数据，2运动

    byte[] ravData = new byte[0];
    boolean isFront = false;

    private ArrayList<ByteData> dataList = new ArrayList<>();
    private ArrayList<ByteData> dataListHealth = new ArrayList<>();
    private ArrayList<ByteData> dataListTurn = new ArrayList<>();

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {

        }

    };

    public void setRequestResponseListener(IRequestResponse iRequestResponse){
        this.mIRequestResponse = iRequestResponse;
    }

    public void setDataResponseListener(IDataResponse iDataResponse){
        this.mIDataResponse = iDataResponse ;
    }

    public void setErrorCommandListener(IErrorCommand iErrorCommand){
        this.mIErrorCommand = iErrorCommand;
    }

    public void setOperation(IOperation iOperation){
        this.mIOperation = iOperation ;
    }

    private DataParser(){}
    private static DataParser mDataParser;
    public static DataParser newInstance(){                     // 单例模式，双重锁
        if( mDataParser == null ){
            synchronized (DataParser.class){
                if( mDataParser == null ){
                    mDataParser = new DataParser();
                }
            }
        }
        return mDataParser ;
    }

    private void setDatas(byte heart1,byte heart2,byte breath1,byte breath2){
        if (breathDatas!=null && heartDatas != null){
            heartDatas[0] = ((((0|heart2)<<8)|(heart1&0xff))&0xffff);
            breathDatas[0] = ((((0|breath2)<<8)|(breath1&0xff))&0xffff);
            mIDataResponse.onDrawTheView(heartDatas,breathDatas);
            Log.d("SZIP*******","心率数据="+heartDatas[flag]+"呼吸率数据 = "+breathDatas[flag]);
        }
    }

    boolean tppp = false;
    public void parseData(byte[] data){
        String dataStr = TransUtils.bytes2hex(data);
        String tagHead = dataStr.substring(0, 2);
        String tagStr = dataStr.substring(4, 6);
        // 做了些优化
        if (tagHead.equals("5A")){
            if (tagStr.equals("00")){//设备信息

                mIDataResponse.onGetDeviceVersion(String.format("%d.%d",data[11],data[12]),String.format("%d.%d",data[13],data[14]));

            }else if (tagStr.equals("01")){//绑定回复

            }else if (tagStr.equals("02")){//电量信息
                if (data[4]==0x01){
                    mIDataResponse.onUpdataPower(data[3],true);
                }else {
                    mIDataResponse.onUpdataPower(data[3],false);
                }
            }else if (tagStr.equals("03")){//时间设置

            }else if (tagStr.equals("04")){//设备重置

            }else if (tagStr.equals("06")){//闹钟设置
                if (data[3] == 0x01){//增加闹钟
                    mIDataResponse.onAddClock(data[4],data[5]);
                }else if (data[3] == 0x02){//删除闹钟
                }else if (data[3] == 0x03){//修改闹钟
                    mIDataResponse.onChangeClock(data[4],data[5]);
                }else if (data[3] == 0x04){//查询闹钟
                    if (data.length == 5){
                        mIDataResponse.onCheckClock(new byte[]{(byte) 0xff});
                    }else {
                        byte[] clocks = new byte[data.length-5];
                        System.arraycopy(data,4,clocks,0,data.length-5);
                        mIDataResponse.onCheckClock(clocks);
                    }
                }
            }else if (tagStr.equals("08")){//用户参数
                if (data[3] == 0x01){
                    mIRequestResponse.onSetUserInfo(data[4]);
                }else if (data[3] == 0x00){
                    if (data[4] == 0x01){
                        mIDataResponse.onUpdataUserInfo(true,data[5],data[6],data[7]);
                    }else if (data[4] == 0x00){
                        mIDataResponse.onUpdataUserInfo(false,data[5],data[6],data[7]);
                    }
                }
            }else if (tagStr.equals("09")){//目标参数

            }else if (tagStr.equals("50")){//呼吸率开关
                mIDataResponse.onUpdateHealthData(data[3],data[4]);
            }else if (tagStr.equals("51")){//ADC数据开关
                for (int i = 3;i<=15;i = i+4){
                    setDatas(data[i],data[i+1],data[i+2],data[i+3]);
                }
            }else if (tagStr.equals("52")){//读取睡眠质量数据
                if (data.length==4){//没数据
                    mIDataResponse.onReadSleepData(new byte[0]);
                }else {
                    if (data[3]==0){//未结束
                        if (data[4] == 0)
                            dataList = new ArrayList<>();
                        dataList.add(new ByteData(data));
                    }else {//数据接收结束,传给BleService格式化
                        if(data.length ==5){
                            mIDataResponse.onReadSleepData(new byte[0]);
                        }else {
                            if (data[4] == 0)
                                dataList = new ArrayList<>();
                            dataList.add(new ByteData(data));
                            byte[] datas = DataUtil.ArrayListTobytes(dataList);//把数据拼接成一个大数组，传给Service格式化
                            if (mIDataResponse!=null){
                                mIDataResponse.onReadSleepData(datas);
                            }
                        }
                    }
                }
            }else if (tagStr.equals("53")){//读取历史心率呼吸率数据
                if (data.length==4){
                    mIDataResponse.onReadHealthData(new byte[0]);
                }else {
                    if (data[3]==0){//未结束
                        if (data[4] == 0)
                            dataListHealth = new ArrayList<>();
                        dataListHealth.add(new ByteData(data));
                    }else {//数据接收结束,传给BleService格式化
                        if(data.length ==5){
                            mIDataResponse.onReadHealthData(new byte[0]);
                        }else {
                            if (data[4] == 0)
                                dataListHealth = new ArrayList<>();
                            dataListHealth.add(new ByteData(data));
                            byte[] datas = DataUtil.ArrayListTobytes(dataListHealth);//把数据拼接成一个大数组，传给Service格式化
                            if (mIDataResponse!=null){
                                mIDataResponse.onReadHealthData(datas);
                            }
                        }
                    }
                }
            }else if (tagStr.equals("54")){//历史翻身数据
                if (data.length==4){
                    mIDataResponse.onReadTurnOverData(new byte[0]);
                }else {
                    if (data[3]==0){//未结束
                        if (data[4] == 0)
                            dataListTurn = new ArrayList<>();
                        dataListTurn.add(new ByteData(data));
                    }else {//数据接收结束,传给BleService格式化
                        if(data.length ==5){
                            mIDataResponse.onReadTurnOverData(new byte[0]);
                        }else {
                            if (data[4] == 0)
                                dataListTurn = new ArrayList<>();
                            dataListTurn.add(new ByteData(data));
                            byte[] datas = DataUtil.ArrayListTobytes(dataListTurn);//把数据拼接成一个大数组，传给Service格式化
                            if (mIDataResponse!=null){
                                mIDataResponse.onReadTurnOverData(datas);
                            }
                        }
                    }
                }

            }else if (tagStr.equals("57")){//删除翻身数据
                if ((data[4]&0xff)==0x01){
                    mIRequestResponse.onDeleteOverTimeData((byte) 1);
                }
            }else if (tagStr.equals("60")){
                if (data[7]==0){//离床
                    mIDataResponse.onWakeup();
                }
            }else if (tagStr.equals("80")){

            }
        }

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




    public void sendMsg0() {
//        byte[] smsg = {0x05,0x00,0x01};
//        smsg = Tools.makeSend(smsg);
//        BleHandler.getInstance(getApplication()).sendMsg(smsg);
    }

    public void sendMsg1() { // 开始同步
//        byte[] smsg = {0x05, 0x02, 0x01};
//        smsg = Tools.makeSend(smsg);
        mIOperation.onDoSynchronizedHistorySport();
    }

    public void sendMsg2() {
//        byte[] smsg = {0x05, 0x04, 0x01};
//        smsg = Tools.makeSend(smsg);
        mIOperation.onDoSynchronizedHistorySleep();
    }

    public void sendMsg3() {
//        byte[] smsg = {0x05, 0x06, 0x01};
//        smsg = Tools.makeSend(smsg);
        mIOperation.onDoSynchronizedHistoryHeartRate();
    }

    public void sendMsg4() {
//        byte[] smsg = {0x05, 0x08, 0x01};
//        smsg = Tools.makeSend(smsg);
        mIOperation.onDoSynchronizedHistoryBloodPressure();
    }

    public void nextSend(boolean isdel) {
        if (syncType == 0x02) {
            if (isdel) {
                sendMsgDel(1);
            } else {
                mHandler.sendEmptyMessage(3);
            }

            Log.i("aa==", "运动完成");

        } else if (syncType == 0x04) {
            if (isdel) {
                sendMsgDel(2);
            } else {
                mHandler.sendEmptyMessage(4);
            }
            Log.i("aa==", "睡眠完成");
        } else if (syncType == 0x06) {
            if (isdel) {
                sendMsgDel(3);
            } else {
                mHandler.sendEmptyMessage(5);
            }

        } else {

//            if (MyApplication.isSyncing == true) {
//                sendMsgDel(4);
//            }
        }
    }


    /**
     *
     * @param index     1 表示   删除运动数据
     *                                 2 表示   删除睡眠数据
     *                                 3 表示   删除心率数据
     *                                 4 表示   删除血压数据
     */
    public void sendMsgDel(int index) {
        if (index == 1) {                                   // index 为 1 表示   删除运动数据
//            byte[] smsg = {0x05, 0x40, 0x02};
//            smsg = Tools.makeSend(smsg);
            mIOperation.onDeleteSport();
        } else if (index == 2) {                        // index 为 2 表示   删除睡眠数据
//            byte[] smsg = {0x05, 0x41, 0x02};
//            smsg = Tools.makeSend(smsg);
            mIOperation.onDeleteSleep();
        } else if (index == 3) {                        // index 为 3 表示   删除心率数据
//            byte[] smsg = {0x05, 0x42, 0x02};
//            smsg = Tools.makeSend(smsg);
            mIOperation.onDeleteHeartRate();
        } else if (index == 4) {                        // index 为 4 表示   删除血压数据
//            byte[] smsg = {0x05, 0x43, 0x02};
//            smsg = Tools.makeSend(smsg);
            mIOperation.onDeleteBloodPressure();
        }
    }

    //打开开关实时步数
    public void sendMsgOpen2() {  //  03,09,09,00,01,00,02,a0,de
        byte[] smsg = {0x03, 0x09, 0x01, 0x00, 0x02};
        smsg = Tools.makeSend(smsg);
    }


    public void initData() {
        ravData = new byte[0];
    }



    public void perGcdMsg(byte[] readData){
        //  后台执行：
//    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        // something
        Log.e("lxd",""+readData.length);
        int lenght = readData.length - 6;
        int headCount = 4;
        int allCount = lenght/3;
//        msgSize = 1000/allCount;
//        int makeS = 0;
        for (int i= 0; i<allCount ;i++) {
            int val = 0;
            int newHex = (readData[headCount + 2 + i * 3] & 0xff);
            String newHexStr = Integer.toBinaryString(newHex);
            int index = 8 - newHexStr.length();
            for (int x = 0; x < index; x++) {
                newHexStr = "0" + newHexStr;
            }
            System.out.println("xxxx=" + newHexStr);
            String erStr = "0";
            if (newHexStr.length() == 8) {
                erStr.substring(0, 1);
            }

            if (erStr.equals("1")) {
                byte[] bval = {(byte) 0xff, readData[headCount + i * 3 + 2], readData[headCount + i * 3 + 1], readData[headCount + i * 3]};
                val = TransUtils.Bytes2Dec(bval);
            } else {
                byte[] bval = {(byte) 0x00, readData[headCount + i * 3 + 2], readData[headCount + i * 3 + 1], readData[headCount + i * 3]};
                val = TransUtils.Bytes2Dec(bval);
            }

//            makeS = makeS + 1;

            int hex = 600;
//            int Ecg_val = MyApplication.getInstance().makeHeartVal(val);
//            Log.e("hr", allCount + ":" + Ecg_val + ":" + val);
//            //----------------------------------------------------------------------add by lixd
//            if(Ecg_val<=-500000){
//
//            }
//            int m = (int) (Ecg_val * 0.007D);
//            if(Math.abs(m)>300){
//                m= m>300?300:(m<-300?-300:m);
//            }
//            Log.e("hr", ""+m);
        }
    }


}
