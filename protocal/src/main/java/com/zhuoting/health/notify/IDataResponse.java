package com.zhuoting.health.notify;

import com.zhuoting.health.bean.BloodInfo;
import com.zhuoting.health.bean.DataWithSleepMBean;
import com.zhuoting.health.bean.HeartInfo;
import com.zhuoting.health.bean.SleepInfo;
import com.zhuoting.health.bean.SportInfo;
import com.zhuoting.health.bean.TurnOverListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hqs on 2018/1/12
 * 设备回传上来的信息
 */
public interface IDataResponse {


    void onGetDeviceVersion(String hardV,String softV);

    /**
     * 更新实时健康数据
     * @param heart           心率
     * @param breath          呼吸率
     */
    void onUpdateHealthData(byte heart,byte breath);

    /**
     * 获取实时adc数据
     * @param heartDatas           心率adc
     * @param breathDatas          呼吸率adc
     */
    void onDrawTheView(int[] heartDatas,int[] breathDatas);

    /**
     * 更新电量信息
     * @param power           电量
     * @param isPower          充电状态
     */
    void onUpdataPower(byte power,boolean isPower);

    /**
     *获取用户信息
     * @param gender        性别
     * @param age           年龄
     * @param stature       身高
     * @param weight        体重
     * */
    void onUpdataUserInfo(boolean gender,byte age,byte stature,byte weight);

    /**
     * 接收完睡眠数据
     * @param sleepDatas           睡眠数据
     */
    void onReadSleepData(byte[] sleepDatas);

    /**
     * 接收完心率/呼吸率数据
     * @param healthDatas           心率/呼吸率数据
     */
    void onReadHealthData(byte[] healthDatas);

    /**
     * 接收完翻身数据
     * @param turnOverDatas           翻身数据
     */
    void onReadTurnOverData(byte[] turnOverDatas);

    void onAddClock(byte index,byte flag);

    void onChangeClock(byte index,byte flag);

    void onCheckClock(byte[] clockData);

    void onWakeup();
}
