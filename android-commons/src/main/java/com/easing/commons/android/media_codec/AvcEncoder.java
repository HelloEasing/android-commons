package com.easing.commons.android.media_codec;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;

import java.nio.ByteBuffer;

import lombok.SneakyThrows;

//将NV21编码为AVC（YUV-H264）
@SuppressWarnings("all")
public class AvcEncoder {

    static final int INPUT_COLOR_FORMAT = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
    static final String OUTPUT_MEDIA_FORMAT = MediaFormat.MIMETYPE_VIDEO_AVC;

    MediaCodec mediaCodec;
    int width;
    int height;
    int frameRate;
    int bitRate;

    byte[] spNalu; //SPS和PPS
    byte[] originYuv; //摄像头YUV数据，横屏
    byte[] yuv; //旋转后的YUV，竖屏

    long frameIndex = 0;

    //帧率越高，画面越流畅
    //比特率越高，画面越清晰
    //但是帧率和比特率过大，会造网络数据阻塞
    @SneakyThrows
    public static AvcEncoder create(int width, int height, int frameRate, int bitRate) {
        AvcEncoder avcEncoder = new AvcEncoder();
        avcEncoder.width = width;
        avcEncoder.height = height;
        avcEncoder.frameRate = frameRate;
        avcEncoder.bitRate = bitRate;

        //判断是否有合适的编码器
        MediaCodecInfo mediaCodecInfo = selectMediaCodec();
        if (mediaCodecInfo == null) return null;
        int colorFormat = selectColorFormat(mediaCodecInfo);
        if (colorFormat == -1) return null;

        int bufferLength = YUVHandler.yuvBufferLength(width, height);
        avcEncoder.yuv = new byte[bufferLength];
        avcEncoder.originYuv = new byte[bufferLength];

        return avcEncoder;
    }

    @SneakyThrows
    public AvcEncoder configure() {
        //创建对应编码器
        mediaCodec = MediaCodec.createEncoderByType(OUTPUT_MEDIA_FORMAT);
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(OUTPUT_MEDIA_FORMAT, height, width);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, INPUT_COLOR_FORMAT);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5); //IDR帧刷新时间
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        return this;
    }

    @SneakyThrows
    public AvcEncoder start() {
        if (mediaCodec == null) configure();
        mediaCodec.start();
        return this;
    }

    public AvcEncoder pause() {
        mediaCodec.stop();
        return this;
    }

    public AvcEncoder close() {
        mediaCodec.stop();
        mediaCodec.release();
        mediaCodec = null;
        return this;
    }

    //编码YUV数据
    @SneakyThrows
    public int encodeFrame(byte[] input, byte[] output) {
        //摄像头是横屏采集的，要旋转才能正常显示
        YUVHandler.nv21ToNv12(input, originYuv, width, height);
        YUVHandler.yuv420spRotate90(originYuv, yuv, width, height);

        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(yuv);
            long presentationTimeUs = computePresentationTime(frameIndex);
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, yuv.length, presentationTimeUs, 0);
            frameIndex ++;
        }

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        int pos = 0;
        while (outputBufferIndex >= 0) {
            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
            byte[] outData = new byte[bufferInfo.size];
            outputBuffer.get(outData);

            if (spNalu != null) {
                System.arraycopy(outData, 0, output, pos, outData.length);
                pos += outData.length;
            } else {
                //保存SPS和PPS
                ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);
                if (spsPpsBuffer.getInt() == 0x00000001) {
                    spNalu = new byte[outData.length];
                    System.arraycopy(outData, 0, spNalu, 0, outData.length);
                } else return -1;
            }

            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }

        //判断NALU类型是否为IDR
        if (output[4] == 0x65) {
            //给IDR帧添加SPS和PPS
            System.arraycopy(output, 0, yuv, 0, pos);
            System.arraycopy(spNalu, 0, output, 0, spNalu.length);
            System.arraycopy(yuv, 0, output, spNalu.length, pos);
            pos += spNalu.length;
        }

        return pos;
    }

    //判断是否存在指定OUTPUT_MEDIA_FORMAT的编码器
    private static MediaCodecInfo selectMediaCodec() {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) continue;
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++)
                if (types[j].equalsIgnoreCase(OUTPUT_MEDIA_FORMAT))
                    return codecInfo;
        }
        return null;
    }

    //判断是否支持将指定INPUT_COLOR_FORMAT转为想要的OUTPUT_MEDIA_FORMAT
    private static int selectColorFormat(MediaCodecInfo codecInfo) {
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(OUTPUT_MEDIA_FORMAT);
        for (int colorFormat : capabilities.colorFormats)
            if (colorFormat == INPUT_COLOR_FORMAT)
                return colorFormat;
        return -1;
    }

    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / frameRate;
    }

}
