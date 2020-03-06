package com.easing.commons.android.ui.control.image_viewer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.easing.commons.android.value.measure.Pos;
import com.easing.commons.android.manager.GlobalHandler;
import com.easing.commons.android.view.Views;
import com.easing.commons.android.R;
import com.easing.commons.android.ui.control.scroll.ScrollerM1;

import java.util.List;

@TargetApi(23)
public class ImageViewerM1 extends LinearLayout {
  
  private ScrollerM1<Bitmap, ImageView> scroller;
  private ImageView selected_img;
  private ImageView display_img;
  
  private int SCREEN_WIDTH = 0;
  private int IMAGE_WIDTH = 300;
  private int PADDER_WIDTH = 0;
  
  public ImageViewerM1(Context context) {
    super(context);
    customStyle(context, null);
  }
  
  public ImageViewerM1(Context context, AttributeSet attrs) {
    super(context, attrs);
    customStyle(context, attrs);
  }
  
  public ImageViewerM1(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    customStyle(context, attrs);
  }
  
  private void customStyle(Context context, AttributeSet attrs) {
    View dispaly_pane = Views.inflate(context, R.layout.layout_gallery_m01);
    display_img = (ImageView) dispaly_pane.findViewById(R.id.iv_img);
    ImageView iv_pre = (ImageView) dispaly_pane.findViewById(R.id.iv_pre);
    ImageView iv_next = (ImageView) dispaly_pane.findViewById(R.id.iv_next);
    scroller = new ScrollerM1(context);
    scroller.setOnScrollChangeListener((v, x, y, ox, oy) -> selectItem(computeSelectedItem()));
    super.setOrientation(LinearLayout.VERTICAL);
    super.addView(dispaly_pane);
    super.addView(scroller);
    
    iv_pre.setOnClickListener((v) -> scrollToPrevious());
    iv_next.setOnClickListener((v) -> scrollToNext());
    
    SCREEN_WIDTH = Views.getScreenSize((Activity) this.getContext()).w;
    PADDER_WIDTH = SCREEN_WIDTH / 2 - IMAGE_WIDTH / 2;
    Views.size(scroller, Views.MATCH_PARENT, IMAGE_WIDTH, 0f);
    Views.size(dispaly_pane, Views.MATCH_PARENT, 0, 1f);
    Views.size(scroller.getLeftPadder(), PADDER_WIDTH, 1, 0);
    Views.size(scroller.getRightPadder(), PADDER_WIDTH, 1, 0);
    
    Views.setBackground(scroller, R.drawable.color_black_20);
    scroller.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
    scroller.setViewMapper((bitmap) ->
    {
      ImageView iv = new ImageView(scroller.getContext());
      iv.setImageBitmap(bitmap);
      Views.setBackground(iv, R.drawable.container_back_m17);
      Views.size(iv, IMAGE_WIDTH, IMAGE_WIDTH, 0);
      iv.setOnClickListener((v) -> scrollToItem(scroller.getViews().indexOf(iv)));
      return iv;
    });
    
  }
  
  public void addData(Bitmap bitmap) {
    this.scroller.addData(bitmap);
    if (this.selected_img != null) this.selected_img.setSelected(false);
  }
  
  public void addDatas(List<Bitmap> bitmaps) {
    for (Bitmap bitmap : bitmaps)
      addData(bitmap);
    GlobalHandler.post(() -> updateUI());
  }
  
  public void updateUI() {
    scroller.smoothScrollBy(1, 0);
  }
  
  private int computeSelectedItem() {
    int index = -1;
    
    for (int i = 0; i < scroller.getViews().size(); i++) {
      View v = scroller.getViews().get(i);
      Pos pos = Views.getPositionOnScreen(v);
      int x1 = pos.x;
      int x2 = pos.x + v.getMeasuredWidth();
      if (x1 <= SCREEN_WIDTH / 2 && x2 >= SCREEN_WIDTH / 2) return i;
    }
    
    return index;
  }
  
  public void selectItem(int index) {
    if (index < 0 || index >= scroller.getViews().size()) return;
    
    ImageView view = scroller.getViews().get(index);
    if (view != selected_img) {
      view.setSelected(true);
      if (selected_img != null) selected_img.setSelected(false);
      selected_img = view;
    }
    display_img.setImageBitmap(scroller.viewToData(selected_img));
  }
  
  public void scrollToItem(int index) {
    if (index < 0 || index >= scroller.getViews().size()) return;
    
    int scroll_width = PADDER_WIDTH;
    for (int i = 0; i <= index; i++)
      scroll_width = scroll_width + IMAGE_WIDTH;
    scroll_width = scroll_width - IMAGE_WIDTH / 2 - SCREEN_WIDTH / 2;
    scroller.smoothScrollTo(scroll_width, 0);
  }
  
  public int size() {
    return scroller.getViews().size();
  }
  
  public int selectedIndex() {
    return scroller.getViews().indexOf(selected_img);
  }
  
  public void scrollToPrevious() {
    scrollToItem(selectedIndex() - 1);
  }
  
  public void scrollToNext() {
    scrollToItem(selectedIndex() + 1);
  }
}
