package com.example.bbmediaplayer.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bbmediaplayer.R;
import com.example.bbmediaplayer.domain.MediaItem;
import com.example.bbmediaplayer.domain.SearchBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络视频列表的listview的适配器
 */
public class SearchAdpter extends BaseAdapter {

    private final Context context;
    private final List<SearchBean.ResultBean.ListBean> mediaItems;


    public SearchAdpter(Context context, List<SearchBean.ResultBean.ListBean> mediaItems){
        this.context = context;
        this.mediaItems = mediaItems;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mediaItems.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder viewHoder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_netvideo_pager, null);
            viewHoder = new ViewHoder();
            viewHoder.iv_icon = convertView.findViewById(R.id.iv_icon);
            viewHoder.tv_name = convertView.findViewById(R.id.tv_name);
            viewHoder.tv_desc = convertView.findViewById(R.id.tv_desc);
            viewHoder.iv_video_ico = convertView.findViewById(R.id.iv_video_ico);

            convertView.setTag(viewHoder);
        }else{
            viewHoder = (ViewHoder) convertView.getTag();
        }

            viewHoder.iv_video_ico.setVisibility(View.GONE);


        //根据位置得到数据
        SearchBean.ResultBean.ListBean mediaItem = mediaItems.get(position);
        viewHoder.tv_name.setText(mediaItem.getTitleX());
        viewHoder.tv_desc.setText(mediaItem.getContentX());
        //使用xutils加载图片，功能简单
//        x.image().bind(viewHoder.iv_icon,mediaItem.getImageUrl());//设置图片
        //使用Glide加载图片，比较强大
        RequestOptions myOptions = new RequestOptions()
                .fitCenter() //居中
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) //自动缓存模式
                .placeholder(R.drawable.video_default) //未加载前显示的图片
                .error(R.drawable.video_default); //加载错误显示的图片
        Glide.with(context)
                .load(mediaItem.getPicX())
                .apply(myOptions)
                .into(viewHoder.iv_icon);

        return convertView;
    }
    static class ViewHoder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
        ImageView iv_video_ico;
    }
}

