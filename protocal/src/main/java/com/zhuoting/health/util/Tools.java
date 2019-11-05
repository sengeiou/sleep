package com.zhuoting.health.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;


import com.zhuoting.health.bean.RunInfo;
import com.mycj.protocal.R;
import com.zhuoting.health.bean.PhoneModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Tools {

	static {
		System.loadLibrary("JNICrc");
	}

	public static String username;

	/**
	 * 二进制转十六进制
	 * @param s
	 * @return
	 */
	public static String BinaryToHex(String s){
		if (s.equals("")) {
			return "0";
		}
		return Long.toHexString(Long.parseLong(s,2));
	}

	/**
	 * 获取uuid
	 */
	public static String getUUID(){
		UUID uuid = UUID.randomUUID();
		String uniqueId = uuid.toString();
		return uniqueId;

	}


	public static int getDpi(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		int height = 0;
		@SuppressWarnings("rawtypes")
		Class c;
		try {
			c = Class.forName("android.view.Display");
			@SuppressWarnings("unchecked")
			Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
			method.invoke(display, dm);
			height = dm.heightPixels;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return height;
	}
	public static int[] getScreenWH(Context poCotext) {
		WindowManager wm = (WindowManager) poCotext
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		return new int[] { width, height };
	}
	public static int getVrtualBtnHeight(Context poCotext) {
		int location[] = getScreenWH(poCotext);
		int realHeiht = getDpi((Activity) poCotext);
		int virvalHeight = realHeiht - location[1];
		return virvalHeight;
	}


	public static byte[] makeSend(byte[] test){
		byte[] testc = new byte[test.length + 1];

		int count = 0;

		for (int i = 0; i < test.length; i++) {
			testc[i+count] = test[i];
			if (i == 0){
				int length = test.length + 2;
				testc[1] = (byte) (length);
				count = 1;
			}
		}

		return makeCRC16(testc);
	}



	public static byte[] makeCRC16(byte[] test){
		byte crc = (new Crc16Util()).makeCRC(test,test.length);

		byte[] testc = new byte[test.length + 1];
		for (int i = 0; i < test.length; i++) {
			testc[i] = test[i];
		}
		testc[test.length] = crc;
		Log.e("crc16",DataUtil.byteToHexString(testc));
		return testc;
	}


	//====数据拆分
	//====数据拆分
	public static List<byte[]> makeSendMsg(byte[] data,int datasize){
		List<byte[]> msglist = new ArrayList<byte[]>();
		int index = data.length/datasize;
		if (data.length%datasize > 0) {
			index = index + 1;
		}
		for (int i = 0;i<index;i++) {
			int lenght = datasize;
			if (i*datasize+datasize > data.length) {
				lenght = (int)data.length - i*datasize;
			}
			byte[] msg = new byte[lenght];

			System.arraycopy(data, i*datasize, msg, 0, lenght);

			msglist.add(msg);
		}

		return msglist;
	}




	public static String logbyte(byte[] data){
		if (data == null){
			return "null";
		}
		String revmsg = "";
		for(int n=0;n<data.length;n++){
			String aa = Integer.toHexString(data[n]);
			if (aa.length() == 1) {
				revmsg = revmsg + "0"+aa+",";
			}else{
				aa = aa.replace("ffffff", "");
				revmsg = revmsg +aa+",";
			}
		}
		return revmsg;
	}


}
