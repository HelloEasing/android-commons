package com.easing.commons.android.ui.control.record;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import lombok.SneakyThrows;

public class AudioRecordService extends Service {

    private String savePath;

    private MediaRecorder recorder = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        savePath = intent.getStringExtra("path");
        startRecord();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopRecord();
        super.onDestroy();
    }

    //开始录音
    @SneakyThrows
    public void startRecord() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(savePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioChannels(1);
        recorder.setAudioSamplingRate(44100);
        recorder.setAudioEncodingBitRate(192000);
        recorder.prepare();
        recorder.start();
    }

    //停止录音
    public void stopRecord() {
        recorder.stop();
        recorder.release();
    }
}
