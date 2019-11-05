package com.zhuoting.health.bean;


import com.zhuoting.health.util.Tools;

import java.io.Serializable;


public class ClockInfo implements Serializable{
	public boolean t_open;
	public int c_hour;
	public int c_min;
	public String valueArray;

	public ClockInfo(){
		valueArray = "0";
	}

	public ClockInfo(byte []smsg){
		System.out.println(Tools.logbyte(smsg));

		int week =  (smsg[3] & 0xff);

		String str = Integer.toBinaryString(week);
		System.out.println(str.length());
		if (str.length() < 8) {
			int lg = 8-str.length();
			for (int i = 0; i < lg; i++) {
				str = "0"+str;
			}
		}

//		if (week == 0){
//			str = "0";
//		}
		

		
		String val = "";
		for (int i = 0; i<8; i++) {
		     int ts = Integer.parseInt(str.substring(i, i+1));
		     if (i == 0) {
				if (ts == 0) {
					t_open = false;
				}else{
					t_open = true;
				}
			}else{
				if (ts == 1) {
					if (!val.equals("")) {
						val = (8-i) + "," + val;
					}else{
						val = (8-i) + val;
					}
				}
			}
		}
		if (val.equals("")) {
			val = "0";
		}
		
		valueArray = val;


		byte[] rates2 = { 0x00, smsg[1] };
		c_hour = smsg[1] & 0xff;//Tools.BCD_TO_TEN(TransUtils.bytes2short(rates2));
		
		byte[] rates3 = { 0x00, smsg[2] };
		c_min = smsg[2] & 0xff;//Tools.BCD_TO_TEN(TransUtils.bytes2short(rates3));
	}
	
	public int getweek(){
		String[] str = valueArray.split(",");
		String val = "";
		for (int i = 0; i<8; i++) {
			if (i == 7) {
				if (t_open) {
					val = "1" + val;	
				}else{
					val = "0" + val;
				} 
				break;
			}
	        boolean on = false;
	        for (String msg : str) {
	            if (i == Integer.parseInt(msg)-1) {
	                on = true;
	                break;
	            }
	        }
	        if (on) {
	        	val = "1" + val;	
			}else{
				val = "0" + val;
			} 
		}
		
		int week = Integer.parseInt(Tools.BinaryToHex(val), 16);
		return week;
	}


	@Override
	public String toString() {
		return "ClockInfo{" +
				"t_open=" + t_open +
				", c_hour=" + c_hour +
				", c_min=" + c_min +
				", valueArray='" + valueArray + '\'' +
				'}';
	}
}
