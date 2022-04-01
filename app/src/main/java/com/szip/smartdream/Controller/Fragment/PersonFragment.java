package com.szip.smartdream.Controller.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szip.smartdream.Controller.AboutActivity;
import com.szip.smartdream.Controller.FeedbackActivity;
import com.szip.smartdream.Controller.HelpCentreActivity;
import com.szip.smartdream.Controller.MyDeviceActivity;
import com.szip.smartdream.Controller.PersonInfoActivity;
import com.szip.smartdream.Controller.UserInfoActivity;
import com.szip.smartdream.MyApplication;
import com.szip.smartdream.R;

/**
 * Created by Administrator on 2019/1/23.
 */

public class PersonFragment extends BaseFragment {

    private Context mContext;

    private TextView nameTv;
//    private LinearLayout dataLl;
    private LinearLayout myDeviceLl;
    private LinearLayout userInfoLl;
    private LinearLayout personInfoLl;
    private LinearLayout helpLl;
    private LinearLayout adviseLl;
    private LinearLayout feedbackLl;
    private LinearLayout aboutLl;


    /**
     * 返回一个fragment实例，Activity中调用
     * */
    public static PersonFragment newInstance(String param){
        Bundle bundle = new Bundle();
        bundle.putString("param",param);
        PersonFragment fragment = new PersonFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_person;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();
        initView();
        initEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        nameTv.setText(getString(R.string.hi)+((MyApplication)getActivity().getApplicationContext())
                .getUserInfo().getUserName()+"!");
    }

    private void initView() {
        nameTv = getView().findViewById(R.id.topTv);
//        dataLl = getView().findViewById(R.id.dataLl);
        myDeviceLl = getView().findViewById(R.id.myDeviceLl);
        userInfoLl = getView().findViewById(R.id.userInfoLl);
        personInfoLl = getView().findViewById(R.id.personInfoLl);
        helpLl = getView().findViewById(R.id.helpLl);
        adviseLl = getView().findViewById(R.id.adviseLl);
        feedbackLl = getView().findViewById(R.id.feedbackLl);
        aboutLl = getView().findViewById(R.id.aboutLl);
    }

    private void initEvent() {
//        dataLl.setOnClickListener(onClickListener);
        myDeviceLl.setOnClickListener(onClickListener);
        userInfoLl.setOnClickListener(onClickListener);
        personInfoLl.setOnClickListener(onClickListener);
        helpLl.setOnClickListener(onClickListener);
        adviseLl.setOnClickListener(onClickListener);
        feedbackLl.setOnClickListener(onClickListener);
        aboutLl.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
//                case R.id.dataLl:{
////                    ProgressHudModel.newInstance().show(getActivity(),getString(R.string.updown),getString(R.string.httpError),1000);
//
////                    String string = AllDataModel.ListToJsonString(0);
////                    try {
//////                        HttpMessgeUtil.getInstance(mContext).postForUpdownReportData(((MyApplication)getActivity().getApplicationContext()).getUserId(),
//////                                "",null);
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
//
//                }
//                break;
                case R.id.myDeviceLl:{
                    startActivity(new Intent(getActivity(),MyDeviceActivity.class));
                }
                break;
                case R.id.userInfoLl:{
                    startActivity(new Intent(getActivity(),UserInfoActivity.class));
                }
                break;
                case R.id.personInfoLl:{
                    startActivity(new Intent(getActivity(),PersonInfoActivity.class));
                }
                break;
                case R.id.helpLl:
                {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), HelpCentreActivity.class);
                    intent.putExtra("flag",0);
                    startActivity(intent);
                }
                break;
                case R.id.adviseLl:{
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), HelpCentreActivity.class);
                    intent.putExtra("flag",1);
                    startActivity(intent);
                }
                break;

                case R.id.feedbackLl:{
                    startActivity(new Intent(getActivity(),FeedbackActivity.class));
                }
                break;
                case R.id.aboutLl:{
                    startActivity(new Intent(getActivity(),AboutActivity.class));
                }
                break;
            }
        }
    };
}
