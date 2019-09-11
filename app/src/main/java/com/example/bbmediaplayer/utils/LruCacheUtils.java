package com.example.bbmediaplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LruCacheUtils {
    //创建Cache缓存,第一个泛型表示缓存的标识key，第二个泛型表示需要缓存的对象
    private LruCache<String, Bitmap> mCaches;

    public class VideoThumb {
        String key;
        Bitmap bitmap;
    }

    public LruCacheUtils(Context context) {

        int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取最大的应用运行时的最大内存
        //通过获得最大的运行时候的内存，合理分配缓存的内存空间大小
        int cacheSize = maxMemory / 2;//取最大运行内存的1/4;
        mCaches = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {//加载正确的内存大小
                return value.getByteCount();//在每次存入缓存的时候调用
            }

        };
        if (CacheUtils.getHashMapData(context, VideoThumb.class) != null) {
            HashMap<String, VideoThumb> map = CacheUtils.getHashMapData(context, VideoThumb.class);
//            for (VideoThumb v : map.values()) {
//                mCaches.put(v.key, v.bitmap);
//            }
//            CacheUtils.showMapCache(context, map);
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                VideoThumb videoThumb = map.get(it.next());
                Log.e("BB",map.toString());
                Log.e("BB",videoThumb.key+"---"+videoThumb.bitmap);
                if (videoThumb.key != null && videoThumb.bitmap != null) {
                    mCaches.put(videoThumb.key, videoThumb.bitmap);
                    Log.e("BB", "从本地sp获取");
                }
            }


        }else {
            Log.e("BB", "未从本地sp获取");
        }
    }

    public void showCache() {
        //key
        for (String key : mCaches.snapshot().keySet()) {
            System.out.println(key);
        }
//value
        for (Bitmap bt : mCaches.snapshot().values()) {
            System.out.println(bt.toString());
        }
    }

    //将图片保存在LruCache中
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {//判断当前的Url对应的Bitmap是否在Lru缓存中，如果不在缓存中，就把当前url对应的Bitmap对象加入Lru缓存
            mCaches.put(url, bitmap);
        }

    }

    //将图片从LruCache中读取出来
    public Bitmap getBitmapFromCache(String url) {
        Bitmap bitmap = mCaches.get(url);//实际上LruCache就是一个Map,底层是通过HashMap来实现的
        return bitmap;
    }

    public Map<String, Bitmap> getSnapshot() {
        return mCaches.snapshot();
    }

}
