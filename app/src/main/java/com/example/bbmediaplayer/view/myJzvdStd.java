package com.example.bbmediaplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * @ProjectName: BbMediaPlayer
 * @Package: com.example.bbmediaplayer.base
 * @ClassName: myJzvdStd
 * @Author: Bb
 * @CreateDate: 2019/9/5 18:29
 * @Version: 1.0
 * @Description: java类作用描述
 */
public class myJzvdStd extends JzvdStd {
    Context context;
    public myJzvdStd(Context context) {
        this(context,null);
    }

    public myJzvdStd(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setFocusableInTouchMode(true);
        setScreen(0);
    }

    @Override
    public void clearFloatScreen() {
        super.clearFloatScreen();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        if(screen == 1){
//            clearFloatScreen();
//        }

//        Toast.makeText(context, screen+"", Toast.LENGTH_SHORT).show();
        return super.onTouch(v, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//        Toast.makeText(context, "onKeyLongPress", Toast.LENGTH_SHORT).show();
        return super.onKeyLongPress(keyCode, event);
    }



}
