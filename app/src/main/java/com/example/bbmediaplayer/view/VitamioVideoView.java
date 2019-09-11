package com.example.bbmediaplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import io.vov.vitamio.widget.VideoView;

public class VitamioVideoView extends VideoView {

    /**
     * 代码中创建时调用
     * @param context
     */
    public VitamioVideoView(Context context) {
        this(context,null);
    }

    /**
     * 布局中使用时调用
     * @param context
     * @param attrs
     */
    public VitamioVideoView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    /**
     * 设置样式时调用
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public VitamioVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    /**
     * 设置视频的尺寸
     * @param videoWidth
     * @param videoHeight
     */
    public void setVideoSize(int videoWidth,int videoHeight){
        ViewGroup.LayoutParams params = getLayoutParams();//获得布局信息参数
        params.width=videoWidth;
        params.height=videoHeight;
        setLayoutParams(params);//重新设置布局

    }
}
