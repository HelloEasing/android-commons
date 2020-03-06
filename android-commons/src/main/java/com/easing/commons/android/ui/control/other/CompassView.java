package com.easing.commons.android.ui.control.other;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

import com.easing.commons.android.R;
import com.easing.commons.android.manager.Bitmaps;

import static android.content.Context.SENSOR_SERVICE;

public class CompassView extends View {

    private Activity ctx;

    private Paint paint;
    private Bitmap circleBitmap;
    private Bitmap pointerBitmap;

    private float rot1;
    private float rot2;
    private float rot3;

    private int w;
    private int h;

    public CompassView(Context context) {
        this(context, null, 0);
    }

    public CompassView(Context context, AttributeSet attrSet) {
        this(context, attrSet, 0);
    }

    public CompassView(Context context, AttributeSet attrSet, int style) {
        super(context, attrSet, style);
        init(context, attrSet);
    }

    private void init(Context context, AttributeSet attrSet) {
        this.ctx = (Activity) context;

        paint = new Paint();
        paint.setAntiAlias(true);

        circleBitmap = Bitmaps.decodeBitmapFromResource(ctx, R.drawable.compass_view_circle);
        pointerBitmap = Bitmaps.decodeBitmapFromResource(ctx, R.drawable.compass_view_pointer);

        //重力传感器，监测手机方向
        SensorManager manager = (SensorManager) ctx.getSystemService(SENSOR_SERVICE);
        Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        manager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (ctx.isFinishing()) {
                    manager.unregisterListener(this);
                    return;
                }
                float[] rs = event.values;
                rot1 = rs[0];  //rz，沿Z轴旋转的角度，对应手机水平旋转，[0，360]
                rot2 = rs[1];  //ry，沿X轴旋转的角度，对应手机上下翻转，[-180,180]
                rot3 = rs[2];  //rz，沿Y轴旋转的角度，对应手机左右翻转，[-90，90]
                invalidate();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int size = Math.min(w, h);
        int startX = (w - size) / 2;
        int startY = (h - size) / 2;
        Rect from = new Rect(0, 0, circleBitmap.getWidth(), circleBitmap.getHeight());
        Rect to = new Rect(startX, startY, startX + size, startY + size);
        canvas.drawBitmap(circleBitmap, from, to, paint);
        canvas.rotate(rot1, w / 2, h / 2);
        canvas.drawBitmap(pointerBitmap, from, to, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
    }

}
