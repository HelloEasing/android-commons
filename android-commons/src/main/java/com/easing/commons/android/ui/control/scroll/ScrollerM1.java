package com.easing.commons.android.ui.control.scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class ScrollerM1<K, V extends View> extends HorizontalScrollView {

    private Context ctx;
    private LinearLayout scroller;

    @Getter
    private final LinkedList<K> datas = new LinkedList();
    @Getter
    private final LinkedList<V> views = new LinkedList();

    private Map<K, V> dataViewMap = new HashMap();
    private Map<V, K> viewDataMap = new HashMap();

    @Getter
    private View leftPadder;
    @Getter
    private View rightPadder;

    @Setter
    private ViewMapper<K, V> viewMapper;

    public ScrollerM1(Context context) {
        super(context, null, 0);
    }

    public ScrollerM1(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public ScrollerM1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.ctx = context;
        this.leftPadder = new View(context);
        this.rightPadder = new View(context);
        this.scroller = new LinearLayout(context);
        this.scroller.setHorizontalGravity(Gravity.LEFT);
        this.scroller.setVerticalGravity(Gravity.CENTER);
        this.scroller.addView(leftPadder);
        this.scroller.addView(rightPadder);
        super.addView(scroller);
        super.setHorizontalScrollBarEnabled(false);
        super.setVerticalScrollBarEnabled(false);
        super.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
    }

    public void addData(K data) {
        if (this.datas.contains(data)) {
            View view = this.dataViewMap.get(data);
            this.datas.remove(data);
            this.views.remove(view);
            this.dataViewMap.remove(data);
            this.viewDataMap.remove(view);
            super.removeView(view);
        }

        V view = viewMapper.buildView(data);
        this.datas.add(data);
        this.views.add(view);
        this.dataViewMap.put(data, view);
        this.viewDataMap.put(view, data);
        this.scroller.addView(view, views.size());
    }

    public V dataToView(K data) {
        return dataViewMap.get(data);
    }

    public K viewToData(V view) {
        return viewDataMap.get(view);
    }

    public interface ViewMapper<K, V extends View> {
        V buildView(K data);
    }
}
