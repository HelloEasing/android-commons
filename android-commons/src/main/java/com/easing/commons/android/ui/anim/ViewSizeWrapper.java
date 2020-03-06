package com.easing.commons.android.ui.anim;

import android.view.View;

public class ViewSizeWrapper<T extends View> {

    public static final String FIELD_WIDTH = "width";
    public static final String FIELD_HEIGHT = "height";

    private View view;

    public static <T extends View> ViewSizeWrapper wrap(T view) {
        ViewSizeWrapper<T> wrapper = new ViewSizeWrapper();
        wrapper.view = view;
        return wrapper;
    }

    private ViewSizeWrapper() {
    }

    public int getWidth() {
        return view.getLayoutParams().width;
    }

    public ViewSizeWrapper setWidth(int width) {
        view.getLayoutParams().width = width;
        view.requestLayout();
        return this;
    }

    public int getHeight() {
        return view.getLayoutParams().height;
    }

    public ViewSizeWrapper setHeight(int height) {
        view.getLayoutParams().height = height;
        view.requestLayout();
        return this;
    }
}
