package com.dya.noor.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.database.PrayerInfo;
import com.dya.noor.utlis.Utils;
import com.dya.noor.database.MydbClass;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class NowIsPrayerTimeActivity extends BaseActivity {

    AlarmManager alarmManager;
    SharedPreferences preferences ;
    private MediaPlayer mediaPlayer;
    int AnimationDuration = 2000;

    public Vibrator vibrator;
    TextView closeIv ;
    String bangs ="";
    TextView tvPrayer ;



    private FrameLayout prayerTimeRoot;

    private ImageView ivPrayerIcon;

    private TextView tvPrayerName;
    private TextView tvPrayerTime;
    private TextView tvNextPrayer;


    @SuppressLint({"MissingInflatedId", "SetTextI18n", "SimpleDateFormat", "WakelockTimeout"})
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_is_prayer_time);

        prayerTimeRoot = findViewById(R.id.prayerTimeRoot);

        tvPrayer = findViewById(R.id.tvPrayer);

        ivPrayerIcon = findViewById(R.id.ivPrayerIcon);

        tvPrayerName = findViewById(R.id.tvPrayerName);

        tvPrayerTime = findViewById(R.id.tvPrayerTime);

        tvNextPrayer = findViewById(R.id.tvNextPrayer);

        closeIv = findViewById(R.id.closeIv);
        preferences = getSharedPreferences("key", MODE_PRIVATE);
        bangs = preferences.getString("bang", "1");
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        // Acquire the wake lock.
        wakeLock.acquire();

        // Do your work here.


        // Release the wake lock.
        wakeLock.release();

        // bo pishandan le katy qfLda

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(/**WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|*/
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );




        new Utils(this);
        new MydbClass(this);


        setupPrayerUI();

        closeIv.setOnClickListener(v -> {
            vibrator.cancel();
            finish();

        });

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();

        //MediaPlayer lera dabne
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = {500, 700, 500, 700, 700};

        if (ringerMode == AudioManager.RINGER_MODE_VIBRATE) {
            // Do not play media
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            vibrator.vibrate(pattern, 0);
        } else if (ringerMode == AudioManager.RINGER_MODE_SILENT ) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            vibrator.vibrate(0);
        }
        else{
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            // Calculate the desired volume level (50% of the maximum volume)
            int desiredVolume = maxVolume / 2;
           // audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

            // Get the current volume level for the music stream
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            // Check if the current volume is already at 50% or higher
            if (currentVolume < desiredVolume) {
                // Set the volume to the desired level
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, desiredVolume, 0);
            }
            vibrator.vibrate(pattern, 0);
        }
        if (bangs.equals("1")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.bang2);
            mediaPlayer.start();

        }
        else if (bangs.equals("2")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.bang_dan);
            mediaPlayer.start();
        }
        else if (bangs.equals("3")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.hijaz_azan);
            mediaPlayer.start();
        }
        else if (bangs.equals("4")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.rad_muhammad_alkurdi);
            mediaPlayer.start();
        }
        else if (bangs.equals("5")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.peshawa_qadir);
            mediaPlayer.start();
        }
        else if (bangs.equals("6")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.abdul_basit_abdul_samad);
            mediaPlayer.start();
        }
        else if (bangs.equals("7")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.islam_sobhi);
            mediaPlayer.start();
        }

        mediaPlayer.setOnCompletionListener(mp -> {
            mediaPlayer.seekTo(0);
            vibrator.cancel();
            finish();
        });

    }


    private void setupPrayerUI() {

        PrayerInfo currentPrayer = MydbClass.getCurrentClosestPrayer();

        PrayerInfo nextPrayer = MydbClass.getNextPrayer();

        if (currentPrayer == null) {
            return;
        }

        String prayerName =
                currentPrayer.prayerNameKurdish;

        tvPrayerName.setText(prayerName);


        // Prayer time
        String currentTime =
                new SimpleDateFormat("HH:mm")
                        .format(new Date(currentPrayer.milli));

        tvPrayerTime.setText(currentTime);


        // Next prayer
        if (nextPrayer != null) {

            String nextTime =
                    new SimpleDateFormat("HH:mm")
                            .format(new Date(nextPrayer.milli));

            tvNextPrayer.setText(
                    "بانگی دواتر لە "
                            + nextTime
                            + " "
                            + nextPrayer.prayerNameKurdish
                            + " دەبێت"
            );
        }


        // Set icon + background
        setPrayerAppearance(currentPrayer);
    }
    private void setPrayerAppearance(PrayerInfo prayer) {

        if (prayer == null) {
            return;
        }

        ArrayList<Long> prayerTimes =
                MydbClass.getTodayPrayers(true);

        if (prayerTimes == null || prayerTimes.size() < 6) {
            return;
        }


        // 0 = Fajr
        // 1 = Sunrise
        // 2 = Dhuhr
        // 3 = Asr
        // 4 = Maghrib
        // 5 = Isha


        if (prayer.milli == prayerTimes.get(0)) {

            // Fajr
            ivPrayerIcon.setImageResource(
                    R.drawable.sunset1
            );

            prayerTimeRoot.setBackgroundResource(
                    R.drawable.bg_prayer_fajr
            );

        }

        else if (prayer.milli == prayerTimes.get(2)) {

            // Dhuhr
            ivPrayerIcon.setImageResource(
                    R.drawable.sun
            );

            prayerTimeRoot.setBackgroundResource(
                    R.drawable.bg_prayer_dhuhr
            );

        }

        else if (prayer.milli == prayerTimes.get(3)) {

            // Asr
            ivPrayerIcon.setImageResource(
                    R.drawable.cloudy_day
            );

            prayerTimeRoot.setBackgroundResource(
                    R.drawable.bg_prayer_asr
            );

        }

        else if (prayer.milli == prayerTimes.get(4)) {

            // Maghrib
            ivPrayerIcon.setImageResource(
                    R.drawable.sunset
            );

            prayerTimeRoot.setBackgroundResource(
                    R.drawable.bg_prayer_maghrib
            );

        }

        else if (prayer.milli == prayerTimes.get(5)) {

            // Isha
            ivPrayerIcon.setImageResource(
                    R.drawable.night_moon
            );

            prayerTimeRoot.setBackgroundResource(
                    R.drawable.bg_prayer_isha
            );
        }
    }


    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        return super.getOnBackInvokedDispatcher();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            vibrator.cancel();
        }
    }

    // lekaty sawtdan yan kzkrdn mediaPlayer awastet
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {



                    finish();


                }
                return true;
            default:

        return super.dispatchKeyEvent(event);
    }
    }
}