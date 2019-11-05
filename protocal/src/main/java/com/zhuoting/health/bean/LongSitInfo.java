package com.zhuoting.health.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.json.JSONException;
import org.json.JSONObject;

public class LongSitInfo {
	public int s_hour1;
	public int s_min1;
	public int e_hour1;
	public int e_min1;

	public int s_hour2;
	public int s_min2;
	public int e_hour2;
	public int e_min2;

	public int remindGap;
	public boolean open;
	public String valueArray;//周一至周日的值


	public LongSitInfo(){
		valueArray = "1,2,3,4,5";
		s_hour1 = 8;
		e_hour1 = 12;
		s_hour2 = 13;
		s_min2 = 30;
		e_hour2 = 15;
		e_min2 = 30;

		open = false;
		remindGap = 1;
	}
	public void setValue(Context context){	
		SharedPreferences sp = context.getSharedPreferences("smartam", context.MODE_PRIVATE);
		String msg = sp.getString("longsit", null);
		
		if (msg == null) {
			return;
		}
		
		try {
			JSONObject list = new JSONObject(msg);
			
			s_hour1 = list.getInt("s_hour1");
			s_min1 = list.getInt("e_min1");

			e_hour1 = list.getInt("e_hour1");
			e_min1 = list.getInt("e_min1");

			s_hour2 = list.getInt("s_hour2");
			s_min2 = list.getInt("e_min2");

			e_hour2 = list.getInt("e_hour2");
			e_min2 = list.getInt("e_min2");

			remindGap = list.getInt("remindGap");
			open = list.getBoolean("open");

			valueArray = list.getString("valueArray"); 

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveValue(Context context){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("s_hour1", s_hour1);
			jsonObject.put("s_min1", s_min1);
			jsonObject.put("e_hour1", e_hour1);
			jsonObject.put("e_min1", e_min1);
			jsonObject.put("s_hour2", s_hour2);
			jsonObject.put("s_min2", s_min2);
			jsonObject.put("e_hour2", e_hour2);
			jsonObject.put("e_min2", e_min2);
			jsonObject.put("remindGap", remindGap);
			jsonObject.put("open", open);
			jsonObject.put("valueArray", valueArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SharedPreferences sp = context.getSharedPreferences("smartam", context.MODE_PRIVATE);
		//存入数据
		Editor editor = sp.edit();
		editor.putString("longsit", jsonObject.toString());
		editor.commit();
	}
	
	public String getTime1(){
		String str = "";
		
		if (s_hour1<10) {
			str = str+"0"+s_hour1;
		}else{
			str = str+s_hour1;
		}
		str = str+":";
		if (s_min1<10) {
			str = str+"0"+s_min1;
		}else{
			str = str+s_min1;
		}
		
		return str;
	}

	public String getTime2(){
		String str = "";
		
		if (e_hour1<10) {
			str = str+"0"+e_hour1;
		}else{
			str = str+e_hour1;
		}
		str = str+":";
		if (e_min1<10) {
			str = str+"0"+e_min1;
		}else{
			str = str+e_min1;
		}
		
		return str;
	}
	
	public String getTime3(){
		String str = "";
		
		if (s_hour2<10) {
			str = str+"0"+s_hour2;
		}else{
			str = str+s_hour2;
		}
		str = str+":";
		if (s_min2<10) {
			str = str+"0"+s_min2;
		}else{
			str = str+s_min2;
		}
		
		return str;
	}

	public String getTime4(){
		String str = "";
		
		if (e_hour2<10) {
			str = str+"0"+e_hour2;
		}else{
			str = str+e_hour2;
		}
		str = str+":";
		if (e_min2<10) {
			str = str+"0"+e_min2;
		}else{
			str = str+e_min2;
		}
		
		return str;
	}


	@Override
	public String toString() {
		return "LongSitInfo{" +
				"s_hour1=" + s_hour1 +
				", s_min1=" + s_min1 +
				", e_hour1=" + e_hour1 +
				", e_min1=" + e_min1 +
				", s_hour2=" + s_hour2 +
				", s_min2=" + s_min2 +
				", e_hour2=" + e_hour2 +
				", e_min2=" + e_min2 +
				", remindGap=" + remindGap +
				", open=" + open +
				", valueArray='" + valueArray + '\'' +
				'}';
	}
}
