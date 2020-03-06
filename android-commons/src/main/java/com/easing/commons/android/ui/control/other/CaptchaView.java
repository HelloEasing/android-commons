package com.easing.commons.android.ui.control.other;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.easing.commons.android.format.Maths;
import com.easing.commons.android.value.color.Colors;

import java.util.Random;

public class CaptchaView extends AppCompatImageView {
	
	public static char[] CHARS = {
			'1', '2', '3', '4', '5', '6', '7', '8', '9',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
	};
	
	public static int BACKGROUND_COLOR = 0xFFEEEEEE; //背景颜色
	
	public static int BITMAP_WIDTH = 400; //图片大小
	public static int BITMAP_HEIGHT = 100; //图片大小
	
	public static int CODE_LENGTH = 6; //验证码长度
	public static int TEXT_SIZE = 50; //验证码字体大小
	
	public static int PADDING_LEFT_BASE = 40; //默认左边距
	public static int PADDING_LEFT_EXTRA = 20; //左边距偏移量
	public static int PADDING_TOP_BASE = 70; //默认上边距
	public static int PADDING_TOP_EXTRA = 10; //上边距偏移量
	
	public static int LINE_COUNT = 6; //干扰线数量
	public static int LINE_WIDTH = 3; //干扰线线条宽度
	
	private Context ctx;
	private String code;
	
	private int paddingLeft;
	private int paddingTop;
	
	public CaptchaView(Context context) {
		this(context, null, 0);
	}
	
	public CaptchaView(Context context, AttributeSet attrSet) {
		this(context, attrSet, 0);
	}
	
	public CaptchaView(Context context, AttributeSet attrSet, int defStyleAttr) {
		super(context, attrSet, defStyleAttr);
		init(context, attrSet);
	}
	
	private void init(Context context, AttributeSet attrSet) {
		this.ctx = context;
		randomBitmap();
		super.setOnClickListener(v -> randomBitmap());
	}
	
	//随机生成验证码图片
	private void randomBitmap() {
		//随机生成验证码字符
		randomCode();
		//生成Bitmap，用来绘制验证码
		Bitmap bitmap = Bitmap.createBitmap(BITMAP_WIDTH, BITMAP_HEIGHT, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(BACKGROUND_COLOR); //绘制背景
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(TEXT_SIZE);
		//随机生成变形偏移，绘制字符
		paddingLeft = 0;
		for (int i = 0; i < code.length(); i++) {
			randomStyle(paint);
			randomPadding();
			canvas.drawText(code.charAt(i) + "", paddingLeft, paddingTop, paint);
		}
		//随机生成干扰横线
		for (int i = 0; i < LINE_COUNT; i++)
			randomLine(canvas, paint);
		//设置Bitmap
		super.setImageBitmap(bitmap);
		super.setScaleType(ScaleType.CENTER_INSIDE);
	}
	
	//随机生成验证码字符
	private void randomCode() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < CODE_LENGTH; i++)
			builder.append(CHARS[Maths.randomInt(0, CHARS.length - 1)]);
		code = builder.toString();
	}
	
	//随机生成变形样式
	private void randomStyle(Paint paint) {
		paint.setColor(Colors.randomColor());
		paint.setFakeBoldText(new Random().nextBoolean());
		paint.setTextSkewX(Maths.randomInt(-10, 10) / 10f);
	}
	
	//随机生成位置偏移
	private void randomPadding() {
		paddingLeft = paddingLeft + PADDING_LEFT_BASE + Maths.randomInt(5, PADDING_LEFT_EXTRA);
		paddingTop = PADDING_TOP_BASE + Maths.randomInt(2, PADDING_TOP_EXTRA);
	}
	
	//随机生成干扰横线
	private void randomLine(Canvas canvas, Paint paint) {
		int color = Maths.randomInt(0xBB000000, 0xBBFFFFFF);
		int startX = Maths.randomInt(0, BITMAP_WIDTH);
		int startY = Maths.randomInt(0, BITMAP_HEIGHT);
		int endX = Maths.randomInt(0, BITMAP_WIDTH);
		int endY = Maths.randomInt(0, BITMAP_HEIGHT);
		int width = Maths.randomInt(1, LINE_WIDTH) + 1;
		paint.setColor(color);
		paint.setStrokeWidth(width);
		canvas.drawLine(startX, startY, endX, endY, paint);
	}
	
	public String code() {
		return code.toUpperCase();
	}
	
	public boolean pass(String code) {
		return code.equalsIgnoreCase(this.code);
	}
}
