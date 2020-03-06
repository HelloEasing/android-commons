package com.easing.commons.android.media_codec;

import android.view.SurfaceHolder;

public interface SurfaceHolderCallback extends SurfaceHolder.Callback {

    @Override
    default void surfaceCreated(SurfaceHolder holder) {}

    @Override
    default void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    default void surfaceDestroyed(SurfaceHolder holder) {}
}
