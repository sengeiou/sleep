package com.szip.smartdream.Controller;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.szip.smartdream.Bean.HttpBean.BaseApi;
import com.szip.smartdream.Bean.UserInfo;
import com.szip.smartdream.Bean.UserInfoWriteBean;
import com.szip.smartdream.Interface.HttpCallbackWithBase;
import com.szip.smartdream.Model.ProgressHudModel;
import com.szip.smartdream.MyApplication;
import com.szip.smartdream.R;
import com.szip.smartdream.Service.BleService;
import com.szip.smartdream.Util.DateUtil;
import com.szip.smartdream.Util.HttpMessgeUtil;
import com.szip.smartdream.Util.StatusBarCompat;
import com.szip.smartdream.View.CharacterPickerWindow;
import com.szip.smartdream.View.DialogBottom;
import com.szip.smartdream.View.MyAlerDialog;
import com.szip.smartdream.View.character.OnOptionChangedListener;
import com.zhuoting.health.util.DataUtil;
import com.zhuoting.health.write.ProtocolWriter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.szip.smartdream.Util.HttpMessgeUtil.SETINFO_FLAG;

public class PersonInfoActivity extends BaseActivity implements HttpCallbackWithBase{

    private Context mContext;
    /**
     * 用户数据
     * */
    private UserInfo infoBean;
    /**
     * 数据选择框
     * */
    private CharacterPickerWindow window;
    private CharacterPickerWindow window1;
    private CharacterPickerWindow window2;
    /**
     * 返回按键
     * */
    private ImageView backIv;

    /**
     * 保存按键
     * */
    private TextView saveTv;

    /**
     * 姓名
     * */
    private LinearLayout nameLl;
    private TextView nameTv;
    /**
     * 性别
     * */
    private LinearLayout genderLl;
    private TextView genderTv;
    private boolean isMale = true;
    /**
     * 年龄
     * */
    private LinearLayout ageLl;
    private TextView ageTv;
    private String birthday = new String();
    private int age;
    /**
     * 制式
     * */
    private LinearLayout unitLl;
    private TextView unitTv;
    private boolean isBritish = false;
    /**
     * 身高
     * */
    private LinearLayout statureLl;
    private TextView statureTv;
    /**
     * 体重
     * */
    private LinearLayout weightLl;
    private TextView weightTv;
    /**
     * 性别选择
     * */
    private DialogBottom dialogBottom;
    private Dialog dialog;
    /**
     * 制式选择
     * */
    private boolean isUnit;//当前的弹出框是不是制式选择
    private Dialog dialog1;


    private MyApplication app;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 300:
                    if (BleService.getInstance().getConnectState()==2){
                        int cm;
                        int kg;
                        int index = statureTv.getText().toString().indexOf("ft");
                        if (index>=0){
                            cm = DataUtil.ftTocm(Integer.valueOf(statureTv.getText().toString().substring(0,index)),
                                    Integer.valueOf(statureTv.getText().toString().substring(index+2,index+4)));
                        }else
                            cm = Integer.valueOf(statureTv.getText().toString().substring(0,3));

                        index = weightTv.getText().toString().indexOf("lb");
                        if (index>=0){
                            kg = DataUtil.lbToKg(Integer.valueOf(weightTv.getText().toString().substring(0,index)));
                        }else
                            kg = Integer.valueOf(weightTv.getText().toString().substring(0,weightTv.getText().toString().indexOf("kg")));
                        ProgressHudModel.newInstance().setLabel(getString(R.string.writeToDev));
                        BleService.getInstance().write(ProtocolWriter.writeForWriteUser(isMale?(byte) 1:(byte) 0,(byte) age,(byte) cm,(byte) kg));
                    }else {
                        ProgressHudModel.newInstance().diss();
                            Toast.makeText(PersonInfoActivity.this,getString(R.string.writeInfoError),Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_person_info);
        StatusBarCompat.translucentStatusBar(PersonInfoActivity.this,true);
        mContext = getApplicationContext();
        app = (MyApplication) getApplicationContext();
        initWindow();
        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithBase(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        HttpMessgeUtil.getInstance(mContext).setHttpCallbackWithBase(null);
    }

    /**
     * 初始化视图
     * */
    private void initView() {
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.personInfo));
        backIv = findViewById(R.id.backIv);
        backIv.setVisibility(View.VISIBLE);

        saveTv = findViewById(R.id.saveTv);
        saveTv.setText(getString(R.string.save));
        saveTv.setVisibility(View.VISIBLE);

        nameLl = findViewById(R.id.nameLl);
        nameTv = findViewById(R.id.nameTv);
        genderLl = findViewById(R.id.genderLl);
        genderTv = findViewById(R.id.genderTv);
        ageLl = findViewById(R.id.ageLl);
        ageTv = findViewById(R.id.ageTv);
        unitLl = findViewById(R.id.unitLl);
        unitTv = findViewById(R.id.unitTv);
        statureLl = findViewById(R.id.statureLl);
        statureTv = findViewById(R.id.statureTv);
        weightLl = findViewById(R.id.weightLl);
        weightTv = findViewById(R.id.weightTv);
        dialogBottom = new DialogBottom(this,onClickListener);
    }

    /**
     * 初始化数据
     * */
    private void initData() {
        infoBean = app.getUserInfo();
        nameTv.setText(infoBean.getUserName());
        isMale = infoBean.getSex()==0?false:true;
        genderTv.setText(isMale?getString(R.string.male):getString(R.string.female));
        try {
            ageTv.setText(infoBean.getBirthday());
        } catch (Exception e) {
            e.printStackTrace();
        }
        birthday = infoBean.getBirthday();
        statureTv.setText(infoBean.getHeight());
        weightTv.setText(infoBean.getWeight());

        if (infoBean.getUnit()==null)
            isBritish = false;
        else
            isBritish = infoBean.getUnit().equals(getString(R.string.metric))?false:true;


        if (isBritish){
            unitTv.setText(getString(R.string.british));
            initStatureAndWeight();
        } else {
            unitTv.setText(getString(R.string.metric));
            initStatureAndWeight();
        }
    }

    /**
     * 初始化事件监听
     * */
    private void initEvent() {
        backIv.setOnClickListener(onClickListener);
        saveTv.setOnClickListener(onClickListener);
        nameLl.setOnClickListener(onClickListener);
        genderLl.setOnClickListener(onClickListener);
        ageLl.setOnClickListener(onClickListener);
        statureLl.setOnClickListener(onClickListener);
        weightLl.setOnClickListener(onClickListener);
        unitLl.setOnClickListener(onClickListener);
    }

    /**
     * 初始化选择器
     * */
    private void initWindow() {
        ArrayList list1 = DateUtil.getYearList();
        //选项选择器
        window = new CharacterPickerWindow(PersonInfoActivity.this,getString(R.string.birthday));
        //初始化选项数据
        window.getPickerView().setPickerForDate(list1);
        //设置默认选中的三级项目
        window.setCurrentPositions(list1.size()/2, 0, 0);
        //监听确定选择按钮
        window.setOnoptionsSelectListener(new OnOptionChangedListener() {
            @Override
            public void onOptionChanged(int option1, int option2, int option3) {
                try {
                    birthday = String.format("%4d-%02d-%02d",(1930+option1),(option2+1),(option3+1));
                    age = DateUtil.getAge(DateUtil.parse(birthday));
                    ageTv.setText(birthday);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        //选项选择器
        window1 = new CharacterPickerWindow(PersonInfoActivity.this,getString(R.string.stature));
        if (isBritish){
            final ArrayList<List<String>> list2 = DateUtil.getStatureWithBritish();
            //初始化选项数据
            window1.getPickerView().setPickerWithoutLink(list2.get(0),list2.get(1));
            //设置默认选中的三级项目
            window1.setCurrentPositions(list2.get(0).size()/2, list2.get(1).size(), 0);
            //监听确定选择按钮
            window1.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {

                    try {
                        statureTv.setText(list2.get(0).get(option1)+"ft"+list2.get(1).get(option2)+"in");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }else {
            final ArrayList list2 = DateUtil.getStature();
            //初始化选项数据
            window1.getPickerView().setPicker(list2);
            //设置默认选中的三级项目
            window1.setCurrentPositions(list2.size()/2, 0, 0);
            //监听确定选择按钮
            window1.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {

                    try {
                        statureTv.setText(list2.get(option1)+"cm");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        //选项选择器
        window2 = new CharacterPickerWindow(PersonInfoActivity.this,getString(R.string.weight));
        //初始化选项数据
        if (isBritish){
            final ArrayList list3 = DateUtil.getWeightWithBritish();
            window2.getPickerView().setPicker(list3);
            //设置默认选中的三级项目
            window2.setCurrentPositions(list3.size()/2, 0, 0);
            //监听确定选择按钮
            window2.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {
                    try {
                        weightTv.setText(list3.get(option1)+"lb");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {
            final ArrayList list3 = DateUtil.getWeight();
            window2.getPickerView().setPicker(list3);
            //设置默认选中的三级项目
            window2.setCurrentPositions(list3.size()/2, 0, 0);
            //监听确定选择按钮
            window2.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {
                    try {
                        weightTv.setText(list3.get(option1)+"kg");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    private void initStatureAndWeight(){
        //选项选择器
        window1 = new CharacterPickerWindow(PersonInfoActivity.this,getString(R.string.stature));
        if (isBritish){
            final ArrayList<List<String>> list2 = DateUtil.getStatureWithBritish();
            //初始化选项数据
            window1.getPickerView().setPickerWithoutLink(list2.get(0),list2.get(1));
            //设置默认选中的三级项目
            window1.setCurrentPositions(list2.size()/2, 0, 0);
            //监听确定选择按钮
            window1.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {

                    try {
                        statureTv.setText(list2.get(0).get(option1)+"ft"+list2.get(1).get(option2)+"in");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {

            final ArrayList list2 = DateUtil.getStature();
            //初始化选项数据
            window1.getPickerView().setPicker(list2);
            //设置默认选中的三级项目
            window1.setCurrentPositions(list2.size()/2, 0, 0);
            //监听确定选择按钮
            window1.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {

                    try {
                        statureTv.setText(list2.get(option1)+"cm");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        //选项选择器
        window2 = new CharacterPickerWindow(PersonInfoActivity.this,getString(R.string.weight));
        //初始化选项数据
        if (isBritish){
            final ArrayList list3 = DateUtil.getWeightWithBritish();
            window2.getPickerView().setPicker(list3);
            //设置默认选中的三级项目
            window2.setCurrentPositions(list3.size()/2, 0, 0);
            //监听确定选择按钮
            window2.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {
                    try {
                        weightTv.setText(list3.get(option1)+"lb");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {

            final ArrayList list3 = DateUtil.getWeight();
            window2.getPickerView().setPicker(list3);
            //设置默认选中的三级项目
            window2.setCurrentPositions(list3.size()/2, 0, 0);
            //监听确定选择按钮
            window2.setOnoptionsSelectListener(new OnOptionChangedListener() {
                @Override
                public void onOptionChanged(int option1, int option2, int option3) {
                    try {
                        weightTv.setText(list3.get(option1)+"kg");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 事件监听
     * */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.backIv:
                    finish();
                    break;

                case R.id.saveTv:

                    ProgressHudModel.newInstance().show(PersonInfoActivity.this,getString(R.string.waitting),
                            getString(R.string.httpError),10000);
                    try {
                        HttpMessgeUtil.getInstance(mContext).postForSetUserInfo(nameTv.getText().toString(),isMale?"1":"0",
                                birthday,statureTv.getText().toString(),weightTv.getText().toString(),unitTv.getText().toString(),SETINFO_FLAG);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.nameLl:
                    MyAlerDialog.getSingle().showAlerDialogWithEdit(getString(R.string.name),"",getString(R.string.inputName),getString(R.string.confirm),
                            getString(R.string.cancel),true, AlerDialogEditOnclickListener,PersonInfoActivity.this);
                    break;
                case R.id.genderLl:
                    isUnit = false;
                    dialog = dialogBottom.show(getString(R.string.male),getString(R.string.female),true);
                    break;
                case R.id.unitLl:
                    isUnit = true;
                    dialog1 = dialogBottom.show(getString(R.string.metric),getString(R.string.british),true);
                    break;
                case R.id.ageLl:
                    window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                    break;
                case R.id.statureLl:
                    window1.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                    break;
                case R.id.weightLl:
                    window2.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                    break;
                case R.id.button1:
                    if (isUnit){
                        unitTv.setText(getString(R.string.metric));
                        isBritish = false;
                        initStatureAndWeight();
                        dialog1.dismiss();
                    }else {
                        genderTv.setText(getString(R.string.male));
                        isMale = true;
                        dialog.dismiss();
                    }
                    break;
                case R.id.button2:{
                    if (isUnit){
                        unitTv.setText(getString(R.string.british));
                        isBritish = true;
                        initStatureAndWeight();
                        dialog1.dismiss();
                    }else {
                        genderTv.setText(getString(R.string.female));
                        isMale = false;
                        dialog.dismiss();
                    }

                }

                break;
                case R.id.btn_cancel:{
                    if (dialog!=null){
                        dialog.dismiss();
                    }
                    if (dialog1!=null){
                        dialog1.dismiss();
                    }
                }
                break;
            }
        }
    };


    /**
     * 将信息同步到设备完成
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void isWriteOk(UserInfoWriteBean userInfoWriteBean){
        if (userInfoWriteBean.isOk()){
            ProgressHudModel.newInstance().diss();
            Toast.makeText(PersonInfoActivity.this,getString(R.string.saveOK),Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 弹出框回调
     * */
    private MyAlerDialog.AlerDialogEditOnclickListener AlerDialogEditOnclickListener = new MyAlerDialog.AlerDialogEditOnclickListener() {
        @Override
        public void onDialogEditTouch(String edit1) {
            nameTv.setText(edit1);
        }

        @Override
        public void onDialogEditWithRadioTouch(String edit1, int flag, int position) {
        }
    };


    @Override
    public void onCallback(BaseApi baseApi, int id) {
        infoBean.setUserName(nameTv.getText().toString());
        infoBean.setSex(isMale?1:0);
        infoBean.setBirthday(birthday);
        infoBean.setHeight(statureTv.getText().toString());
        infoBean.setWeight(weightTv.getText().toString());
        infoBean.setUnit(unitTv.getText().toString());
        handler.sendEmptyMessage(300);
    }
}
