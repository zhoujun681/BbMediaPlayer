package com.example.bbmediaplayer.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.bbmediaplayer.R;
import com.example.bbmediaplayer.base.BasePager;
import com.example.bbmediaplayer.pagers.AudioPager;
import com.example.bbmediaplayer.pagers.NetAudioPager;
import com.example.bbmediaplayer.pagers.NetVideoPager;
import com.example.bbmediaplayer.pagers.VideoPager;

import java.util.ArrayList;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class MainActivity extends BaseActivity {
    private FrameLayout fl_main_content;
    private RadioGroup rg_bottom_tag;
    private static ArrayList<BasePager> basePagers; //新建一个列表存储四个页面
    private static int position;//当前显示的页面序号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fl_main_content = findViewById(R.id.fl_main_content);
        rg_bottom_tag = findViewById(R.id.rg_bottom_tag);
        String [] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CAMERA};
        //获得授权
        performCodeWithPermission("获取动态权限", new PermissionCallback() {
                    @Override
                    public void hasPermission() {

                    }

                    @Override
                    public void noPermission() {

                    }
                }, permissions);
        /**
         * 初始化四个页面
         */
        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this));
        basePagers.add(new AudioPager(this));
        basePagers.add(new NetVideoPager(this));
        basePagers.add(new NetAudioPager(this));


        //设置RadioGroup监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        //设置默认选中的页面，注意要放到初始化和监听后面
        rg_bottom_tag.check(R.id.rb_video);
    }

    /**
     * 设置RadioGroup监听接口类
     */
    private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                default:
                    position = 0;  //默认为本地视频
                    break;
                case R.id.rb_audio:
                    position = 1;
                    break;
                case R.id.rb_net_video:
                    position = 2;
                    break;
                case R.id.rb_net_audio:
                    position = 3;
                    break;
            }
            JzvdStd.releaseAllVideos();
            setFrament();
        }
    }

    /**
     * 把页面添加到Frament中
     */
    private void setFrament() {
        //1.得到FramentManager,注意页面必须继承FramentActivity才能使用这个方法
        FragmentManager manager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction ft = manager.beginTransaction();
        //3.替换
        ft.replace(R.id.fl_main_content, new MyFragment());

        //4.提交事务
        ft.commit();
    }

    public static class MyFragment extends Fragment {
        private static BasePager basePager;

        public MyFragment() {

        }

        public static final MyFragment newInstance(BasePager page) {
            MyFragment fragment = new MyFragment();
            Bundle bundle = new Bundle();
            basePager = page;
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            basePager = getBasePager();
            if (basePager != null) {
                return basePager.rootView; //返回各个视图页面
            }
            return null;
        }

    }

    private boolean isExit = false;//是否点过一次返回
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode ==  KeyEvent.KEYCODE_BACK){
//            if (position != 0) {//如果不是第一个页面则返回首页
//                position = 0;
//                rg_bottom_tag.check(R.id.rb_video);
//                return true;//截断操作
//            }else if(!isExit){
//                    isExit = true;//如果是第一次点返回键，则把标志设为true，第二次点击则不会走这个分支
//                Toast.makeText(MainActivity.this, "再点一次退出", Toast.LENGTH_SHORT).show();
//                new Handler().postDelayed(new Runnable() {//两秒之内没有再点，则把标志重新设为false
//                    @Override
//                    public void run() {
//                        isExit = false;
//                    }
//                },2000);
//                JzvdStd.backPress();
//                return true;
//            }
//        }
//        JzvdStd.backPress();
//        return super.onKeyDown(keyCode, event);
//    }


    @Override
    protected void onPause() {
        JzvdStd.releaseAllVideos();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (JzvdStd.backPress()) {
            return;
        }
        if (position != 0) {//如果不是第一个页面则返回首页
            position = 0;
            rg_bottom_tag.check(R.id.rb_video);
            return;
        } else if (!isExit) {
            isExit = true;//如果是第一次点返回键，则把标志设为true，第二次点击则不会走这个分支
            Toast.makeText(MainActivity.this, "再点一次退出", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {//两秒之内没有再点，则把标志重新设为false
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
            return;
        }

        super.onBackPressed();
    }


    @Override
    protected void onStop() {
        super.onStop();
        JzvdStd.releaseAllVideos();
    }

    @Override
    protected void onDestroy() {
        JzvdStd.releaseAllVideos();
        super.onDestroy();
    }

    /**
     * 根据位置获取页面
     *
     * @return
     */
    private static BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        if (basePager != null && !basePager.isInitData) {//第一次初始化才初始化数据
            basePager.initData();//创建时初始化数据,如联网或请求数据
            basePager.isInitData = true;
        }
        return basePager;
    }


}
