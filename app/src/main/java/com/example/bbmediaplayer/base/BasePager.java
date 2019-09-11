package com.example.bbmediaplayer.base;

import android.content.Context;
import android.view.View;

/**
 * 基类页面
 * VideoPager
 * NetVideoPager
 * AudioPager
 * NetAudioPager
 * 四个页面将继承这个类
 */

public abstract class BasePager {
    //上下文，子类需要用，所有用public
    public final Context context;
    public View rootView;
    public boolean isInitData;

    public BasePager(Context context) {
        this.context=context;
        rootView = initView();
    }

    /**
     * 子类必须实现
     * 用于特定的效果
     * @return
     */
    public abstract View initView();

    /**
     * 当子页面需要请求数据，如联网请求数据或绑定本地数据时重写这个方法。
     */
    public void initData(){

    }


}
