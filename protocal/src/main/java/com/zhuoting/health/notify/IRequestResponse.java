package com.zhuoting.health.notify;

/**
 * Created by Hqs on 2018/1/12
 * 该接口用于查看发送请求后，收到设备的响应，以此来判断是否设置成功和设置失败的原因
 */
public interface IRequestResponse {

    void onSetUserInfo(byte isSeccuss);

    void onDeleteOverTimeData(byte isSeccuss);
}
