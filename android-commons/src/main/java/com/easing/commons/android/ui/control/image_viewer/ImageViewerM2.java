package com.easing.commons.android.ui.control.image_viewer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.easing.commons.android.view.Views;
import com.easing.commons.android.R;

import java.util.List;

public class ImageViewerM2 extends LinearLayout
{
  private GalleryM1 gallery;
  private ImageView display_img;

  public ImageViewerM2(Context context)
  {
    super(context);
    customStyle(context, null);
  }

  public ImageViewerM2(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    customStyle(context, attrs);
  }

  public ImageViewerM2(Context context, AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
    customStyle(context, attrs);
  }

  private void customStyle(Context context, AttributeSet attrs)
  {
    View dispaly_pane = Views.inflate(context, R.layout.layout_gallery_m02);
    display_img = (ImageView) dispaly_pane.findViewById(R.id.iv_img);
    ImageView iv_pre = (ImageView) dispaly_pane.findViewById(R.id.iv_pre);
    ImageView iv_next = (ImageView) dispaly_pane.findViewById(R.id.iv_next);
    gallery = new GalleryM1(context);
    super.setOrientation(LinearLayout.VERTICAL);
    super.addView(dispaly_pane);
    super.addView(gallery);

    Views.size(dispaly_pane, Views.MATCH_PARENT, 0, 1f);
    Views.size(gallery, Views.MATCH_PARENT, Views.WRAP_CONTENT, 0f);

    iv_pre.setOnClickListener((v) -> gallery.scrollToPrevious());
    iv_next.setOnClickListener((v) -> gallery.scrollToNext());
    gallery.setSelectionEvent((v) -> display_img.setImageBitmap(v.getBitmap()));
  }

  public void initPaths(List<String> paths)
  {
    gallery.initPaths(paths);
  }
}
