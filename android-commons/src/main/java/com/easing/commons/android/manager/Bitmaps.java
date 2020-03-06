package com.easing.commons.android.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.easing.commons.android.ui.dialog.TipBox;
import com.easing.commons.android.value.measure.Size;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import lombok.SneakyThrows;

public class Bitmaps {

    public static Bitmap decodeBitmapFromResource(Context context, int drawableId) {
        return BitmapFactory.decodeResource(context.getResources(), drawableId);
    }

    public static Bitmap decodeBitmapFromFile(String path) {
        return BitmapFactory.decodeFile(path);
    }

    public static Bitmap decodeBitmapFromStream(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }

    public static void loadImageToView(String path, ImageView iv) {
        Bitmap bitmap = Bitmaps.decodeBitmapFromFile(path);
        iv.setImageBitmap(bitmap);
    }

    public static int byteSize(Bitmap bitmap) {
        return bitmap.getByteCount();
    }

    @SneakyThrows
    public static void writeBitmapToFile(Bitmap bitmap, String dst, int quality) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        bitmap.recycle();
        FileOutputStream fos = new FileOutputStream(dst);
        fos.write(bos.toByteArray());
        fos.flush();
        fos.close();
        bos.close();
    }

    //压缩图片文件，并保存至指定位置
    public static void compressImageFile(String src, String dst, int quality) {
        Bitmap bitmap = Bitmaps.decodeBitmapFromFile(src);
        Bitmaps.writeBitmapToFile(bitmap, dst, quality);
    }

    public static Size tryBitmapSize(Context context, int drawableId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), drawableId, opt);
        return new Size(opt.outWidth, opt.outHeight);
    }
}
