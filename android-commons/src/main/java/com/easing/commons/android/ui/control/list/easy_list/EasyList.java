package com.easing.commons.android.ui.control.list.easy_list;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.easing.commons.android.R;
import com.easing.commons.android.app.CommonActivity;
import com.easing.commons.android.view.Views;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lombok.Getter;

public class EasyList extends FrameLayout {

    @Getter
    private BaseList baseList;
    private ImageView emptyView;

    int maxHeight;

    private final RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (baseList.getAdapter() == null) {
                baseList.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                return;
            }
            boolean empty = baseList.getAdapter().getItemCount() == 0;
            if (empty) {
                baseList.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                baseList.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        }
    };

    public EasyList(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.EasyList, 0, 0);
        maxHeight = (int) array.getDimension(R.styleable.EasyList_maxHeight, -1);
        FrameLayout parent = Views.inflate(context, R.layout.layout_easy_list);
        baseList = parent.findViewById(R.id.list);
        emptyView = parent.findViewById(R.id.image_empty);
        addView(parent, Views.MATCH_PARENT, Views.MATCH_PARENT);
        verticalLayout();
    }

    //设置布局
    public EasyList verticalLayout() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        baseList.setLayoutManager(manager);
        return this;
    }

    //设置布局
    public EasyList horizontalLayout() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        baseList.setLayoutManager(manager);
        return this;
    }

    //设置布局
    public EasyList gridLayout(int column) {
        LinearLayoutManager manager = new GridLayoutManager(getContext(), column);
        baseList.setLayoutManager(manager);
        return this;
    }

    //设置布局
    public EasyList layout(RecyclerView.LayoutManager layoutManager) {
        baseList.setLayoutManager(layoutManager);
        return this;
    }

    //设置数据适配器
    public EasyList adapter(EasyListAdapter adapter) {
        if (baseList.getAdapter() != null)
            baseList.getAdapter().unregisterAdapterDataObserver(dataObserver);
        baseList.setAdapter(adapter);
        adapter.list = this;
        adapter.ctx = (CommonActivity) getContext();
        if (adapter != null)
            baseList.getAdapter().registerAdapterDataObserver(dataObserver);
        dataObserver.onChanged();
        return this;
    }

    //设置是否拦截触摸事件
    public EasyList interceptable(boolean interceptable) {
        baseList.setInterceptable(interceptable);
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specSize > maxHeight && maxHeight > -1)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //设置最大高度
    public void maxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        requestLayout();
    }
}
