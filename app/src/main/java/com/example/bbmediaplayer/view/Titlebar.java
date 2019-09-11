package com.example.bbmediaplayer.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bbmediaplayer.R;
import com.example.bbmediaplayer.activity.SearchActivity;

public class Titlebar extends LinearLayout implements View.OnClickListener {
    private View tv_search;
    private View rl_game;
    private View iv_record;
    private Context context;
    /**
     * 在代码中实例化该类的时候使用这个方法
     *
     * @param context
     */
    public Titlebar(Context context) {
        this(context, null);
    }

    /**
     * 当布局文件使用该类的时候，android使用这个构造方法实例化该类
     *
     * @param context
     * @param attrs
     */
    public Titlebar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 当需要设置样式的时候使用这个构造方法
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public Titlebar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    /**
     * 当布局文件加载完成后回调这个方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //得到孩子实例
        rl_game = getChildAt(2);
        tv_search = getChildAt(1);
        iv_record = getChildAt(3);
        //设置点击事件
        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search_title:
                Intent intent = new Intent(context, SearchActivity.class);
                context.startActivity(intent);
                break;
            case R.id.rl_game:
                Toast.makeText(context, "游戏功能尚未上线，敬请期待", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_record:
                Toast.makeText(context, "播放历史功能尚未上线，敬请期待", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}