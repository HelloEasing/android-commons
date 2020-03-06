package com.easing.commons.android.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easing.commons.android.ui.adapter.CardAdapter.Holder;
import com.easing.commons.android.R;

import java.util.List;

public class CardAdapter extends RecyclerAdapter<CardItem, Holder> {
  
  public CardAdapter(RecyclerView recyclerView) {
    super(recyclerView, R.layout.item_card_list);
  }
  
  public CardAdapter(RecyclerView recyclerView, List<CardItem> datas) {
    super(recyclerView, R.layout.item_card_list, datas);
  }
  
  @Override
  public Holder createHolder(View root) {
    CardAdapter.Holder holder = new CardAdapter.Holder(root);
    root.setTag(holder);
    return holder;
  }
  
  @Override
  public void bindHolder(Holder holder, CardItem card) {
    holder.iv.setImageResource(card.icon);
    holder.tv.setText(card.title);
  }
  
  public static class Holder extends ViewHolder {
    public ImageView iv;
    public TextView tv;
    
    public Holder(View root) {
      super(root);
      iv = root.findViewById(R.id.iv);
      tv = root.findViewById(R.id.tv);
    }
  }
  
}
