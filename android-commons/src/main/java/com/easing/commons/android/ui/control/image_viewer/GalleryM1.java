package com.easing.commons.android.ui.control.image_viewer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.easing.commons.android.value.measure.Pos;
import com.easing.commons.android.manager.GlobalHandler;
import com.easing.commons.android.struct.ObjectPool;
import com.easing.commons.android.thread.Threads;
import com.easing.commons.android.view.Views;
import com.easing.commons.android.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class GalleryM1 extends HorizontalScrollView {
    private Context context;
    private LinearLayout root;
    private View leftPadder;
    private View rightPadder;
    private ImageViewM1 selected_view;
    private ObjectPool<ImageViewM1> view_pool;

    private final LinkedList<String> paths = new LinkedList();
    private final Map<String, Bitmap> bitmap_mapper = new HashMap();

    @Setter
    private SelectionEvent<ImageViewM1> selectionEvent;

    private int SCREEN_WIDTH = 0;
    private int IMAGE_WIDTH = 300;
    private int PADDER_WIDTH = 0;
    private int POOL_SIZE = 0;

    public GalleryM1(Context context) {
        super(context);
        customStyle(context, null);
    }

    public GalleryM1(Context context, AttributeSet attrs) {
        super(context, attrs);
        customStyle(context, attrs);
    }

    public GalleryM1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        customStyle(context, attrs);
    }

    private void customStyle(Context context, AttributeSet attrs) {
        this.context = context;
        this.leftPadder = new View(context);
        this.rightPadder = new View(context);
        this.root = new LinearLayout(context);
        this.root.setHorizontalGravity(Gravity.LEFT);
        this.root.setVerticalGravity(Gravity.CENTER);
        this.root.addView(leftPadder);
        this.root.addView(rightPadder);
        super.addView(root);
        super.setHorizontalScrollBarEnabled(false);
        super.setVerticalScrollBarEnabled(false);
        super.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        SCREEN_WIDTH = Views.getScreenSize((Activity) this.getContext()).w;
        PADDER_WIDTH = SCREEN_WIDTH / 2 - IMAGE_WIDTH / 2;
        POOL_SIZE = SCREEN_WIDTH / IMAGE_WIDTH + 2;
        view_pool = ObjectPool.init(POOL_SIZE, () -> {
            return buildImageViewM1();
        });

        Views.size(leftPadder, PADDER_WIDTH, IMAGE_WIDTH, 0);
        Views.size(rightPadder, PADDER_WIDTH, IMAGE_WIDTH, 0);
        Views.setBackground(this, R.drawable.color_black_20);
    }

    @Override
    protected void onScrollChanged(int x, int y, int ox, int oy) {
        selectView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            int scrollX = this.getScrollX();
            if (scrollX == 0) loadLastItem();
            else if (scrollX == IMAGE_WIDTH * (view_pool.getActiveItems().size() - 1))
                loadNextItem();
        }
        return super.onTouchEvent(ev);
    }

    public void initPaths(List<String> paths) {
        this.paths.clear();
        this.bitmap_mapper.clear();
        this.paths.addAll(paths);

        //移除旧图片
        for (int i = view_pool.getActiveItems().size() - 1; i >= 0; i--) {
            ImageViewM1 active_v = view_pool.getActiveItems().get(i);
            active_v.path = null;
            active_v.bitmap = null;
            active_v.setSelected(false);
            super.removeView(active_v);
            view_pool.recycle(active_v);
        }

        //初始化图片
        int count = paths.size() < POOL_SIZE ? paths.size() : POOL_SIZE;
        Threads.post(() ->
        {
            for (int i = 0; i < count; i++)
                loadBitmap(paths.get(i));
        });
        GlobalHandler.postLater(() -> selectView(), 300);
    }

    private void loadLastItem() {
        if (view_pool.getActiveItems().isEmpty()) return;
        int index = paths.indexOf(view_pool.getActiveItems().getFirst().path);
        if (index == 0) return;

        ImageViewM1 iv_last = view_pool.recycleFromLast();
        iv_last.path = null;
        iv_last.bitmap = null;
        root.removeView(iv_last);

        String path = paths.get(index - 1);
        Bitmap bitmap = bitmap_mapper.get(path);
        ImageViewM1 iv_new = view_pool.obtainToFirst();
        iv_new.path = path;
        iv_new.bitmap = bitmap;
        iv_new.setImageBitmap(bitmap);
        root.addView(iv_new, 1);
        onViewSelected(iv_new);
    }

    private void loadNextItem() {
        if (view_pool.getActiveItems().isEmpty()) return;
        int index = paths.indexOf(view_pool.getActiveItems().getLast().path);
        if (index == paths.size() - 1) return;

        ImageViewM1 iv_first = view_pool.recycleFromFirst();
        iv_first.path = null;
        iv_first.bitmap = null;
        root.removeView(iv_first);

        String path = paths.get(index + 1);
        Bitmap bitmap = bitmap_mapper.get(path);
        if (bitmap != null) {
            ImageViewM1 iv_new = view_pool.obtainToLast();
            iv_new.path = path;
            iv_new.bitmap = bitmap;
            iv_new.setImageBitmap(bitmap);
            root.addView(iv_new, root.getChildCount() - 1);
            onViewSelected(iv_new);
        } else Threads.post(() -> loadBitmap(path));
    }

    private void selectView() {
        for (ImageViewM1 v : view_pool.getActiveItems()) {
            Pos pos = Views.getPositionOnScreen(v);
            int x1 = pos.x;
            int x2 = pos.x + v.getMeasuredWidth();
            if (x1 <= SCREEN_WIDTH / 2 && x2 >= SCREEN_WIDTH / 2) {
                onViewSelected(v);
                break;
            }
        }
    }

    private void onViewSelected(ImageViewM1 v) {
        if (selected_view != null) selected_view.setSelected(false);
        v.setSelected(true);
        selected_view = v;
        if (selectionEvent != null) selectionEvent.action(v);
    }

    private ImageViewM1 buildImageViewM1() {
        ImageViewM1 iv = new ImageViewM1(super.getContext());
        Views.size(iv, IMAGE_WIDTH, IMAGE_WIDTH, 0f);
        Views.setBackground(iv, R.drawable.container_back_m18);
        return iv;
    }

    private void loadBitmap(String path) {
        //加载图片需要时间，paths不会立刻出现新load的bitmap，快速滑动两次，就可能出现连续加载两次相同图片的情况
        synchronized (bitmap_mapper) {
            if (bitmap_mapper.get(path) != null) return;
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            bitmap_mapper.put(path, bitmap);
        }

        GlobalHandler.post(() -> {
            Bitmap bitmap = bitmap_mapper.get(path);
            ImageViewM1 iv = view_pool.obtainToLast();
            iv.path = path;
            iv.bitmap = bitmap;
            iv.setImageBitmap(bitmap);
            root.addView(iv, root.getChildCount() - 1);
        });
    }

    public int size() {
        return view_pool.getActiveItems().size();
    }

    public int selectedIndex() {
        return view_pool.getActiveItems().indexOf(selected_view);
    }

    public void scrollToItem(int index) {
        if (index < 0 || index >= view_pool.getActiveItems().size()) return;

        int scroll_width = PADDER_WIDTH;
        for (int i = 0; i <= index; i++)
            scroll_width = scroll_width + IMAGE_WIDTH;
        scroll_width = scroll_width - IMAGE_WIDTH / 2 - SCREEN_WIDTH / 2;
        this.smoothScrollTo(scroll_width, 0);
    }

    public void scrollToPrevious() {
        scrollToItem(selectedIndex() - 1);
    }

    public void scrollToNext() {
        scrollToItem(selectedIndex() + 1);
    }

    @Getter
    public static class ImageViewM1 extends AppCompatImageView {
        private String path;
        private Bitmap bitmap;

        public ImageViewM1(Context context) {
            super(context);
        }
    }

    public static interface SelectionEvent<V> {
        public void action(V view);
    }
}
