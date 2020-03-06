package com.easing.commons.android.ui.control.record;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;

import com.easing.commons.android.R;
import com.easing.commons.android.time.Times;
import com.easing.commons.android.io.Files;
import com.easing.commons.android.manager.GlobalHandler;
import com.easing.commons.android.ui.dialog.TipBox;

import lombok.Setter;

//这个类使用了AudioRecordService，必须在清单中注册服务，否则录音文件为空
//可以通过设置OnStopListener，来控制录音结束时如何处理
//可以通过设置autoCancelOnFinish，来控制录音结束后，录音对话框是否自动隐藏
public class AudioRecorder extends DialogFragment {

    private AppCompatActivity ctx;

    private String saveFolder;
    private String savePath;

    private String simpleFolder;
    private String simplePath;

    private Chronometer timer;
    private ImageView recordButton;

    private boolean recording = false;

    @Setter
    private OnStopListener onFinishListener = new OnStopListener() {
        @Override
        public void onFinish(String saveFolder, String savePath, String simpleFolder, String simplePath) {
            TipBox.tip("录音结束\n文件保存至：" + simpleFolder);
        }
    };

    @Setter
    private boolean autoCancelOnFinish = true;


    //静态创建方法
    public static AudioRecorder create(String saveFolder) {
        AudioRecorder fragment = new AudioRecorder();
        fragment.saveFolder = Files.getAndroidExternalFolder(saveFolder);
        fragment.simpleFolder = "存储卡/" + saveFolder;
        return fragment;
    }

    //显示
    public void show(AppCompatActivity ctx) {
        this.ctx = ctx;
        FragmentManager manager = ctx.getSupportFragmentManager();
        show(manager, "AudioRecorder");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View root = getActivity().getLayoutInflater().inflate(R.layout.layout_audio_recorder, null);
        builder.setCancelable(false);
        builder.setView(root);
        AlertDialog dialog = builder.create();

        //解析控件
        timer = root.findViewById(R.id.timer);
        recordButton = root.findViewById(R.id.bt_audio_capture);

        //监听器：开始录音，结束录音
        recordButton.setOnClickListener((v) -> {
            recording = !recording;
            if (recording)
                startRecord();
            else
                stopRecord();
        });

        return dialog;
    }

    //监听器：取消录制，删除已保存文件
    @Override
    public void onCancel(DialogInterface dialog) {
        cancelRecord();
        super.onCancel(dialog);
    }

    //开始录音
    public void startRecord() {
        recording = true;
        recordButton.setImageResource(R.drawable.round_icon_m01_stop);
        TipBox.tip("录音开始");
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        Intent intent = new Intent(ctx, AudioRecordService.class);
        String filename = Times.now(Times.FORMAT_03) + ".aac";
        savePath = saveFolder + "/" + filename;
        simplePath = simpleFolder + "/" + filename;
        Files.createFile(savePath);
        intent.putExtra("path", savePath);
        ctx.startService(intent);
        //防止快速点击，MediaRecorder无法及时响应
        recordButton.setEnabled(false);
        GlobalHandler.postLater(() -> recordButton.setEnabled(true), 500);
    }

    //结束录音
    public void stopRecord() {
        recording = false;
        recordButton.setImageResource(R.drawable.round_icon_m01_speaker);
        timer.stop();
        timer.setText("00:00");
        Intent intent = new Intent(ctx, AudioRecordService.class);
        ctx.stopService(intent);
        if (onFinishListener != null)
            onFinishListener.onFinish(saveFolder, savePath, simpleFolder, simplePath);
        if (autoCancelOnFinish)
            dismiss();
        //防止快速点击，MediaRecorder无法及时响应
        recordButton.setEnabled(false);
        GlobalHandler.postLater(() -> recordButton.setEnabled(true), 500);
    }

    //取消录音
    private void cancelRecord() {
        if (recording) {
            recording = false;
            recordButton.setImageResource(R.drawable.round_icon_m01_speaker);
            timer.stop();
            timer.setText("00:00");
            Intent intent = new Intent(ctx, AudioRecordService.class);
            ctx.stopService(intent);
            TipBox.tip("录音取消");
            GlobalHandler.postLater(() -> Files.deleteFile(savePath), 500);
        }
    }

    //停止监听器
    public interface OnStopListener {
        void onFinish(String saveFolder, String savePath, String simpleFolder, String simplePath);
    }
}
