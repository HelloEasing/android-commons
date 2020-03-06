package com.easing.commons.android.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;

import com.easing.commons.android.ui.adapter.TextAdapter.Holder;
import com.easing.commons.android.R;

import java.util.List;

public class TextAdapter extends RecyclerAdapter<String, Holder> {
  
  public TextAdapter(RecyclerView recyclerView) {
    super(recyclerView, R.layout.item_text_list);
  }
  
  public TextAdapter(RecyclerView recyclerView, List<String> datas) {
    super(recyclerView, R.layout.item_text_list, datas);
  }
  
  @Override
  public Holder createHolder(View root) {
    TextAdapter.Holder holder = new TextAdapter.Holder(root);
    root.setTag(holder);
    return holder;
  }
  
  @Override
  public void bindHolder(Holder holder, String text) {
    holder.tv.setText(text);
  }
  
  public static class Holder extends ViewHolder {
    public TextView tv;
    
    public Holder(View root) {
      super(root);
      tv = root.findViewById(R.id.tv);
    }
  }
  
}
