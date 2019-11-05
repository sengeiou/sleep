package com.szip.sleepee.Controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.szip.sleepee.R;
import com.szip.sleepee.Util.StatusBarCompat;

public class FAQActivity extends BaseActivity {

    private ImageView backIv;
    private FrameLayout frameLayout;
    private TextView questionTv;
    private TextView answerTv;

    private ImageView image2,image3,image4,image5,image6;

    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_faq);
        StatusBarCompat.translucentStatusBar(FAQActivity.this,true);
        Intent intent = getIntent();
        flag = intent.getIntExtra("flag",1);
        initView();
        initEvent();
        initFragment();
    }

    private void initView() {
        if(flag>11)
            ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.advise));
        else
            ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.question));
        backIv = findViewById(R.id.backIv);
    }

    private void initEvent() {
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化顶部布局
     * */
    private void initFragment() {
        frameLayout = findViewById(R.id.reportDataFl);
        switch (flag){
            case 1:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_pro2,null);
                frameLayout.addView(view);
                questionTv = view.findViewById(R.id.proTv);
                questionTv.setText(getString(R.string.problem1));
//                image2 = view.findViewById(R.id.iamge2);
//                image3 = view.findViewById(R.id.iamge3);
//                image4 = view.findViewById(R.id.iamge4);
//                image5 = view.findViewById(R.id.iamge5);
//                image6 = view.findViewById(R.id.iamge6);

//                if (!getResources().getConfiguration().locale.getCountry().equals("CN")){
//                    image2.setImageResource(R.mipmap.deviceconnection_2_en);
//                    image3.setImageResource(R.mipmap.deviceconnection_3_en);
//                    image4.setImageResource(R.mipmap.deviceconnection_4_en);
//                    image5.setImageResource(R.mipmap.sleepstatistic_1_en);
//                    image6.setImageResource(R.mipmap.sleepstatistic_2_en);
//
//                }
            }
            break;
            case 2:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_pro1,null);
                frameLayout.addView(view);
                questionTv = view.findViewById(R.id.proTv);
                questionTv.setText(getString(R.string.problem2));
                answerTv = view.findViewById(R.id.answerTv);
                answerTv.setText(getString(R.string.answer2));
            }
            break;
            case 3:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_pro1,null);
                frameLayout.addView(view);
                questionTv = view.findViewById(R.id.proTv);
                questionTv.setText(getString(R.string.problem3));
                answerTv = view.findViewById(R.id.answerTv);
                answerTv.setText(getString(R.string.answer3));
            }
            break;
            case 4:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_pro1,null);
                frameLayout.addView(view);
                questionTv = view.findViewById(R.id.proTv);
                questionTv.setText(getString(R.string.problem4));
                answerTv = view.findViewById(R.id.answerTv);
                answerTv.setText(getString(R.string.answer4));
            }
            break;
            case 5:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_pro1,null);
                frameLayout.addView(view);
                questionTv = view.findViewById(R.id.proTv);
                questionTv.setText(getString(R.string.problem5));
                answerTv = view.findViewById(R.id.answerTv);
                answerTv.setText(getString(R.string.answer5));
            }
            break;
            case 6:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_pro1,null);
                frameLayout.addView(view);
                questionTv = view.findViewById(R.id.proTv);
                questionTv.setText(getString(R.string.problem6));
                answerTv = view.findViewById(R.id.answerTv);
                answerTv.setText(getString(R.string.answer6));
            }
            break;
            case 7:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_pro1,null);
                frameLayout.addView(view);
                questionTv = view.findViewById(R.id.proTv);
                questionTv.setText(getString(R.string.problem7));
                answerTv = view.findViewById(R.id.answerTv);
                answerTv.setText(getString(R.string.answer7));
            }
            break;
            case 8:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_pro1,null);
                frameLayout.addView(view);
                questionTv = view.findViewById(R.id.proTv);
                questionTv.setText(getString(R.string.problem8));
                answerTv = view.findViewById(R.id.answerTv);
                answerTv.setText(getString(R.string.answer8));
            }
            break;
            case 9:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_pro1,null);
                frameLayout.addView(view);
                questionTv = view.findViewById(R.id.proTv);
                questionTv.setText(getString(R.string.problem9));
                answerTv = view.findViewById(R.id.answerTv);
                answerTv.setText(getString(R.string.answer9));
            }
            break;
            case 10:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_pro1,null);
                frameLayout.addView(view);
                questionTv = view.findViewById(R.id.proTv);
                questionTv.setText(getString(R.string.problem10));
                answerTv = view.findViewById(R.id.answerTv);
                answerTv.setText(getString(R.string.answer10));
            }
            break;
            case 11:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_pro1,null);
                frameLayout.addView(view);
                questionTv = view.findViewById(R.id.proTv);
                questionTv.setText(getString(R.string.problem11));
                answerTv = view.findViewById(R.id.answerTv);
                answerTv.setText(getString(R.string.answer11));
            }
            break;

            case 12:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_pro1,null);
                frameLayout.addView(view);
                questionTv = view.findViewById(R.id.proTv);
                questionTv.setText(getString(R.string.advise1));
                answerTv = view.findViewById(R.id.answerTv);
                answerTv.setText(getString(R.string.advise1Info));
            }
            break;

            case 13:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_advise_sleep_state,null);
                frameLayout.addView(view);
            }
            break;

            case 14:{

//                if (!getResources().getConfiguration().locale.getCountry().equals("CN")){
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_sleep_time_advise_en,null);
                frameLayout.addView(view);

//                }else{
//                    View view = LayoutInflater.from(this).inflate(R.layout.fragment_pro1,null);
//                    frameLayout.addView(view);
//                    questionTv = view.findViewById(R.id.proTv);
//                    questionTv.setText(getString(R.string.advise3));
//                    answerTv = view.findViewById(R.id.answerTv);
//                    answerTv.setText(getString(R.string.advise3Info));
//                }
            }
            break;

            case 15:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_to_sleep,null);
                frameLayout.addView(view);
            }
            break;

            case 16:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_breath,null);
                frameLayout.addView(view);
            }
            break;

            case 17:{
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_pro1,null);
                frameLayout.addView(view);
                questionTv = view.findViewById(R.id.proTv);
                questionTv.setText(getString(R.string.advise6));
                answerTv = view.findViewById(R.id.answerTv);
                answerTv.setText(getString(R.string.advise6Info));
            }
            break;
        }

    }
}
