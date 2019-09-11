package com.example.bbmediaplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PicUtils {

    public static   void saveBitmap(Context context,String bitName, Bitmap mBitmap) {
        if(mBitmap == null){
            return;
        }
        String path = context.getApplicationContext().getFilesDir().getAbsolutePath()+ "/pic/";
        File file = new File(path);
        if (!file.exists())
        {
            file.mkdirs();
        }
        path = path+bitName + ".png";
        Log.e("BB","PATH：" + path);
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (IOException e) {
            Log.e("BB","在保存图片时出错：" + e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmap(Context context, String bitName) {
        String path=context.getApplicationContext().getFilesDir().getAbsolutePath() + "/pic/" +  bitName + ".png";
        File f = new File(path);
        if(f.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            return bitmap;
        }
        return null;
    }


    public static  void clearCache(Context context) {
        String path=context.getApplicationContext().getFilesDir().getAbsolutePath() + "/pic/";
        File files = new File(path);
        File [] mfiles = files.listFiles();
// 删除子文件夹和子文件
        for (File file : mfiles){
                file.delete();
        }

    }
}
