package com.easing.commons.android.ui.control.record;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.easing.commons.android.R;
import com.easing.commons.android.time.Times;
import com.easing.commons.android.helper.thread.AliveState;
import com.easing.commons.android.io.Files;
import com.easing.commons.android.manager.GlobalHandler;
import com.easing.commons.android.media.MediaManager;
import com.easing.commons.android.thread.Threads;
import lombok.SneakyThrows;

public class AudioPlayer extends DialogFragment {

    private AppCompatActivity ctx;

    private TextView sourceText;
    private SeekBar progressBar;
    private TextView finishDurationText;
    private TextView totalDurationText;
    private ImageView playButton;

    private String source;
    private int duration;

    private MediaPlayer mediaPlayer;
    private boolean playing = false;

    private AliveState state;

    //创建一个音频播放器
    public static AudioPlayer create(String source) {
        AudioPlayer player = new AudioPlayer();
        player.source = source;
        player.duration = MediaManager.getMediaDuration(source);
        return player;
    }

    //显示
    public void show(AppCompatActivity ctx) {
        this.ctx = ctx;
        FragmentManager manager = ctx.getSupportFragmentManager();
        show(manager, "AudioPlayer");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View root = getActivity().getLayoutInflater().inflate(R.layout.layout_audio_player, null);
        builder.setView(root);
        AlertDialog dialog = builder.create();

        //解析控件
        sourceText = root.findViewById(R.id.text_source);
        progressBar = root.findViewById(R.id.progress);
        finishDurationText = root.findViewById(R.id.text_finish_duration);
        totalDurationText = root.findViewById(R.id.text_total_duration);
        playButton = root.findViewById(R.id.bt_play);

        //设置控件内容
        sourceText.setText(Files.getFileName(source));
        totalDurationText.setText(Times.msToMSTime(duration));

        //监听器：播放，暂停
        playButton.setOnClickListener((v) -> {
            playing = !playing;
            if (playing) {
                if (mediaPlayer == null)
                    play();
                else
                    resume();
            } else
                pause();
        });

        //进度条监听器
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser || mediaPlayer == null)
                    return;
                mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        return dialog;
    }

    //监听器：弹窗取消
    @Override
    public void onDismiss(DialogInterface dialog) {
        stop();
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroy() {
        stop();
        super.onDestroy();
    }

    //首次播放
    @SneakyThrows
    public void play() {
        playing = true;
        playButton.setImageResource(R.drawable.round_icon_m01_pause);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(source);
        mediaPlayer.prepare();
        progressBar.setMax(duration);

        //监听器：加载完毕
        mediaPlayer.setOnPreparedListener((mp) -> mediaPlayer.start());
        //监听器：播放完毕
        mediaPlayer.setOnCompletionListener((mp) -> {
            playing = false;
            playButton.setImageResource(R.drawable.round_icon_m01_play);
            mediaPlayer.seekTo(0);
        });
        //定时器：开启线程，监听播放进度
        state = AliveState.create();
        Threads.postByInterval(() -> {
            GlobalHandler.post(() -> {
                synchronized (mediaPlayer) {
                    int finish = mediaPlayer.getCurrentPosition();
                    progressBar.setProgress(finish);
                    finishDurationText.setText(Times.msToMSTime(finish));
                }
            });
        }, 500, state);
    }

    //恢复播放
    public void resume() {
        playing = true;
        playButton.setImageResource(R.drawable.round_icon_m01_pause);
        mediaPlayer.start();
    }

    //暂停
    public void pause() {
        playing = false;
        playButton.setImageResource(R.drawable.round_icon_m01_play);
        mediaPlayer.pause();
    }

    //停止
    public void stop() {
        if (mediaPlayer == null)
            return;

        playing = false;
        progressBar.setProgress(0);
        finishDurationText.setText("00:00");
        state.kill();

        playButton.setImageResource(R.drawable.round_icon_m01_play);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
