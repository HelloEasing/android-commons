package com.easing.commons.android.media_codec;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

public class AspectRatioTextureView extends TextureView {

    double ratio = -1;

    Surface surface;

    public AspectRatioTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAspectRatio(int w, int h) {
        ratio = (double) w / (double) h;
        requestLayout();
    }

    public void resetAspectRatio() {
        ratio = -1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (ratio == -1) {
            setMeasuredDimension(width, height);
            return;
        }
        if (width < height * ratio)
            setMeasuredDimension(width, (int) (width / ratio));
        else
            setMeasuredDimension((int) (height * ratio), height);
    }

    public Surface getSurface(int previewWidth, int previewHeight) {
        if (surface == null) {
            SurfaceTexture texture = getSurfaceTexture();
            texture.setDefaultBufferSize(previewWidth, previewHeight);
            surface = new Surface(texture);
        }
        return surface;
    }

}
