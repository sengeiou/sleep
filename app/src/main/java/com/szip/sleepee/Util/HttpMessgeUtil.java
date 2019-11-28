package com.szip.sleepee.Util;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.szip.sleepee.Bean.HttpBean.AddClockBean;
import com.szip.sleepee.Bean.HttpBean.BaseApi;
import com.szip.sleepee.Bean.HttpBean.ClockDataBean;
import com.szip.sleepee.Bean.HttpBean.LoginBean;
import com.szip.sleepee.Bean.HttpBean.RegisterBean;
import com.szip.sleepee.Bean.HttpBean.ReportDataBean;
import com.szip.sleepee.Bean.HttpBean.UpdataBean;
import com.szip.sleepee.Bean.HttpBean.UserInfoBean;
import com.szip.sleepee.Bean.UserInfo;
import com.szip.sleepee.Controller.LoginActivity;
import com.szip.sleepee.Controller.UserInfoActivity;
import com.szip.sleepee.DB.SaveDataUtil;
import com.szip.sleepee.Interface.HttpCallbackWithAddClock;
import com.szip.sleepee.Interface.HttpCallbackWithBase;
import com.szip.sleepee.Interface.HttpCallbackWithClockData;
import com.szip.sleepee.Interface.HttpCallbackWithLogin;
import com.szip.sleepee.Interface.HttpCallbackWithRegist;
import com.szip.sleepee.Interface.HttpCallbackWithReport;
import com.szip.sleepee.Interface.HttpCallbackWithUpdata;
import com.szip.sleepee.Interface.HttpCallbackWithUserInfo;
import com.szip.sleepee.Model.ProgressHudModel;
import com.szip.sleepee.MyApplication;
import com.szip.sleepee.R;
import com.szip.sleepee.Service.BleService;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.IOException;

import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;

public class HttpMessgeUtil {

    private String FILE = "sleepEE";
    private static HttpMessgeUtil mInstance;
//    private String url = "https://cloud.znsdkj.com:8443/sleep/";
    private String url = "https://test.znsdkj.com:8443/sleep/";
    private String token = "null";
    private String language = "zh-CN";
    private String time;

    private Context mContext;

    private HttpCallbackWithBase httpCallbackWithBase;
    private HttpCallbackWithRegist httpCallbackWithRegist;
    private HttpCallbackWithLogin httpCallbackWithLogin;
    private HttpCallbackWithUpdata httpCallbackWithUpdata;
    private HttpCallbackWithReport httpCallbackWithReport;
    private HttpCallbackWithUserInfo httpCallbackWithUserInfo;
    private HttpCallbackWithAddClock httpCallbackWithAddClock;
    private HttpCallbackWithClockData httpCallbackWithClockData;

    public static int REGIST_FLAG = 100;
    public static int GETVERIFICATION_FLAG = 101;
    public static int LOGIN_FLAG = 102;
    public static int FOTGET_FLAG = 103;
    public static int FEEDBACK_FLAG = 104;
    public static int SOFT_FLAG = 105;
    public static int GETINFO_FLAG = 106;
    public static int SETINFO_FLAG = 107;
    public static int BINDMAIL_FLAG = 108;
    public static int UNBINDMAIL_FLAG = 109;
    public static int BINDPHONE_FLAG = 110;
    public static int UNBINDPHONE_FLAG = 111;
    public static int CHANGEPASSWORD_FLAG = 112;
    public static int BINDDEVICE_FLAG = 113;
    public static int UNBINDDEVICE_FLAG = 114;
    public static int UPDOWNDATA_FLAG = 115;
    public static int DOWNLOADDATA_FLAG = 116;
    public static int ADDALARM_FLAG = 117;
    public static int DELETEALARM_FLAG = 118;
    public static int CHANGEALARM_FLAG = 119;
    public static int GETALARM_FLAG = 120;


    public static HttpMessgeUtil getInstance(Context context)
    {
        if (mInstance == null)
        {
            synchronized (HttpMessgeUtil.class)
            {
                if (mInstance == null)
                {
                    mInstance = new HttpMessgeUtil(context);
                }
            }
        }
        return mInstance;
    }

    private HttpMessgeUtil(Context context){
        mContext = context;
        if (context.getResources().getConfiguration().locale.getCountry().equals("CN")){
            language = "zh-CN";
        }else{
            language = "en-US";
        }
        time = DateUtil.getGMTWithString();
    }

    public void setToken(String token){
        this.token = token;
    }

    public void setHttpCallbackWithBase(HttpCallbackWithBase httpCallbackWithBase) {
        this.httpCallbackWithBase = httpCallbackWithBase;
    }

    public void setHttpCallbackWithRegist(HttpCallbackWithRegist httpCallbackWithRegist) {
        this.httpCallbackWithRegist = httpCallbackWithRegist;
    }

    public void setHttpCallbackWithLogin(HttpCallbackWithLogin httpCallbackWithLogin) {
        this.httpCallbackWithLogin = httpCallbackWithLogin;
    }

    public void setHttpCallbackWithUpdata(HttpCallbackWithUpdata httpCallbackWithUpdata) {
        this.httpCallbackWithUpdata = httpCallbackWithUpdata;
    }

    public void setHttpCallbackWithReport(HttpCallbackWithReport httpCallbackWithReport) {
        this.httpCallbackWithReport = httpCallbackWithReport;
    }

    public void setHttpCallbackWithUserInfo(HttpCallbackWithUserInfo httpCallbackWithUserInfo) {
        this.httpCallbackWithUserInfo = httpCallbackWithUserInfo;
    }

    public void setHttpCallbackWithAddClock(HttpCallbackWithAddClock httpCallbackWithAddClock) {
        this.httpCallbackWithAddClock = httpCallbackWithAddClock;
    }

    public void setHttpCallbackWithClockData(HttpCallbackWithClockData httpCallbackWithClockData) {
        this.httpCallbackWithClockData = httpCallbackWithClockData;
    }

    /**
     * 注册接口
     * @param type                   注册类型1：手机注册 2：邮箱注册
     * @param areaCode               区号
     * @param phoneNumber            手机
     * @param email                  邮箱
     * @param verifyCode             验证码
     * @param password               密码
     * */
    private void _postRegister(String type,String areaCode,String phoneNumber,String email,String verifyCode,String password,
                               int id) throws IOException {
        String url = this.url+"user/signUp";
        OkHttpUtils
                .jpost()
                .url(url)
                .id(id)
                .addHeader("Accept-Language",language)
                .addHeader("Time-Diff",time)
                .addParams("type",type)
                .addParams("areaCode",areaCode)
                .addParams("phoneNumber",phoneNumber)
                .addParams("email",email)
                .addParams("verifyCode",verifyCode)
                .addParams("password",password)
                .build()
                .execute(registerBeanGenericsCallback);
    }

    /**
     * 获取验证码接口
     * @param type                  验证码类型1：手机 2：邮箱
     * @param areaCode              区号
     * @param phoneNumber           手机号码
     * @param email                 邮箱
     * @param id                    回调标识
     * */
        private void _getVerificationCode(String type,String areaCode,String phoneNumber,String email,int id)throws IOException{
        final String url = this.url+"user/sendVerifyCode";
        OkHttpUtils
                .jpost()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("Accept-Language",language)
                .addParams("type",type)
                .addParams("areaCode",areaCode)
                .addParams("phoneNumber",phoneNumber)
                .addParams("email",email)
                .build()
                .execute(baseApiGenericsCallback);
    }


    /**
     * 登录接口
     * @param type                   注册类型1：手机注册 2：邮箱注册
     * @param areaCode               区号
     * @param phoneNumber            手机
     * @param email                  邮箱
     * @param password               密码
     * */
    private void _postLogin(String type,String areaCode, String phoneNumber, String email, String password, int id)throws IOException{
        String url = this.url+"user/login";
        OkHttpUtils
                .jpost()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("Accept-Language",language)
                .addParams("project","sleep")
                .addParams("type",type)
                .addParams("areaCode",areaCode)
                .addParams("phoneNumber",phoneNumber)
                .addParams("email",email)
                .addParams("password",password)
                .build()
                .execute(loginBeanGenericsCallback);
    }

    /**
     * 忘记密码接口
     * @param type               找回类型1：手机  2：邮箱
     * @param areaCode           区号
     * @param phoneNumber        手机号码
     * @param email              邮箱
     * @param verifyCode         验证码
     * @param password           密码
     * @param id                 回调标识
     * */
    private void _postForgotPassword(String type,String areaCode,String phoneNumber,String email,String verifyCode,
                                     String password,int id)throws IOException{
        String url = this.url+"user/retrievePassword";
        OkHttpUtils
                .jpost()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("Accept-Language",language)
                .addParams("type",type)
                .addParams("areaCode",areaCode)
                .addParams("phoneNumber",phoneNumber)
                .addParams("email",email)
                .addParams("verifyCode",verifyCode)
                .addParams("password",password)
                .build()
                .execute(baseApiGenericsCallback);
    }



    /**
     * 发送意见反馈接口
     * */
    private void _postSendFeedback(String content,int id)throws IOException{
        String url = this.url+"comm/feedback";
        OkHttpUtils
                .fpost()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("content",content)
                .build()
                .execute(baseApiGenericsCallback);
    }

    /**
     * 获取固件升级
     * @param hardwareVersion   硬件版本
     * @param id                回调标识
     * */
    private void _getForUpdata(String hardwareVersion,int id)throws IOException{
        String url = this.url+"comm/upgrade";
        OkHttpUtils
                .get()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("versionNumber",hardwareVersion)
                .build()
                .execute(updataBeanGenericsCallback);
    }


    /**
     * 获取个人信息
     * @param id         回调标识
     * */
    private void _getForGetInfo(int id)throws IOException{
        String url = this.url+"user/getUserInfo";
        OkHttpUtils
                .get()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .build()
                .execute(userInfoBeanGenericsCallback);
    }


    /**
     * 设置用户信息
     * @param name             名字
     * @param sex              性别
     * @param birthday         生日
     * @param height           身高
     * @param weight           体重
     * @param id               回调标识
     * */
    private void _postForSetUserInfo(String name,String sex,String birthday,String height,String weight,String units,int id)throws IOException{
        String url = this.url+"user/updateUserInfo";
        OkHttpUtils
                .jpost()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("userName",name)
                .addParams("lastName","")
                .addParams("firstName","")
                .addParams("sex",sex)
                .addParams("birthday",birthday)
                .addParams("nation","")
                .addParams("height",height)
                .addParams("weight",weight)
                .addParams("blood","")
                .addParams("units",units)
                .build()
                .execute(baseApiGenericsCallback);
    }

    /**
     * 绑定邮箱
     * @param email                 邮箱
     * @param verifyCode            验证码
     * */
    private void _postForBindEmail(String email,String verifyCode,int id)throws IOException{
        String url = this.url+"user/bindEmail";
        OkHttpUtils
                .jpost()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("email",email)
                .addParams("verifyCode",verifyCode)
                .build()
                .execute(baseApiGenericsCallback);
    }

    /**
     * 解绑邮箱
     * */
    private void _getForUnbindEmail(int id)throws IOException{
        String url = this.url+"user/unbindEmail";
        OkHttpUtils
                .get()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .build()
                .execute(baseApiGenericsCallback);
    }

    /**
     * 绑定手机
     * @param phoneNumber           手机号码
     * @param verifyCode            验证码
     * */
    private void _postForBindPhone(String areaCode,String phoneNumber,String verifyCode,int id)throws IOException{
        String url = this.url+"user/bindPhoneNumber";
        OkHttpUtils
                .jpost()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("areaCode",areaCode)
                .addParams("phoneNumber",phoneNumber)
                .addParams("verifyCode",verifyCode)
                .build()
                .execute(baseApiGenericsCallback);
    }

    /**
     * 解绑手机
     * */
    private void _getForUnbindPhone(int id)throws IOException{
        String url = this.url+"user/unbindPhoneNumber";
        OkHttpUtils
                .get()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .build()
                .execute(baseApiGenericsCallback);
    }

    /**
     * 修改密码
     * @param currentPassword      旧密码
     * @param newPassword          新密码
     * @param id                   回调标识
     * */
    private void _postForChangePassword(String currentPassword,String newPassword,int id)throws IOException{
        String url = this.url+"user/changePassword";
        OkHttpUtils
                .jpost()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("currentPassword",currentPassword)
                .addParams("newPassword",newPassword)
                .build()
                .execute(baseApiGenericsCallback);

    }

    /**
     * 绑定设备
     * */
    private void _getBindDevice(String deviceCode,int id)throws IOException{
        String url = this.url+"device/bindDevice";
        OkHttpUtils
                .get()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("deviceCode",deviceCode)
                .build()
                .execute(baseApiGenericsCallback);
    }

    /**
     * 解绑设备
     * */
    private void _getUnbindDevice(int id)throws IOException{
        String url = this.url+"device/unbindDevice";
        OkHttpUtils
                .get()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .build()
                .execute(baseApiGenericsCallback);
    }

    /**
     * 上传数据到服务器
     * @param list             数据
     * @param id               回调标识
     * */
    private void _postForUpdownReportData(String list,int id)throws IOException{
        String url = this.url+"device/uploadSleepData";
        OkHttpUtils
                .listpost()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("data",list)
                .build()
                .execute(baseApiGenericsCallback);
    }

    /**
     * 获取服务器上的数据
     * */
    private void _getForDownloadReportData(String time, String size, int id)throws IOException{
        String url = this.url+"device/getSleepData";
        OkHttpUtils
                .get()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("time",time)
                .addParams("size",size)
                .build()
                .execute(reportDataBeanGenericsCallback);
    }


    /**
     * 修改闹钟
     * */
    private void _postForChangeClock(String clockId,String type,String hour,String minute,String index,String isPhone,
                                     String isOn,String repeat,String music,String isIntelligentWake,String remark,int id)throws IOException{
        String url = this.url+"alarmClock/edit";
        OkHttpUtils
                .jpost()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("id",clockId)
                .addParams("type",type)
                .addParams("hour",hour)
                .addParams("minute",minute)
                .addParams("index",index)
                .addParams("isPhone",isPhone)
                .addParams("isOn",isOn)
                .addParams("repeat",repeat)
                .addParams("music",music)
                .addParams("isIntelligentWake",isIntelligentWake)
                .addParams("remark",remark)
                .build()
                .execute(addClockBeanGenericsCallback);
    }

    /**
     * 删除闹钟
     * */
    private void _getForDeleteClock(String clockId,GenericsCallback<BaseApi> callback,int id)throws IOException{
        String url = this.url+"alarmClock/delete";
        OkHttpUtils
                .get()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("id",clockId)
                .build()
                .execute(callback);
    }

    /**
     * 添加闹钟
     * */
    private void _postForAddClock(String type,String hour,String minute,String index,String isPhone,
                                  String isOn,String repeat,String music,String isIntelligentWake,String remark,int id)throws IOException{
        String url = this.url+"alarmClock/add";
        OkHttpUtils
                .jpost()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .addParams("type",type)
                .addParams("hour",hour)
                .addParams("minute",minute)
                .addParams("index",index)
                .addParams("isPhone",isPhone)
                .addParams("isOn",isOn)
                .addParams("repeat",repeat)
                .addParams("music",music)
                .addParams("isIntelligentWake",isIntelligentWake)
                .addParams("remark",remark)
                .build()
                .execute(addClockBeanGenericsCallback);
    }

    /**
     * 获取闹钟
     * */
    private void _getForGetClockList(int id)throws IOException{
        String url = this.url+"alarmClock/getList";
        OkHttpUtils
                .get()
                .url(url)
                .id(id)
                .addHeader("Time-Diff",time)
                .addHeader("token",token)
                .addHeader("Accept-Language",language)
                .build()
                .execute(clockDataBeanGenericsCallback);
    }



    /**
     * 提供给用户的方法
     * */
    public void postRegister(String type,String areaCode,String phoneNumber,String email,String verifyCode,String password,
                             int id) throws IOException{
        _postRegister(type,areaCode,phoneNumber,email,verifyCode,password,id);
    }

    public void getVerificationCode(String type,String areaCode,String phoneNumber,String email,int id)throws IOException{

        _getVerificationCode(type,areaCode,phoneNumber,email,id);
    }

    public void postLogin(String type, String areaCode,String phoneNumber, String email, String password, int id)throws IOException{
        _postLogin(type,areaCode,phoneNumber,email,password,id);
    }

    public void postForgotPassword(String type,String areaCode,String phoneNumber,String email,String verifyCode,
                                   String password,int id)throws IOException{
        _postForgotPassword(type,areaCode,phoneNumber,email,verifyCode,password,id);
    }

    public void getForUpdata(String hardwareVersion, int id)throws IOException{
        _getForUpdata(hardwareVersion,id);
    }

    public void postForSetUserInfo(String name,String sex,String birthday,String height,String weight,String units,int id)throws IOException{
        _postForSetUserInfo(name,sex,birthday,height,weight,units,id);
    }

    public void getForGetInfo(int id)throws IOException{
        _getForGetInfo(id);
    }

    public void postForBindEmail(String email,String verifyCode,int id)throws IOException{
        _postForBindEmail(email,verifyCode,id);
    }

    public void getForUnbindEmail(int id)throws IOException{
        _getForUnbindEmail(id);
    }

    public void postForBindPhone(String areaCode,String phoneNumber,String verifyCode,int id)throws IOException{
        _postForBindPhone(areaCode,phoneNumber,verifyCode,id);
    }

    public void getForUnbindPhone(int id)throws IOException{
        _getForUnbindPhone(id);
    }

    public void postForChangePassword(String currentPassword,String newPassword,int id)throws IOException{
        _postForChangePassword(currentPassword,newPassword,id);
    }

    public void getBindDevice(String deviceCode,int id)throws IOException{
        _getBindDevice(deviceCode,id);
    }

    public void getUnbindDevice(int id)throws IOException{
        _getUnbindDevice(id);
    }

    public void postForUpdownReportData(String list,int id)throws IOException{
        _postForUpdownReportData(list,id);
    }

    public void getForDownloadReportData(String time, String size, int id)throws IOException{
        _getForDownloadReportData(time,size,id);
    }

    public void postForChangeClock(String clockId,String type,String hour,String minute,String index,String isPhone,
                                   String isOn,String repeat,String music,String isIntelligentWake,String remark,int id)throws IOException{
        _postForChangeClock(clockId,type,hour,minute,index,isPhone,isOn,repeat,music,isIntelligentWake,remark,id);
    }

    public void getForDeleteClock(String clockId,GenericsCallback<BaseApi> callback,int id)throws IOException{
        _getForDeleteClock(clockId,callback,id);
    }

    public void postForAddClock(String type,String hour,String minute,String index,String isPhone,
                                String isOn,String repeat,String music,String isIntelligentWake,String remark,int id)throws IOException{
        _postForAddClock(type,hour,minute,index,isPhone,isOn,repeat,music,isIntelligentWake,remark,id);
    }

    public void getForGetClockList(int id)throws IOException{
        _getForGetClockList(id);
    }



    public void postSendFeedback(String content,int id)throws IOException{
        _postSendFeedback(content,id);
    }

    /**
     * 接口回调
     * */
    private GenericsCallback<BaseApi> baseApiGenericsCallback = new GenericsCallback<BaseApi>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {
        }

        @Override
        public void onResponse(BaseApi response, int id) {
            if (response.getCode() == 200){
                if (id == UPDOWNDATA_FLAG){
                    SharedPreferences sharedPreferences =  mContext.getSharedPreferences(FILE,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("lastLoadTime",MathUitl.getLastTimeFromData());
                    editor.commit();
                }
                if (httpCallbackWithBase != null)
                    httpCallbackWithBase.onCallback(response,id);
            }else if (response.getCode() == 401){
                tokenTimeOut();
            }else {
                ProgressHudModel.newInstance().diss();
                MathUitl.showToast(mContext,response.getMessage());
            }
        }
    };

    private GenericsCallback<RegisterBean> registerBeanGenericsCallback = new GenericsCallback<RegisterBean>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(RegisterBean response, int id) {
            if (response.getCode() == 200){
                if (httpCallbackWithRegist!=null)
                    httpCallbackWithRegist.onRegist(response);
            }else {
                ProgressHudModel.newInstance().diss();
                MathUitl.showToast(mContext,response.getMessage());
            }
        }
    };

    private GenericsCallback<LoginBean> loginBeanGenericsCallback = new GenericsCallback<LoginBean>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(LoginBean response, int id) {
            if (response.getCode() == 200){
                if (httpCallbackWithLogin!=null)
                    httpCallbackWithLogin.onLogin(response);
            }else {
                ProgressHudModel.newInstance().diss();
                MathUitl.showToast(mContext,response.getMessage());
            }
        }
    };

    private GenericsCallback<UpdataBean> updataBeanGenericsCallback = new GenericsCallback<UpdataBean>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(UpdataBean response, int id) {
            if (response.getCode() == 200){
                if (httpCallbackWithUpdata!=null)
                    httpCallbackWithUpdata.onUpdata(response);
            }else if (response.getCode() == 401){
                tokenTimeOut();
            }else {
                ProgressHudModel.newInstance().diss();
                MathUitl.showToast(mContext,response.getMessage());
            }
        }
    };

    private GenericsCallback<UserInfoBean> userInfoBeanGenericsCallback = new GenericsCallback<UserInfoBean>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(UserInfoBean response, int id) {
            if (response.getCode() == 200){
                if (httpCallbackWithUserInfo!=null)
                    httpCallbackWithUserInfo.onUserInfo(response);
            }else if (response.getCode() == 401){
                tokenTimeOut();
            }else {
                ProgressHudModel.newInstance().diss();
                MathUitl.showToast(mContext,response.getMessage());
            }
        }
    };

    private GenericsCallback<ReportDataBean> reportDataBeanGenericsCallback = new GenericsCallback<ReportDataBean>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {
        }

        @Override
        public void onResponse(ReportDataBean response, int id) {
            if (response.getCode() == 200){
                if (response.getData().getList().size()!=0){
                    if (httpCallbackWithReport!=null)
                        httpCallbackWithReport.onReport(true);
                    MathUitl.saveDataFromHttp(response.getData().getList(),mContext);
                }else {
                    if (httpCallbackWithReport!=null)
                        httpCallbackWithReport.onReport(false);
                }
            }else if (response.getCode() == 401){
                tokenTimeOut();
            }else {
                ProgressHudModel.newInstance().diss();
                MathUitl.showToast(mContext,response.getMessage());
            }
        }
    };

    private GenericsCallback<AddClockBean> addClockBeanGenericsCallback = new GenericsCallback<AddClockBean>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(AddClockBean response, int id) {
            if (id == -1){
                ProgressHudModel.newInstance().diss();
            }else {
                if (response.getCode() == 200){
                    if (httpCallbackWithAddClock!=null)
                        httpCallbackWithAddClock.onAddClock(response,id);
                }else if (response.getCode() == 401){
                    tokenTimeOut();
                }else {
                    ProgressHudModel.newInstance().diss();
                    MathUitl.showToast(mContext,response.getMessage());
                }
            }
        }
    };

    private GenericsCallback<ClockDataBean> clockDataBeanGenericsCallback = new GenericsCallback<ClockDataBean>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(ClockDataBean response, int id) {
            if (response.getCode() == 200){
                if (httpCallbackWithClockData!=null)
                    httpCallbackWithClockData.onClockData(response);
            }else if (response.getCode() == 401){
                tokenTimeOut();
            }else {
                ProgressHudModel.newInstance().diss();
                MathUitl.showToast(mContext,response.getMessage());
            }
        }
    };

    private void tokenTimeOut(){
        SharedPreferences sharedPreferences ;
        ProgressHudModel.newInstance().diss();
        BleService.getInstance().disConnect();
        BleService.getInstance().setmMac(null);
        ((MyApplication)(mContext.getApplicationContext())).setUserInfo(null);
        ((MyApplication)(mContext.getApplicationContext())).clearClockList();
        ((MyApplication)(mContext.getApplicationContext())).setReportDate(DateUtil.getStringToDate("today"));
        SaveDataUtil.newInstance(mContext).clearDB();
        sharedPreferences = mContext.getSharedPreferences(FILE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogin",false);
        editor.putBoolean("isBind",false);
        editor.commit();
        MathUitl.showToast(mContext,mContext.getString(R.string.tokenTimeout));
        Intent intentmain=new Intent(mContext,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intentmain);
    }
}
