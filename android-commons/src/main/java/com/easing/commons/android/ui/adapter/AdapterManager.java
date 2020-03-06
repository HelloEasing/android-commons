package com.easing.commons.android.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.easing.commons.android.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdapterManager {

    //Spinner下拉框适配器
    public static void bindSelectorAdapter(Spinner selector, String[] datas) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(selector.getContext(), R.layout.item_selector, datas);
        selector.setAdapter(adapter);
    }

    //Spinner下拉框适配器
    public static ArrayAdapter<String> getSelectorAdapter(Context ctx, String... datas) {
        return new ArrayAdapter<String>(ctx, R.layout.item_selector, datas);
    }

    //Spinner下拉框适配器
    public static ArrayAdapter<String> getSelectorAdapter(Context ctx, List<String> datas) {
        return new ArrayAdapter<String>(ctx, R.layout.item_selector, datas.toArray(new String[datas.size()]));
    }

    //文本适配器
    public static TextAdapter getTextAdapter(RecyclerView rv, List<String> datas) {
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        TextAdapter adapter = new TextAdapter(rv, datas);
        return adapter;
    }

    //文本适配器
    public static TextAdapter getTextAdapter(RecyclerView rv, String[] datas) {
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        TextAdapter adapter = new TextAdapter(rv, Arrays.asList(datas));
        return adapter;
    }

    //图片适配器
    public static ImageAdapter getImageAdapter(RecyclerView rv, List<Integer> datas) {
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        ImageAdapter adapter = new ImageAdapter(rv, datas);
        return adapter;
    }

    //图片适配器
    public static ImageAdapter getImageAdapter(RecyclerView rv, Integer[] datas) {
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        ImageAdapter adapter = new ImageAdapter(rv, Arrays.asList(datas));
        return adapter;
    }

    //图片文字适配器
    public static CardAdapter getCardAdapter(RecyclerView rv, List<Integer> icons, List<String> titles) {
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        List<CardItem> cards = new ArrayList();
        int size = icons.size();
        for (int i = 0; i < size; i++)
            cards.add(CardItem.create(icons.get(i), titles.get(i)));
        CardAdapter adapter = new CardAdapter(rv, cards);
        return adapter;
    }

    //图片文字适配器
    public static CardAdapter getCardAdapter(RecyclerView rv, Integer[] icons, String[] titles) {
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        List<CardItem> cards = new ArrayList();
        int length = icons.length;
        for (int i = 0; i < length; i++)
            cards.add(CardItem.create(icons[i], titles[i]));
        CardAdapter adapter = new CardAdapter(rv, cards);
        return adapter;
    }
}
