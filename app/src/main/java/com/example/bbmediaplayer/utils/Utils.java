package com.example.bbmediaplayer.utils;

import android.content.Context;
import android.graphics.Color;
import android.net.TrafficStats;
import android.os.Message;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Random;


public class Utils {
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public Utils() {
        //转换成字符串的时间
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    /**
     * 把毫秒转换成：1：20：30这样的形式
     *
     * @param timeMs
     * @return
     */
    public String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 判断是否是网络Uri
     * @param uri
     * @return
     */
    public static boolean isNetUri(String uri){
        boolean reasult = false;
        if(uri!=null){
            if(uri.toLowerCase().startsWith("http")||uri.toLowerCase().startsWith("rtsp")||uri.toLowerCase().startsWith("mms")){
                reasult = true;
            }
        }
        return  reasult;
    }

    /**
     * 得到网速，每隔两秒调用一次
     * @param context
     * @return
     * 模拟器可能不支持getUidRxBytes方法，会返回TrafficStats.UNSUPPORTED，值为-1
     */
    public String getNetSpeed(Context context) {
        String netspeed = "0 kb/s";
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid)== TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        netspeed = speed + " kb/s";

//        LogUtil.e("----------start--------:");
//        LogUtil.e("nowTotalRxBytes:"+nowTotalRxBytes);
//        LogUtil.e("TrafficStats.getTotalTxBytes():"+TrafficStats.getTotalTxBytes());
//        LogUtil.e("lastTotalRxBytes:"+lastTotalRxBytes);
//        LogUtil.e("nowTimeStamp - lastTimeStamp:"+(nowTimeStamp - lastTimeStamp));
//        LogUtil.e("netspeed:"+netspeed);
//        LogUtil.e("----------end--------:");
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;

        return netspeed;
    }

    /**
     * 得到系统时间
     * @return
     */
    public static String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    /**
     * 获取十六进制的颜色代码.例如  "#5A6677"
     * 分别取R、G、B的随机值，然后加起来即可
     *
     * @return String
     */
    public static String getRandStrColor() {
        String R, G, B;
        Random random = new Random();
        R = Integer.toHexString(random.nextInt(256)).toUpperCase();
        G = Integer.toHexString(random.nextInt(256)).toUpperCase();
        B = Integer.toHexString(random.nextInt(256)).toUpperCase();

        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;
        return R + G + B;
    }

    /**
     * 获取int颜色
     *
     * @return int
     */
    public static int getRandIntColor() {
        int r,g,b;
        Random random = new Random();
        r = random.nextInt(256);
        g = random.nextInt(256);
        b = random.nextInt(256);

        return Color.rgb(r,g,b);
    }


}