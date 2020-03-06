package com.easing.commons.android.media_codec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import java.nio.ByteBuffer;

import lombok.SneakyThrows;

//将AVC解码为图像
@SuppressWarnings("all")
public class AvcDecoder {

    //编码类型
    static final String MEDIA_FORMAT = MediaFormat.MIMETYPE_VIDEO_AVC;

    MediaCodec mediaCodec;
    Surface surface;
    int width;
    int height;
    int frameRate;

    long frameIndex = 0;

    public static AvcDecoder create(int width, int height, int frameRate, Surface surface) {
        AvcDecoder avcDecoder = new AvcDecoder();
        avcDecoder.width = width;
        avcDecoder.height = height;
        avcDecoder.frameRate = frameRate;
        avcDecoder.surface = surface;
        return avcDecoder;
    }

    @SneakyThrows
    public AvcDecoder configure() {
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MEDIA_FORMAT, width, height);
        mediaCodec = MediaCodec.createDecoderByType(MEDIA_FORMAT);
        mediaCodec.configure(mediaFormat, surface, null, 0);
        return this;
    }

    @SneakyThrows
    public AvcDecoder start() {
        if (mediaCodec == null) configure();
        mediaCodec.start();
        return this;
    }

    public AvcDecoder pause() {
        mediaCodec.stop();
        return this;
    }

    public AvcDecoder close() {
        mediaCodec.stop();
        mediaCodec.release();
        mediaCodec = null;
        return this;
    }

    public boolean decodeNalu(byte[] nalu) {
        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(100);

        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(nalu);
            long presentationTimeUs = computePresentationTime(frameIndex);
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, nalu.length, presentationTimeUs, 0);
            frameIndex++;
        } else return false;

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 100);
        while (outputBufferIndex >= 0) {
            mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
        return true;
    }

    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / frameRate;
    }

}
