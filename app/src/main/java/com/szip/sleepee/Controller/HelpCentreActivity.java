package com.szip.sleepee.Controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szip.sleepee.R;
import com.szip.sleepee.Util.StatusBarCompat;

public class HelpCentreActivity extends BaseActivity {

    private ImageView backIv;
    private LinearLayout proLl1;
    private LinearLayout proLl2;
    private LinearLayout proLl3;
    private LinearLayout proLl4;
    private LinearLayout proLl5;
    private LinearLayout proLl6;
    private LinearLayout proLl7;
    private LinearLayout proLl8;
    private LinearLayout proLl9;
    private LinearLayout proLl10;
    private LinearLayout proLl11;

    private LinearLayout adviseLl1;
    private LinearLayout adviseLl2;
    private LinearLayout adviseLl3;
    private LinearLayout adviseLl4;
    private LinearLayout adviseLl5;
    private LinearLayout adviseLl6;

    private FrameLayout frameLayout;
    private int flag;//0帮助  1睡眠建议


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_help_centre);
        StatusBarCompat.translucentStatusBar(HelpCentreActivity.this,true);
        Intent intent = getIntent();
        flag = intent.getIntExtra("flag",0);
        initView();
        initFragment();
        initEvent();
    }

    private void initView() {
        backIv = findViewById(R.id.backIv);
    }



    /**
     * 初始化顶部布局
     * */
    private void initFragment() {
        frameLayout = findViewById(R.id.reportDataFl);
        switch (flag) {
            case 0: {
                ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.help));
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_help, null);
                frameLayout.addView(view);
                proLl1 = view.findViewById(R.id.proLl1);
                proLl2 = view.findViewById(R.id.proLl2);
                proLl3 = view.findViewById(R.id.proLl3);
                proLl4 = view.findViewById(R.id.proLl4);
                proLl5 = view.findViewById(R.id.proLl5);
                proLl6 = view.findViewById(R.id.proLl6);
                proLl7 = view.findViewById(R.id.proLl7);
                proLl8 = view.findViewById(R.id.proLl8);
                proLl9 = view.findViewById(R.id.proLl9);
                proLl10 = view.findViewById(R.id.proLl10);
                proLl11 = view.findViewById(R.id.proLl11);
            }
            break;
            case 1: {
                ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.advise));
                View view = LayoutInflater.from(this).inflate(R.layout.fragment_advise, null);
                frameLayout.addView(view);
                adviseLl1 = view.findViewById(R.id.adviseLl1);
                adviseLl2 = view.findViewById(R.id.adviseLl2);
                adviseLl3 = view.findViewById(R.id.adviseLl3);
                adviseLl4 = view.findViewById(R.id.adviseLl4);
                adviseLl5 = view.findViewById(R.id.adviseLl5);
                adviseLl6 = view.findViewById(R.id.adviseLl6);
            }
            break;
        }
    }

    private void initEvent() {
        backIv.setOnClickListener(onClickListener);
        if (flag == 0){
            proLl1.setOnClickListener(onClickListener);
            proLl2.setOnClickListener(onClickListener);
            proLl3.setOnClickListener(onClickListener);
            proLl4.setOnClickListener(onClickListener);
            proLl5.setOnClickListener(onClickListener);
            proLl6.setOnClickListener(onClickListener);
            proLl7.setOnClickListener(onClickListener);
            proLl8.setOnClickListener(onClickListener);
            proLl9.setOnClickListener(onClickListener);
            proLl10.setOnClickListener(onClickListener);
            proLl11.setOnClickListener(onClickListener);
        }else {
            adviseLl1.setOnClickListener(onClickListener);
            adviseLl2.setOnClickListener(onClickListener);
            adviseLl3.setOnClickListener(onClickListener);
            adviseLl4.setOnClickListener(onClickListener);
            adviseLl5.setOnClickListener(onClickListener);
            adviseLl6.setOnClickListener(onClickListener);
        }


    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.backIv:
                    finish();
                    break;
                case R.id.proLl1:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",1);
                    startActivity(intent);
                }
                break;
                case R.id.proLl2:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",2);
                    startActivity(intent);
                }
                break;
                case R.id.proLl3:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",3);
                    startActivity(intent);
                }
                break;
                case R.id.proLl4:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",4);
                    startActivity(intent);
                }
                break;
                case R.id.proLl5:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",5);
                    startActivity(intent);
                }
                break;
                case R.id.proLl6:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",6);
                    startActivity(intent);
                }
                break;
                case R.id.proLl7:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",7);
                    startActivity(intent);
                }
                break;
                case R.id.proLl8:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",8);
                    startActivity(intent);
                }
                break;
                case R.id.proLl9:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",9);
                    startActivity(intent);
                }
                break;
                case R.id.proLl10:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",10);
                    startActivity(intent);
                }
                break;
                case R.id.proLl11:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",11);
                    startActivity(intent);
                }
                break;

                case R.id.adviseLl1:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",12);
                    startActivity(intent);
                }
                break;
                case R.id.adviseLl2:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",13);
                    startActivity(intent);
                }
                break;
                case R.id.adviseLl3:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",14);
                    startActivity(intent);
                }
                break;
                case R.id.adviseLl4:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",15);
                    startActivity(intent);
                }
                break;
                case R.id.adviseLl5:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",16);
                    startActivity(intent);
                }
                break;
                case R.id.adviseLl6:{
                    Intent intent = new Intent();
                    intent.setClass(HelpCentreActivity.this,FAQActivity.class);
                    intent.putExtra("flag",17);
                    startActivity(intent);
                }
                break;

            }
        }
    };

}
