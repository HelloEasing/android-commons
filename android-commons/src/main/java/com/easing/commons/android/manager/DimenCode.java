package com.easing.commons.android.manager;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.easing.commons.android.code.Console;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

public class DimenCode {

    private static int logoRadius = 50;

    public static Bitmap create(String data) {
        return create(data, 512);
    }

    public static Bitmap create(String data, int size) {
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size, hints);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++)
                for (int x = 0; x < size; x++)
                    if (bitMatrix.get(x, y))
                        pixels[y * size + x] = 0xFF000000;
                    else
                        pixels[y * size + x] = 0xFFFFFFFF;
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (Exception e) {
            Console.error(e);
            return null;
        }
    }

    public static Bitmap create(String data, Bitmap logo) {
        return create(data, logo, 512);
    }

    public static Bitmap create(String data, Bitmap logo, int size) {
        try {
            logoRadius = size / 10;
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int halfW = width / 2;
            int halfH = height / 2;
            Matrix m = new Matrix();
            float sx = (float) 2 * logoRadius / logo.getWidth();
            float sy = (float) 2 * logoRadius / logo.getHeight();
            m.setScale(sx, sy);
            logo = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), m, false);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++)
                for (int x = 0; x < size; x++)
                    if (x > halfW - logoRadius && x < halfW + logoRadius && y > halfH - logoRadius && y < halfH + logoRadius)
                        pixels[y * width + x] = logo.getPixel(x - halfW + logoRadius, y - halfH + logoRadius);
                    else {
                        if (bitMatrix.get(x, y))
                            pixels[y * size + x] = 0xff000000;
                        else
                            pixels[y * size + x] = 0xffffffff;
                    }
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (Exception e) {
            Console.error(e);
            return null;
        }
    }


}


