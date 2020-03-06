package com.easing.commons.android.ui.control.list.easy_list;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.easing.commons.android.app.CommonActivity;
import com.easing.commons.android.manager.Dimens;
import com.easing.commons.android.struct.Collections;
import com.easing.commons.android.value.color.Colors;
import com.easing.commons.android.view.Views;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;

//配套EasyList使用的数据适配器
//绑定到Adapter中的List数据，不要在Adapter之外改变，否则可能产生并发错误
//需要操作绑定的List数据时，可以通过临时的中间List对象来实现
abstract public class EasyListAdapter<T, HOLDER extends EasyHolder> extends RecyclerView.Adapter {

    protected EasyList list;

    protected CommonActivity ctx;

    protected Handler handler;

    protected final List<T> datas = new ArrayList();

    protected int layoutId;

    @Setter
    protected View headerView;
    @Setter
    protected View footerView;

    private EasyListAdapter() {
    }

    public EasyListAdapter(EasyList list, int itemLayoutId) {
        this.list = list;
        this.ctx = (CommonActivity) list.getContext();
        this.handler = ctx.handler;
        this.layoutId = itemLayoutId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) return new ViewHolder(headerView);
        if (viewType == 2) return new ViewHolder(footerView);
        View root = LayoutInflater.from(parent.getContext()).inflate(layoutId, null);
        ViewHolder viewHolder = new ViewHolder(root);
        HOLDER holder = onViewCreate();
        holder.root = root;
        ButterKnife.bind(holder, root);
        viewHolder.holder = holder;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder superHolder, int position) {
        if (getItemViewType(position) != 0) return;
        ViewHolder viewHolder = (ViewHolder) superHolder;
        HOLDER holder = (HOLDER) viewHolder.holder;
        onViewBind(holder, datas.get(headerView == null ? position : position - 1));
    }

    abstract public HOLDER onViewCreate();

    abstract public void onViewBind(HOLDER holder, T data);

    @Override
    public int getItemCount() {
        if (datas == null) return 0;
        int count = datas.size();
        if (headerView != null) count++;
        if (footerView != null) count++;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        //HeaderView
        if (headerView != null)
            if (position == 0)
                return 1;
        //FooterView
        if (footerView != null)
            if (position == getItemCount() - 1)
                return 2;
        //普通item
        return 0;
    }

    public void setEmptyHeader(int height) {
        View header = new View(ctx);
        header.setLayoutParams(new LinearLayout.LayoutParams(Views.MATCH_PARENT, Dimens.toPx(height)));
        setHeaderView(header);
    }

    public void setEmptyFooter(int height) {
        View header = new View(ctx);
        header.setLayoutParams(new LinearLayout.LayoutParams(Views.MATCH_PARENT, Dimens.toPx(height)));
        setFooterView(header);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public EasyHolder holder;

        public ViewHolder(View root) {
            super(root);
            this.root = root;
        }
    }

    public void reset(List<T> datas) {
        this.datas.clear();
        this.datas.addAll(datas);
        handler.post(super::notifyDataSetChanged);

    }

    public void reset(T[] datas) {
        if (datas == null)
            this.datas.clear();
        else
            this.datas.addAll(Collections.asList(datas));
        handler.post(super::notifyDataSetChanged);
    }
}
