package com.example.bbmediaplayer.adpater;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bbmediaplayer.R;
import com.example.bbmediaplayer.utils.DoubleClick;
import com.example.bbmediaplayer.view.myJzvdStd;
import com.example.bbmediaplayer.domain.NetAudioPagerData;
import com.example.bbmediaplayer.utils.Utils;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.IOException;
import java.net.URL;
import java.util.List;


import cn.jzvd.JzvdStd;
import pl.droidsonroids.gif.GifImageView;

/**
 * 网络音乐列表的listview的适配器
 */
public class NetAudioPagerAdpter extends BaseAdapter {

    private final Context context;
    private final List<NetAudioPagerData.ListBean> datas;
    private Utils utils;


    /**
     * 视频
     */
    private static final int TYPE_VIDEO = 0;

    /**
     * 图片
     */
    private static final int TYPE_IMAGE = 1;

    /**
     * 文字
     */
    private static final int TYPE_TEXT = 2;

    /**
     * GIF图片
     */
    private static final int TYPE_GIF = 3;


    /**
     * 软件推广
     */
    private static final int TYPE_AD = 4;
    private String thumbPic;

    public NetAudioPagerAdpter(Context context, List<NetAudioPagerData.ListBean> datas) {
        this.context = context;
        this.datas = datas;
        utils = new Utils();

    }


    @Override
    public int getCount() {
        return datas.size();
    } //5种类型

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    /**
     * 根据位置得到类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        NetAudioPagerData.ListBean listBean = datas.get(position);
        String type = listBean.getType();//video,text,image,gif,ad
        int itemViewType = -1;
        switch (type) {
            case "video":
                itemViewType = TYPE_VIDEO;
                break;
            case "image":
                itemViewType = TYPE_IMAGE;
                break;
            case "text":
                itemViewType = TYPE_TEXT;
                break;
            case "gif":
                itemViewType = TYPE_GIF;
                break;
            default:
                itemViewType = TYPE_AD;
                break;
        }
        return itemViewType;
    }


    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            //初始化
            viewHolder = new ViewHolder();
            //初始化item布局
            convertView = initView(convertView, itemViewType, viewHolder);
            initCommonView(convertView, itemViewType, viewHolder);
            //设置tag
            convertView.setTag(viewHolder);
        } else {
            //获取tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //绑定数据
        //根据位置得到数据,绑定数据
        NetAudioPagerData.ListBean mediaItem = datas.get(position);
        bindData(itemViewType, viewHolder, mediaItem,position);

        return convertView;
    }

    private void bindData(int itemViewType, ViewHolder viewHolder,
                          NetAudioPagerData.ListBean mediaItem,int position) {
        switch (itemViewType) {
            case TYPE_VIDEO://视频
                bindData(viewHolder, mediaItem);
                //第一个参数是视频播放地址，第二参数是标题，第三个参数是显示模式
                viewHolder.jcv_videoplayer.setUp(mediaItem.getVideo().getVideo().get(0),
                        mediaItem.getU().getName(), JzvdStd.SCREEN_NORMAL);
                viewHolder.tv_play_nums.setText(mediaItem.getVideo().getPlaycount() + "次播放");
                viewHolder.tv_video_duration.setText(utils.stringForTime(mediaItem.getVideo().getDuration() * 1000) + "");
                //获得热评
                List<NetAudioPagerData.ListBean.TopCommentsBean> top_comments = mediaItem.getTop_comments();
                if(top_comments !=null&&top_comments.size()>0){
                    String content = mediaItem.getTop_comments().get(0).getContent();
                    viewHolder.tv_commant_context.setText(content);
                }
                //获得缩略图
                thumbPic = mediaItem.getVideo().getThumbnail().get(0);
                viewHolder.jcv_videoplayer.thumbImageView.setImageResource(R.drawable.video_default);//设置视频缩略图
                viewHolder.jcv_videoplayer.thumbImageView.setTag(thumbPic); //设置tag
                viewHolder.jcv_videoplayer.positionInList = position;
                viewHolder.jcv_videoplayer.setOnTouchListener(new DoubleClick(new DoubleClick.MyClickCallBack(){

                    @Override
                    public void oneClick() {

                    }

                    @Override
                    public void doubleClick() {
//                        Toast.makeText(context, "双击了", Toast.LENGTH_SHORT).show();
                    }
                }));
                new myAsyncTask(viewHolder,mediaItem).execute();
                break;
            case TYPE_IMAGE://图片
                bindData(viewHolder, mediaItem);
                viewHolder.iv_image_icon.setImageResource(R.drawable.bg_item);
                int height =
                        mediaItem.getImage().getHeight() <= DensityUtil.getScreenHeight() * 0.75
                                ? mediaItem.getImage().getHeight() :
                                (int) (DensityUtil.getScreenHeight() * 0.75);

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(DensityUtil.getScreenWidth(), height);
                viewHolder.iv_image_icon.setLayoutParams(params);
                if (mediaItem.getImage() != null && mediaItem.getImage().getBig() != null && mediaItem.getImage().getBig().size() > 0) {
                    ImageOptions imageOptions = new ImageOptions.Builder()
                            .setUseMemCache(true)
                            .setIgnoreGif(true)//是否忽略gif图。false表示不忽略。不写这句，默认是true
//                                   .setImageScaleType(ImageView.ScaleType.CENTER)
                            .build();
                    x.image().bind(viewHolder.iv_image_icon, mediaItem.getImage().getBig().get(0), imageOptions);
//                    Glide.with(context)
//                            .load(mediaItem.getImage().getBig().get(0))
//                            .apply(new RequestOptions().placeholder(R.drawable.bg_item))
//                            .apply(new RequestOptions().error(R.drawable.bg_item))
//                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
//                            .into(viewHolder.iv_image_icon);
                }
                break;
            case TYPE_TEXT://文字
                bindData(viewHolder, mediaItem);
                break;
            case TYPE_GIF://gif
                bindData(viewHolder, mediaItem);
//                System.out.println("mediaItem.getGif().getImages().get(0)" + mediaItem.getGif().getImages().get(0));
                ImageOptions imageOptions2 = new ImageOptions.Builder()
                        .setUseMemCache(true)
                        .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
//                                   .setImageScaleType(ImageView.ScaleType.CENTER)
                        .build();

                x.image().bind(viewHolder.iv_image_gif,mediaItem.getGif().getImages().get(0), imageOptions2);
//                Glide.with(context).load(mediaItem.getGif().getImages().get(0))
//                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
//                        .into(viewHolder.iv_image_gif);

                break;
            case TYPE_AD://软件广告
                break;
        }


        //设置文本
        viewHolder.tv_context.setText(mediaItem.getText());


    }

    private class myAsyncTask  extends AsyncTask<String, Integer, Bitmap>{
        ViewHolder viewHolder;
        NetAudioPagerData.ListBean mediaItem;
        private myAsyncTask(ViewHolder viewHolder,NetAudioPagerData.ListBean mediaItem){
            this.viewHolder = viewHolder;
            this.mediaItem = mediaItem;
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param strings The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Bitmap doInBackground(String... strings) {
            URL picUrl = null;
            try {
                picUrl = new URL(mediaItem.getVideo().getThumbnail().get(0));
                Bitmap pngBM = BitmapFactory.decodeStream(picUrl.openStream());
                return pngBM;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(viewHolder.jcv_videoplayer.thumbImageView.getTag().equals(thumbPic)){
                if(bitmap!=null){
                    viewHolder.jcv_videoplayer.thumbImageView.setImageBitmap(bitmap);//设置视频缩略图
                }
            }

            super.onPostExecute(bitmap);
        }
    }

    private void bindData(ViewHolder viewHolder, NetAudioPagerData.ListBean mediaItem) {
        if (mediaItem.getU() != null && mediaItem.getU().getHeader() != null && mediaItem.getU().getHeader().get(0) != null) {
            x.image().bind(viewHolder.iv_headpic, mediaItem.getU().getHeader().get(0));
        }
        if (mediaItem.getU() != null && mediaItem.getU().getName() != null) {
            viewHolder.tv_name.setText(mediaItem.getU().getName() + "");
        }

        viewHolder.tv_time_refresh.setText(mediaItem.getPasstime());

        //设置标签
        List<NetAudioPagerData.ListBean.TagsBean> tagsEntities = mediaItem.getTags();
        if (tagsEntities != null && tagsEntities.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < tagsEntities.size(); i++) {
                buffer.append(tagsEntities.get(i).getName() + " ");
            }
            viewHolder.tv_video_kind_text.setText(buffer.toString());
        }

        //设置点赞，踩,转发
        viewHolder.tv_shenhe_ding_number.setText(mediaItem.getUp() + "");
        viewHolder.tv_shenhe_cai_number.setText(mediaItem.getDown() + "");
        viewHolder.tv_posts_number.setText(mediaItem.getForward() + "");

    }

    private void initCommonView(View convertView, int itemViewType, ViewHolder viewHolder) {
        switch (itemViewType) {
            case TYPE_VIDEO://视频
            case TYPE_IMAGE://图片
            case TYPE_TEXT://文字
            case TYPE_GIF://gif
                //加载除开广告部分的公共部分视图
                //user info
                viewHolder.iv_headpic = convertView.findViewById(R.id.iv_headpic);
                viewHolder.tv_name = convertView.findViewById(R.id.tv_name);
                viewHolder.tv_time_refresh =
                        convertView.findViewById(R.id.tv_time_refresh);
                viewHolder.iv_right_more = convertView.findViewById(R.id.iv_right_more);
                //bottom
                viewHolder.iv_video_kind = convertView.findViewById(R.id.iv_video_kind);
                viewHolder.tv_video_kind_text =
                        convertView.findViewById(R.id.tv_video_kind_text);
                viewHolder.tv_shenhe_ding_number =
                        convertView.findViewById(R.id.tv_shenhe_ding_number);
                viewHolder.tv_shenhe_cai_number =
                        convertView.findViewById(R.id.tv_shenhe_cai_number);
                viewHolder.tv_posts_number =
                        convertView.findViewById(R.id.tv_posts_number);
                viewHolder.ll_download = convertView.findViewById(R.id.ll_download);

                break;
        }


        //中间公共部分 -所有的都有
        viewHolder.tv_context = convertView.findViewById(R.id.tv_context);
    }

    private View initView(View convertView, int itemViewType, ViewHolder viewHolder) {
        switch (itemViewType) {
            case TYPE_VIDEO://视频
                convertView = View.inflate(context, R.layout.all_video_item, null);
                //在这里实例化特有的
                viewHolder.tv_play_nums = convertView.findViewById(R.id.tv_play_nums);
                viewHolder.tv_video_duration =
                        convertView.findViewById(R.id.tv_video_duration);
                viewHolder.iv_commant = convertView.findViewById(R.id.iv_commant);
                viewHolder.tv_commant_context =
                        convertView.findViewById(R.id.tv_commant_context);
                viewHolder.jcv_videoplayer =
                        convertView.findViewById(R.id.jcv_videoplayer);
                break;
            case TYPE_IMAGE://图片
                convertView = View.inflate(context, R.layout.all_image_item, null);
                viewHolder.iv_image_icon = convertView.findViewById(R.id.iv_image_icon);
                break;
            case TYPE_TEXT://文字
                convertView = View.inflate(context, R.layout.all_text_item, null);
                break;
            case TYPE_GIF://gif
                convertView = View.inflate(context, R.layout.all_gif_item, null);
                viewHolder.iv_image_gif =
                        convertView.findViewById(R.id.iv_image_gif);
                break;
            case TYPE_AD://软件广告
                convertView = View.inflate(context, R.layout.all_ad_item, null);
                viewHolder.btn_install = convertView.findViewById(R.id.btn_install);
                viewHolder.iv_image_icon = convertView.findViewById(R.id.iv_image_icon);
                break;
        }
        return convertView;
    }

    static class ViewHolder {
        //user_info
        ImageView iv_headpic;
        TextView tv_name;
        TextView tv_time_refresh;
        ImageView iv_right_more;
        //bottom
        ImageView iv_video_kind;
        TextView tv_video_kind_text;
        TextView tv_shenhe_ding_number;
        TextView tv_shenhe_cai_number;
        TextView tv_posts_number;
        LinearLayout ll_download;

        //中间公共部分 -所有的都有
        TextView tv_context;


        //Video
//        TextView tv_context;
        TextView tv_play_nums;
        TextView tv_video_duration;
        ImageView iv_commant;
        TextView tv_commant_context;
        myJzvdStd jcv_videoplayer;

        //Image
        ImageView iv_image_icon;
//        TextView tv_context;

        //Text
//        TextView tv_context;

        //Gif
        GifImageView iv_image_gif;
//        TextView tv_context;

        //软件推广
        Button btn_install;
//        TextView iv_image_icon;
        //TextView tv_context;


    }
}

