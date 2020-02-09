package com.szip.smartdream.View;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.szip.smartdream.R;


public class DialogBottom {

    private Dialog dialog;
    private Context mContext;
    private View.OnClickListener onClickListener;
    private Button btn1;
    private Button btn2;
    private String button1;
    private String button2;

    public DialogBottom(Context context, View.OnClickListener onClickListener){
        this.mContext = context;
        this.onClickListener = onClickListener;
    }

    public Dialog show(String button1,String button2,boolean btnFlag) {
        dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.dialog_bottom, null);
        //初始化控件
        btn1 = inflate.findViewById(R.id.button1);
        btn1.setOnClickListener(onClickListener);
        btn1.setText(button1);
        btn1.setEnabled(btnFlag);
        btn2 = inflate.findViewById(R.id.button2);
        btn2.setOnClickListener(onClickListener);
        btn2.setText(button2);

        inflate.findViewById(R.id.btn_cancel).setOnClickListener(onClickListener);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        if(dialogWindow == null){
            return null;
        }
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;//设置Dialog距离底部的距离
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框

        return dialog;
    }
}
