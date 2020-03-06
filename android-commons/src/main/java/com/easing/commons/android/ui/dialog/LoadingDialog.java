package com.easing.commons.android.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.app.CommonActivity;
import com.easing.commons.android.code.Console;
import com.easing.commons.android.format.Texts;
import com.easing.commons.android.thread.Threads;

import java.util.concurrent.locks.ReentrantLock;

//加载框
public class LoadingDialog extends DialogFragment {

    final Object stateLock = new Object();
    final ReentrantLock showLock = new ReentrantLock();

    DialogState state = DialogState.CREATED;

    CommonActivity ctx;

    String bufferMessage = "";
    String message = "";

    Integer openTimes = 0;

    //创建
    public static LoadingDialog create(CommonActivity ctx) {
        //创建
        LoadingDialog dialog = new LoadingDialog();
        dialog.ctx = ctx;
        dialog.setCancelable(false);
        return dialog;
    }

    //设置消息
    public LoadingDialog message(Object message) {
        bufferMessage = String.valueOf(message);
        return this;
    }

    //显示
    public LoadingDialog show() {
        final String tempMessage = bufferMessage;
        synchronized (openTimes) {
            openTimes++;
        }
        Threads.post(() -> {
            while (state != DialogState.CREATED && state != DialogState.CLOSED || showLock.isLocked()) Threads.sleep(10);
            showLock.lock();
            Console.info("show", "lock");
            synchronized (stateLock) {
                Console.info("show", Threads.currentThreadId(), "Lock");
                ctx.post(() -> {
                    synchronized (stateLock) {
                        message = tempMessage;
                        FragmentManager manager = ctx.getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.add(this, Texts.random(false, false));
                        transaction.commitAllowingStateLoss();
                    }
                });
                Console.info("DialogState", "OPENING");
                state = DialogState.OPENING;
                showLock.unlock();
                Console.info("show", "unlock");
                Console.info("show", Threads.currentThreadId(), "Unlock");
            }
        });
        return this;
    }

    //关闭
    public LoadingDialog close() {
        synchronized (openTimes) {
            if (openTimes < 1) return this;
            openTimes--;
        }
        Threads.post(() -> {
            while (state != DialogState.OPEN || showLock.isLocked()) Threads.sleep(10);
            showLock.lock();
            Console.info("show", "lock");
            synchronized (stateLock) {
                Console.info("close", Threads.currentThreadId(), "Lock");
                ctx.postLater(() -> {
                    synchronized (stateLock) {
                        dismissAllowingStateLoss();
                    }
                }, 300);
                Console.info("DialogState", "CLOSING");
                state = DialogState.CLOSING;
                showLock.unlock();
                Console.info("show", "unlock");
                Console.info("close", Threads.currentThreadId(), "Unlock");
            }
        });
        return this;
    }

    //销毁
    public LoadingDialog closeImmediately() {
        synchronized (openTimes) {
            if (openTimes < 1) return this;
            openTimes--;
        }
        Threads.post(() -> {
            while (state != DialogState.OPEN || showLock.isLocked()) Threads.sleep(10);
            showLock.lock();
            synchronized (stateLock) {
                Console.info("closeImmediately", Threads.currentThreadId(), "Unlock");
                ctx.post(() -> {
                    synchronized (stateLock) {
                        dismissAllowingStateLoss();
                    }
                });
                Console.info("DialogState", "CLOSING");
                state = DialogState.CLOSING;
                showLock.unlock();
                Console.info("closeImmediately", Threads.currentThreadId(), "Unlock");
            }
        });
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View root = getActivity().getLayoutInflater().inflate(R.layout.layout_loading_dialog, null);
        builder.setView(root);
        AlertDialog dialog = builder.create();
        //设置图片动画
        ImageView iv = root.findViewById(R.id.iv);
        AnimationDrawable animation = (AnimationDrawable) getResources().getDrawable(R.drawable.progress_m01);
        iv.setImageDrawable(animation);
        animation.start();
        //设置提示信息
        TextView messageText = root.findViewById(R.id.text_msg);
        messageText.setText(message);
        //更新状态
        synchronized (stateLock) {
            Console.info("DialogState", "OPEN");
            state = DialogState.OPEN;
        }
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        synchronized (stateLock) {
            Console.info("DialogState", "CLOSED");
            state = DialogState.CLOSED;
        }
    }
}
