package com.example.bbmediaplayer.utils;

import android.content.Context;

public class DensityUtil {
    /**
     * 根据手机分辨率从dip转为px(像素)
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context,float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale + 0.5f);
    }

    /**
     * 根据手机分辨率从px转为dip
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context,float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue/scale + 0.5f);
    }
}
