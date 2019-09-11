package com.example.bbmediaplayer.adpater;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bbmediaplayer.R;
import com.example.bbmediaplayer.domain.MediaItem;
import com.example.bbmediaplayer.utils.CacheUtils;
import com.example.bbmediaplayer.utils.LogUtil;
import com.example.bbmediaplayer.utils.LruCacheUtils;
import com.example.bbmediaplayer.utils.PicUtils;
import com.example.bbmediaplayer.utils.Utils;

import org.xutils.cache.LruCache;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频列表的listview的适配器
 */
public class VideoPagerAdpter extends BaseAdapter {
    LruCacheUtils lruCacheUtils;
    private final Context context;
    private final ArrayList<MediaItem> mediaItems;
    private final Boolean isVideo;
    Utils utils;
    private MediaItem mediaItem;
    private ViewHoder viewHoder;
    private View aconvertView;

    /**
     * 获取视频文件截图
     *
     * @param path 视频文件的路径
     * @return Bitmap 返回获取的Bitmap
     */

    public static Bitmap getVideoThumb(String path) {

        MediaMetadataRetriever media = new MediaMetadataRetriever();
        Log.e("BB", path);
        try {
            File file = new File(path);
            media.setDataSource(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bitmap bitmap = media.getFrameAtTime();
        bitmap = matrix(bitmap);
        bitmap = RGB_565(bitmap);
        return bitmap;
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap RGB_565(Bitmap bitmap) {
        byte[] bytes = bitmap2Bytes(bitmap);
        System.out.println("XXX压缩前byte.length " + bytes.length);
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options2);
        byte[] bytes1 = bitmap2Bytes(bitmap1);
        System.out.println("XXX压缩后byte.length " + bytes1.length);
        return bitmap1;
    }


    /**
     * 压缩bitmap大小
     * @param bit
     * @return
     */
    public static Bitmap matrix(Bitmap bit) {
        Matrix matrix = new Matrix();
        matrix.setScale(0.2f, 0.2f);
        Bitmap bitmap = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),
                bit.getHeight(), matrix, true);
        return bitmap;
    }


    public VideoPagerAdpter(Context context, ArrayList<MediaItem> mediaItems, Boolean isVideo) {
        this.context = context;
        this.mediaItems = mediaItems;
        this.isVideo = isVideo;
        utils = new Utils();
        lruCacheUtils = new LruCacheUtils(context);

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
     * @param position    The position of the item within the adapter's data set of the item
     *                    whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not
     *                    possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that
     *                    this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_video_pager, null);
            viewHoder = new ViewHoder();
            viewHoder.iv_icon = convertView.findViewById(R.id.iv_icon);
            viewHoder.tv_name = convertView.findViewById(R.id.tv_name);
            viewHoder.tv_time = convertView.findViewById(R.id.tv_time);
            viewHoder.tv_size = convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHoder);
            this.aconvertView = convertView;
        } else {

            viewHoder = (ViewHoder) convertView.getTag();
        }
        //根据位置得到数据
        mediaItem = mediaItems.get(position);
        viewHoder.tv_name.setText(mediaItem.getName());
        viewHoder.tv_size.setText(Formatter.formatFileSize(context, mediaItem.getSize()));
        //使用Formatter包吧byte转为对应的大小
        viewHoder.tv_time.setText(utils.stringForTime((int) mediaItem.getDuration()));
        //使用SimpleDateFormat包把毫秒数转为时间格式
        if (!isVideo) {

            viewHoder.iv_icon.setImageResource(R.drawable.audio_default_icon);
        } else {
            viewHoder.iv_icon.setImageResource(R.drawable.video_default_icon);
            String myName = viewHoder.tv_name.getText().toString().trim();
            if (myName != null && myName.length() > 0) {
                viewHoder.iv_icon.setTag(myName);
            }
            if (lruCacheUtils.getBitmapFromCache(myName) != null) {
                LogUtil.e("缓存图片");
                Bitmap bitmap = lruCacheUtils.getBitmapFromCache(myName);
                viewHoder.iv_icon.setImageBitmap(bitmap);
            } else {
                if (PicUtils.getBitmap(context, myName) != null) {
                    LogUtil.e("硬盘图片");
                    viewHoder.iv_icon.setImageBitmap(PicUtils.getBitmap(context, myName));
                    lruCacheUtils.addBitmapToCache(myName, PicUtils.getBitmap(context, myName));
                } else {
                    new MyAsyncTask(viewHoder, myName).execute(mediaItem);
                }

            }

        }

        return convertView;
    }


    static class ViewHoder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;

    }

    class MyAsyncTask extends AsyncTask<MediaItem, Void, Bitmap> {


        private final ViewHoder myViewHoder;
        private final String mMyname;
        private boolean isDefaultBitmap = false;

        public MyAsyncTask(ViewHoder viewHoder, String myname) {
            myViewHoder = viewHoder;
            mMyname = myname;
        }


        //第一阶段————准备阶段让进度条显示
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //第二阶段——网络获取图片
        @Override
        protected Bitmap doInBackground(MediaItem... params) {
            //从可变参数的数组中拿到第0位的图片地址
            MediaItem mediaItem = params[0];
            String myTag = myViewHoder.iv_icon.getTag().toString().trim();
            Bitmap bitmap = null;
            if (myTag != null && myTag.equals(mMyname)) {
                bitmap = getVideoThumb(mediaItem.getData());
                //返回结果bitmap
                if (bitmap == null) {
                    isDefaultBitmap = true;
                    bitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.video_default_icon);
                }

//            lruCacheUtils.showCache();
                Log.e("BB", "新生成图片");

            } else {
                Log.e("BB", "跳过");
            }

            return bitmap;
        }

        //第三阶段，拿到结果bitmap图片，更新ui
        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (myViewHoder.iv_icon.getTag() != null && myViewHoder.iv_icon.getTag().equals(mMyname)) {
                if (mMyname != null && mMyname.length() > 0 && bitmap != null) {
                    lruCacheUtils.addBitmapToCache(mMyname, bitmap);
                    viewHoder.iv_icon.setImageBitmap(bitmap);
                    if (!isDefaultBitmap) {
                        new Thread() {
                            @Override
                            public void run() {
                                PicUtils.saveBitmap(context, mMyname, bitmap);
                            }
                        }.run();
                    }
                }
            }

        }

    }
}

