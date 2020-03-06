package com.easing.commons.android.ui.control.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.easing.commons.android.manager.Dimens;
import com.easing.commons.android.value.color.Colors;

public class RoundColorView extends View {

  public static final int RED = 0xffff6655;
  public static final int GREEN = 0xff44bb66;
  public static final int BLUE = 0xff44aaff;
  public static final int ORANGE = 0xffffbb44;
  public static final int BROWN = 0xffbb7733;
  public static final int[] COLORS = {RED, GREEN, BLUE, ORANGE, BROWN};

  private Activity ctx;

  private Paint cirlePaint;
  private Paint textPaint;
  private String text = "未读";

  private int w;
  private int h;

  public RoundColorView(Context context) {
    this(context, null, 0);
  }

  public RoundColorView(Context context, AttributeSet attrSet) {
    this(context, attrSet, 0);
  }

  public RoundColorView(Context context, AttributeSet attrSet, int style) {
    super(context, attrSet, style);
    init(context, attrSet);
  }

  private void init(Context context, AttributeSet attrSet) {
    this.ctx = (Activity) context;

    cirlePaint = new Paint();
    cirlePaint.setAntiAlias(true);
    cirlePaint.setColor(Colors.LIGHT_BLUE);

    textPaint = new Paint();
    textPaint.setAntiAlias(true);
    textPaint.setColor(Colors.WHITE);
    textPaint.setTextSize(Dimens.toPx(ctx, 14));
  }

  @Override
  protected void onDraw(Canvas canvas) {
    int size = Math.min(w, h);
    float startX = (w - size) / 2f;
    float startY = (h - size) / 2f;
    Rect textBound = new Rect();
    textPaint.getTextBounds(text, 0, text.length(), textBound);
    int textWidth = textBound.right - textBound.left;
    Paint.FontMetrics metrics = textPaint.getFontMetrics();
    canvas.drawCircle(startX + size / 2, startY + size / 2, size / 2, cirlePaint);
    canvas.drawText(text, w / 2 - textWidth / 2, h / 2 + (metrics.descent - metrics.ascent) / 2 - metrics.descent, textPaint);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    this.w = w;
    this.h = h;
  }

  public void set(Integer circleColor, Integer textColor, String text, Integer fontSize) {
    if (circleColor != null)
      cirlePaint.setColor(circleColor);
    if (textColor != null)
      textPaint.setColor(textColor);
    if (text != null)
      this.text = text;
    if (fontSize != null)
      textPaint.setTextSize(Dimens.toPx(ctx, fontSize));
    invalidate();
  }
}
