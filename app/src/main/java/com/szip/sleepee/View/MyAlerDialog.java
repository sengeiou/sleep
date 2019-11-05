package com.szip.sleepee.View;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.szip.sleepee.R;


public class MyAlerDialog {
    private static MyAlerDialog dialogUtil;




    public static MyAlerDialog getSingle(){
        if (dialogUtil==null){
            synchronized (MyAlerDialog.class){
                if (dialogUtil == null){
                    return new MyAlerDialog();
                }
            }
        }
        return dialogUtil;
    }

    public AlertDialog showAlerDialog(String title, String msg, String positive, String negative, boolean cancelable,
                                      final AlerDialogOnclickListener onclickListener, Context context){

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setCancelable(true)
                .create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_layout);
        TextView tv_title = window.findViewById(R.id.dialogTitle);
        tv_title.setText(title);
        TextView tv_message =  window.findViewById(R.id.msgTv);
        tv_message.setText(msg);

        Button cancel = window.findViewById(R.id.btn_cancel);
        cancel.setText(negative);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });//取消按钮
        Button confirm = window.findViewById(R.id.btn_comfirm);
        confirm.setText(positive);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onclickListener!=null){
                    onclickListener.onDialogTouch(true);
                    alertDialog.dismiss();
                }
            }
        });//确定按钮

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return alertDialog;
    }

    public AlertDialog showAlerDialogWithEdit(String title, String edit1, String editHint1, String positive, String negative, boolean cancelable,
                                              final AlerDialogEditOnclickListener onclickListener, Context context){

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setCancelable(true)
                .create();
        alertDialog.setView(new EditText(context));
        alertDialog.show();
        alertDialog.setCancelable(cancelable);
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_layout_edit);
        TextView tv_title = window.findViewById(R.id.dialogTitle);
        tv_title.setText(title);
        final EditText et1 =  window.findViewById(R.id.highHeartEt);
        et1.setHint(editHint1);
        et1.setText(edit1);

        Button cancel = window.findViewById(R.id.btn_cancel);
        cancel.setText(negative);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });//取消按钮
        Button confirm = window.findViewById(R.id.btn_comfirm);
        confirm.setText(positive);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onclickListener!=null){
                    onclickListener.onDialogEditTouch(et1.getText().toString());
                }
                alertDialog.dismiss();
            }
        });//确定按钮

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return alertDialog;

    }


    public AlertDialog showAlerDialogWithEdit(String title, final int flag, String edit1, String editHint1, String positive, String negative, boolean cancelable,
                                              final AlerDialogEditOnclickListener onclickListener, Context context){

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setCancelable(true)
                .create();
        alertDialog.setView(new EditText(context));
        alertDialog.show();
        alertDialog.setCancelable(cancelable);
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_layout_edit);
        TextView tv_title = window.findViewById(R.id.dialogTitle);
        tv_title.setText(title);
        final EditText et1 =  window.findViewById(R.id.highHeartEt);
        RadioButton radioButton1 = window.findViewById(R.id.radio1);
        RadioButton radioButton2 = window.findViewById(R.id.radio2);
        final RadioGroup group = window.findViewById(R.id.group);
        group.setVisibility(View.VISIBLE);
        et1.setHint(editHint1);
        et1.setText(edit1);

        if (flag == 0){
            radioButton1.setText("cm");
            radioButton2.setText("in");
        }else if (flag == 1){
            radioButton1.setText("kg");
            radioButton2.setText("lb");
        }

        Button cancel = window.findViewById(R.id.btn_cancel);
        cancel.setText(negative);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });//取消按钮
        Button confirm = window.findViewById(R.id.btn_comfirm);
        confirm.setText(positive);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onclickListener!=null){
                    onclickListener.onDialogEditWithRadioTouch(et1.getText().toString(),flag,group.getCheckedRadioButtonId() == R.id.radio1?0:1);
                }
                alertDialog.dismiss();
            }
        });//确定按钮

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return alertDialog;

    }



    public interface AlerDialogOnclickListener {
        void onDialogTouch(boolean flag);
    }

    public interface AlerDialogEditOnclickListener {
        void onDialogEditTouch(String edit1);
        void onDialogEditWithRadioTouch(String edit1, int flag, int position);
    }

}
