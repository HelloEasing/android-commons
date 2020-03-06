package com.easing.commons.android.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;

import android.view.View;

import com.easing.commons.android.view.Views;
import com.easing.commons.android.app.CommonActivity;

//弹出任意布局
public class LayoutDialog extends DialogFragment {

    private CommonActivity ctx;
    private View root;

    //静态创建方法
    public static LayoutDialog create(CommonActivity ctx, int layoutId) {
        LayoutDialog dialog = new LayoutDialog();
        dialog.setCancelable(false);
        dialog.ctx = ctx;
        dialog.root = Views.inflate(ctx, layoutId);
        FragmentManager manager = ctx.getSupportFragmentManager();
        dialog.show(manager, "LayoutDialog");
        return dialog;
    }

    //隐藏
    public void dispose() {
        ctx.postLater(super::dismiss, 500);
        ctx = null;
        root = null;
    }

    //销毁
    public void disposeImmediately() {
        ctx.post(super::dismiss);
        ctx = null;
        root = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(root);
        AlertDialog dialog = builder.create();
        return dialog;
    }

    //解析子控件
    public <T> T findView(int viewId) {
        return (T) root.findViewById(viewId);
    }
}
