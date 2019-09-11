package com.example.bbmediaplayer.application;

import android.app.Application;

import com.example.bbmediaplayer.R;
import com.example.bbmediaplayer.utils.CacheUtils;
import com.iflytek.cloud.SpeechUtility;

import org.xutils.BuildConfig;
import org.xutils.x;

import cn.jzvd.Jzvd;

public class Myapplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
        SpeechUtility utility = SpeechUtility.createUtility(this, "appid=5d5ce90d");
    }



}
