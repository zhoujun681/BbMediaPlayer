/**
 * @ProjectName: BbMediaPlayer
 * @Package: com.example.bbmediaplayer.demain
 * @ClassName: Lyric
 * @Description: 歌词
 * @Author: Bb
 * @CreateDate: 2019/8/8 14:17
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/8/8 14:17
 * @UpdateRemark: 例如[00:01:01] HelloWorld
 * @Version: 1.0
 */
package com.example.bbmediaplayer.domain;

public class Lyric {


    /**
     * 歌词内容
     */
    private String content;
    /**
     * 时间戳
     */
    private long timePoint;
    /**
     * 休眠时间或高亮显示时间
     */
    private long sleeptime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public long getSleeptime() {
        return sleeptime;
    }

    public void setSleeptime(long sleeptime) {
        this.sleeptime = sleeptime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", sleeptime=" + sleeptime +
                '}';
    }
}
