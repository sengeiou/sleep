package com.szip.smartdream.Controller;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.szip.smartdream.Bean.HttpBean.BaseApi;
import com.szip.smartdream.Interface.HttpCallbackWithBase;
import com.szip.smartdream.Model.ProgressHudModel;
import com.szip.smartdream.R;
import com.szip.smartdream.Util.HttpMessgeUtil;
import com.szip.smartdream.Util.StatusBarCompat;

import java.io.IOException;

public class FeedbackActivity extends BaseActivity implements HttpCallbackWithBase{

    private EditText feedbackEt;
    private TextView lenghtTv;
    private TextView saveTv;
    private RelativeLayout feedbackRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_feedback);
        StatusBarCompat.translucentStatusBar(this,true);
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HttpMessgeUtil.getInstance(this).setHttpCallbackWithBase(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HttpMessgeUtil.getInstance(this).setHttpCallbackWithBase(null);
    }

    private void initView() {
        ((TextView)findViewById(R.id.titleTv)).setText(getString(R.string.feedback));
        saveTv = findViewById(R.id.saveTv);
        saveTv.setVisibility(View.VISIBLE);
        saveTv.setText(getString(R.string.submit));
        feedbackEt = findViewById(R.id.feedbackEt);
        lenghtTv = findViewById(R.id.wordLenghtTv);
        feedbackRl = findViewById(R.id.feedbackRl);

    }

    private void initEvent() {
        findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        feedbackEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int len = s.toString().length();
                lenghtTv.setText(String.format("%d/120",len));
                if (len>120)
                    lenghtTv.setTextColor(Color.RED);
                else
                    lenghtTv.setTextColor(getResources().getColor(R.color.black2));
            }
        });

        saveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedbackEt.getText().toString().length()>120)
                    showToast(getString(R.string.tooLong));
                else if (feedbackEt.getText().toString().equals(""))
                    showToast(getString(R.string.empty));
                else
                    try {
                        ProgressHudModel.newInstance().show(FeedbackActivity.this,getString(R.string.waitting),
                                getString(R.string.httpError),5000);
                        HttpMessgeUtil.getInstance(FeedbackActivity.this).postSendFeedback(feedbackEt.getText().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        });

        feedbackRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedbackEt.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(feedbackEt, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    @Override
    public void onCallback(BaseApi baseApi, int id) {
        ProgressHudModel.newInstance().diss();
        showToast(getString(R.string.send_success));
        finish();
    }
}
