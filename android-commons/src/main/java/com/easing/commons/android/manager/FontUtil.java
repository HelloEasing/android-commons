package com.easing.commons.android.manager;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easing.commons.android.ui.control.other.LyricView;

public class FontUtil
{
  public static enum Font
  {
    LIBIAN("font/libian.ttf"), YAHEI_CONSOLAS("font/yahei_consolas.ttf");

    private String path;

    Font(String path)
    {
      this.path = path;
    }
  }

  public static void bindFont(View root, Font font)
  {
    Context context = root.getContext();
    Typeface typeface = Typeface.createFromAsset(context.getAssets(), font.path);

    if (root instanceof ViewGroup)
    {
      ViewGroup viewGroup = (ViewGroup) root;
      for (int i = 0; i < viewGroup.getChildCount(); i++)
        bindFont(viewGroup.getChildAt(i), font);
    }

    else if (root instanceof TextView)
    {
      TextView tv = (TextView) root;
      tv.setTypeface(typeface);
    }

    else if (root instanceof LyricView)
    {
      LyricView tv = (LyricView) root;
      tv.setTypeface(typeface);
    }
  }

  public static Typeface getTypeface(View v, Font font)
  {
    return Typeface.createFromAsset(v.getContext().getAssets(), font.path);
  }

}
