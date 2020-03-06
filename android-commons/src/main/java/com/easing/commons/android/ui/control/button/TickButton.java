package com.easing.commons.android.ui.control.button;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.easing.commons.android.R;
import com.easing.commons.android.value.color.Colors;

//带动画的打钩按钮
public class TickButton extends View {
	
	private Context ctx;
	
	private Paint circlePaint;
	private Paint hookPaint;
	private Paint wrapCirclePaint;
	private Path hookPath;
	private int colorA;
	private int colorB;
	private float circleRadius;
	private float strokeWidth;
	
	private int state = STATE_START;
	private static final int STATE_START = 0;
	private static final int STATE_ROTATING = 1;
	private static final int STATE_WRAPPING = 2;
	private static final int STATE_ZOOMING = 3;
	private static final int STATE_FINISH = 4;
	
	private ValueAnimator anim1;
	private ValueAnimator anim2;
	private ValueAnimator anim3;
	
	private float sweepAngle;
	private float wrapRadius;
	private float strechRadius;
	
	private float width;
	private float height;
	private float offsetX;
	private float offsetY;
	
	public TickButton(Context context) {
		this(context, null, 0);
	}
	
	public TickButton(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public TickButton(Context context, @Nullable AttributeSet attrs, int style) {
		super(context, attrs, style);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		this.ctx = context;
		
		//解析属性
		TypedArray typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.TickButton);
		circleRadius = typedArray.getDimension(R.styleable.TickButton_circleRadius, 150);
		strokeWidth = typedArray.getDimension(R.styleable.TickButton_strokeWidth, 10);
		colorA = typedArray.getColor(R.styleable.TickButton_circleColor, Colors.LIGHT_BLUE);
		colorB = typedArray.getColor(R.styleable.TickButton_tickColor, Colors.WHITE);
		
		//初始画笔
		circlePaint = new Paint();
		hookPaint = new Paint();
		wrapCirclePaint = new Paint();
		
		circlePaint.setAntiAlias(true);
		hookPaint.setAntiAlias(true);
		wrapCirclePaint.setAntiAlias(true);
		
		hookPaint.setStyle(Paint.Style.STROKE);
		hookPaint.setStrokeWidth(strokeWidth);
		hookPaint.setAntiAlias(true);
		hookPaint.setStrokeCap(Paint.Cap.ROUND);
		hookPaint.setStrokeJoin(Paint.Join.ROUND);
		hookPaint.setPathEffect(new CornerPathEffect(10));
		
		wrapCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		wrapCirclePaint.setColor(Colors.WHITE);
		
		resetPaint();
		
		//初始化动画
		initAnimation();
		
		//点击事件
		setOnClickListener(v -> {
			if (state == STATE_START) {
				state = STATE_ROTATING;
				anim1.start();
			}
			if (state == STATE_FINISH) {
				state = STATE_ZOOMING;
				anim3.start();
			}
			resetPaint();
			invalidate();
		});
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//重新计算绘制位置
		width = super.getMeasuredWidth();
		height = super.getMeasuredHeight();
		offsetX = width / 2 - circleRadius;
		offsetY = height / 2 - circleRadius;
		
		//初始化钩子路径
		float[] index = new float[6];
		index[0] = offsetX + circleRadius * 2 / 3;
		index[1] = offsetY + 1.05F * circleRadius;
		index[2] = offsetX + circleRadius;
		index[3] = offsetY + 1.35F * circleRadius;
		index[4] = offsetX + circleRadius * 2 / 3 * 2;
		index[5] = offsetY + 0.7F * circleRadius;
		hookPath = new Path();
		hookPath.moveTo(index[0], index[1]);
		hookPath.lineTo(index[2], index[3]);
		hookPath.lineTo(index[4], index[5]);
		
		//初始
		if (state == STATE_START) {
			canvas.drawArc(new RectF(offsetX, offsetY, offsetX + circleRadius * 2, offsetY + circleRadius * 2), 0, 360, false, circlePaint);
			canvas.drawPath(hookPath, hookPaint);
			return;
		}
		
		//旋转
		if (state == STATE_ROTATING) {
			canvas.drawArc(new RectF(offsetX, offsetY, offsetX + circleRadius * 2, offsetY + circleRadius * 2), 0, sweepAngle, false, circlePaint);
			return;
		}
		
		//收缩
		if (state == STATE_WRAPPING) {
			canvas.drawCircle(width / 2, height / 2, circleRadius, circlePaint);
			canvas.drawCircle(width / 2, height / 2, wrapRadius, wrapCirclePaint);
			return;
		}
		
		//放大再缩小
		if (state == STATE_ZOOMING) {
			canvas.drawCircle(width / 2, height / 2, strechRadius, circlePaint);
			canvas.drawPath(hookPath, hookPaint);
		}
		
		//完成
		if (state == STATE_FINISH) {
			canvas.drawCircle(width / 2, height / 2, circleRadius, circlePaint);
			canvas.drawPath(hookPath, hookPaint);
		}
	}
	
	//每次状态改变后重置画笔
	public void resetPaint() {
		if (state == STATE_START) {
			circlePaint.setStyle(Paint.Style.STROKE);
			circlePaint.setStrokeWidth(strokeWidth);
			circlePaint.setColor(colorA);
			hookPaint.setColor(colorA);
			return;
		}
		
		circlePaint.setColor(colorA);
		hookPaint.setColor(colorB);
		
		if (state == STATE_ROTATING) {
			circlePaint.setStyle(Paint.Style.STROKE);
			circlePaint.setStrokeWidth(strokeWidth);
		} else
			circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
	}
	
	//开始动画
	public void initAnimation() {
		//圆圈动画：弧角度和动画值一致
		anim1 = ValueAnimator.ofFloat(0, 360);
		anim1.setDuration(800);
		anim1.setInterpolator(new LinearInterpolator());
		anim1.addUpdateListener(animation -> {
			sweepAngle = (float) animation.getAnimatedValue();
			invalidate();
		});
		
		//收缩动画：圆半径和动画值一致
		anim2 = ValueAnimator.ofFloat(circleRadius, 0);
		anim2.setDuration(800);
		anim2.setInterpolator(new LinearInterpolator());
		anim2.addUpdateListener(animation -> {
			wrapRadius = (float) animation.getAnimatedValue();
			invalidate();
		});
		
		//伸缩动画：圆半径先变大再还原
		anim3 = ValueAnimator.ofFloat(circleRadius, circleRadius * 1.5F, circleRadius);
		anim3.setDuration(800);
		anim3.setInterpolator(new LinearInterpolator());
		anim3.addUpdateListener(animation -> {
			strechRadius = (float) animation.getAnimatedValue();
			resetPaint();
			invalidate();
		});
		
		anim1.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				state = STATE_WRAPPING;
				anim2.start();
				resetPaint();
				invalidate();
			}
		});
		
		anim2.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				state = STATE_ZOOMING;
				anim3.start();
				resetPaint();
				invalidate();
			}
		});
		
		anim3.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				state = STATE_FINISH;
				resetPaint();
				invalidate();
			}
		});
	}
}
