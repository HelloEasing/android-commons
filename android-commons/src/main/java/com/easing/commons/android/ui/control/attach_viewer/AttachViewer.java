package com.easing.commons.android.ui.control.attach_viewer;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easing.commons.android.R;
import com.easing.commons.android.io.Files;
import com.easing.commons.android.media.MediaType;
import com.easing.commons.android.view.Views;

import lombok.Getter;

public class AttachViewer extends FrameLayout {

    Context context;
    Handler handler = new Handler();

    @Getter
    String path;
    @Getter
    String name;

    ImageView image;
    TextView label;

    public AttachViewer(Context context, String path, String name) {
        super(context);
        init(context, null, path, name);
    }

    public AttachViewer(Context context) {
        super(context);
        init(context, null, null, null);
    }

    public AttachViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, null, null);
    }

    public AttachViewer(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs, null, null);
    }

    private void init(Context context, AttributeSet attrSet, String path, String name) {
        this.context = context;
        View root = Views.inflate(context, R.layout.layout_attach_viewer);
        image = Views.findView(root, R.id.iv);
        label = Views.findView(root, R.id.label);
        super.addView(root, new FrameLayout.LayoutParams(Views.MATCH_PARENT, Views.MATCH_PARENT, Gravity.CENTER));

        //默认显示内容
        initContent();
    }

    //延迟加载内容
    public void loadLater(String path, String name, int ms) {
        handler.postDelayed(() -> load(path, name), ms);
    }

    //加载内容
    public void load(String path, String name) {
        this.path = path;
        this.name = name;
        label.setText(name);

        //缩放模式与边距
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setPadding(0, 0, 0, 0);

        //网络图片
        if (MediaType.isWebResource(path))
            Glide.with(context).asBitmap().load(path).into(image);

            //文件不存在
        else if (!Files.exist(path))
            label.setText("loading...");

            //图片文件
        else if (MediaType.isImage(name))
            Glide.with(context).asBitmap().load(path).into(image);

            //音频文件
        else if (MediaType.isAudio(name))
            Glide.with(context).asBitmap().load(R.drawable.image_file_type_m01_audio).into(image);

            //视频文件
        else if (MediaType.isVideo(name))
            Glide.with(context).asBitmap().load(R.drawable.image_file_type_m01_video).into(image);

            //文本文件
        else if (MediaType.isText(name))
            Glide.with(context).asBitmap().load(R.drawable.image_file_type_m01_text).into(image);

            //文档文件
        else if (MediaType.isDocument(name))
            Glide.with(context).asBitmap().load(R.drawable.image_file_type_m01_doc).into(image);

            //未知资源
        else {
            label.setText("unknown type");
            Glide.with(context).asBitmap().load(R.drawable.image_file_type_m01_unknown).into(image);
        }
    }

    //默认显示内容
    public void initContent() {
        Glide.with(context).asGif().load(R.drawable.gif_loading).into(image);
        label.setText("loading");
    }

    //是否显示文件名
    public void showName(boolean showName) {
        if (showName)
            label.setVisibility(View.VISIBLE);
        else
            label.setVisibility(View.GONE);
    }
}
