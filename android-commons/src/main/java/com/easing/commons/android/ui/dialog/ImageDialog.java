package com.easing.commons.android.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.easing.commons.android.R;
import com.easing.commons.android.manager.Device;
import com.easing.commons.android.ui.control.button.ImageButtonM2;
import com.easing.commons.android.ui.control.image.photo_view.PhotoView;
import com.easing.commons.android.value.measure.Size;

//图片显示框
public class ImageDialog extends DialogFragment {

    private AppCompatActivity ctx;

    private String url = null;

    private View root;
    private PhotoView iv;

    //静态创建方法
    public static ImageDialog create() {
        ImageDialog dialog = new ImageDialog();
        dialog.setCancelable(false);
        return dialog;
    }

    //静态创建方法
    public static ImageDialog create(String url) {
        ImageDialog dialog = new ImageDialog();
        dialog.setCancelable(true);
        dialog.url = url;
        return dialog;
    }

    //设置消息
    public ImageDialog url(String url) {
        this.url = url;
        return this;
    }

    //显示
    public void show(AppCompatActivity ctx) {
        this.ctx = ctx;
        FragmentManager manager = ctx.getSupportFragmentManager();
        super.show(manager, "MessageDialog");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        root = getActivity().getLayoutInflater().inflate(R.layout.layout_image_dialog, null);
        builder.setView(root);
        AlertDialog dialog = builder.create();
        //解析控件
        iv = root.findViewById(R.id.iv);
        Glide.with(ctx).asDrawable().load(url).into(iv);
        ImageButtonM2 okButton = root.findViewById(R.id.bt_ok);
        okButton.setOnClickListener(v -> {
            dismiss();
        });
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Size screenSize = Device.getScreenSize(ctx);
        int w = (int) (screenSize.w * 0.9);
        int h = (int) (screenSize.h * 0.9);
        getDialog().getWindow().setLayout(w, h);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.color_white);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) root.getLayoutParams();
        layoutParams.width = w;
        layoutParams.height = h;
        layoutParams.gravity = Gravity.CENTER;
        root.setLayoutParams(layoutParams);
    }


}

