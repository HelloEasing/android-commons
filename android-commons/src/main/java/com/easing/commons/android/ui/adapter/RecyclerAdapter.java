package com.easing.commons.android.ui.adapter;

import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easing.commons.android.app.CommonActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

//RecyclerView使用时必须设置LayoutManager，否则不会显示任何内容
abstract public class RecyclerAdapter<DATA, HOLDER extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    protected CommonActivity ctx;
    protected Handler handler;

    protected RecyclerView recyclerView;

    @Getter
    private List<DATA> datas;

    private int itemLayout;

    //初始化
    public RecyclerAdapter(RecyclerView recyclerView, int itemLayout) {
        this.recyclerView = recyclerView;
        this.ctx = (CommonActivity) recyclerView.getContext();
        this.handler = new Handler();
        this.datas = new ArrayList();
        this.itemLayout = itemLayout;
    }

    public RecyclerAdapter(RecyclerView recyclerView, int itemLayout, List<DATA> datas) {
        this.recyclerView = recyclerView;
        this.ctx = (CommonActivity) recyclerView.getContext();
        this.handler = new Handler();
        this.datas = new ArrayList();
        if (datas != null && !datas.isEmpty())
            this.datas.addAll(datas);
        this.itemLayout = itemLayout;
    }

    public RecyclerAdapter(RecyclerView recyclerView, int itemLayout, List<DATA> datas, RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
        this.recyclerView = recyclerView;
        this.ctx = (CommonActivity) recyclerView.getContext();
        this.handler = new Handler();
        this.datas = new ArrayList();
        if (datas != null && !datas.isEmpty())
            this.datas.addAll(datas);
        this.itemLayout = itemLayout;
    }

    //Item位置
    public int index(DATA data) {
        return datas.indexOf(data);
    }

    //跨线程刷新Adapter
    public void refresh() {
        handler.post(super::notifyDataSetChanged);
    }

    //更新数据
    public void update(List<DATA> datas) {
        this.datas.clear();
        this.datas.addAll(datas);
        refresh();
    }

    //局部刷新
    public void update(DATA data) {
        int pos = datas.indexOf(datas);
        View view = recyclerView.getChildAt(pos);
        if (view != null) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) view.getTag();
            handler.post(() -> onBindViewHolder(holder, pos));
        }
    }

    //清空数据
    public void clear() {
        this.datas.clear();
        refresh();
    }

    //删除Item
    public void remove(int pos) {
        datas.remove(pos);
        super.notifyItemRangeRemoved(pos, 1);
    }

    //删除全部
    public void removeAll() {
        handler.post(() -> {
            int count = datas.size();
            if (count > 0) {
                datas.clear();
                super.notifyItemRangeRemoved(0, count);
            }
        });
    }

    //添加Item
    public void add(int pos, DATA data) {
        datas.add(pos, data);
        super.notifyItemInserted(pos);
    }

    //添加Items
    public void add(List<DATA> datas) {
        this.datas.addAll(datas);
        handler.post(() -> {
            super.notifyItemRangeInserted(this.datas.size() - datas.size(), datas.size());
        });
    }

    //改变Item
    public void change(int pos, DATA data) {
        datas.remove(pos);
        datas.add(pos, data);
        super.notifyItemChanged(pos);
    }

    public void move(int srcPos, int destPos) {
        Collections.swap(datas, srcPos, destPos);
        super.notifyItemMoved(srcPos, destPos);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(ctx).inflate(itemLayout, recyclerView, false);
        return createHolder(root);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
        DATA data = datas.get(pos);
        bindHolder((HOLDER) holder, data);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    //解除资源引用
    public void unbind() {
        datas.clear();
        datas = null;
        recyclerView = null;
        handler = null;
        ctx = null;
    }

    abstract public HOLDER createHolder(View root);

    abstract public void bindHolder(HOLDER holder, DATA data);
}
