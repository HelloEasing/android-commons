package com.easing.commons.android.ui.control.other;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.easing.commons.android.value.color.Colors;

public class SpiderView extends View {
	
	private Context ctx;
	
	private float centerX; //圆心X
	private float centerY; //圆心Y
	private float radius; //半径
	private float hierarchy; //不同档次之间的间隔
	
	private int edgeCount = 4; //多边形边数
	private int levelCount = 4; //评分等级数
	private float radians = (float) Math.toRadians(90); //每个维度的总弧度
	private float[] scores = {0.5f, 0.8f, 0.7f, 0.8f}; //每个维度的评分
	
	private Paint spiderPaint; //蜘蛛线画笔
	private Paint scorePaint; //评分画笔
	
	public SpiderView(Context context) {
		this(context, null, 0);
	}
	
	public SpiderView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public SpiderView(Context context, @Nullable AttributeSet attrs, int style) {
		super(context, attrs, style);
		init(context, attrs);
	}
	
	public void setScores(float[] scores) {
		this.scores = scores;
		this.edgeCount = scores.length;
		this.radians = (float) Math.toRadians(360f / edgeCount);
		invalidate();
	}
	
	public void setLevelCount(int levelCount) {
		this.levelCount = levelCount;
		invalidate();
	}
	
	private void init(Context context, AttributeSet attrSet) {
		this.ctx = context;
		
		//蜘蛛网画笔
		spiderPaint = new Paint();
		spiderPaint.setColor(Colors.LIGHT_BLUE);
		spiderPaint.setStyle(Paint.Style.STROKE);
		spiderPaint.setStrokeCap(Paint.Cap.ROUND);
		spiderPaint.setStrokeWidth(2);
		spiderPaint.setAntiAlias(true);
		
		//分数画笔
		scorePaint = new Paint();
		scorePaint.setColor(Colors.RED_50);
		scorePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		scorePaint.setAntiAlias(true);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//计算半径，中心，等级间距
		int width = super.getMeasuredWidth();
		int height = super.getMeasuredHeight();
		centerX = width / 2f;
		centerY = height / 2f;
		radius = Math.min(width, height) / 2f;
		hierarchy = radius / levelCount;
		
		//绘制每个等级对应的内圆
		for (int i = 1; i <= levelCount; i++) {
			//绘制蜘蛛线
			Path spiderPath = new Path();
			//当前等级圆半径
			float levelRadius = hierarchy * i;
			
			//绘制每个维度对应的路径
			for (int j = 0; j < edgeCount; j++) {
				//蜘蛛线起点弧度
				float startRadians = radians * j;
				//蜘蛛线起点坐标
				float startSpiderX = centerX + (float) Math.sin(startRadians) * levelRadius;
				float startSpiderY = centerY - (float) Math.cos(startRadians) * levelRadius;
				
				//绘制每个维度的边界线
				if (i == levelCount) {
					Path linePath = new Path();
					linePath.moveTo(centerX, centerY);
					linePath.lineTo(startSpiderX, startSpiderY);
					canvas.drawPath(linePath, spiderPaint);
				}
				
				//绘制蜘蛛线路径
				if (j == 0)
					spiderPath.moveTo(startSpiderX, startSpiderY);
				else
					spiderPath.lineTo(startSpiderX, startSpiderY);
			}
			
			//绘制蜘蛛线
			spiderPath.close();
			canvas.drawPath(spiderPath, spiderPaint);
		}
		
		//绘制评分多边形路径
		Path scorePath = new Path();
		for (int j = 0; j < edgeCount; j++) {
			//得分对应的半径
			float scoreRadius = radius * scores[j];
			//得分点的坐标
			float startScoreX = centerX + (float) Math.sin(radians * j) * scoreRadius;
			float startScoreY = centerY - (float) Math.cos(radians * j) * scoreRadius;
			//绘制分数路径
			if (j == 0)
				scorePath.moveTo(startScoreX, startScoreY);
			else
				scorePath.lineTo(startScoreX, startScoreY);
		}
		scorePath.close();
		canvas.drawPath(scorePath, scorePaint);
	}
}
