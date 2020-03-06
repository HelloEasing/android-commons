package com.easing.commons.android.ui._old;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

public class EmptyTipListView extends SwipeMenuRecyclerView {

    private View emptyView;

    private AdapterDataObserver dataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (emptyView == null)
                return;
            if (getAdapter() == null) {
                emptyView.setVisibility(View.VISIBLE);
                EmptyTipListView.this.setVisibility(View.GONE);
                return;
            }
            boolean empty = getOriginAdapter().getItemCount() == 0;
            emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
            EmptyTipListView.this.setVisibility(empty ? View.GONE : View.VISIBLE);
        }
    };

    public EmptyTipListView(Context context) {
        super(context);
    }

    public EmptyTipListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyTipListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void bindEmptyTipView(View v) {
        emptyView = v;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (getAdapter() != null)
            getAdapter().unregisterAdapterDataObserver(dataObserver);
        super.setAdapter(adapter);
        if (adapter != null)
            adapter.registerAdapterDataObserver(dataObserver);
        dataObserver.onChanged();
    }
}
