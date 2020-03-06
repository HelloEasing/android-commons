package com.easing.commons.android.ui.control.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.easing.commons.android.format.Maths;
import com.easing.commons.android.time.Times;
import com.easing.commons.android.code.Console;
import com.easing.commons.android.manager.Device;
import com.easing.commons.android.value.color.Colors;

public class SinWaveSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder holder;
    private Paint paint;
    private boolean isDrawing;

    private Path path;

    private double x;
    private double y;

    private int screenWidth;

    public SinWaveSurfaceView(Context context) {
        this(context, null, 0);
    }

    public SinWaveSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SinWaveSurfaceView(Context context, AttributeSet attrSet, int style) {
        super(context, attrSet, style);
        init(context, attrSet);
    }

    private void init(Context context, AttributeSet attrSet) {
        holder = getHolder();
        holder.addCallback(this);
        paint = new Paint();
        paint.setColor(Colors.LIGHT_BLUE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(10);
        path = new Path();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDrawing = true;
        path.moveTo(0, 800);
        path.moveTo(0, 0);
        screenWidth = Device.getScreenSize(getContext()).w;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawing = false;
    }

    private void draw() {
        long start = Times.timestamp();
        Canvas canvas = holder.lockCanvas();
        if (x > screenWidth)
            canvas.translate((float) (screenWidth - x), Maths.randomInt(-1, 1));
        canvas.drawColor(Colors.BLACK_80);
        canvas.drawPath(path, paint);
        holder.unlockCanvasAndPost(canvas);
        long end = Times.timestamp();
        //绘制一帧的时间
        Console.info(end - start);
    }

    @Override
    public void run() {
        while (isDrawing) {
            ++x;
            y = 100 * Math.sin(2 * Math.PI * x / 200) + 800;
            path.lineTo((int) x, (int) y);
            draw();
        }
    }
}
