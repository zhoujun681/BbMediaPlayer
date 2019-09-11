package com.example.bbmediaplayer.pagers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bbmediaplayer.R;
import com.example.bbmediaplayer.activity.BBsSystemVideoPlayer;
import com.example.bbmediaplayer.activity.VitamioVideoPlayer;
import com.example.bbmediaplayer.adpater.NetVideoPagerAdpter;
import com.example.bbmediaplayer.base.BasePager;
import com.example.bbmediaplayer.domain.MediaItem;
import com.example.bbmediaplayer.utils.Constants;
import com.example.bbmediaplayer.utils.LogUtil;
import com.example.bbmediaplayer.utils.Utils;
import com.example.bbmediaplayer.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

public class NetVideoPager extends BasePager {
    private boolean useSystemPlayer = Constants.UseSystemPlayer;
    private ArrayList<MediaItem> mediaItems;
    private boolean isLoadMore; //是否加载更多

    @ViewInject(R.id.listview)  //初始化listview
    private XListView mlistview;

    @ViewInject(R.id.tv_nonet)
    private TextView mTv_nonet;

    @ViewInject(R.id.pb_loading)
    private ProgressBar mProgressBar;
    private NetVideoPagerAdpter netvideoPagerAdapter;

    public NetVideoPager(Context context) {
        super(context);
        rootView=initView(); //父类的视图设置为子类视图
    }

    /**
     * 子类必须实现
     * 用于特定的效果
     *
     * @return
     */
    @Override
    public View initView() {
        LogUtil.e("网络视频页面初始化成功");
        View view = View.inflate(context, R.layout.netvideo_pager, null);
        x.view().inject(this,view);//必须先使用这个代码后才能使用注解

        //设置点击事件
        mlistview.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络视频页面的数据初始化成功");
        getDataFromNet();//联网请求
        mlistview.setPullLoadEnable(true);
        mlistview.setXListViewListener(new XListView.IXListViewListener() {
            /**
             * 上拉刷新
             */
            @Override
            public void onRefresh() {
                getDataFromNet();//联网请求
                onLoad();
            }
            /**
             *  下拉加载更多
             */
            @Override
            public void onLoadMore() {
                getMoreDataFromNet();
            }
        });
    }

    private void onLoad() {
        mlistview.stopRefresh();
        mlistview.stopLoadMore();
        mlistview.setRefreshTime("刷新时间："+Utils.getSystemTime());
    }

    private void getMoreDataFromNet() {
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功："+result);
                isLoadMore = true;
                processData(result);//处理数据
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败："+ex.getMessage());
                isLoadMore =false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled："+cex.getMessage());
                isLoadMore =false;

            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished");
                isLoadMore =false;

            }
        });
    }

    /**
     * 联网请求数据
     */
    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功："+result);
                processData(result);//处理数据
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败："+ex.getMessage());

            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled："+cex.getMessage());

            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished");

            }
        });
    }

    /**
     * ListView的点击事件
     */
    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //获得点击的项目
            MediaItem mediaItem = mediaItems.get(position);
            Intent intent;
            if(useSystemPlayer){
                intent = new Intent(context, BBsSystemVideoPlayer.class);
            }else{
                intent = new Intent(context, VitamioVideoPlayer.class);
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("vediolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position-1);//由于有了下拉刷新，多了个头，需要减一
            context.startActivity(intent);
        }
    }

    /**
     * 处理json并绑定适配器
     * @param json
     */
    private void processData(String json) {
        if(!isLoadMore){//如果不是加载更多数据时
            mediaItems = parseJson(json);
            if (mediaItems != null && mediaItems.size() > 0) {
                //有数据
                //设置适配器
                netvideoPagerAdapter = new NetVideoPagerAdpter(context,mediaItems);
                mlistview.setAdapter(netvideoPagerAdapter);
                onLoad();
                //隐藏文本
                mTv_nonet.setVisibility(View.GONE);
            } else {
                //没有数据
                //显示文本
                mTv_nonet.setVisibility(View.VISIBLE);
            }
            //ProgressBar隐藏
            mProgressBar.setVisibility(View.GONE);
        }else{
            //加载更多数据时
            mediaItems.addAll(parseJson(json));//把新数据添加到集合
            isLoadMore = false;
            //刷新适配器
            netvideoPagerAdapter.notifyDataSetChanged();
            onLoad();
        }

    }

    /**
     * 解析json到一个集合
     * @param json
     * @return
     */
    private ArrayList<MediaItem> parseJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<MediaItem>(); //设置播放列表需要的集合
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");//获取内容
            if(jsonArray != null && jsonArray.length()>0){
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);//获取每一条json数据
                    if(jsonObjectItem != null){
                        MediaItem mediaItem = new MediaItem();//创建一个bean
                        //获取json数据并赋值给bean
                        String movieName = jsonObjectItem.optString("movieName"); //name
                        mediaItem.setName(movieName);
                        String coverImg = jsonObjectItem.optString("coverImg"); //imageUrl
                        mediaItem.setImageUrl(coverImg);
                        String hightUrl = jsonObjectItem.optString("hightUrl");//data
                        mediaItem.setData(hightUrl);
                        String videoTitle = jsonObjectItem.optString("videoTitle");//desc
                        mediaItem.setDesc(videoTitle);

                        //把bean添加到集合
                        mediaItems.add(mediaItem);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItems;
    }
}
