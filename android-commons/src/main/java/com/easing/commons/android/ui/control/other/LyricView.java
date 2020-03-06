package com.easing.commons.android.ui.control.other;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.easing.commons.android.manager.FontUtil;
import com.easing.commons.android.manager.FontUtil.Font;
import com.easing.commons.android.R;
import com.easing.commons.android.value.color.Colors;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.SneakyThrows;

// 使用方法：
// 1. 在XML中引用控件，可以配置fontSize, lineSpacing, rowCount, normalTextColor, activeTextColor等属性
// 2. 在APP监控播放进度，调用updateLyricPath和updateLyric方法更新控件内容
public class LyricView extends View {
	
	private final int BASE_TEXT_SIZE = 50;
	private final int BASE_SPACING = 30;
	private final int BASE_ROW_COUNT = 15;
	
	private final String LRC_NONE = "Lyric Not Found";
	private final String LRC_WAITING = "Loading ...";
	
	private final int SCROLL_DURATION = 300;
	
	private float textSize;
	private float spacing;
	private int rowCount;
	
	private int width;
	private int height;
	
	private Paint normalPaint;
	private Paint activePaint;
	
	private Scroller scroller;
	private int scrollUnit;
	private int scrollY;
	
	private String lyricPath = null;
	private LinkedList<LyricLine> lrcs = new LinkedList();
	
	//记录歌词位置
	private int currentLine = -1;
	
	public LyricView(Context context) {
		super(context);
		init(context, null);
	}
	
	public LyricView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	public LyricView(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
		init(context, attrs);
	}
	
	// 初始化属性值，和需要用到的辅助对象
	private void init(Context context, AttributeSet attrSet) {
		//获取属性值
		TypedArray attrs = getContext().obtainStyledAttributes(attrSet, R.styleable.LyricView);
		textSize = attrs.getFloat(R.styleable.LyricView_fontSize, 1.0f);
		spacing = attrs.getFloat(R.styleable.LyricView_lineSpace, 1.0f);
		rowCount = attrs.getInteger(R.styleable.LyricView_rowCount, BASE_ROW_COUNT);
		int normalTextColor = attrs.getColor(R.styleable.LyricView_normalTextColor, Colors.BLACK);
		int activeTextColor = attrs.getColor(R.styleable.LyricView_activeTextColor, Colors.RED);
		attrs.recycle();
		
		//计算LyricView高度
		height = (int) (textSize * BASE_TEXT_SIZE + spacing * BASE_SPACING) * rowCount;
		
		//初始化绘制普通文字的画笔
		normalPaint = new Paint();
		normalPaint.setTextSize(textSize * BASE_TEXT_SIZE);
		normalPaint.setColor(normalTextColor);
		normalPaint.setAntiAlias(true);//防锯齿
		
		//初始化绘制激活文字的画笔
		activePaint = new Paint();
		activePaint.setTextSize(textSize * BASE_TEXT_SIZE);
		activePaint.setColor(activeTextColor);
		activePaint.setAntiAlias(true);
		
		//设置画笔阴影
		BlurMaskFilter blur = new BlurMaskFilter(0.5f, Blur.SOLID);
		normalPaint.setMaskFilter(blur);
		normalPaint.setShadowLayer(0.5f, 0.3f, 0.3f, Colors.WHITE_50);
		activePaint.setMaskFilter(blur);
		activePaint.setShadowLayer(0.5f, 0.3f, 0.3f, Colors.BLACK_50);
		
		//绑定字体
		FontUtil.bindFont(this, Font.LIBIAN);
		
		//计算每句歌词需要滚动的距离
		Rect textBounds = new Rect();
		activePaint.getTextBounds(LRC_NONE, 0, LRC_NONE.length(), textBounds);
		int textHeight = textBounds.height();
		scrollUnit = (int) (textHeight + spacing * BASE_SPACING);
		
		//初始化滚动器
		//这个对象并不会对实际View进行滚动，只是记录预期的滚动状态
		//在onDraw方法中，再根据Scroller的状态值绘制控件状态
		scroller = new Scroller(context, new LinearInterpolator());
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		float centerY = super.getMeasuredHeight() / 2;
		
		//没有歌词，显示无歌词
		if (lyricPath == null || lrcs.isEmpty()) {
			canvas.drawText(LRC_NONE, (width - activePaint.measureText(LRC_NONE)) / 2, centerY, activePaint);
			return;
		}
		
		//歌曲未开始，显示加载中
		if (currentLine < 0) {
			canvas.drawText(LRC_WAITING, (width - activePaint.measureText(LRC_NONE)) / 2, centerY, activePaint);
			return;
		}
		
		//绘制当前正在播放的歌词，显示在正中间
		String currentLrc = lrcs.get(currentLine).line;
		float currentX = (width - activePaint.measureText(currentLrc)) / 2;
		canvas.drawText(currentLrc, currentX, centerY + scrollUnit - scrollY, activePaint);
		
		//确定显示范围
		int firstLine = currentLine - rowCount / 2;
		if (firstLine < 0)
			firstLine = 0;
		int lastLine = currentLine + rowCount / 2;
		if (lastLine >= lrcs.size() - 1)
			lastLine = lrcs.size() - 1;
		
		//绘制前面的歌词
		for (int i = 1; currentLine - i >= firstLine; i++) {
			String lrc = lrcs.get(currentLine - i).line;
			float X = (width - normalPaint.measureText(lrc)) / 2;
			canvas.drawText(lrc, X, centerY - i * scrollUnit + scrollUnit - scrollY, normalPaint);
		}
		
		//绘制后面的歌词
		for (int i = 1; currentLine + i <= lastLine; i++) {
			String lrc = lrcs.get(currentLine + i).line;
			float X = (width - normalPaint.measureText(lrc)) / 2;
			canvas.drawText(lrc, X, centerY + i * scrollUnit + scrollUnit - scrollY, normalPaint);
		}
	}
	
	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset())  //滚动开始
		{
			scrollY = scroller.getCurrY();
			super.postInvalidate();
		}
	}
	
	@Override
	protected void onMeasure(int w, int h) {
		//最大不超过歌词总高度
		h = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
		super.onMeasure(w, h);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int old_w, int old_h) {
		//大小变化时，重新计算LyricView宽度
		super.onSizeChanged(w, h, old_w, old_h);
		width = super.getMeasuredWidth();
	}
	
	//更新歌词文件
	synchronized public void updateLyricPath(String path) {
		clearLrcs();
		if (path != null) {
			this.lyricPath = path;
			loadLrcs();
		}
		super.postInvalidate();
	}
	
	//更新歌词进度
	synchronized public void updateLyricTime(long time) {
		if (lyricPath == null)
			return;
		
		//计算歌词位置
		int tempLine = -1;
		for (int i = 0; i < lrcs.size(); i++)
			if (lrcs.get(i).time <= time)
				tempLine = i;
		
		//无变化，则不更新
		if (tempLine == currentLine)
			return;
		
		//进入下一句，显示动画
		currentLine = tempLine;
		scroller.startScroll(0, 0, 0, scrollUnit, SCROLL_DURATION);
		super.postInvalidate();
	}
	
	private void clearLrcs() {
		this.lyricPath = null;
		lrcs.clear();
		currentLine = -1;
	}
	
	@SneakyThrows
	private void loadLrcs() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.lyricPath)));
		
		String line;
		LinkedList<LyricLine> currentLines;//支持[00:10][03:30]XXXXX格式，一行可能对应多句歌词
		LinkedList<LyricLine> lrcLines = new LinkedList();
		
		while ((line = reader.readLine()) != null) {
			currentLines = parseLine(line);
			if (currentLines != null)
				lrcLines.addAll(currentLines);
		}
		reader.close();
		
		Collections.sort(lrcLines);
		lrcs.addAll(lrcLines);
	}
	
	//解析歌词，格式：[00:10][03:30]XXXXX
	private LinkedList<LyricLine> parseLine(String line) {
		//简单匹配是不是歌词（不严谨）
		String pattern = "^\\[+[0-9][0-9]:[0-5][0-9]\\.[0-9][0-9]\\]+.*$";
		Matcher matcher = Pattern.compile(pattern).matcher(line);
		if (!matcher.matches())
			return null;
		
		//分割时间和歌词，空歌词用省略号代替
		line = line.replaceAll("\\[", "");
		if (line.endsWith("]"))
			line += "...";
		String[] strs = line.split("\\]");
		
		//返回LrcLine对象，一行歌词可能对应多个时间，所以有多个LyricLine
		LinkedList<LyricLine> lyricLines = new LinkedList();
		for (int i = 0; i < strs.length - 1; i++) {
			LyricLine lrc = new LyricLine();
			lrc.time = parseTime(strs[i]);
			lrc.line = strs[strs.length - 1];
			lyricLines.add(lrc);
		}
		
		return lyricLines;
	}
	
	//解析时间
	private Long parseTime(String time) {
		String min = time.split(":")[0];
		String sec = time.split(":")[1].split("\\.")[0];
		String mil = time.split(":")[1].split("\\.")[1];
		return Long.parseLong(min) * 60 * 1000 + Long.parseLong(sec) * 1000 + Long.parseLong(mil) * 10;
	}
	
	public void setTypeface(Typeface tf) {
		normalPaint.setTypeface(tf);
		activePaint.setTypeface(tf);
	}
	
	private static class LyricLine implements Comparable<LyricLine> {
		private String line;
		private long time;
		
		public int compareTo(LyricLine another) {
			return (int) (time - another.time);
		}
	}
}
