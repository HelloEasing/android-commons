package com.easing.commons.android.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.format.Texts;
import com.easing.commons.android.manager.Device;
import com.easing.commons.android.view.Views;
import com.easing.commons.android.app.CommonActivity;
import com.easing.commons.android.value.color.Colors;

//弹出任意布局
public class OptionDialog<T> extends DialogFragment {

    private CommonActivity ctx;
    private View root;
    private T[] options;
    private NameTranslator<T> nameTranslator;
    private OnSelectListener<T> listener;

    private int textWidth;

    //静态创建方法
    public static <T> OptionDialog<T> create(CommonActivity ctx, T[] options, OnSelectListener<T> listener) {
        OptionDialog dialog = new OptionDialog();
        dialog.setCancelable(true);
        dialog.ctx = ctx;
        dialog.root = Views.inflate(ctx, R.layout.layout_option_dialog);
        dialog.options = options;
        dialog.listener = listener;
        return dialog;
    }

    //静态创建方法
    public static <T> OptionDialog<T> create(CommonActivity ctx, T[] options, NameTranslator<T> nameTranslator, OnSelectListener<T> listener) {
        OptionDialog dialog = OptionDialog.create(ctx, options, listener);
        dialog.nameTranslator = nameTranslator;
        return dialog;
    }

    //显示
    public void show() {
        FragmentManager manager = ctx.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(this, Texts.random(false, false));
        transaction.commitAllowingStateLoss();
    }

    //隐藏
    public void dispose() {
        dismissAllowingStateLoss();
        ctx = null;
        root = null;
        options = null;
        listener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LinearLayout container = root.findViewById(R.id.layout_items);
        for (T option : options) {
            //添加选项文本
            TextView tv = Views.inflate(ctx, R.layout.item_option_dialog);
            float length = tv.getPaint().measureText(String.valueOf(option));
            textWidth = Math.max(textWidth, (int) length);
            tv.setText(nameTranslator == null ? String.valueOf(option) : nameTranslator.name(option));
            tv.setTag(option);
            container.addView(tv);
            //添加分割线
            View split = new View(ctx);
            container.addView(split);
        }
        for (int i = 0; i < container.getChildCount(); i++) {
            if (i % 2 != 0) {
                View v = container.getChildAt(i);
                v.setLayoutParams(new LinearLayout.LayoutParams(Views.MATCH_PARENT, 1, 0));
                if (i != container.getChildCount() - 1)
                    v.setBackgroundColor(Colors.LIGHT_GREY);
                continue;
            }
            TextView tv = (TextView) container.getChildAt(i);
            tv.setLayoutParams(new LinearLayout.LayoutParams(Views.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
            tv.setOnClickListener(v -> {
                int index = (container.indexOfChild(v) + 1) / 2;
                if (listener != null)
                    listener.onSelect(tv, (T) v.getTag(), index);
                dispose();
            });
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(root);
        AlertDialog dialog = builder.create();
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        int screenWidth = Device.getScreenSize(ctx).w;
        getDialog().getWindow().setLayout((int) (screenWidth * 0.9), Views.WRAP_CONTENT);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) root.getLayoutParams();
        layoutParams.width = Views.MATCH_PARENT;
        layoutParams.height = Views.WRAP_CONTENT;
        root.setLayoutParams(layoutParams);
    }

    //解析子控件
    public <T> T findView(int viewId) {
        return (T) root.findViewById(viewId);
    }

    //选择监听器
    public interface OnSelectListener<T> {
        void onSelect(TextView itemView, T option, int index);
    }

    //名称转换器
    public interface NameTranslator<T> {
        String name(T option);
    }
}
