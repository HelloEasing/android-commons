package com.easing.commons.android.ui.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.easing.commons.android.R;
import com.easing.commons.android.view.Views;
import com.easing.commons.android.value.measure.Pos;

//显示在指定控件的某侧
public class SideDialog extends PopupWindow {

    private Context ctx;
    private View root;

    public static SideDialog create(Context ctx, int layoutId) {
        SideDialog pop = new SideDialog();
        pop.ctx = ctx;
        pop.root = Views.inflate(ctx, layoutId);
        return pop;
    }

    private SideDialog() {
    }

    public <T extends View> T findView(int viewId) {
        return root.findViewById(viewId);
    }

    //显示在指定控件底部位置
    public void showAtBottom(View anchor) {
        super.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        super.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        super.setContentView(root);
        super.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.container_back_m19_bottom));
        super.setFocusable(true);
        Pos pos = Views.getPositionOnScreen(anchor);
        super.showAtLocation(anchor, Gravity.NO_GRAVITY, pos.x, pos.y + anchor.getMeasuredHeight() - root.getMeasuredHeight());
    }

    //显示在指定控件顶部位置
    public void showAtTop(View anchor) {
        super.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        super.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        super.setContentView(root);
        super.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.container_back_m19_top));
        super.setFocusable(true);
        Pos pos = Views.getPositionOnScreen(anchor);
        super.showAtLocation(anchor, Gravity.NO_GRAVITY, pos.x, pos.y);
    }

    //显示在指定控件左边位置
    public void showAtLeft(View anchor) {
        super.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        super.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        super.setContentView(root);
        super.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.container_back_m19_left));
        super.setFocusable(true);
        super.showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0);
    }

    //显示在指定控件右边位置
    public void showAtRight(View anchor) {
        super.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        super.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        super.setContentView(root);
        super.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.container_back_m19_right));
        super.setFocusable(true);
        Pos pos = Views.getPositionOnScreen(anchor);
        super.showAtLocation(anchor, Gravity.NO_GRAVITY, pos.x + anchor.getMeasuredWidth() - root.getMeasuredWidth(), pos.y);
    }

    //显示在指定控件中间位置
    public void showInMiddle(View anchor) {
        super.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        super.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        super.setContentView(root);
        super.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.color_white_95));
        super.setFocusable(true);
        Pos pos = Views.getPositionOnScreen(anchor);
        super.showAtLocation(anchor, Gravity.NO_GRAVITY, pos.x, pos.y);
    }

    //显示在指定区域
    public void showInBound(View anchor) {
        super.setWidth(anchor.getMeasuredWidth());
        super.setHeight(anchor.getMeasuredHeight());
        super.setContentView(root);
        super.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.color_white_95));
        super.setFocusable(true);
        Pos pos = Views.getPositionOnScreen(anchor);
        super.showAtLocation(anchor, Gravity.NO_GRAVITY, pos.x, pos.y);
    }
}
