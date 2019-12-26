package com.szip.sleepee.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.roamer.slidelistview.SlideBaseAdapter;
import com.roamer.slidelistview.SlideListView;
import com.szip.sleepee.Bean.HttpBean.BaseApi;
import com.szip.sleepee.Bean.HttpBean.ClockData;
import com.szip.sleepee.Controller.LoginActivity;
import com.szip.sleepee.DB.SaveDataUtil;
import com.szip.sleepee.Model.ProgressHudModel;
import com.szip.sleepee.MyApplication;
import com.szip.sleepee.R;
import com.szip.sleepee.Service.BleService;
import com.szip.sleepee.Util.DateUtil;
import com.szip.sleepee.Util.HttpMessgeUtil;
import com.szip.sleepee.Util.JsonGenericsSerializator;
import com.szip.sleepee.Util.MathUitl;
import com.zhuoting.health.write.ProtocolWriter;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.IOException;
import java.util.ArrayList;

import ch.ielse.view.SwitchView;
import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;
import static com.szip.sleepee.MyApplication.FILE;

public class AlarmClockAdapter extends SlideBaseAdapter {

    private ArrayList<ClockData> list;
    private int pos;
    ;

    public void setList(ArrayList<ClockData> list){
        this.list = list;
    }

    public AlarmClockAdapter(Context context, ArrayList<ClockData> list) {
        super(context);
        this.list = list;
    }

    @Override
    public SlideListView.SlideMode getSlideModeInPosition(int position) {
        return super.getSlideModeInPosition(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getFrontViewId(int position) {
        return R.layout.adapter_alarm_clock;
    }

    @Override
    public int getLeftBackViewId(int position) {
        return 0;
    }

    @Override
    public int getRightBackViewId(int position) {
        return R.layout.leftdelect;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        HolderView holder =null;
        if (view == null) {
            view = createConvertView(position);
            holder = new HolderView();
            holder.time = view.findViewById(R.id.timeTv);
            holder.type = view.findViewById(R.id.typeTv);
            holder.smart = view.findViewById(R.id.smartTv);
            holder.cycle = view.findViewById(R.id.cycleTv);
            holder.onoff = view.findViewById(R.id.onoffCb);
            holder.phone = view.findViewById(R.id.isPhoneIv);
            holder.delete= view.findViewById(R.id.delete);
            view.setTag(holder);
        } else {
            holder = (HolderView) view.getTag();
        }

        final ClockData clockData = list.get(position);


        holder.time.setText(clockData.getTime());
        if (clockData.getType() == 1||clockData.getType() == 0){
            holder.type.setText(mContext.getString(R.string.nurseItem));
        }else if (clockData.getType() == 2){
            holder.type.setText(mContext.getString(R.string.wakeItem));
        }else if (clockData.getType() == 3){
            holder.type.setText(mContext.getString(R.string.goodNightItem));
        }
        holder.smart.setVisibility(clockData.getIsIntelligentWake()==1?View.VISIBLE:View.GONE);
        holder.cycle.setText(clockData.makeCycle());

        if (clockData.getIsOn() == 1){
            holder.onoff.setChecked(true);
            holder.time.setTextColor(mContext.getResources().getColor(R.color.alarmOn));
            holder.type.setTextColor(mContext.getResources().getColor(R.color.alarmOn));
            holder.smart.setTextColor(mContext.getResources().getColor(R.color.alarmOn));
            holder.cycle.setTextColor(mContext.getResources().getColor(R.color.alarmOn));
            if (clockData.getIsPhone() == 0){
                holder.phone.setImageResource(R.mipmap.alarm_redevice_sleepee_on);
            }else {
                holder.phone.setImageResource(R.mipmap.alarm_redevice_phone_on);
            }
        }else {
            holder.onoff.setChecked(false);
            holder.time.setTextColor(mContext.getResources().getColor(R.color.alarmOff));
            holder.type.setTextColor(mContext.getResources().getColor(R.color.alarmOff));
            holder.smart.setTextColor(mContext.getResources().getColor(R.color.alarmOff));
            holder.cycle.setTextColor(mContext.getResources().getColor(R.color.alarmOff));
            if (clockData.getIsPhone() == 0){
                holder.phone.setImageResource(R.mipmap.alarm_redevice_sleepee_off);
            }else {
                holder.phone.setImageResource(R.mipmap.alarm_redevice_phone_off);
            }
        }



        holder.delete.setLayoutParams(new LinearLayout.LayoutParams(mContext.getResources().getDisplayMetrics().widthPixels / 5, LinearLayout.LayoutParams.MATCH_PARENT));
        holder.delete.setTag(position);


        if (holder.onoff!=null){
            holder.onoff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((CheckBox)v).isChecked()){
                        if (clockData.getType()==2){
                            if (BleService.getInstance().isConnect()){
                                BleService.getInstance().write(ProtocolWriter.writeForAddClock(clockData.getIsPhone()==0?(byte) 0x0:0x01,(byte)0x1,(byte)0x0,
                                        (byte)0x0,getRepeat(clockData.getRepeat()),(byte) clockData.getHour(),(byte) clockData.getMinute(),
                                        clockData.getIsIntelligentWake() ==1?(byte)0x1:0x00,(byte)2));

                                try {
                                    HttpMessgeUtil.getInstance(mContext).postForChangeClock(clockData.getId()+"",clockData.getType()+"",
                                            clockData.getHour()+"",clockData.getMinute()+"","-1",clockData.getIsPhone()+"",
                                            "1",MathUitl.ArrayToString(clockData.getRepeat()),"","0",clockData.getRemark(),-1);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                clockData.setIsOn(1);
                            }else {
                                Toast.makeText(mContext,mContext.getString(R.string.bluetoochError),Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            ProgressHudModel.newInstance().show(mContext,mContext.getString(R.string.waitting),mContext.getString(R.string.httpError),
                                    5000);
                            pos = position;
                            try {
                                HttpMessgeUtil.getInstance(mContext).postForChangeClock(clockData.getId()+"",clockData.getType()+"",
                                        clockData.getHour()+"",clockData.getMinute()+"","-1",clockData.getIsPhone()+"",
                                        "1",MathUitl.ArrayToString(clockData.getRepeat()),"","0",clockData.getRemark(),-1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            clockData.setIsOn(1);
                        }
                    }else {
                        if (clockData.getType()==2){
                            if (BleService.getInstance().isConnect()){
                                BleService.getInstance().write(ProtocolWriter.writeForDeleteClock((byte) list.get(position).getIndex()));
                                try {
                                    HttpMessgeUtil.getInstance(mContext).postForChangeClock(clockData.getId()+"",clockData.getType()+"",
                                            clockData.getHour()+"",clockData.getMinute()+"","-1",clockData.getIsPhone()+"",
                                            "0",MathUitl.ArrayToString(clockData.getRepeat()),"","0",clockData.getRemark(),-1);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                clockData.setIsOn(0);
                            }else {
                                Toast.makeText(mContext,mContext.getString(R.string.bluetoochError),Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            ProgressHudModel.newInstance().show(mContext,mContext.getString(R.string.waitting),mContext.getString(R.string.httpError),
                                    5000);
                            pos = position;
                            try {
                                HttpMessgeUtil.getInstance(mContext).postForChangeClock(clockData.getId()+"",clockData.getType()+"",
                                        clockData.getHour()+"",clockData.getMinute()+"","-1",clockData.getIsPhone()+"",
                                        "0",MathUitl.ArrayToString(clockData.getRepeat()),"","0",clockData.getRemark(),-1);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            clockData.setIsOn(0);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

        if (holder.delete != null) {
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clockData.getType()==2){
                        if (BleService.getInstance().isConnect()){
                            BleService.getInstance().write(ProtocolWriter.writeForDeleteClock((byte) list.get(position).getIndex()));
                            Log.d("CLOCK******","clock is delet for me:"+String.format("at:%d,%d:%d",list.get(position).getId(),
                                    list.get(position).getHour(),list.get(position).getMinute()));

                            try {
                                HttpMessgeUtil.getInstance(mContext).getForDeleteClock(clockData.getId() + "", callback,0);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ((MyApplication)mContext.getApplicationContext()).removeClock(position);
                            notifyDataSetChanged();
                        }else {
                            Toast.makeText(mContext,mContext.getString(R.string.bluetoochError),Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        ProgressHudModel.newInstance().show(mContext,mContext.getString(R.string.waitting),mContext.getString(R.string.httpError),
                                5000);
                        pos = position;
                        try {
                            HttpMessgeUtil.getInstance(mContext).getForDeleteClock(clockData.getId()+"",callback,1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        return view;
    }

    private GenericsCallback<BaseApi> callback = new GenericsCallback<BaseApi>(new JsonGenericsSerializator()) {
        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(BaseApi response, int id) {

            ProgressHudModel.newInstance().diss();
            if (response.getCode() == 200){
                if (id == 1){
                    ((MyApplication)mContext.getApplicationContext()).removeClock(pos);
                    notifyDataSetChanged();
                }
            }else if (response.getCode() == 401){
                SharedPreferences sharedPreferences ;
                BleService.getInstance().disConnect();
                BleService.getInstance().setmMac(null);
                ((MyApplication)(mContext.getApplicationContext())).clearClockList();
                ((MyApplication)(mContext.getApplicationContext())).setReportDate(DateUtil.getStringToDate("today"));
                SaveDataUtil.newInstance(mContext).clearDB();
                MathUitl.showToast(mContext,mContext.getString(R.string.tokenTimeout));
                Intent intentmain=new Intent(mContext,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intentmain);
            }else {
                MathUitl.showToast(mContext,response.getMessage());
            }
        }
    };


    private byte getRepeat(ArrayList<String> repeat){
        byte repeatState = 0;
        if (repeat.contains("7")){
            repeatState = (byte) (repeatState|0x01);
        }if (repeat.contains("1")){
            repeatState = (byte) (repeatState|(0x01<<1));
        }if (repeat.contains("2")){
            repeatState = (byte) (repeatState|(0x01<<2));
        }if (repeat.contains("3")){
            repeatState = (byte) (repeatState|(0x01<<3));
        }if (repeat.contains("4")){
            repeatState = (byte) (repeatState|(0x01<<4));
        }if (repeat.contains("5")){
            repeatState = (byte) (repeatState|(0x01<<5));
        }if (repeat.contains("6")){
            repeatState = (byte) (repeatState|(0x01<<6));
        }
        return repeatState;
    }

    class HolderView{
        TextView time;
        TextView type;
        TextView smart;
        TextView cycle;
        ImageView phone;
        CheckBox onoff;
        Button delete;
    }
}
