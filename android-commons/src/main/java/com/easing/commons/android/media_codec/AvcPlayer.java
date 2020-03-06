package com.easing.commons.android.media_codec;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.nio.ByteBuffer;

import lombok.Getter;
import lombok.SneakyThrows;

//集成了AvcDecoder解码功能的SurfaceView
public class AvcPlayer extends StateSurfaceView {

    //编码类型
    static final String MEDIA_FORMAT = MediaFormat.MIMETYPE_VIDEO_AVC;

    MediaCodec mediaCodec;

    Callback callback;

    int width;
    int height;

    @Getter
    boolean playing = false;

    public AvcPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(int width, int height, Callback callback) {
        this.callback = callback;
        this.width = width;
        this.height = height;
    }

    @SneakyThrows
    private void configureMediaCodec() {
        mediaCodec = MediaCodec.createDecoderByType(MEDIA_FORMAT);
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MEDIA_FORMAT, width, height);
        mediaCodec.configure(mediaFormat, getHolder().getSurface(), null, 0);
        mediaCodec.start();
    }

    public void decodeNalu(byte[] nalu) {
        if (!playing) return;

        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
        if (inputBufferIndex < 0)
            return;

        ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
        inputBuffer.clear();
        inputBuffer.put(nalu, 0, nalu.length);
        mediaCodec.queueInputBuffer(inputBufferIndex, 0, nalu.length, 0, 0);

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 100);
        while (outputBufferIndex >= 0) {
            mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }

        if (callback != null)
            callback.onFrameUpdate(nalu);
    }

    public void startPreview() {
        if (playing) return;
        configureMediaCodec();
        playing = true;
    }

    public void stopPreview() {
        if (!playing) return;
        playing = false;
        mediaCodec.stop();
        mediaCodec.release();
        mediaCodec = null;
    }

    public interface Callback {

        void onFrameUpdate(byte[] frame);
    }
}
