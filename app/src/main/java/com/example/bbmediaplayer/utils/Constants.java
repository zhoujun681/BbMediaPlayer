package com.example.bbmediaplayer.utils;

/**
 * 常量类
 */
public class Constants {
    /**
     * 网络视频api地址
     */
    public static final String NET_URL = "http://api.m.mtime.cn/PageSubArea/TrailerList.api";
    /**
     * 搜索的api地址
     */
    public static final String SEARCH_URL_START = "https://api.jisuapi.com/news/search?keyword=";
    public static final String SEARCH_URL_END = "&appkey=c55b8b77188697d6";
    /**
     * 网络音乐(百思不得姐)的api地址
     */
    public static final String ALL_RES_URL = "http://s.budejie.com/topic/list/jingxuan/1/budejie" +
            "-android-6.2.8/0-20.json?market=baidu&udid=863425026599592&appname=baisibudejie&os=4" +
            ".2.2&client=android&visiting=&mac=98%3A6c%3Af5%3A4b%3A72%3A6d&ver=6.2.8";
    /**
     * 是否使用系统播放器
     */
    public static final boolean UseSystemPlayer = false;
}
