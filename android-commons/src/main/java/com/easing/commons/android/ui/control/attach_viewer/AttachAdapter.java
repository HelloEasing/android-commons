package com.easing.commons.android.ui.control.attach_viewer;

import android.view.View;
import android.widget.ImageView;

import com.easing.commons.android.R;
import com.easing.commons.android.R2;
import com.easing.commons.android.io.Files;
import com.easing.commons.android.manager.Dimens;
import com.easing.commons.android.manager.Uris;
import com.easing.commons.android.media.MediaType;
import com.easing.commons.android.ui.control.list.easy_list.EasyHolder;
import com.easing.commons.android.ui.control.list.easy_list.EasyList;
import com.easing.commons.android.ui.control.list.easy_list.EasyListAdapter;
import com.easing.commons.android.ui.decoration.SpacingDecoration;
import com.easing.commons.android.ui.dialog.ImageDialog;
import com.easing.commons.android.view.Views;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AttachAdapter extends EasyListAdapter<Attach, AttachAdapter.Holder> {

    boolean removable = true;
    boolean showName = true;

    SpacingDecoration decoration = new SpacingDecoration(10, 3);

    public AttachAdapter(EasyList list) {
        super(list, R.layout.item_attach_list);
    }

    @Override
    public AttachAdapter.Holder onViewCreate() {
        return new Holder();
    }

    @Override
    public void onViewBind(AttachAdapter.Holder holder, Attach data) {
        Views.size(holder.attachViewer, Views.MATCH_PARENT, Dimens.toPx(ctx, 90));
        holder.attachViewer.load(data.path, data.name);
        holder.attachViewer.setOnClickListener(v -> {
            if (MediaType.isImage(data.path))
                ImageDialog.create(data.path).show(ctx);
            else
                Files.openFile(ctx, data.path, Uris.AUTHORITY_FILE_PROVIDER);
        });
        holder.removeButton.setOnClickListener(v -> {
            remove(data);
        });
        //禁止移除操作
        if (removable)
            holder.removeButton.setVisibility(View.VISIBLE);
        else
            holder.removeButton.setVisibility(View.GONE);
        //显示文件名
        if(showName)
            holder.attachViewer.label.setVisibility(View.VISIBLE);
        else
            holder.attachViewer.label.setVisibility(View.GONE);
    }

    public static class Holder extends EasyHolder {
        @BindView(R2.id.attach_viewer)
        AttachViewer attachViewer;
        @BindView(R2.id.bt_remove)
        ImageView removeButton;
    }

    public List<String> attachs() {
        List<String> paths = new ArrayList();
        for (Attach data : datas)
            paths.add(data.path);
        return paths;
    }

    public AttachAdapter add(Attach attach) {
        datas.add(attach);
        notifyDataSetChanged();
        fixMargin();
        return this;
    }

    public AttachAdapter remove(Attach attach) {
        datas.remove(attach);
        notifyDataSetChanged();
        fixMargin();
        return this;
    }

    public void removable(boolean removable) {
        this.removable = removable;
    }

    public void showName(boolean showName) {
        this.showName = showName;
    }

    public AttachAdapter addItemDecoration() {
        list.getBaseList().addItemDecoration(decoration);
        return this;
    }

    //解决布局BUG：更新后需要重新设置间距
    public void fixMargin() {
        ctx.postLater(() -> {
            list.getBaseList().removeItemDecoration(decoration);
            list.getBaseList().addItemDecoration(decoration);
        }, 200);
    }
}
