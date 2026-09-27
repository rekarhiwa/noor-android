package com.dya.noor.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;


public class ActivityBangdan extends BaseActivity {


    ImageButton back ;
    ImageView btnBangPlay;
    MediaPlayer mediaPlayer;
    int TextSize;



    SeekBar seekBar;
    TextView txtCurrent, txtTotal;
    Handler handler = new Handler();
    Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bangdan);


        back =findViewById(R.id.back);
        btnBangPlay = findViewById(R.id.btnBangPlay);
        seekBar = findViewById(R.id.seekBar);
        txtCurrent = findViewById(R.id.txtCurrent);
        txtTotal = findViewById(R.id.txtTotal);




        mediaPlayer = MediaPlayer.create(this,R.raw.bang_dan);

// total duration
        int totalDuration = mediaPlayer.getDuration();
        seekBar.setMax(totalDuration);
        txtTotal.setText(formatTime(totalDuration));

        back.setOnClickListener(v -> onBackPressed());

        btnBangPlay.setOnClickListener(v -> {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                btnBangPlay.setImageResource(R.drawable.ic_pause);
                updateSeekBar();
            } else {
                mediaPlayer.pause();
                btnBangPlay.setImageResource(R.drawable.ic_play);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                txtCurrent.setText(formatTime(progress));
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            btnBangPlay.setImageResource(R.drawable.ic_play);
            mediaPlayer.seekTo(0);
            seekBar.setProgress(0);
            txtCurrent.setText("00:00");
            handler.removeCallbacks(runnable);
        });

    }


    private void updateSeekBar() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    txtCurrent.setText(formatTime(currentPosition));
                    handler.postDelayed(this, 500);
                }
            }
        };
        handler.post(runnable);
    }


    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void onBackPressed() {

        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        super.onBackPressed();
        finish();
    }
}