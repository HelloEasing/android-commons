package com.easing.commons.android.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.app.CommonActivity;
import com.easing.commons.android.format.Texts;
import com.easing.commons.android.helper.callback.Action;
import com.easing.commons.android.thread.Threads;
import com.easing.commons.android.ui.control.button.ImageButtonM2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//消息框
public class MessageDialog extends DialogFragment {

    Lock lock = new ReentrantLock();

    DialogState state = DialogState.CREATED;

    CommonActivity ctx;

    String bufferMessage = "";
    String message = "";

    Integer openTimes = 0;

    Action onClose;

    boolean showIcon = true;

    //静态创建方法
    public static MessageDialog create(CommonActivity ctx) {
        MessageDialog dialog = new MessageDialog();
        dialog.ctx = ctx;
        dialog.setCancelable(false);
        return dialog;
    }

    //设置消息
    public MessageDialog message(Object message) {
        bufferMessage = String.valueOf(message);
        return this;
    }

    //关闭时的消息监听器
    public MessageDialog onClose(Action onClose) {
        this.onClose = onClose;
        return this;
    }

    //显示
    public MessageDialog show() {
        synchronized (openTimes) {
            openTimes++;
        }
        final String tempMessage = bufferMessage;
        Threads.post(() -> {
            lock.lock();
            while (state != DialogState.CREATED && state != DialogState.CLOSED)
                Threads.sleep(100);
            ctx.post(() -> {
                showIcon = true;
                message = tempMessage;
                FragmentManager manager = ctx.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(this, Texts.random(false, false));
                transaction.commitAllowingStateLoss();
            });
            state = DialogState.OPENING;
            lock.unlock();
        });
        return this;
    }

    //显示
    public MessageDialog showWithoutIcon() {
        synchronized (openTimes) {
            openTimes++;
        }
        final String tempMessage = bufferMessage;
        Threads.post(() -> {
            lock.lock();
            while (state != DialogState.CREATED && state != DialogState.CLOSED)
                Threads.sleep(100);
            ctx.post(() -> {
                showIcon = false;
                message = tempMessage;
                FragmentManager manager = ctx.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(this, Texts.random(false, false));
                transaction.commitAllowingStateLoss();
            });
            state = DialogState.OPENING;
            lock.unlock();
        });
        return this;
    }

    //关闭
    public MessageDialog close() {
        synchronized (openTimes) {
            if (openTimes < 1) return this;
            openTimes--;
        }
        Threads.post(() -> {
            lock.lock();
            while (state != DialogState.OPEN)
                Threads.sleep(100);
            ctx.post(() -> {
                dismissAllowingStateLoss();
                state = DialogState.CLOSING;
            });
            lock.unlock();
        });
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View root = getActivity().getLayoutInflater().inflate(R.layout.layout_message_dialog, null);
        builder.setView(root);
        AlertDialog dialog = builder.create();
        //解析控件
        TextView messageText = root.findViewById(R.id.text_msg);
        ImageButtonM2 okButton = root.findViewById(R.id.bt_ok);
        messageText.setText(message);
        okButton.showIcon(showIcon);
        okButton.setOnClickListener(v -> {
            dismissAllowingStateLoss();
            state = DialogState.CLOSING;
        });
        //更新状态
        state = DialogState.OPEN;
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        state = DialogState.CLOSED;
        //窗口关闭时的事件回调
        if (onClose != null) onClose.act();
    }
}
