package com.easing.commons.android.ui.adapter;

public class CardItem {
  
  public int icon;
  public String title;
  
  public static CardItem create(int icon, String title) {
    CardItem card = new CardItem();
    card.icon = icon;
    card.title = title;
    return card;
  }
}
