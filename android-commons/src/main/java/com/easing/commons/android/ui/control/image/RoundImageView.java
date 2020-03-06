package com.easing.commons.android.ui.control.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.easing.commons.android.R;
import com.easing.commons.android.manager.Bitmaps;
import com.easing.commons.android.manager.Dimens;
import com.easing.commons.android.value.color.Colors;

public class RoundImageView extends View {

    public static final int RED = 0xffff6655;
    public static final int GREEN = 0xff44bb66;
    public static final int BLUE = 0xff44aaff;
    public static final int ORANGE = 0xffffbb44;
    public static final int BROWN = 0xffbb7733;
    public static final int[] COLORS = {RED, GREEN, BLUE, ORANGE, BROWN};

    private static final int MODE_BITMAP = 1;
    private static final int MODE_TEXT = 2;

    private int mode = 0;

    private Context context;

    private int fontSize;
    private int textColor;
    private boolean drawBorder;
    private int borderColor;
    private int borderWidth;

    private Paint textPaint;
    private Paint bitmapPaint;
    private Paint backgroundPaint;
    private Paint borderPaint;

    private int radius;
    private int centerX;
    private int centerY;

    private int backgroundColor = COLORS[0];

    private int drawableId;

    private String text;

    private Bitmap srcBitmap;
    private Bitmap dstBitmap;

    public RoundImageView(Context context) {
        this(context, null, 0);
    }

    public RoundImageView(Context context, AttributeSet attrSet) {
        this(context, attrSet, 0);
    }

    public RoundImageView(Context context, AttributeSet attrSet, int style) {
        super(context, attrSet, style);
        init(context, attrSet);
    }

    private void init(Context context, AttributeSet attrSet) {
        this.context = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrSet, R.styleable.RoundImageView);
        drawableId = typedArray.getResourceId(R.styleable.RoundImageView_image, -1);
        fontSize = typedArray.getDimensionPixelSize(R.styleable.RoundImageView_fontSize, Dimens.toPx(context, 24));
        textColor = typedArray.getColor(R.styleable.RoundImageView_textColor, Colors.WHITE);
        drawBorder = typedArray.getBoolean(R.styleable.RoundImageView_border, false);
        borderColor = typedArray.getColor(R.styleable.RoundImageView_borderColor, Colors.WHITE);
        borderWidth = typedArray.getDimensionPixelSize(R.styleable.RoundImageView_borderWidth, 4);
        typedArray.recycle();

        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundColor);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setTextSize(fontSize);
        textPaint.setTextAlign(Paint.Align.LEFT);

        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderWidth);

        mode = (drawableId == -1) ? MODE_TEXT : MODE_BITMAP;
        if (mode == MODE_BITMAP)
            srcBitmap = Bitmaps.decodeBitmapFromResource(context, drawableId);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        centerX = w / 2;
        centerY = h / 2;
        radius = Math.min(w, h) / 2;
        dstBitmap = createRoundDstBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mode == 0)
            return;
        //绘制圆形背景
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);
        //绘制图像
        if (mode == MODE_BITMAP)
            drawBitmap(canvas);
        //绘制文本
        if (mode == MODE_TEXT)
            drawText(canvas);
        //绘制边框
        if (drawBorder)
            canvas.drawCircle(centerX, centerY, radius - borderWidth / 2, borderPaint);
    }

    //绘制图片
    private void drawBitmap(Canvas canvas) {
        //利用XferMode在圆形底图上绘制源图像
        Canvas bitmapCanvas = new Canvas(dstBitmap);
        Rect srcRect = new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
        Rect dstRect = new Rect(0, 0, radius * 2, radius * 2);
        bitmapCanvas.drawBitmap(srcBitmap, srcRect, dstRect, bitmapPaint);
        //绘制最终的圆形图片
        canvas.drawBitmap(dstBitmap, 0, 0, bitmapPaint);
    }

    //绘制圆形底图
    private Bitmap createRoundDstBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(0, 0, radius * 2, radius * 2, radius, radius, paint);
        return bitmap;
    }

    //绘制文字
    private void drawText(Canvas canvas) {
        //绘制文字
        Rect textBound = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBound);
        int textWidth = textBound.right - textBound.left;
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        canvas.drawText(text, 0, text.length(), centerX - textWidth / 2, centerY + (metrics.descent - metrics.ascent) / 2 - metrics.descent, textPaint);
    }

    public void setText(String text) {
        mode = MODE_TEXT;
        this.text = text;
        invalidate();
    }

    public void setImage(int drawableId) {
        mode = MODE_BITMAP;
        this.srcBitmap = Bitmaps.decodeBitmapFromResource(context, drawableId);
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(this.textColor);
        invalidate();
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        backgroundPaint.setColor(this.backgroundColor);
        invalidate();
    }
}
