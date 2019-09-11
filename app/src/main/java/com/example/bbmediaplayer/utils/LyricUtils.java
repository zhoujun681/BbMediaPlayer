/**
 * @ProjectName: BbMediaPlayer
 * @Package: com.example.bbmediaplayer.utils
 * @ClassName: LyricUtils
 * @CreateDate: 2019-8-13
 * @Author: Bb
 * @Version: 1.0
 * @Description: 解析歌词的工具类
 */
package com.example.bbmediaplayer.utils;

import com.example.bbmediaplayer.domain.Lyric;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LyricUtils {


    //定义区------------------------------------------------------
    private ArrayList<Lyric> lyrics; //歌词列表



    private boolean isExistsLyric = false; //是否存在歌词

    //方法区------------------------------------------------------
    //---构造方法区-------------------------------------------
    //---继 承方法区-------------------------------------------
    //---普通方法区-------------------------------------------

    public boolean isExistsLyric() {
        return isExistsLyric;
    }

    /**
     * 得到解析好的歌词列表
     * @return
     */
    public ArrayList<Lyric> getLyrics() {
        return lyrics;
    }

    /**
     * 读取歌词文件
     *
     * @param file 歌词文件
     */
    public void readLyricFile(File file) {
        if (file == null || !file.exists()) {
            //歌词文件不存在
            lyrics = null;
            isExistsLyric = false;
        } else {
            //歌词文件存在
            try {
                lyrics = new ArrayList<>();
                //1.解析歌词，一行一行读取
                isExistsLyric = true;
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), getCharset(file)));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    line = parsedLyric(line); //读取不为空则解析歌词
                }
                reader.close();
                //2.排序
                Collections.sort(lyrics, new Comparator<Lyric>() {
                    @Override
                    public int compare(Lyric lhs, Lyric rhs) {
                        if (lhs.getTimePoint() < rhs.getTimePoint()) {
                            return -1;
                        } else if (lhs.getTimePoint() > rhs.getTimePoint()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });
                //3.计算每句高亮时间
                for (int i = 0; i < lyrics.size(); i++) {
                    Lyric oneLyric = lyrics.get(i);
                    if (i + 1 < lyrics.size()) { //如果存在下一句，则使用下一句的时间戳减去这一句的时间戳，得到高亮时间
                        Lyric twoLyric = lyrics.get(i + 1);
                        oneLyric.setSleeptime(twoLyric.getTimePoint() - oneLyric.getTimePoint());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 解析一句歌词并放入集合
     * [01:00,05][02:03,04][05:06,07]  云南欢迎您
     *
     * @param line
     * @return
     */
    private String parsedLyric(String line) {
        int pos1 = line.indexOf("["); //第一次出现 [ 的位置,没找到返回-1
        int pos2 = line.indexOf("]"); //第一次出现 ] 的位置,没找到返回-1
        if (pos1 == 0 && pos2 != -1) {//找到第一句
            long[] times = new long[getCountTag(line)];
            String strTime = line.substring(pos1 + 1, pos2);//获得时间01:00,05

            times[0] = strTime2LongTime(strTime); //转换为long时间
            if(times[0] == -1){
                return strTime;
            }
            String content = line;//用来存储解析剩下部分歌词
            int i = 1;
            while (pos1 == 0 && pos2 != -1) { //循环后面的歌词，解析每一句
                content = content.substring(pos2 + 1);//解析剩下部分
                pos1 = content.indexOf("["); //第一次出现 [ 的位置,没找到返回-1
                pos2 = content.indexOf("]"); //第一次出现 ] 的位置,没找到返回-1

                if (pos2 != -1) {
                    strTime = content.substring(pos1 + 1, pos2);
                    times[i] = strTime2LongTime(strTime);

                    if (times[i] == -1) {
                        return "";
                    }
                    i++;
                }
            }
            //把时间数组和文本关联起来加入集合
            Lyric lyric = new Lyric();
            for (int j = 0; j < times.length; j++) {
                if (times[j] != 0) {//有时间戳
                    lyric.setContent(content);
                    lyric.setTimePoint(times[j]);
                    //添加到集合中
                    lyrics.add(lyric);
                    lyric = new Lyric(); //相当于把Lyric lyric = new Lyric();加入循环
                }
            }
            return content;

        }

        return "";
    }

    /**
     * 把String时间转为Long类型
     *
     * @param strTime
     * @return 01:00,05
     */
    private long strTime2LongTime(String strTime) {
        long result = -1;
        try {
            //1.把时间01:00,05切割成01和00,05
            String[] s1 = strTime.split(":");
            //2.把00,05按照 , 切割成00和05
            String[] s2 = s1[1].split("\\.");

            //3.得到分秒和毫秒
            long min = Long.parseLong(s1[0]);
            long second = Long.parseLong(s2[0]);
            long mil = Long.parseLong(s2[1]);

            //4.计算出结果
            result = min * 60 * 1000 + second * 1000 + mil * 10;

        } catch (Exception e) {
            e.printStackTrace();
            result = -1;
        }
        return result;
    }

    /**
     * 判断这一行有多少句歌词
     *
     * @param line
     * @return
     */
    private int getCountTag(String line) {
        int result = -1;
        String[] left = line.split("\\["); //通过 [ 把行分为几部分
        String[] right = line.split("\\]");//通过 ] 把行分为几部分

        if (left.length == 0 && right.length == 0) {
            //如果这一句不包含时间戳，则返回1
            result = -1;
        } else if (left.length > right.length) {
            //如果包含时间戳，则取大值。确保大于实际句数
            result = left.length;
        } else {
            result = right.length;
        }
        return result;
    }

    /**
     * 判断文件编码
     * @param file 文件
     * @return 编码：GBK,UTF-8,UTF-16LE
     */
    public String getCharset(File file) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }
//接口区------------------------------------------------------
//内部类区----------------------------------------------------
//其他区域----------------------------------------------------  
}
