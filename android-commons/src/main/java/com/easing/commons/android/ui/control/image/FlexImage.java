package com.easing.commons.android.ui.control.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.easing.commons.android.value.measure.Size;
import com.easing.commons.android.manager.GlobalHandler;
import com.easing.commons.android.manager.Bitmaps;
import com.easing.commons.android.view.Views;

import lombok.Getter;

// 一个可以自由缩放定位的图像控件
public class FlexImage extends View {

    private Context context;
    private int resId = -1;

    @Getter
    private ScaleMode scaleMode = ScaleMode.NO_SCALE;
    @Getter
    private LocateType locateType = LocateType.LOCATE_BY_PERCENT;

    private float px = 0.5f;
    private float py = 0.5f;

    private Bitmap bitmap;
    private int vw;
    private int vh;
    private int mvw;
    private int mvh;
    private int bw;
    private int bh;

    private Paint paint;

    public static enum ScaleMode {
        FIT_BOTH_SIDE, FIT_SHORT_SIDE, FIT_LONG_SIDE, NO_SCALE
    }

    public static enum LocateType {
        LOCATE_START, // 图像左上角对齐控件左上角
        LOCATE_END, // 图像右下角对齐控件右下角
        LOCATE_CENTER, // 在[LOCATE_START,LOCATE_END]正中间
        LOCATE_BY_PERCENT // 在[LOCATE_START,LOCATE_END]之间按比例偏移
    }

    public FlexImage(Context context) {
        super(context);
        init(context, null);
    }

    public FlexImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FlexImage(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        GlobalHandler.postLater(() -> recordSizeChange(), 10);
    }

    private void recordSizeChange() {
        vw = super.getLayoutParams().width;
        vh = super.getLayoutParams().height;
        if (vw == Views.WRAP_CONTENT && vh == Views.WRAP_CONTENT)
            throw new RuntimeException("width and height can not use WRAP_CONTENT at the same time");
        mvw = super.getMeasuredWidth();
        mvh = super.getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap == null || bitmap.isRecycled())
            return;

        float x = 0;
        float y = 0;
        switch (locateType) {
            case LOCATE_START:
                x = y = 0;
                break;

            case LOCATE_END:
                x = mvw - bw;
                y = mvh - bh;
                break;

            case LOCATE_CENTER:
                x = (mvw - bw) * 0.5f;
                y = (mvh - bh) * 0.5f;
                break;

            case LOCATE_BY_PERCENT:
                x = (mvw - bw) * px;
                y = (mvh - bh) * py;
                break;
        }
        Rect rect = new Rect((int) x, (int) y, (int) (x + bw), (int) (y + bh));
        canvas.drawBitmap(bitmap, null, rect, paint);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        recordSizeChange();
        if (resId != -1)
            loadBitmap(resId);
        super.onSizeChanged(w, h, ow, oh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void loadBitmap(int resId) {
        this.resId = resId;
        Size size = Bitmaps.tryBitmapSize(context, resId);

        if (vw == Views.WRAP_CONTENT) {
            mvw = mvh * size.w / size.h;
            setMeasuredDimension(mvw, mvh);
        }

        if (vh == Views.WRAP_CONTENT) {
            mvh = mvw * size.h / size.w;
            setMeasuredDimension(mvw, mvh);
        }

        decodeBitmap();
        super.invalidate();
    }

    private void decodeBitmap() {
        Size size = Bitmaps.tryBitmapSize(context, resId);
        switch (scaleMode) {
            case NO_SCALE:
                bw = size.w;
                bh = size.h;
                break;

            case FIT_BOTH_SIDE:
                bw = mvw;
                bh = mvh;
                break;

            case FIT_LONG_SIDE:
                if ((float) size.w / mvw > (float) size.h / mvh) {
                    bw = mvw;
                    bh = mvw * size.h / size.w;
                } else {
                    bh = mvh;
                    bw = mvh * size.w / size.h;
                }
                break;

            case FIT_SHORT_SIDE:
                if ((float) size.w / mvw < (float) size.h / mvh) {
                    bw = mvw;
                    bh = mvw * size.h / size.w;
                } else {
                    bh = mvh;
                    bw = mvh * size.w / size.h;
                }
                break;
        }

        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = false;
        op.inScaled = true;
        if (bw < size.w && vh < size.h) {
            final int r1 = Math.round((float) size.w / (float) bw);
            final int r2 = Math.round((float) size.h / (float) bh);
            op.inSampleSize = r1 < r2 ? r1 : r2;
        }
        op.outWidth = bw;
        op.outHeight = bh;
        bitmap = BitmapFactory.decodeResource(context.getResources(), resId, op);
    }

    public void setScaleModeAndLocateType(ScaleMode mode, LocateType type, Float px, Float py) {
        if (bitmap == null)
            return;

        boolean redraw = (scaleMode != mode);
        if (mode != null)
            this.scaleMode = mode;
        if (type != null)
            this.locateType = type;
        if (px != null)
            this.px = px;
        if (py != null)
            this.py = py;
        if (redraw)
            loadBitmap(resId);
        super.invalidate();
    }
}
