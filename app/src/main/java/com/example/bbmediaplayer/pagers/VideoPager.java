package com.example.bbmediaplayer.pagers;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bbmediaplayer.R;
import com.example.bbmediaplayer.activity.VitamioVideoPlayer;
import com.example.bbmediaplayer.adpater.VideoPagerAdpter;
import com.example.bbmediaplayer.base.BasePager;
import com.example.bbmediaplayer.domain.MediaItem;
import com.example.bbmediaplayer.utils.Constants;
import com.example.bbmediaplayer.utils.LogUtil;
import com.example.bbmediaplayer.activity.BBsSystemVideoPlayer;
import com.example.bbmediaplayer.utils.PicUtils;

import java.util.ArrayList;

public class VideoPager extends BasePager {
    private ListView listview;
    private TextView tv_nomedia;
    private ProgressBar pb_loading;
    //存储扫描出来的所有视频信息
    private ArrayList<MediaItem> mediaItems;
    //适配器
    private VideoPagerAdpter videoPagerAdapter;
    private boolean useSystemPlayer = Constants.UseSystemPlayer;


    public VideoPager(Context context) {
        super(context);
        rootView = initView(); //父类的视图设置为子类视图
//        PicUtils.clearCache(context);
    }

    /**
     * 子类必须实现
     * 用于特定的效果
     *
     * @return
     */
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager, null);
        //初始化部件
        listview = view.findViewById(R.id.listview);
        tv_nomedia = view.findViewById(R.id.tv_nomedia);
        pb_loading = view.findViewById(R.id.pb_loading);
        //设置点击事件
        listview.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地视频页面的数据初始化成功");
        //加载本地视频数据
        getDataFromLocal();
    }


    /**
     * hander用来处理从子线程获得的消息
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size() > 0) {
                //有数据
                //设置适配器
                videoPagerAdapter = new VideoPagerAdpter(context,mediaItems,true);
                listview.setAdapter(videoPagerAdapter);

                //隐藏文本
                tv_nomedia.setVisibility(View.GONE);
            } else {
                //没有数据
                //显示文本
                tv_nomedia.setVisibility(View.VISIBLE);
            }
            //ProgressBar隐藏
            pb_loading.setVisibility(View.GONE);
        }
    };

    /**
     * 从本地存储获取数据
     * 方法1:遍历sdcard，通过后缀名来提取。
     * 速度慢，只适用于扫描特定文件夹少量文件
     * 方法2：从内容提供者里面提取视频
     * android6.0以上不能读取外部存储，需要单独处理
     */
    private void getDataFromLocal() {
        mediaItems = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                super.run();
                //1.获得内容提供者
                ContentResolver resolver = context.getContentResolver();
                //2.设置内置的获取视频的uri
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                //3.设置需要查询内容的String数组
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,    //视频文件在存储中的名称
                        MediaStore.Video.Media.DURATION,        //视频文件持续时间
                        MediaStore.Video.Media.SIZE,            //视频文件的大小
                        MediaStore.Video.Media.DATA,            //视频文件在存储中的绝对路径
                        MediaStore.Video.Media.ARTIST,          //音频歌曲的演唱者

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
                //向主线程发送消息，告诉主线程已经完成扫描
                handler.sendEmptyMessage(100);
            }

        }.start();
    }

    /**
     * ListView的点击事件
     */
    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //获得点击的项目
            MediaItem mediaItem = mediaItems.get(position);
//            Toast.makeText(context, "mediaItem=="+mediaItem.toString(), Toast.LENGTH_SHORT).show();

        //1.调用系统所有的播放器,隐式调用
//            Intent intent=new Intent();
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//            context.startActivity(intent);
            //2.调用自己定义的播放器
//            Intent intent = new Intent(context,BBsSystemVideoPlayer.class);
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//            context.startActivity(intent);
            //3.传递列表数据-对象-序列化
            Intent intent;
            if(useSystemPlayer){
                 intent = new Intent(context,BBsSystemVideoPlayer.class);
            }else{
                intent = new Intent(context, VitamioVideoPlayer.class);
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("vediolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
            context.startActivity(intent);
        }
    }
}
