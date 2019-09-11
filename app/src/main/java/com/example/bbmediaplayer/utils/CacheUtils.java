package com.example.bbmediaplayer.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

/**
 * 缓存工具类
 */
public class CacheUtils{
    private static final String SPNAME="bbmediaplayer";
    /**
     * 缓存字符串数据
     * @param context
     * @param key
     * @param values
     */
    public static void putString(Context context, String key, String values) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPNAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key,values).commit();
    }

    /**
     * 得到缓存的字符串数据
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPNAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }
    /**
     * 缓存整形数据
     * @param context
     * @param key
     * @param values
     */
    public static void putInt(Context context, String key, int values) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPNAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key,values).commit();
    }

    /**
     * 得到缓存的整形数据
     * @param context
     * @param key
     * @return
     */
    public static int getInt(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SPNAME,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key,0);
    }


    /**
     * 用于保存集合
     *
     * @param map map数据
     * @return 保存结果
     */
    public static <K, V> boolean putHashMapData(Context context,Map<K, V> map) {
        boolean result;
        SharedPreferences sp = context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        try {
            Gson gson = new Gson();
            String json = gson.toJson(map);
            editor.putString("VideoThumb", json);
            result = true;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        editor.apply();
        return result;
    }


    /**
     * 用于取出集合
     *
     * @return HashMap
     */
    public static <V> HashMap<String, V> getHashMapData(Context context,Class<V> clsV) {
        SharedPreferences sp = context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
        String json = sp.getString("VideoThumb", "");
        HashMap<String, V> map = new HashMap<>();
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        if(json !=""){
            JsonObject obj= jsonParser.parse(json).getAsJsonObject();
            map.put("recognizeUserMap", gson.fromJson(obj, clsV));

            Log.e(TAG, "getHashMapData-------------------" + obj.toString());

            return map;
        }else {
            return null;
        }

    }

    public static <K,V> void showMapCache(Context context,Map<K, V> map){

        for(Map.Entry<K, V> entry : map.entrySet()){
            K mapKey = entry.getKey();
            V mapValue = entry.getValue();
            Log.e("BB","缓存:"+mapKey+":"+mapValue);
        }

    }


}
