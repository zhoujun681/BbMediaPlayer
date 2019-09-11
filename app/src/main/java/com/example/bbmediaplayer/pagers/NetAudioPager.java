package com.example.bbmediaplayer.pagers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.bbmediaplayer.R;
import com.example.bbmediaplayer.adpater.NetAudioPagerAdpter;
import com.example.bbmediaplayer.base.BasePager;
import com.example.bbmediaplayer.domain.NetAudioPagerData;
import com.example.bbmediaplayer.utils.CacheUtils;
import com.example.bbmediaplayer.utils.Constants;
import com.example.bbmediaplayer.utils.LogUtil;
import com.example.bbmediaplayer.utils.ScreenUtil;
import com.example.bbmediaplayer.utils.Utils;
import com.example.bbmediaplayer.view.XListView;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class NetAudioPager extends BasePager {
    @ViewInject(R.id.listview)
    private XListView mlistview;

    @ViewInject(R.id.tv_nonet)
    private TextView tv_nonet;

    @ViewInject(R.id.pb_loading)
    private ProgressBar pb_loading;

    @ViewInject(R.id.iv_fullscreen)
    private ImageView iv_fullscreen;


    private List<NetAudioPagerData.ListBean> datas; //页面的数据
    private NetAudioPagerAdpter adpter;

    public NetAudioPager(Context context) {
        super(context);
        rootView = initView(); //父类的视图设置为子类视图
    }

    /**
     * 子类必须实现
     * 用于特定的效果
     *
     * @return
     */
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.netaudio_pager, null);
        x.view().inject(this, view); //把view绑定到class上，则可以使用注释初始化控件
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络音频初始化成功。。。");
        //缓存
        String saveJson = CacheUtils.getString(context, Constants.ALL_RES_URL);
        if (!TextUtils.isEmpty(saveJson)) {
            //解析数据
            processData(saveJson);
        }
        //联网
        getDataFromNet();
        mlistview.setPullLoadEnable(false);
        mlistview.setPullRefreshEnable(true);
        mlistview.setXListViewListener(new XListView.IXListViewListener() {
            /**
             * 上拉刷新
             */
            @Override
            public void onRefresh() {
                adpter.notifyDataSetChanged();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                onLoad();
            }

            /**
             *  下拉加载更多
             */
            @Override
            public void onLoadMore() {

            }
        });
    }

    private void onLoad() {
        mlistview.stopRefresh();
        mlistview.stopLoadMore();
        mlistview.setRefreshTime("刷新时间：" + Utils.getSystemTime());
    }


    /**
     * 解析json数据和显示数据
     * 1.使用GsonFormat生成bean对象2.用gson解析数据
     *
     * @param json
     */
    private void processData(String json) {
        NetAudioPagerData data = parsedJson(json);
        datas = data.getList();
        iv_fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_fullscreen.setVisibility(View.GONE);
            }
        });
        if (datas != null && datas.size() > 0) {
            //有数据
            tv_nonet.setVisibility(View.GONE);
            //设置适配器
            adpter = new NetAudioPagerAdpter(context, datas);
            mlistview.setAdapter(adpter);
            mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String type = datas.get(position - 1).getType();
//                    Toast.makeText(context, type, Toast.LENGTH_SHORT).show();
                    ImageOptions imageOptions = new ImageOptions.Builder()
//                            .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
//                                   .setImageScaleType(ImageView.ScaleType.CENTER)
                            .build();
                    String data = null;
                    switch (type) {
                        case "image":
                            pb_loading.setVisibility(View.VISIBLE);
//                           Toast.makeText(context, "你点击了image图片", Toast.LENGTH_SHORT).show();
                            iv_fullscreen.setVisibility(View.VISIBLE);
                            LogUtil.e(datas.get(position - 1).getImage().getBig().get(0));
//                           Glide.with(context)
//                                   .load(datas.get(position-1).getImage().getBig().get(0))
//                                   .apply(new RequestOptions().placeholder(R.drawable.bg_item))
//                                   .apply(new RequestOptions().error(R.drawable.bg_item))
//                                   .apply(new RequestOptions().diskCacheStrategy
//                                   (DiskCacheStrategy.ALL))
//                                   .into(iv_fullscreen);
                            data = datas.get(position - 1).getImage().getBig().get(0);
                            x.image().bind(iv_fullscreen, data, imageOptions,
                                    new Callback.CommonCallback<Drawable>() {
                                        @Override
                                        public void onSuccess(Drawable result) {

                                        }

                                        @Override
                                        public void onError(Throwable ex, boolean isOnCallback) {

                                        }

                                        @Override
                                        public void onCancelled(CancelledException cex) {

                                        }

                                        @Override
                                        public void onFinished() {
                                            pb_loading.setVisibility(View.GONE);
                                            iv_fullscreen.bringToFront();
//                            Toast.makeText(context, "-x-"+iv_fullscreen.getX()
//                            +"-y-"+iv_fullscreen.getY()+"-width-"+iv_fullscreen.getWidth()
//                            +"-height-"+iv_fullscreen.getHeight(), Toast.LENGTH_LONG).show();
                                            Toast toast2 = Toast.makeText(context, "点击图片返回",
                                                    Toast.LENGTH_SHORT);
                                            toast2.setGravity(Gravity.CENTER, 0, 0);
                                            toast2.show();
                                        }
                                    });
                            break;
                        case "gif":
                            pb_loading.setVisibility(View.VISIBLE);
                            iv_fullscreen.setVisibility(View.VISIBLE);
                            RequestOptions options = new RequestOptions().
                                    diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).
                                    //使用centerCrop是利用图片图填充ImageView设置的大小，如果ImageView的Height是match_parent则图片就会被拉伸填充,
                                    // 使用fitCenter即缩放图像让图像都测量出来等于或小于 ImageView 的边界范围,该图像将会完全显示，但可能不会填满整个
                                    fitCenter().
                                    priority(Priority.HIGH) ; //优先级
                           Glide.with(context).asGif()
                                   .load(datas.get(position - 1).getGif().getImages().get(0))
                                   .listener(new RequestListener<GifDrawable>() {
                                       @Override
                                       public boolean onLoadFailed(@Nullable GlideException e,
                                                                   Object o,
                                                                   Target<GifDrawable> target,
                                                                   boolean b) {
                                           return false;
                                       }

                                       @Override
                                       public boolean onResourceReady(GifDrawable gifDrawable, Object o, Target<GifDrawable> target, DataSource dataSource, boolean b) {
                                           pb_loading.setVisibility(View.GONE);
                                           iv_fullscreen.bringToFront();
                                           Toast toast2 = Toast.makeText(context, "点击图片返回",
                                                   Toast.LENGTH_SHORT);
                                           toast2.setGravity(Gravity.CENTER, 0, 0);
                                           toast2.show();
                                           return false;
                                       }
                                   })
                                   .apply(options)
                                   .into(iv_fullscreen);
//                            data = datas.get(position - 1).getGif().getImages().get(0);
                            break;
                    }



                }
            });
        } else {
            //没有数据
            tv_nonet.setText("没有找到数据...");
            tv_nonet.setVisibility(View.VISIBLE);
        }
        pb_loading.setVisibility(View.GONE);
    }

    /**
     * Gson解析json到bean对象
     *
     * @param json
     * @return
     */
    private NetAudioPagerData parsedJson(String json) {
        return new Gson().fromJson(json, NetAudioPagerData.class);
    }


    /**
     * 获取网络数据
     */
    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.ALL_RES_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求数据成功: " + result);
                //保存数据
                CacheUtils.putString(context, Constants.ALL_RES_URL, result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("请求数据失败: " + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("CancelledException: " + cex);

            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished");

            }
        });
    }
}
