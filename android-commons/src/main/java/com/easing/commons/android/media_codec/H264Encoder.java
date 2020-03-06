package com.easing.commons.android.media_codec;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;

import java.nio.ByteBuffer;

//代码未经测试，有时间再研究
//YUV420裸帧转H264帧
public class H264Encoder {

    public static final String MIMETYPE = MediaFormat.MIMETYPE_VIDEO_AVC;

    MediaCodec mediaCodec;

    //获取视频格式对应的MediaCodecInfo
    public static MediaCodecInfo getMediaCodecInfo(String mime) {
        int count = MediaCodecList.getCodecCount();
        for (int i = 0; i < count; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder())
                continue;
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++)
                if (types[j].equalsIgnoreCase(mime))
                    return codecInfo;
        }
        return null;
    }

    public static int getColorFormat(MediaCodecInfo mediaCodecInfo, String mime) {
        MediaCodecInfo.CodecCapabilities capabilities = mediaCodecInfo.getCapabilitiesForType(mime);
        for (int i = 0; i < capabilities.colorFormats.length; i++)
            if (capabilities.colorFormats[i] >= MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar
                    && capabilities.colorFormats[i] <= MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar)
                return capabilities.colorFormats[i];
        return -1;
    }

    public H264Encoder configure(int width, int height) {
        try {
            MediaCodecInfo mediaCodecInfo = getMediaCodecInfo(MIMETYPE);
            mediaCodec = MediaCodec.createByCodecName(mediaCodecInfo.getName());
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIMETYPE, width, height);
            mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 300 * 1000);
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, getColorFormat(mediaCodecInfo, MIMETYPE));
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void encodeFrame(byte[] frame, OnFrameEncode onFrameEncode) {
        try {
            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();

            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                inputBuffer.put(frame, 0, frame.length);
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, frame.length, System.nanoTime() / 1000, 0);
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, -1);
            while (outputBufferIndex >= 0) {
                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                byte[] outData = new byte[bufferInfo.size];
                outputBuffer.get(outData);
                if (onFrameEncode != null)
                    onFrameEncode.onFrameEncode(outData);
                mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnFrameEncode {
        void onFrameEncode(byte[] H264Frame);
    }


}
