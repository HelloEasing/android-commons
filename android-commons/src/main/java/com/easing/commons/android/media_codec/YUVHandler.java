package com.easing.commons.android.media_codec;

import android.graphics.Bitmap;

//YUV各种子格式之间进行转换
//未经严格测试，部分方法可能有问题
public class YUVHandler {

    //计算YUV的buffer长度
    public static int yuvBufferLength(int width, int height) {
        int stride = (int) Math.ceil(width / 16.0) * 16;
        int y_size = stride * height;
        int c_stride = (int) Math.ceil(width / 32.0) * 16;
        int c_size = c_stride * height / 2;
        return y_size + c_size * 2;
    }

    //镜像化YUV帧
    public static void yuvMirror(byte[] yuv, int w, int h) {
        int a, b;
        byte temp;

        //镜像化Y
        for (int i = 0; i < h; i++) {
            a = i * w;
            b = (i + 1) * w - 1;
            while (a < b) {
                temp = yuv[a];
                yuv[a] = yuv[b];
                yuv[b] = temp;
                a++;
                b--;
            }
        }

        //镜像化U
        int index = w * h;
        for (int i = 0; i < h / 2; i++) {
            a = i * w / 2;
            b = (i + 1) * w / 2 - 1;
            while (a < b) {
                temp = yuv[a + index];
                yuv[a + index] = yuv[b + index];
                yuv[b + index] = temp;
                a++;
                b--;
            }
        }

        //镜像化V
        index = w * h / 4 * 5;
        for (int i = 0; i < h / 2; i++) {
            a = i * w / 2;
            b = (i + 1) * w / 2 - 1;
            while (a < b) {
                temp = yuv[a + index];
                yuv[a + index] = yuv[b + index];
                yuv[b + index] = temp;
                a++;
                b--;
            }
        }
    }

    //NV21转RGBA格式的Bitmap
    public static Bitmap nv21ToRgbaBitmap(byte[] data, int width, int height) {
        int pixelCount = width * height;
        int[] rgba = new int[pixelCount];

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                int y = (0xff & ((int) data[i * width + j]));
                int u = (0xff & ((int) data[pixelCount + (i >> 1) * width + (j & ~1) + 0]));
                int v = (0xff & ((int) data[pixelCount + (i >> 1) * width + (j & ~1) + 1]));
                y = y < 16 ? 16 : y;

                int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
            }

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.setPixels(rgba, 0, width, 0, 0, width, height);
        return bmp;
    }

    //YUV420SP帧顺时针旋转90度
    public static void yuv420spRotate90(byte[] src, byte[] dst, int srcWidth, int srcHeight) {
        int pixelCount = srcWidth * srcHeight;
        int uvHeight = srcHeight >> 1;

        //旋转Y
        int index = 0;
        for (int i = 0; i < srcWidth; i++) {
            int nPos = 0;
            for (int j = 0; j < srcHeight; j++) {
                dst[index] = src[nPos + i];
                index++;
                nPos += srcWidth;
            }
        }

        //旋转UV
        for (int i = 0; i < srcWidth; i += 2) {
            int nPos = pixelCount;
            for (int j = 0; j < uvHeight; j++) {
                dst[index] = src[nPos + i];
                dst[index + 1] = src[nPos + i + 1];
                index += 2;
                nPos += srcWidth;
            }
        }
    }

    //YUV420SP帧逆时针旋转90度
    public static void yuv420spReverseRotate90(byte[] src, byte[] dst, int width, int height) {
        int pixelCount = width * height;
        int uvHeight = height >> 1;

        //旋转Y
        int index = 0;
        for (int i = 0; i < width; i++) {
            int nPos = width - 1;
            for (int j = 0; j < height; j++) {
                dst[index] = src[nPos - i];
                index++;
                nPos += width;
            }
        }

        //旋转UV
        for (int i = 0; i < width; i += 2) {
            int nPos = pixelCount + width - 1;
            for (int j = 0; j < uvHeight; j++) {
                dst[index] = src[nPos - i - 1];
                dst[index + 1] = src[nPos - i];
                index += 2;
                nPos += width;
            }
        }
    }

    //YUV420SP帧旋转180度（顺时针逆时针效果是一样的）
    public static void yuv420spRotate180(byte[] src, byte[] dst, int width, int height) {
        int pixelCount = width * height;
        int uvHeight = height >> 1;

        //旋转Y
        int index = 0;
        for (int j = height - 1; j >= 0; j--) {
            for (int i = width - 1; i >= 0; i--) {
                dst[index++] = src[width * j + i];
            }
        }


        //旋转UV
        for (int j = uvHeight - 1; j >= 0; j--) {
            for (int i = width - 1; i > 0; i -= 2) {
                dst[index] = src[pixelCount + width * j + i - 1];
                dst[index + 1] = src[pixelCount + width * j + i];
                index += 2;
            }
        }
    }

    //YUV420SP帧顺时针旋转270度
    public static void yuv420spRotate270(byte[] src, byte[] dst, int width, int height) {
        int pixelCount = width * height;
        int uvHeight = height >> 1;

        //旋转Y
        int index = 0;
        for (int j = width - 1; j >= 0; j--) {
            for (int i = 0; i < height; i++) {
                dst[index++] = src[width * i + j];
            }
        }

        //旋转UV
        for (int j = width - 1; j > 0; j -= 2) {
            for (int i = 0; i < uvHeight; i++) {
                dst[index++] = src[pixelCount + width * i + j - 1];
                dst[index++] = src[pixelCount + width * i + j];
            }
        }
    }

    //YV12转YU12（I420）
    public static void yv12ToYu12(byte[] yv12bytes, byte[] i420bytes, int width, int height) {
        System.arraycopy(yv12bytes, 0, i420bytes, 0, width * height);
        System.arraycopy(yv12bytes, width * height + width * height / 4, i420bytes, width * height, width * height / 4);
        System.arraycopy(yv12bytes, width * height, i420bytes, width * height + width * height / 4, width * height / 4);
    }

    //NV12转YU12（I420）
    public static void nv12ToYu12(byte[] nv12, byte[] yu12, int width, int height) {
        int nLenY = width * height;
        int nLenU = nLenY / 4;
        System.arraycopy(nv12, 0, yu12, 0, width * height);
        for (int i = 0; i < nLenU; i++) {
            yu12[nLenY + i] = nv12[nLenY + 2 * i + 1];
            yu12[nLenY + nLenU + i] = nv12[nLenY + 2 * i];
        }
    }

    //YV12转NV12
    public static void yv12ToNv12(byte[] yv12, byte[] nv12, int width, int height) {
        int nLenY = width * height;
        int nLenU = nLenY / 4;
        System.arraycopy(yv12, 0, nv12, 0, width * height);
        for (int i = 0; i < nLenU; i++) {
            nv12[nLenY + 2 * i + 1] = yv12[nLenY + i];
            nv12[nLenY + 2 * i] = yv12[nLenY + nLenU + i];
        }
    }

    //NV21转NV12
    public static void nv21ToNv12(byte[] nv21, byte[] nv12, int width, int height) {
        if (nv21 == null || nv12 == null) return;
        int framesize = width * height;
        int i = 0, j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for (i = 0; i < framesize; i++) {
            nv12[i] = nv21[i];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j - 1] = nv21[j + framesize];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j] = nv21[j + framesize - 1];
        }
    }


}
