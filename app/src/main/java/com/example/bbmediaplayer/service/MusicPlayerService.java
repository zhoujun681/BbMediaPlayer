package com.example.bbmediaplayer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.bbmediaplayer.IMusicPlayerService;
import com.example.bbmediaplayer.R;
import com.example.bbmediaplayer.activity.AudioPlayerActivity;
import com.example.bbmediaplayer.domain.MediaItem;
import com.example.bbmediaplayer.utils.CacheUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;


public class MusicPlayerService extends Service {
    //广播意图的action
    public static final String OPENAUDIO = "com.example.bbmediaplayer_OPENAUDIO";

    /**
     * 重复模式
     */
    public static final int REPEAT_NORMAL = 1;  //顺序播放
    public static final int REPEAT_SINGLE = 2;  //单曲循环
    public static final int REPEAT_ALL = 3; //全部循环
    private int playmode = REPEAT_NORMAL;//默认播放模式

    //存储扫描出来的所有媒体信息
    private ArrayList<MediaItem> mediaItems;

    private IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {
        MusicPlayerService service = MusicPlayerService.this;

        /**
         * Demonstrates some basic types that you can use as parameters
         * and return values in AIDL.
         *
         * @param anInt
         * @param aLong
         * @param aBoolean
         * @param aFloat
         * @param aDouble
         * @param aString
         */
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        /**
         * 打开音乐
         *
         * @param position
         */
        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        /**
         * 开始播放
         */
        @Override
        public void start() throws RemoteException {
            service.start();
        }

        /**
         * 暂停
         */
        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        /**
         * 停止
         */
        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getCurrentDuration() throws RemoteException {
            return service.getCurrentDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        /**
         * 上一曲
         */
        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        /**
         * 下一曲
         */
        @Override
        public void next() throws RemoteException {
            service.next();
        }

        /**
         * 设置播放模式
         *
         * @param playmode
         */
        @Override
        public void setPlayMode(int playmode) throws RemoteException {
            service.setPlayMode(playmode);
        }


        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        /**
         * 跳转到进度
         *
         * @param position
         */
        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return mediaPlayer.getAudioSessionId();
        }
    };
    /**
     * 当前播放文件序号
     */
    private int position;
    /**
     * 当前播放的音频文件
     */
    private MediaItem mediaItem;
    /**
     * 用于播放音乐
     */
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //得到上次的播放模式
        playmode = CacheUtils.getInt(this, "playmode");
        //加载音乐列表
        getDataFromLocal();
    }

    /**
     * 打开音乐
     *
     * @param position
     */
    private void openAudio(int position) {
        this.position = position;
        if (mediaItems != null && mediaItems.size() > 0) {
            mediaItem = mediaItems.get(position);
            if (mediaPlayer != null) {
//                mediaPlayer.release(); //reset已包含release
                mediaPlayer.reset();
            }
            try {
                mediaPlayer = new MediaPlayer();
                /**
                 * 设置监听
                 */
                mediaPlayer.setOnPreparedListener(new myOnPreparedListener()); //准备好就开始播放
                mediaPlayer.setOnCompletionListener(new myOnCompletionListener());//完成播放下一个
                mediaPlayer.setOnErrorListener(new myOnErrorListener());//出错播放下一个
                mediaPlayer.setDataSource(mediaItem.getData());
                mediaPlayer.prepareAsync();//必须设置

                if(playmode == MusicPlayerService.REPEAT_SINGLE){
                    mediaPlayer.setLooping(true); //单曲播放，不会触发播放完成
                }else{
                    mediaPlayer.setLooping(false);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "数据还没有准备好", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 通知管理器
     */
    private NotificationManager manager;

    /**
     * 开始播放
     */
    private void start() {
        mediaPlayer.start();
/**
 * 处理通知
 */
        String channekId = "my_channel_01";
        NotificationChannel mChannel = null;//创建Notification Channel对象
        //得到系统通知服务,设置通知内容，点击后跳转到播放界面
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //如果版本号为8.0以上,定义Notification Channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(channekId, "my_channel", NotificationManager.IMPORTANCE_MIN);//设置唯一的渠道通知Id,IMPORTANCE_MIN为静默
            mChannel.setSound(null, null);
            manager.createNotificationChannel(mChannel);//在NotificationManager中注册渠道通知对象
        }

        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra("Notification", true);//携带信息，说明意图来自通知栏
        PendingIntent pendingintent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, channekId)
                    .setSmallIcon(R.drawable.audio_default_icon) //设置图标
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.bb_mediaplayer_logo))
                    .setContentTitle("Bb播放器")   //设置标题
                    .setContentText("正在播放" + getName()) //设置内容文本
                    .setContentIntent(pendingintent)
                    .setChannelId(channekId)
                    .setOnlyAlertOnce(true)//设置只通知一次
                    .build();
        } else {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.audio_default_icon) //设置图标
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.bb_mediaplayer_logo))
                    .setContentTitle("Bb播放器")   //设置标题
                    .setContentText("正在播放" + getName()) //设置内容文本
                    .setContentIntent(pendingintent)
                    .setSound(null)
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE) //设置只通知一次
                    .build();
        }
        notification.flags = Notification.FLAG_ONGOING_EVENT;//设置通知不可清除
        notification.flags=Notification.FLAG_NO_CLEAR;//设置通知不可清除方法2
        manager.notify(1, notification);

    }

    /**
     * 暂停
     */
    private void pause() {
        mediaPlayer.pause();
        manager.cancel(1);//取消通知
    }

    /**
     * 停止
     */
    private void stop() {
        mediaPlayer.stop();
        manager.cancel(1);//取消通知
    }

    /**
     * 得到当前播放进度
     *
     * @return
     */
    private int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 得到当前播放总长度
     *
     * @return
     */
    private int getCurrentDuration() {
        return mediaPlayer.getDuration();
    }

    /**
     * 得到演唱者
     *
     * @return
     */
    private String getArtist() {
        return mediaItem.getArtist();
    }

    /**
     * 得到歌曲名
     *
     * @return
     */
    private String getName() {
        return mediaItem.getName();
    }


    /**
     * 得到歌曲路径
     *
     * @return
     */
    private String getAudioPath() {
        return mediaItem.getData();
    }


    /**
     * 上一曲
     */
    private void pre() {
        //1.根据播放模式，设置上一个的位置
        setPrePosition();
        //2.根据位置，播放视频
        openPreAudio();
    }

    private void openPreAudio() {
        int playmode = getPlayMode();

        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            if(position>=0){
                //正常范围
                openAudio(position);
            }else {
                position = 0;
            }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            openAudio(position);
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            openAudio(position);
        } else {
            if(position>=0){
                //正常范围
                openAudio(position);
            }else {
                position = 0;
            }
        }
    }

    private void setPrePosition() {
        int playmode = getPlayMode();

        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            position--;
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            position--;
            if(position < 0){
                position =  mediaItems.size() - 1;
            }
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            position--;
            if(position < 0){
                position =  mediaItems.size() - 1;
            }
        } else {
            position--;
        }
    }

    /**
     * 下一曲
     */
    private void next() {
        //1.根据播放模式，设置下一个的位置
        setNextPosition();
        //2.根据位置，播放视频
        openNextAudio();

    }

    private void openNextAudio() {
        int playmode = getPlayMode();

        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
           if(position<mediaItems.size()){
               //正常范围
               openAudio(position);
           }else {
               position = mediaItems.size() - 1;
           }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            openAudio(position);
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            openAudio(position);
        } else {
            if(position<mediaItems.size()){
                //正常范围
                openAudio(position);
            }else {
                position = mediaItems.size() - 1;
            }
        }
    }

    private void setNextPosition() {
        int playmode = getPlayMode();

        if (playmode == MusicPlayerService.REPEAT_NORMAL) {
            position++;
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            position++;
            if(position >= mediaItems.size()){
                position = 0;
            }
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            position++;
            if(position >= mediaItems.size()){
                position = 0;
            }
        } else {
            position++;
        }
    }

    private void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    /**
     * 设置播放模式
     *
     * @param playmode
     */
    private void setPlayMode(int playmode) {
        this.playmode = playmode;
        //缓存播放模式
        CacheUtils.putInt(this,"playmode",playmode);
        if(playmode == MusicPlayerService.REPEAT_SINGLE){
            mediaPlayer.setLooping(true); //单曲播放，不会触发播放完成
        }else{
            mediaPlayer.setLooping(false);
        }
    }

    /**
     * 得到播放模式
     *
     * @return
     */
    private int getPlayMode() {
        return playmode;
    }

    /**
     * 获取媒体列表
     */
    private void getDataFromLocal() {
        mediaItems = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                super.run();
                //1.获得内容提供者
                ContentResolver resolver = getContentResolver();
                //2.设置内置的获取视频的uri
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                //3.设置需要查询内容的String数组
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,    //视频文件在存储中的名称
                        MediaStore.Audio.Media.DURATION,        //视频文件持续时间
                        MediaStore.Audio.Media.SIZE,            //视频文件的大小
                        MediaStore.Audio.Media.DATA,            //视频文件在存储中的绝对路径
                        MediaStore.Audio.Media.ARTIST,          //音频歌曲的演唱者

                };
                //4.查询数据并返回到游标
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                //5.遍历游标，取得每一个结果的值存储到bean中
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaItem = new MediaItem();
                        //把条目添加进列表，这里添加和末尾添加都可以，这里添加空的，后面set后内容会跟着改变
                        mediaItems.add(mediaItem);
                        String name = cursor.getString(0);      //视频文件在存储中的名称
                        mediaItem.setName(name);
                        long duration = cursor.getLong(1);  //视频文件持续时间
                        mediaItem.setDuration(duration);
                        long size = cursor.getLong(2);  //视频文件的大小
                        mediaItem.setSize(size);
                        String data = cursor.getString(3);  //视频文件在存储中的绝对路径
                        mediaItem.setData(data);
                        String artist = cursor.getString(4);    //音频歌曲的演唱者
                        mediaItem.setArtist(artist);
                    }
                    cursor.close();
                }

            }

        }.start();
    }

    /**
     * 获取媒体是否在播放
     *
     * @return
     */
    private boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    private class myOnPreparedListener implements MediaPlayer.OnPreparedListener {
        /**
         * Called when the media file is ready for playback.
         *
         * @param mp the MediaPlayer that is ready for playback
         */
        @Override
        public void onPrepared(MediaPlayer mp) {
            //通知Activity来获取歌曲信息
//            notifyChange(OPENAUDIO); //发广播
            EventBus.getDefault().post(mediaItem); //4.使用Evenbus发布消息，传入内容与订阅者一致
            start();
        }

    }

    /**
     * 根据动作发广播
     *
     * @param action
     */
    private void notifyChange(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private class myOnCompletionListener implements MediaPlayer.OnCompletionListener {
        /**
         * Called when the end of a media source is reached during playback.
         *
         * @param mp the MediaPlayer that reached the end of the file
         */
        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }

    private class myOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true;
        }
    }

}
