package com.easing.commons.android.ui.control.loading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.easing.commons.android.R;

public class LoadingViewI extends View {
	
	private Context ctx;
	
	private Bitmap[] bitmaps;
	private Paint paint;
	
	private long startMillis;
	private float rotatePeriod = 2111;
	private float scalePeriod = 1111;
	
	public LoadingViewI(Context context) {
		this(context, null, 0);
	}
	
	public LoadingViewI(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public LoadingViewI(Context context, AttributeSet attrSet, int style) {
		super(context, attrSet, style);
		init(context, attrSet);
	}
	
	private void init(Context context, AttributeSet attrSet) {
		this.ctx = context;
		
		bitmaps = new Bitmap[4];
		bitmaps[0] = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.progress_m05_01);
		bitmaps[1] = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.progress_m05_02);
		bitmaps[2] = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.progress_m05_03);
		bitmaps[3] = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.progress_m05_04);
		
		paint = new Paint();
		paint.setAntiAlias(true);
	}
	
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		if (visibility == View.VISIBLE)
			startMillis = System.currentTimeMillis();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		int ballRadius = Math.min(w, h) / 6;
		int centerX = w / 2;
		int centerY = h / 2;
		
		long timeOffset = System.currentTimeMillis() - startMillis;
		
		//绘制四个位置相隔90度的圆圈
		//圆圈所处角度不变，只是边距按周期缩放，整个图像按周期旋转
		int startX = centerX - ballRadius;
		//通过sin函数来控制边距按周期缩放
		//2.7控制基础边距，0.15控制缩放幅度，scalePeriod控制缩放周期，rotatePeriod控制旋转周期
		//sin函数参数为弧度，需要将角度转化为弧度，作为参数传入
		int startY = centerY - (int) (ballRadius * (2.7 + 0.15 * Math.sin(Math.PI * 1.5 + timeOffset / scalePeriod * Math.PI * 2)));
		//旋转画布来绘制四个圆圈
		float degreeOffset = timeOffset % rotatePeriod / rotatePeriod * 360;
		for (int i = 0; i < 4; i++) {
			canvas.save(); //保存画布状态再还原，保证每次rotate操作都是相当于初始角度旋转
			canvas.rotate(degreeOffset + 90 * i, centerX, centerY);
			Rect source = new Rect(0, 0, bitmaps[i].getWidth(), bitmaps[i].getHeight());
			Rect dest = new Rect(startX, startY, startX + ballRadius * 2, startY + ballRadius * 2);
			canvas.drawBitmap(bitmaps[i], source, dest, paint);
			canvas.restore();
		}
		
		//递归刷新
		invalidate();
	}
}
