package com.easing.commons.android.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.app.CommonActivity;
import com.easing.commons.android.thread.Threads;
import com.easing.commons.android.view.Views;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

//进度框
//只能弹出一次，下次要重新创建
public class ProgressDialog extends DialogFragment {

    private CommonActivity ctx;
    private String title;

    private TextView progressText;
    private TextView detailText;
    private SeekBar seekBar;

    //创建
    public static ProgressDialog pop(CommonActivity ctx, Object title) {
        //创建
        ProgressDialog dialog = new ProgressDialog();
        dialog.ctx = ctx;
        dialog.title = title.toString();
        dialog.setCancelable(false);
        //弹出
        FragmentTransaction transaction = ctx.getSupportFragmentManager().beginTransaction();
        transaction.add(dialog, "ProgressDialog");
        transaction.commit();
        return dialog;
    }

    //销毁
    public void dispose() {
        Threads.post(() -> {
            //Dialog创建需要时间
            //如果提前调用了dispose，Dialog创建会报错
            while (getDialog() == null)
                Threads.sleep(100);
            ctx.postLater(super::dismissAllowingStateLoss, 500);
            ctx = null;
        });
    }

    //立刻销毁
    public void disposeImmediately() {
        ctx.post(super::dismissAllowingStateLoss);
        ctx = null;
    }

    //更新进度和描述
    public void onProgressUpdate(float percent, String detail) {
        ctx.post(() -> {
            Views.text(progressText, (int) (percent * 100) + "%");
            Views.text(detailText, detail);
            seekBar.setProgress((int) (percent * 100));
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View root = getActivity().getLayoutInflater().inflate(R.layout.layout_progress_dialog, null);
        builder.setView(root);
        AlertDialog dialog = builder.create();
        //解析控件
        TextView titleText = root.findViewById(R.id.text_title);
        titleText.setText(title);
        progressText = root.findViewById(R.id.text_progress);
        detailText = root.findViewById(R.id.text_detail);
        seekBar = root.findViewById(R.id.seek_bar);
        return dialog;
    }

}

