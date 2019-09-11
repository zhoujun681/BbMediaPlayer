/**
 * @ProjectName: BbMediaPlayer
 * @Package: com.example.bbmediaplayer.view
 * @ClassName: ShowLyricView
 * @Description: 显示歌词的View
 * @Author: Bb
 * @CreateDate: 2019/8/8 14:14
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/8/8 14:14
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
package com.example.bbmediaplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DebugUtils;
import android.view.View;

import com.example.bbmediaplayer.domain.Lyric;
import com.example.bbmediaplayer.utils.DensityUtil;

import java.util.ArrayList;

public class ShowLyricView extends View {
    //定义区-------------------------------------------------------------------------------
    private ArrayList<Lyric> lyrics; //歌词列表
    private Paint paint;
    private Paint whitepaint; //白色的画笔
    private float width; //视图的宽
    private float height; //视图的高
    private int index; //歌词列表中的索引
    private float textHeight; //歌词每行的高
    private float currentPosition; //当前播放进度
    private long sleeptime; //高亮显示时间或休眠时间
    private long timePoint; //时间戳，什么时刻高亮哪句歌词

    //方法区-------------------------------------------------------------------------------
    //--构造方法区------------------------
    public ShowLyricView(Context context) {
        this(context, null);
    }


    public ShowLyricView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public ShowLyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    //--继承方法区---------------------------------------
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyrics != null && lyrics.size() > 0) {
            //歌词往上移动
            float plus = 0;
            if(sleeptime == 0){
                plus = 0;
            }else {
                /**
                 * 算法：
                 *        这一句所花的时间 : 休眠时间 = 移动的距离 : 总距离(这一句的行高)
                 *        ->  移动的距离 = (这一句所花的时间 : 休眠时间)*总距离(这一句的行高)
                 *        ->  屏幕坐标 = 行高 + 移动的距离
                 */
                float delta = ((currentPosition - timePoint)/sleeptime)*textHeight;
                plus = textHeight + delta;
            }
            canvas.translate(0,-plus); //动画模式移动距离

            //绘制歌词
            //绘制当前部分
            String currentText = lyrics.get(index).getContent();
            canvas.drawText(currentText, width / 2, height / 2, paint);
            //绘制前面部分
            float tempY = height / 2; //Y轴的中间坐标
            for (int i = index - 1; i >= 0; i--) {
                //绘制每一句歌词
                String preContent = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(preContent, width / 2, tempY, whitepaint);
            }

            //绘制后面部分
            tempY = height / 2; //Y轴的中间坐标
            for (int i = index + 1; i < lyrics.size(); i++) {
                //绘制每一句歌词
                String nextContent = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }
                canvas.drawText(nextContent, width / 2, tempY, whitepaint);
            }
        } else {
            canvas.drawText("没有歌词", width / 2, height / 2, paint);
        }
    }
    //--普通方法区---------------------------------------

    /**
     * 设置歌词
     *
     * @param lyrics
     */
    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }


    /**
     * 初始化视图
     */
    private void initView(Context context) {
        textHeight = DensityUtil.dip2px(context,20); //转换为对应像素
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(DensityUtil.dip2px(context,20));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        whitepaint = new Paint();
        whitepaint.setColor(Color.WHITE);
        whitepaint.setTextSize(DensityUtil.dip2px(context,20));
        whitepaint.setTextAlign(Paint.Align.CENTER);
        whitepaint.setAntiAlias(true);
//        lyrics = new ArrayList<>();
//        for (int i = 0; i < 1000; i++) {
//            Lyric lyric = new Lyric();
//            lyric.setTimePoint(i * 1000);
//            lyric.setSleeptime(1500 + i);
//            lyric.setContent(i + "bbbbbbbbbbbbbbbb" + i);
//            //把歌词添加到集合
//            lyrics.add(lyric);
//        }
    }

    /**
     * 根据播放进度设置高亮显示歌词
     *
     * @param currentPosition
     * 绘制思路：
     * 循环遍历每一句歌词，如果当前播放进度小于这一句歌词的时间戳(即开始显示时间)，
     * 则获取上一句歌词，把上一句歌词显示高亮。
     */
    public void setShowNextLyric(int currentPosition) {
        this.currentPosition = currentPosition;
        if (lyrics == null || lyrics.size() == 0) {
            return;
        }
        for (int i = 1; i < lyrics.size(); i++) {
            //如果当前进度小于此句歌词的开始播放时间
            if (currentPosition < lyrics.get(i).getTimePoint()) {
                int tempIndex = i - 1; //上一句的索引
                //如果进度大于上一句的时间
                long tempTimePoint = lyrics.get(tempIndex).getTimePoint();
                if (currentPosition >= tempTimePoint) {
                    //当前正在播放的歌词，让它高亮显示，即index
                    index = tempIndex;
                    sleeptime = lyrics.get(index).getSleeptime();
                    timePoint = lyrics.get(index).getTimePoint();

                }
            }
        }

        //在主线程重新绘制,如果是子线程用postInvalidate()
        invalidate();

    }


    //接口区--------------------------------------------------------------------------------
}
