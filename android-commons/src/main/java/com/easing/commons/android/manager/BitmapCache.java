package com.easing.commons.android.manager;

import android.graphics.Bitmap;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;

import lombok.Getter;
import lombok.Setter;

public class BitmapCache {

    private LinkedHashMap<Integer, Bitmap> resCache = new LinkedHashMap();
    private LinkedHashMap<File, Bitmap> localCache = new LinkedHashMap();
    private LinkedHashMap<String, Bitmap> netCache = new LinkedHashMap();

    @Getter
    @Setter
    private float maxCacheSize = Runtime.getRuntime().maxMemory() / 10f / 1024f;

    private float totalResCacheSize = 0f;
    private float totalLocalCacheSize = 0f;
    private float totalNetCacheSize = 0f;

    public boolean hasResCache(int resId) {
        return resCache.get(resId) != null;
    }

    //添加缓存时，更新总大小
    public void addResCache(int resId, Bitmap bitmap) {
        if (hasResCache(resId))
            resCache.remove(resId);
        else
            totalResCacheSize += Bitmaps.byteSize(bitmap);
        resCache.put(resId, bitmap);
        checkResCache();
    }

    //LRU算法，最近使用的排到最后
    public Bitmap getResCache(int resId) {
        Bitmap bitmap = resCache.get(resId);
        if (bitmap != null) {
            resCache.remove(resId);
            resCache.put(resId, bitmap);
        }
        return bitmap;
    }

    //Cache满了，清除较少使用的
    private void checkResCache() {
        if (totalResCacheSize > maxCacheSize) {
            Iterator<Integer> it = resCache.keySet().iterator();
            while (it.hasNext()) {
                int key = it.next();
                Bitmap bitmap = resCache.get(key);
                resCache.remove(key);
                float size = Bitmaps.byteSize(bitmap) / 1024f;
                totalResCacheSize -= size;
                if (totalResCacheSize < maxCacheSize / 2f)
                    break;
            }
        }
    }

}
