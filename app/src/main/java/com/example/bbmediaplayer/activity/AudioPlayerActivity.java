package com.example.bbmediaplayer.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bbmediaplayer.IMusicPlayerService;
import com.example.bbmediaplayer.R;
import com.example.bbmediaplayer.domain.MediaItem;
import com.example.bbmediaplayer.service.MusicPlayerService;
import com.example.bbmediaplayer.utils.LyricUtils;
import com.example.bbmediaplayer.utils.Utils;
import com.example.bbmediaplayer.view.BaseVisualizerView;
import com.example.bbmediaplayer.view.ShowLyricView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

public class AudioPlayerActivity extends Activity implements View.OnClickListener {
    //定义区----------------------------------------------------------------------------------------
    private static final int PROCESS = 1;//进度更新标志
    private static final int SHOW_LYRIC = 2; //显示歌词消息标志
    private ImageView iv_icon;
    private int position;
    private TextView tvName;
    private TextView tvArtist;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPlaymode;
    private Button btnAudioPre;
    private Button btnAudioPlayPause;
    private Button btnAudioNext;
    private Button btnLyrc;
    private ShowLyricView showLyricView;
    private BaseVisualizerView baseVisualizerView;

    private Utils utils;
    private boolean notification;//是否从通知栏进入,true:通知栏,false:播放列表
    private IMusicPlayerService service;
    private MyReceiver receiver;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_LYRIC: //显示歌词
                    //1.得到当前进度
                    try {
                        int currentPosition = service.getCurrentPosition();
                        //2.把进度传入ShowLyricView控件，并计算哪一句高亮
                        showLyricView.setShowNextLyric(currentPosition);
                        //3.实时发消息
                        handler.removeMessages(SHOW_LYRIC);
                        handler.sendEmptyMessage(SHOW_LYRIC);//为了性能100毫秒刷新一次足够
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                case PROCESS://更新进度
                    try {
                        //1.得到当前进度
                        int currentPosition = service.getCurrentPosition();
                        //2.设置seekbar进度条
                        seekbarAudio.setProgress(currentPosition);
                        //3.时间显示更新
                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getCurrentDuration()));
                        //4.每秒更新一次
                        handler.removeMessages(PROCESS);
                        handler.sendEmptyMessageDelayed(PROCESS, 1000);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        /**
         * 当连接成功时调用
         * @param name
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMusicPlayerService.Stub.asInterface(iBinder);
            if (service != null) {
                try {
                    if (!notification) {
                        //当不是来自通知栏的时候打开媒体
                        service.openAudio(position);
                    } else {
                        //来自通知栏的时候,只更新媒体信息
                        showViewData();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }


        /**
         * 当断开连接时调用
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (service != null) {
                try {
                    service.stop();
                    service = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //方法区------------------------------------------------------------------------------------
    //---构造方法区-----------------------------------------------------------------------------
    //---继承方法区-----------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getData();
        bindAndStartService();
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mVisualizer != null){
            mVisualizer.release(); //释放频谱对象
        }
    }

    @Override
    protected void onDestroy() {
        //取消注册广播
//        if (receiver != null) {
//            unregisterReceiver(receiver);
//            receiver = null;//不设置也可以，设置了会优先回收
//        }
        //2.Evenbus取消注册
        EventBus.getDefault().unregister(this);
        //取消所有消息
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        if (v == btnAudioPlaymode) {
            // Handle clicks for btnAudioPlaymode
            setPlayMode();
        } else if (v == btnAudioPre) {
            // Handle clicks for btnAudioPre
            if (service != null) {
                try {
                    service.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnAudioPlayPause) {
            // Handle clicks for btnAudioPlayPause
            if (service != null) {
                try {
                    if (service.isPlaying()) {
                        //播放则暂停，按钮变为播放
                        service.pause();
                        btnAudioPlayPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                    } else {
                        //暂停则播放，按钮变为暂停
                        service.start();
                        btnAudioPlayPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnAudioNext) {
            // Handle clicks for btnAudioNext
            if (service != null) {
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnLyrc) {
            // Handle clicks for btnLyrc
        }
    }

    //---普通方法区-----------------------------------------------------------------------------
    private void initData() {
        utils = new Utils();
        /**
         * 注册广播接收器，接收播放器准备好时发送的广播
         */
//        receiver = new MyReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
//        registerReceiver(receiver, intentFilter);
        //1.使用Evenbus注册
        EventBus.getDefault().register(this);

    }



    /**
     * 3.订阅方法,参数和发布者一致就行，传入可以为空
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false, priority = 80)
    public void showData(MediaItem mediaItem) {
        //发送消息开始歌词同步
        showLyric();
        //获取歌曲信息
        showViewData();
        //获取播放模式
        checkPlayMode();
        //初始化频谱
        setupVisualizerFxAndUi();
    }

    private Visualizer mVisualizer;
    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi()
    {
        try {
            int audioSessionid = service.getAudioSessionId();
            Log.e("Bb","audioSessionid=="+audioSessionid);
            mVisualizer = new Visualizer(audioSessionid);
            // 参数内必须是2的位数
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            // 设置允许波形表示，并且捕获它
            baseVisualizerView.setVisualizer(mVisualizer);
            mVisualizer.setEnabled(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showLyric() {
        LyricUtils lyricUtils = new LyricUtils();

        //找歌词文件
        try {
            String path = service.getAudioPath();
            path = path.substring(0,path.lastIndexOf(".")); //找出歌词文件去掉文件名的部分
            File file = new File(path+".txt"); //找是否存在txt歌词
            boolean fileExists = file.exists();
            if(!fileExists){
                file = new File(path+".lrc"); //找是否存在lrc歌词
                fileExists = file.exists();
            }
        //传递歌词文件
            lyricUtils.readLyricFile(file);
        //显示歌词
            showLyricView.setLyrics(lyricUtils.getLyrics());

            if (lyricUtils.isExistsLyric()) {
                handler.sendEmptyMessage(SHOW_LYRIC);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showViewData() {
        try {
            tvArtist.setText(service.getArtist());
            tvName.setText(service.getName());
            //设置最大值
            seekbarAudio.setMax(service.getCurrentDuration());
            //发消息处理进度更新
            handler.sendEmptyMessage(PROCESS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.example.bbmediaplayer_OPENAUDIO");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);//会保证服务唯一
    }

    private void getData() {
        //获得是否来自通知栏的意图
        notification = getIntent().getBooleanExtra("Notification", false);
        //不是来自通知栏，才初始化
        if (!notification) {
            position = getIntent().getIntExtra("position", 0);
        }
    }


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-07-12 16:50:26 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_bb_audio_player);
        iv_icon = findViewById(R.id.iv_icon);
        tvName = findViewById(R.id.tv_name);
        tvArtist = findViewById(R.id.tv_artist);
        tvTime = findViewById(R.id.tv_Time);
        seekbarAudio = findViewById(R.id.seekbar_audio);
        btnAudioPlaymode = findViewById(R.id.btn_audio_playmode);
        btnAudioPre = findViewById(R.id.btn_audio_pre);
        btnAudioPlayPause = findViewById(R.id.btn_audio_play_pause);
        btnAudioNext = findViewById(R.id.btn_audio_next);
        btnLyrc = findViewById(R.id.btn_lyrc);
        showLyricView = findViewById(R.id.showLyricView);
        baseVisualizerView = findViewById(R.id.baseVisualizerView);

        iv_icon.setBackgroundResource(R.drawable.animation_list);
        btnAudioPlaymode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioPlayPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnLyrc.setOnClickListener(this);
        seekbarAudio.setOnSeekBarChangeListener(new myOnSeekBarChangeListener());

        AnimationDrawable rocketAnimation = (AnimationDrawable) iv_icon.getBackground();
        rocketAnimation.start();


    }




    /**
     * 设置播放模式
     */
    private void setPlayMode() {
        try {
            int playmode = service.getPlayMode();

            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                playmode = MusicPlayerService.REPEAT_SINGLE;
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                playmode = MusicPlayerService.REPEAT_ALL;
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            } else {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }
            //设置播放模式
            service.setPlayMode(playmode);
            //显示播放模式
            showPlayMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示播放模式
     */
    private void showPlayMode() {
        try {
            int playmode = service.getPlayMode();

            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
                Toast.makeText(AudioPlayerActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
                Toast.makeText(AudioPlayerActivity.this, "全部循环", Toast.LENGTH_SHORT).show();
            } else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 校验播放模式
     */
    private void checkPlayMode() {
        try {
            int playmode = service.getPlayMode();

            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            } else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            }
            //校验播放和暂停的按钮
            if(service.isPlaying()){
                btnAudioPlayPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
            }else {
                btnAudioPlayPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



    //内部类区-----------------------------------------------------------
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            showData(null);
        }
    }

    private class myOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
