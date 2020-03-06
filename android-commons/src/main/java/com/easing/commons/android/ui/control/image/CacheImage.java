package com.easing.commons.android.ui.control.image;

import com.easing.commons.android.manager.BitmapCache;

// 绘制方法可以模仿FlexImage来写
// 但是缩放模式不可以改变，因为缩放会产生新的Bitmap，不能复用Cache
// 一般用于多个Image大小，缩放方式一致的情景下，比如ListView
// 先在ListViewAdapter中创建Cache，然后和Holder中的View绑定，这样就实现了多个控件共用一个Cache
// 将FlexImage中的枚举变量抽离成attribute，定义到XML中
public class CacheImage {

    private BitmapCache cache;

    public void useCache(BitmapCache cache) {
        this.cache = cache;
    }
}
