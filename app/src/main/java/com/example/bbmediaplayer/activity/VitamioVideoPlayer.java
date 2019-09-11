package com.example.bbmediaplayer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bbmediaplayer.R;
import com.example.bbmediaplayer.domain.MediaItem;
import com.example.bbmediaplayer.utils.LogUtil;
import com.example.bbmediaplayer.utils.Utils;
import com.example.bbmediaplayer.view.VitamioVideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

public class VitamioVideoPlayer extends BaseActivity implements View.OnClickListener {
    private static final int PROGRESS = 1;//播放进度
    private static final int HIDE_MEDIACONTROLLER = 2;//隐藏控制面板的消息
    private static final int SHOW_SPEED = 3;

    private static final int DEFAULT_SCREEN = 1; //默认显示方式
    private static final int FULL_SCREEN = 2; //全屏方式

    private VitamioVideoView videoView;
    private Uri uri;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoPlayPause;
    private Button btnVideoNext;
    private Button btnVideoSwitchScreen;
    private RelativeLayout media_controller;
    private LinearLayout ll_buffer;
    private TextView tv_buffer_netspeed;
    private LinearLayout ll_loading;
    private TextView tv_loading_netspeed;
    //工具类
    private Utils utils;
    //电量广播接收者
    private MyReceiver receiver;
    //传入的视频列表
    ArrayList<MediaItem> mediaItems;
    //当前点击的位置
    private int position;
    //手势识别器
    private GestureDetector detector;
    //是否显示控制面板
    private boolean isShowMediaController;
    //自动隐藏控制面板的时间
    private long hideMediaControllerTime = 3000;
    //是否已经全屏
    private boolean isFullScreen;
    //屏幕的宽
    private int screenWidth;
    //屏幕的高
    private int screenHeight;
    //视频的高
    private int videoHeight;
    //视频的宽
    private int videoWidth;
    //音频管理器
    private AudioManager am;
    //当前音量
    private int currentVoice;
    //最大音量0-15
    private int maxVoice;
    //是否静音
    private boolean isMute;
    //是否是网络视频
    private boolean isNetUri;
    //是否使用系统自带的监听卡顿的功能
    private boolean isUseSystemBuffer = true;
    //上一秒播放的位置
    private int precurrentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitamio_video_player);

        //获得授权
        performCodeWithPermission("获取读取存储卡权限", new PermissionCallback() {
            @Override
            public void hasPermission() {

            }

            @Override
            public void noPermission() {

            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET);

        findViews();
        initData();
        setListener();
        getdata();
        setData();


        //设置系统提供的默认控制面板
//        videoView.setMediaController(new MediaController(this));
    }


    /**
     * 初始化数据
     */
    private void initData() {
        utils = new Utils();
        //注册电量广播
        IntentFilter initentFilter = new IntentFilter();//意图过滤
        initentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);//设置只在电量变化时接收广播
        registerReceiver(receiver, initentFilter);


        //实例化手势识别器，并重写长按、双击、单击的方法
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                playAndPause();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                switchFullScreenAndDefaultScreen();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isShowMediaController) {
                    //显示就隐藏
                    hideMediaController();
                    //移除消息
                    handler.removeMessages(HIDE_MEDIACONTROLLER);
                } else {
                    //隐藏就显示
                    showMediaController();
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, hideMediaControllerTime);
                }
                return super.onSingleTapConfirmed(e);
            }
        });

        //得到屏幕的高和宽
        DisplayMetrics displayMetrics = new DisplayMetrics();//获取一个DisplayMetrics对象
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);//得到显示数据并更新DisplayMetrics对象
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        //得到音量
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);//获取媒体当前音量
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//获取媒体最大音量
        //设置seekbar，必须保证seekbarVoice已经初始化
        seekbarVoice.setMax(maxVoice);
        seekbarVoice.setProgress(currentVoice);


    }

    private void setData() {
        //设置播放地址
        if (mediaItems != null && mediaItems.size() > 0) {
            //传递播放列表情况
            MediaItem mediaItem = mediaItems.get(position);//获取点击的项目
            tvName.setText(mediaItem.getName());//设置标题
            isNetUri = Utils.isNetUri(mediaItem.getData());
//            Toast.makeText(this, mediaItem.getData(), Toast.LENGTH_SHORT).show();
            videoView.setVideoPath(mediaItem.getData());//设置播放地址
        } else if (uri != null) {
            //传递url情况
            tvName.setText(uri.toString());
            isNetUri = Utils.isNetUri(uri.toString());
//            Toast.makeText(this, "播放地址:" + uri, Toast.LENGTH_SHORT).show();
            videoView.setVideoURI(uri);
        } else {
            //未传递正确
            Toast.makeText(this, "老铁，你提供的地址为空或者异常！！！", Toast.LENGTH_SHORT).show();
        }
        //设置按钮状态
        setButtonState();
    }

    private void getdata() {
        //得到播放器地址
        uri = getIntent().getData();//直接得到播放地址，用于直接传url的情况
        //得到传入的播放列表
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("vediolist");
        //得到当前点击位置
        position = getIntent().getIntExtra("position", 0);
    }

    private void switchFullScreenAndDefaultScreen() {
        if (isFullScreen) {
            setVideoType(DEFAULT_SCREEN);
        } else {
            setVideoType(FULL_SCREEN);
        }
    }

    private void setVideoType(int defaultScreen) {
        switch (defaultScreen) {
            case FULL_SCREEN://全屏模式
                //1.设置视频大小--屏幕尺寸
                videoView.setVideoSize(screenWidth, screenHeight);

                //2.设置按钮状态--默认
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_screen_default_selector);
                isFullScreen = true;
                break;
            case DEFAULT_SCREEN://默认状态
                //1.设置视频大小--短边适配算法,拷贝自系统
                //视频实际大小
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;
                //屏幕大小
                int height = screenHeight;
                int width = screenWidth;
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                videoView.setVideoSize(width, height);
                //2.设置按钮状态--全屏
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_screen_full_selector);
                isFullScreen = false;
                break;
        }
    }

    //开始的Y轴
    private float startY;
    private float startX;
    //屏幕的高
    private float touchRang;
    //当前按下时的音量，不随拖动改变
    private int mVol;

    /**
     * 触碰事件
     *
     * @param event
     * @return 往下滑动
     * float distanceY = startY - endY  < 0;
     * 往上滑动
     * float distanceY = startY - endY > 0;
     * 滑动屏幕的距离： 总距离 = 改变声音：音量最大值
     * 改变声音 = （滑动屏幕的距离： 总距离）*音量最大值
     * 最终声音 = 原来的 + 改变声音；
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把触摸事件传递给手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://手指按下
                //1.记录按下相关值
                startY = event.getY();
                startX = event.getX();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight, screenWidth);//取较小边长
                handler.removeMessages(HIDE_MEDIACONTROLLER);//移除自动隐藏消息
                break;
            case MotionEvent.ACTION_MOVE://手指移动
                //2.记录滑动相关值
                float endY = event.getY();//滑动到的Y轴
                float endX = event.getX();
                float distinceY = startY - endY;//计算滑动距离
                if (endX < screenWidth/2){
                    //调节亮度
                    final double FLING_MIN_DISTANCE = 0.5;
                    final double FLING_MIN_VELOCITY = 0.5;
                    if (distinceY > FLING_MIN_DISTANCE && Math.abs(distinceY) > FLING_MIN_VELOCITY) {
                        setBrightness(10);
                    }
                    if (distinceY < FLING_MIN_DISTANCE && Math.abs(distinceY) > FLING_MIN_VELOCITY) {
                        setBrightness(-10);
                    }

                }else {
                    //调节音量
                    //改变声音 = （滑动屏幕的距离： 总距离）*音量最大值
                    float delta = (distinceY / touchRang) * maxVoice;
                    //最终声音 = 原来的 + 改变声音；
                    int voice = (int) Math.min(Math.max(mVol + delta, 0), maxVoice);//超出最大取最大，小于0取0，其他取当前音量+调节音量
                    if (delta != 0) {
                        //如果调节值不为0，则调节音量。
                        isMute = false;
                        updateVoice(voice, isMute);
                    }
                }

                break;
            case MotionEvent.ACTION_UP://手指离开
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, hideMediaControllerTime);//重新发送自动隐藏
                break;
        }
        return super.onTouchEvent(event);
    }


    /*
     * 设置屏幕亮度
     * 0 最暗
     * 1 最亮
     */
    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0.1) {
            lp.screenBrightness = (float) 0.1;
        }
        getWindow().setAttributes(lp);

        float sb = lp.screenBrightness;
        Toast.makeText(this, "亮度：" + (int) Math.ceil(sb * 100) + "%", Toast.LENGTH_SHORT).show();
    }

        /**
         * 接收电量的广播接收者
         */
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);//获取系统的电量广播
            setBattery(level);
        }
    }

    /**
     * 设置电量改变
     */
    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_m_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_m_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_m_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_m_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_m_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_m_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_m_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_m_100);
        }
    }

    private void setListener() {
        //准备好播放监听
        videoView.setOnPreparedListener(new MyOnPreparedListener());
        //播放错误监听
        /*播放出错情况：
        1.播放的视频格式不支持
            跳转到万能播放器
        2.播放网络视频时网络中断
            重试3次，不成功则退出
        3.播放的文件有问题，如有空白段
            重新下载文件
        */
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(VitamioVideoPlayer.this, "播放出错了", Toast.LENGTH_SHORT).show();
                showErrorDialog();
                return true;
            }
        });
        //播放完成监听
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                Toast.makeText(BBsSystemVideoPlayer.this, "播放完成", Toast.LENGTH_SHORT).show();
                playNextVideo();
            }
        });
        //设置视频进度条拖动的监听
        seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * 当手指拖动进度条会回调
             * @param seekBar
             * @param progress
             * @param fromUser 用户引起的是true,否则为false
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }

            /**
             * 当手指触碰回调
             * @param seekBar
             * @param seekBar
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //触碰进度条时不自动隐藏
                handler.removeMessages(HIDE_MEDIACONTROLLER);

            }

            /**
             * 当手指离开时回调
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //手指离开时开始触发自动隐藏
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, hideMediaControllerTime);
            }
        });


        //设置音量条拖动监听
        seekbarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * 当手指拖动进度条会回调
             * @param seekBar
             * @param progress
             * @param fromUser 用户引起的是true,否则为false
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    //如果是用户触发的，就更新音量
                    //如果用户拖动了，也把静音标志设为false
                    isMute = progress <= 0;
                    updateVoice(progress, isMute);
                }
            }

            /**
             * 当手指触碰回调
             * @param seekBar
             * @param seekBar
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //触碰进度条时不自动隐藏
                handler.removeMessages(HIDE_MEDIACONTROLLER);
            }

            /**
             * 当手指离开时回调
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //手指离开时开始触发自动隐藏
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, hideMediaControllerTime);
            }
        });

        //监听视频卡顿,必须大于4.22即api17才有这个功能
        if (isUseSystemBuffer) {
            videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {

                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START://视频开始卡了
//                            Toast.makeText(BBsSystemVideoPlayer.this, "视频有点卡", Toast.LENGTH_SHORT).show();
                            ll_buffer.setVisibility(View.VISIBLE);
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END://视频卡顿结束
//                            Toast.makeText(BBsSystemVideoPlayer.this, "视频卡顿结束了", Toast.LENGTH_SHORT).show();
                            ll_buffer.setVisibility(View.GONE);

                            break;
                    }
                    return true;//返回true代表已经处理
                }
            });
        }


    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bb提醒你");
        builder.setMessage("抱歉，无法播放此视频！！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    /**
     * 调节声音
     *
     * @param progress 声音大小
     * @param isMute   是否静音
     */
    private void updateVoice(int progress, boolean isMute) {
        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);//flag为是否调用系统音频管理
            seekbarVoice.setProgress(0);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);//flag为是否调用系统音频管理
            seekbarVoice.setProgress(progress);//设置seekbar进度
            currentVoice = progress;//保存当前音量onc
        }
    }

    /**
     * Called when pointer capture is enabled or disabled for the current window.
     *
     * @param hasCapture True if the window has pointer capture.
     */
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //更新网速
                case SHOW_SPEED:
                    //得到网速
                    String netSpeed = utils.getNetSpeed(VitamioVideoPlayer.this);
                    //更新文本
                    tv_loading_netspeed.setText("Bb正在为你玩命加载中..." + netSpeed);
                    tv_buffer_netspeed.setText("Bb正在为你玩命缓冲中..." + netSpeed);
                    //2秒更新一次网速
                    handler.removeMessages(SHOW_SPEED);//先移除消息
                    handler.sendEmptyMessageDelayed(SHOW_SPEED, 2000);//延迟2秒发送
                    break;
                //处理隐藏控制面板
                case HIDE_MEDIACONTROLLER:
                    hideMediaController();
                    break;
                //处理播放进度
                case PROGRESS:
                    //1.得到播放进度
                    int currentPosition = (int) videoView.getCurrentPosition();
//                    LogUtil.e("当前播放到:"+currentPosition);
                    //2.设置进度条和时间
                    seekbarVideo.setProgress(currentPosition);
                    tvCurrentTime.setText(utils.stringForTime(currentPosition)); //设置当前播放到的时间
                    tvSystemTime.setText(getSystemTime());//设置系统时间
                    //3.设置缓冲
                    if (isNetUri) {
                        //如果是网络视频才缓冲（系统提供算法）
                        int bufferPercentage = videoView.getBufferPercentage();//获取系统下载的缓冲0-100
                        int totalBuffer = bufferPercentage * seekbarVideo.getMax();//得到总缓存数
                        int secondaryProgress = totalBuffer / 100;//百分比
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    } else {
                        //本地视频不缓冲
                        seekbarVideo.setSecondaryProgress(0);
                    }
                    //4.设置手动监听卡顿
                    if (!isUseSystemBuffer) {//如果使用自定义监听卡顿方式，且视频处于播放状态
                        if (videoView.isPlaying()) {
                            //计算当前进度和前一秒播放进度的差值
                            int buffer = currentPosition - precurrentPosition;
                            if (buffer < 500) {
                                //如果差值小于500，说明视频卡了。前提是1秒更新一次
                                ll_buffer.setVisibility(View.VISIBLE);
                            } else {
                                //视频不卡
                                ll_buffer.setVisibility(View.GONE);
                            }
                        } else {
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }
                    //5.定时更新
                    handler.removeMessages(PROGRESS);//先移除消息
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);//延迟一秒发送
                    break;

            }
        }
    };

    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        /**
         * Called when the media file is ready for playback.
         * 当准备好播放就播放
         *
         * @param mp the MediaPlayer that is ready for playback
         */
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();
            videoView.start();//开始播放
            int duration = (int) videoView.getDuration();//获取视频总时间
            LogUtil.e("视频总时长：" + duration);
            seekbarVideo.setMax(duration);//设置进度条长度
            tvDuration.setText(utils.stringForTime(duration));
            hideMediaController();//隐藏控制面板
            LogUtil.e("开始发送消息：");
            handler.sendEmptyMessage(PROGRESS);
            //设置屏幕默认播放方式
            setVideoType(DEFAULT_SCREEN);
            //把页面加载页面隐藏
            ll_loading.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        //移除所有消息
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2019-05-16 17:15:16 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        Vitamio.isInitialized(this);//初始化
        tvName = findViewById(R.id.tv_name);
        ivBattery = findViewById(R.id.iv_battery);
        tvSystemTime = findViewById(R.id.tv_system_time);
        btnVoice = findViewById(R.id.btn_voice);
        seekbarVoice = findViewById(R.id.seekbar_voice);
        btnSwitchPlayer = findViewById(R.id.btn_switch_player);
        llBottom = findViewById(R.id.ll_bottom);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        seekbarVideo = findViewById(R.id.seekbar_video);
        tvDuration = findViewById(R.id.tv_duration);
        btnExit = findViewById(R.id.btn_exit);
        btnVideoPre = findViewById(R.id.btn_video_pre);
        btnVideoPlayPause = findViewById(R.id.btn_video_play_pause);
        btnVideoNext = findViewById(R.id.btn_video_next);
        btnVideoSwitchScreen = findViewById(R.id.btn_video_switch_screen);
        videoView = findViewById(R.id.videoView);
        media_controller = findViewById(R.id.media_controller);
        ll_buffer = findViewById(R.id.ll_buffer);
        tv_buffer_netspeed = findViewById(R.id.tv_buffer_netspeed);
        ll_loading = findViewById(R.id.ll_loading);
        tv_loading_netspeed = findViewById(R.id.tv_loading_netspeed);

        btnVoice.setOnClickListener(this);
        btnSwitchPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoPlayPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSwitchScreen.setOnClickListener(this);

        //开始更新网速
        handler.sendEmptyMessage(SHOW_SPEED);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2019-05-16 17:15:16 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            isMute = !isMute;//每次点击，取反静音标志
            // Handle clicks for btnVoice
            updateVoice(currentVoice, isMute);//更新声音
        } else if (v == btnSwitchPlayer) {
            // Handle clicks for btnSwitchPlayer
            showSwitchPlayerDialog();
        } else if (v == btnExit) {
            // Handle clicks for btnExit
            finish();
        } else if (v == btnVideoPre) {
            // Handle clicks for btnVideoPre
            playPreVideo();
        } else if (v == btnVideoPlayPause) {
            // Handle clicks for btnVideoPlayPause
            playAndPause();
        } else if (v == btnVideoNext) {
            // Handle clicks for btnVideoNext
            playNextVideo();

        } else if (v == btnVideoSwitchScreen) {
            // Handle clicks for btnVideoSwitchScreen
            switchFullScreenAndDefaultScreen();
        }
        //更新自动隐藏消息
        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, hideMediaControllerTime);
    }

    private void showSwitchPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bb的万能播放器提醒您");
        builder.setMessage("你正在切换系统播放器，某些视频可能无法播放。如果当前播放花屏时建议切换！！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startSystemPlayer();
            }
        });
        builder.setNegativeButton("取消",null);//使用系统播放器继续播放
        builder.show();
    }

    private void startSystemPlayer() {
        //停止当前播放并释放资源
        if (videoView != null) {
            videoView.stopPlayback();
        }
        Intent intent = new Intent(this, BBsSystemVideoPlayer.class);
        if (mediaItems != null && mediaItems.size() > 0) {
            //传递列表数据-对象-序列化
            Bundle bundle = new Bundle();
            bundle.putSerializable("vediolist", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
        } else if (uri != null) {
            intent.setData(uri);
        }
        startActivity(intent);
        finish();//关闭页面
    }

    private void playPreVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //播放上一个
            position--;
            if (position >= 0) {
                //显示加载面板
                ll_loading.setVisibility(View.VISIBLE);
                //没有到第一个就播放
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = Utils.isNetUri(mediaItem.getData());
                videoView.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        } else if (uri != null) {
            setButtonState();
        }
    }

    private void playNextVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //播放下一个
            position++;
            if (position < mediaItems.size()) {
                //显示加载面板
                ll_loading.setVisibility(View.VISIBLE);
                //没有到最后就设置播放下一个
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = Utils.isNetUri(mediaItem.getData());
                videoView.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        } else if (uri != null) {
            setButtonState();
        }
    }

    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0) {
            if (mediaItems.size() == 1) {
                //如果只有一个元素，pre和next按钮都不可点击
                setEnabled(false);
            } else if (mediaItems.size() == 2) {
                //有两个元素的时候
                if (position == 0) {
                    //播放第一个视频，pre按钮不可点击,next可点
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                } else if (position == mediaItems.size() - 1) {
                    //播放最后一个视频，next按钮不可点，pre可点
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);
                } else {
                    //其他情况，pre和next都可点
                    setEnabled(true);
                }
            } else {
                if (position == 0) {
                    //播放第一个视频，pre按钮不可点击
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                } else if (position == mediaItems.size() - 1) {
                    //播放最后一个视频，next按钮不可点
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                } else {
                    //其他情况，pre和next都可点
                    setEnabled(true);
                }
            }


        } else if (uri != null) {
            //单个视频状态，上一个和下一个按钮不可用
            setEnabled(false);
        }
    }

    private void setEnabled(boolean i) {
        if (i) {
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btnVideoPre.setEnabled(true);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            btnVideoNext.setEnabled(true);
        } else {
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }

    }

    private void playAndPause() {
        if (videoView.isPlaying()) {
            //如果在播放，则暂停
            videoView.pause();
            //设置按钮状态显示为播放
            btnVideoPlayPause.setBackgroundResource(R.drawable.btn_video_play_selector);
        } else {
            //如果暂停，则开始播放
            videoView.start();
            //设置按钮状态显示为暂停
            btnVideoPlayPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**
     * 显示控制面板
     */
    private void showMediaController() {
        media_controller.setVisibility(View.VISIBLE);
        isShowMediaController = true;
    }

    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {
        media_controller.setVisibility(View.GONE);
        isShowMediaController = false;
    }

    /**
     * 当按下音量键时更新seekbar
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //按下音量减，更新音量并调节seekbar
            currentVoice--;
            updateVoice(currentVoice, false);
            //重发消息
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, hideMediaControllerTime);
            return true;//返回true会截获音量系统事件，返回false则同时起作用
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //按下音量加，更新音量并调节seekbar
            currentVoice++;
            updateVoice(currentVoice, false);
            //重发消息
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, hideMediaControllerTime);
            return true;//返回true会截获音量系统事件，返回false则同时起作用
        }
        return super.onKeyDown(keyCode, event);
    }
    public void ck_rotate(View view){
        //如果是竖排,则改为横排
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        //如果是横排,则改为竖排

        else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        videoView.setVideoSize(screenHeight,screenWidth);

    }

}
