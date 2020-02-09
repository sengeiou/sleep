package com.szip.smartdream.Util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.szip.smartdream.MyApplication;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtil {

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SleepEE/";
    private File filePath = new File(path);

    private Context mContext;
    private MyApplication app;

    public FileUtil(Context mContext){
        this.mContext = mContext;
        Init();
    }

    private void Init() {
        if (!filePath.exists()) {
            filePath.mkdirs();
            Log.d("FileXml", "XML已经存在");
        }
    }

    public String getPath(){
        return path;
    }

    public boolean downloadAble(String fileName){
        File[] files;
        files = filePath.listFiles();
        for (File file:files){
            if (file.getName().indexOf(fileName)>=0){
                return false;
            }else if (file.getName().indexOf(fileName.substring(0,4))>=0){
                file.delete();
                return true;
            }else {
                return true;
            }
        }
        return true;
    }

    public static void writeToDat(String fileName, byte[] data){
        try {

            FileOutputStream fout = new FileOutputStream(fileName,true);

            fout.write(data);
            fout.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
