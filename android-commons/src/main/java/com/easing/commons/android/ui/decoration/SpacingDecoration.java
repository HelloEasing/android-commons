package com.easing.commons.android.ui.decoration;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class SpacingDecoration extends RecyclerView.ItemDecoration {

    private int spacing;
    private int columnCount;

    public SpacingDecoration(int spacing, int columnCount) {
        this.spacing = spacing;
        this.columnCount = columnCount;
    }

    @Override
    public void getItemOffsets(Rect rect, View view, RecyclerView parent, RecyclerView.State state) {
        int index = parent.getChildLayoutPosition(view);
        int max = parent.getAdapter().getItemCount() - 1;
        rect.right = spacing;
        rect.bottom = spacing;
        if (index % columnCount == columnCount - 1)
            rect.right = 0;
        if (index / columnCount == max / columnCount)
            rect.bottom = 0;
    }

}
