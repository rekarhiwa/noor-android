package com.dya.noor.activities;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.QariNameListAdapterMP3;
import com.dya.noor.adapters.QuranSuraListAdapterMP3;
import com.dya.noor.customImageView.WaveSeekBar;
import com.dya.noor.database.MydbClass;
import com.dya.noor.module.AmbienceSound;
import com.dya.noor.module.QariNameListMP3Item;
import com.dya.noor.module.QuranSuraLisMP3Item;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class QuranMP3Activity extends BaseActivity {

    TextView textViewQariName, textViewSuraName, textViewDuration;
    MediaPlayer mediaPlayer;

    ImageView btnPlay, btnRewind, btnForward, btnDownload;

    String id, SuaraName, fileUrl;

    RecyclerView QuranRecyclerView;

    QariNameListAdapterMP3 qariNameAdapter;
    ArrayList<QariNameListMP3Item> qariNameItems;
    String Mp3Url, SuraNameForUrl = "", SuraName, FolderName, strQariName;
    @SuppressLint("StaticFieldLeak")
    public static BottomSheetDialog dialog;

    @SuppressLint("StaticFieldLeak")
    public static EditText editSearch;

    QuranSuraListAdapterMP3 suraAdapter;
    ArrayList<QuranSuraLisMP3Item> suraList;

    ConnectivityManager.NetworkCallback networkCallback;
    MydbClass myDbClass;

    SharedPreferences pref, prefSura;
    String qariUrl;
    String folderName;

    WaveSeekBar waveSeekBar;
    Handler seekBarHandler = new Handler();
    Runnable updateSeekBarRunnable;

    ImageView imgVinylDisc, imgVinylNeedle;
    ObjectAnimator discAnimator;
    ObjectAnimator needleAnimator;


    TextView textViewBackground;
    View ambienceOverlay;
    TextureView videoBackgroundView;
    Surface ambienceSurface;
    MediaPlayer ambienceVideoPlayer;
    MediaPlayer ambienceAudioPlayer;
    ScrollView defaultScrollView;

    TextView textViewSuraNameAmbience, textViewQariNameAmbience, textViewCurrentTimeAmbience, textViewRemainingTimeAmbience;
    SeekBar seekBarAmbience;
    ImageView btnPlayAmbience, btnRewindAmbience, btnForwardAmbience, btnBackAmbience;

    SharedPreferences prefAmbience;
    String currentAmbienceName = null;
    ArrayList<AmbienceSound> ambienceSounds;

    TextView textViewChangeBackground; // add with the other ambience fields

    private int lastVideoWidth = 0, lastVideoHeight = 0;

    private boolean ambienceAudioReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quran_mp3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pref = getSharedPreferences("QariName", MODE_PRIVATE);
        prefSura = getSharedPreferences("SuraName", MODE_PRIVATE);

        strQariName = pref.getString("nameQari", "أبو بکر الشاطري");
        folderName = pref.getString("folder", "shatri");
        qariUrl = pref.getString("url", "https://server11.mp3quran.net/shatri/");

        SuraName = prefSura.getString("name", "سُورَةُ الفَاتِحَةِ");
        SuraNameForUrl = prefSura.getString("link", "001.mp3");

        textViewQariName = findViewById(R.id.textViewQariName);
        textViewSuraName = findViewById(R.id.textViewSuraName);
        textViewDuration = findViewById(R.id.textViewDuration);
        btnDownload = findViewById(R.id.btnDownload);

        textViewDuration.setText("00:00 - 00:00");
        textViewQariName.setText(strQariName);

        myDbClass = new MydbClass(this);
        qariNameItems = new ArrayList<>();

        btnPlay = findViewById(R.id.btnPlay);
        btnRewind = findViewById(R.id.btnRewind);
        btnForward = findViewById(R.id.btnForward);

        imgVinylDisc = findViewById(R.id.imgVinylDisc);
        imgVinylNeedle = findViewById(R.id.imgVinylNeedle);


        prefAmbience = getSharedPreferences("Ambience", MODE_PRIVATE);

        textViewBackground = findViewById(R.id.textViewBackground);
        ambienceOverlay = findViewById(R.id.ambienceOverlay);
        videoBackgroundView = findViewById(R.id.videoBackgroundView);
        defaultScrollView = findViewById(R.id.scrollDefaultRoot);

        textViewSuraNameAmbience = findViewById(R.id.textViewSuraNameAmbience);
        textViewQariNameAmbience = findViewById(R.id.textViewQariNameAmbience);
        textViewCurrentTimeAmbience = findViewById(R.id.textViewCurrentTimeAmbience);
        textViewRemainingTimeAmbience = findViewById(R.id.textViewRemainingTimeAmbience);
        seekBarAmbience = findViewById(R.id.seekBarAmbience);
        btnPlayAmbience = findViewById(R.id.btnPlayAmbience);
        btnRewindAmbience = findViewById(R.id.btnRewindAmbience);
        btnForwardAmbience = findViewById(R.id.btnForwardAmbience);
        btnBackAmbience = findViewById(R.id.btnBackAmbience);

        // add in onCreate, with the other ambience findViewById calls
        textViewChangeBackground = findViewById(R.id.textViewChangeBackground);
        textViewChangeBackground.setOnClickListener(v -> showAmbienceSelectionDialog());

        setupAmbienceSounds();

        textViewBackground.setOnClickListener(v -> showAmbienceSelectionDialog());
        btnBackAmbience.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();

            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    setPlayButtonIcon(R.drawable.play_dng);
                    setPlaybackAnimation(false);
                    stopSeekBarUpdate();
                }
            }
        });
        btnPlayAmbience.setOnClickListener(v -> playSurah());
        btnRewindAmbience.setOnClickListener(v -> btnRewind.performClick());
        btnForwardAmbience.setOnClickListener(v -> btnForward.performClick());

        seekBarAmbience.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar sb, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    try { mediaPlayer.seekTo(progress); } catch (Exception ignored) {}
                }
            }
            @Override public void onStartTrackingTouch(android.widget.SeekBar sb) {}
            @Override public void onStopTrackingTouch(android.widget.SeekBar sb) {}
        });

        String savedAmbience = prefAmbience.getString("selected", null);
        if (savedAmbience != null) {
            AmbienceSound saved = findAmbienceByName(savedAmbience);
            if (saved != null) applyAmbience(saved);
        }




        // Disc Rotation Setup
        discAnimator = android.animation.ObjectAnimator.ofFloat(imgVinylDisc, "rotation", 0f, 360f);
        discAnimator.setDuration(15000);
        discAnimator.setRepeatCount(android.animation.ValueAnimator.INFINITE);
        discAnimator.setInterpolator(new android.view.animation.LinearInterpolator());

        // Needle Swing Setup
        needleAnimator = android.animation.ObjectAnimator.ofFloat(imgVinylNeedle, "rotation", 0f, 12f);
        needleAnimator.setDuration(800);
        needleAnimator.setInterpolator(new android.view.animation.DecelerateInterpolator());

        waveSeekBar = findViewById(R.id.waveSeekBar);

        waveSeekBar.setOnWaveSeekBarChangeListener(new WaveSeekBar.OnWaveSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(WaveSeekBar waveSeekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    try {
                        mediaPlayer.seekTo(progress);
                        textViewDuration.setText(formatTime(progress) + "-" + formatTime(mediaPlayer.getDuration()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        StoreSura();
        StoreQariName();


        textViewSuraNameAmbience.setOnClickListener(v -> showSuraSheet());
        textViewQariNameAmbience.setOnClickListener(v -> ShowDialog());

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();

            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    setPlayButtonIcon(R.drawable.play_dng);
                    setPlaybackAnimation(false);
                    stopSeekBarUpdate();
                }
            }

        });

        qariNameAdapter = new QariNameListAdapterMP3(qariNameItems, this,
                item -> {
                    textViewQariName.setText(item.getName());
                    if (textViewQariNameAmbience != null) textViewQariNameAmbience.setText(item.getName());
                    FolderName = item.getFolderName();
                    Mp3Url = item.getUrl();
                    resetPlayer();
                    updateBtnDownloadResource();

                    SharedPreferences.Editor editor = getSharedPreferences("QariName", MODE_PRIVATE).edit();
                    editor.putString("nameQari", item.getName());
                    editor.putString("folder", item.getFolderName());
                    editor.putString("url", item.getUrl());
                    editor.apply();
                    qariNameAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
        );

        QariNameuIUpdate();

        textViewQariName.setOnClickListener(v -> ShowDialog());
        textViewSuraName.setOnClickListener(v -> showSuraSheet());
        btnPlay.setOnClickListener(v -> playSurah());

        btnForward.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                try {
                    int currentPos = mediaPlayer.getCurrentPosition();
                    int totalDuration = mediaPlayer.getDuration();
                    int newPos = Math.min(currentPos + 10000, totalDuration);
                    mediaPlayer.seekTo(newPos);
                    waveSeekBar.setProgress(newPos);
                    textViewDuration.setText(formatTime(newPos) + " - " + formatTime(totalDuration));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnRewind.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                try {
                    int currentPos = mediaPlayer.getCurrentPosition();
                    int totalDuration = mediaPlayer.getDuration();
                    int newPos = Math.max(currentPos - 10000, 0);
                    mediaPlayer.seekTo(newPos);
                    waveSeekBar.setProgress(newPos);
                    textViewDuration.setText(formatTime(newPos) + " - " + formatTime(totalDuration));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnDownload.setOnClickListener(v -> {
            File audioDir = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), FolderName);
            if (!audioDir.exists()) audioDir.mkdirs();
            File mp3File = new File(audioDir, SuraNameForUrl);
            if (!mp3File.exists()) {
                downloadMp3AndPlay(mp3File);
            }
        });
    }

    // --- ANIMATION CONTROL HELPER METHOD ---
    private void setPlaybackAnimation(boolean isPlaying) {
        if (isPlaying) {
            if (!discAnimator.isStarted()) {
                discAnimator.start();
            } else if (discAnimator.isPaused()) {
                discAnimator.resume();
            }
            if (needleAnimator != null) {
                needleAnimator.start();
            }
        } else {
            if (discAnimator != null && discAnimator.isRunning()) {
                discAnimator.pause();
            }
            if (needleAnimator != null) {
                needleAnimator.reverse(); // Smoothly returns rotation back to 0 degrees
            }
        }
    }




    private void playSurah() {
        boolean isOnline = isNetworkAvailable();
        File audioDir = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), FolderName);
        if (!audioDir.exists()) audioDir.mkdirs();
        File mp3File = new File(audioDir, SuraNameForUrl);

        if (!isOnline) {
            if (mp3File.exists()) {
                playMp3File(mp3File);
            } else {
                Toast.makeText(this, "No internet and file not downloaded", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mp3File.exists()) {
                playMp3File(mp3File);
            } else {
                playStream(Mp3Url + SuraNameForUrl);
            }
        }
    }

    void StoreQariName() {
        ArrayList<QariNameListMP3Item> normal = new ArrayList<>();
        Cursor cursor = myDbClass.readQariName();
        while (cursor.moveToNext()) {
            QariNameListMP3Item item = new QariNameListMP3Item(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("db_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("db_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("url"))
            );
            normal.add(item);
        }
        qariNameItems.clear();
        qariNameItems.addAll(normal);
    }

    void StoreSura() {
        suraList = new ArrayList<>();
        Cursor cursor = myDbClass.readAllData();
        while (cursor.moveToNext()) {
            QuranSuraLisMP3Item item = new QuranSuraLisMP3Item(
                    cursor.getInt(cursor.getColumnIndexOrThrow("soraid")),
                    cursor.getString(cursor.getColumnIndexOrThrow("sura_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("link"))
            );
            suraList.add(item);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    void ShowDialog() {
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.qari_botomsheet_mps);

        RecyclerView recyclerAll = dialog.findViewById(R.id.qariNameRecyclerViewMp3);
        editSearch = dialog.findViewById(R.id.editTextSearchQariNameMp3);

        assert recyclerAll != null;
        recyclerAll.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerAll.setNestedScrollingEnabled(true);
        recyclerAll.setAdapter(qariNameAdapter);

        editSearch.setOnTouchListener((v2, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editSearch.getRight() - editSearch.getCompoundDrawables()[2].getBounds().width())) {
                    editSearch.setText("");
                    qariNameAdapter.filter("");
                    return true;
                }
            }
            return false;
        });

        editSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                qariNameAdapter.filter(s.toString());
            }
        });

        editSearch.setText("");
        qariNameAdapter.filter("");
        dialog.show();

        View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
            recyclerAll.setNestedScrollingEnabled(true);
        }
    }

    public void QariNameuIUpdate(){
        strQariName = getSharedPreferences("QariName",MODE_PRIVATE).getString("nameQari","أبو بکر الشاطري");
        FolderName = getSharedPreferences("QariName",MODE_PRIVATE).getString("folder","shatri");
        Mp3Url = getSharedPreferences("QariName",MODE_PRIVATE).getString("url","https://server11.mp3quran.net/shatri/");

        SuraName = getSharedPreferences("SuraName",MODE_PRIVATE).getString("name","سُورَةُ الفَاتِحَةِ");
        SuraNameForUrl = getSharedPreferences("SuraName",MODE_PRIVATE).getString("link","001.mp3");

        textViewQariName.setText(strQariName);
        textViewSuraName.setText(SuraName);
    }

    void showSuraSheet() {
        BottomSheetDialog suraDialog = new BottomSheetDialog(this);
        suraDialog.setContentView(R.layout.sura_name_sheet);
        RecyclerView recycler = suraDialog.findViewById(R.id.suraRecyclerViewMp3);

        suraAdapter = new QuranSuraListAdapterMP3(this, suraList, item -> {
            resetPlayer();
            textViewSuraName.setText(item.getName());
            if (textViewSuraNameAmbience != null) textViewSuraNameAmbience.setText(item.getName());
            SuraNameForUrl = item.getLink();

            SharedPreferences.Editor editor = getSharedPreferences("SuraName", MODE_PRIVATE).edit();
            editor.putString("name", item.getName());
            editor.putString("link", item.getLink());
            editor.apply();
            updateBtnDownloadResource();
            suraDialog.dismiss();
        });

        assert recycler != null;
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(suraAdapter);
        suraDialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    void updateBtnDownloadResource(){
        File audioDir = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), FolderName);
        if (!audioDir.exists()) audioDir.mkdirs();
        File mp3File = new File(audioDir, SuraNameForUrl);
        if (!mp3File.exists()) {
            btnDownload.setImageResource(R.drawable.ic_download);
        } else {
            btnDownload.setImageResource(R.drawable.ic_download_done);
        }
    }

    @SuppressLint({"ObsoleteSdkInt", "SetTextI18n"})
    private void downloadMp3AndPlay(File mp3File) {
        View progressView = getLayoutInflater().inflate(R.layout.dialog_download, null);
        ProgressBar progressBar = progressView.findViewById(R.id.downloadProgressBar);
        TextView txtPercentage = progressView.findViewById(R.id.txtPercentage);

        AlertDialog progressDialog = new AlertDialog.Builder(this).setView(progressView).create();
        if (progressDialog.getWindow() != null)
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        progressDialog.setCancelable(false);
        progressDialog.show();

        String channelId = "download_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Downloads", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("دابەزاندنی " + textViewSuraName.getText())
                .setContentText("خەریکی دابەزاندنە...")
                .setSmallIcon(R.drawable.ic_download)
                .setProgress(100, 0, false);

        int notificationId = 101;
        notificationManager.notify(notificationId, notificationBuilder.build());

        new Thread(() -> {
            BufferedInputStream input = null;
            BufferedOutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(Mp3Url + mp3File.getName());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Accept-Encoding", "identity");
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);
                connection.connect();

                int fileLength = connection.getContentLength();
                input = new BufferedInputStream(connection.getInputStream(), 256 * 1024);
                output = new BufferedOutputStream(new FileOutputStream(mp3File), 256 * 1024);

                byte[] buffer = new byte[256 * 1024];
                long total = 0;
                int count;
                long lastUpdateTime = 0;

                while ((count = input.read(buffer)) != -1) {
                    total += count;
                    output.write(buffer, 0, count);

                    long now = System.currentTimeMillis();
                    if (now - lastUpdateTime > 800 && fileLength > 0) {
                        int progress = (int) (total * 100 / fileLength);
                        runOnUiThread(() -> {
                            progressBar.setProgress(progress);
                            txtPercentage.setText(progress + "%");
                        });
                        notificationBuilder.setProgress(100, progress, false);
                        notificationManager.notify(notificationId, notificationBuilder.build());
                        lastUpdateTime = now;
                    }
                }
                output.flush();

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    notificationManager.cancel(notificationId);
                    Toast.makeText(this, "فایلەکە بەسەرکەوتی دابەزی", Toast.LENGTH_LONG).show();
                    btnDownload.setImageResource(R.drawable.ic_download_done);
                    playMp3File(mp3File);
                });
            } catch (Exception e) {
                if (mp3File.exists()) mp3File.delete();
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    notificationManager.cancel(notificationId);
                    Toast.makeText(this, "هەڵەیەک ڕوویدا", Toast.LENGTH_SHORT).show();
                });
            } finally {
                try {
                    if (input != null) input.close();
                    if (output != null) output.close();
                    if (connection != null) connection.disconnect();
                } catch (Exception ignored) {}
            }
        }).start();
    }

    private void startSeekBarUpdate() {
        stopSeekBarUpdate();
        updateSeekBarRunnable = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    try {
                        if (mediaPlayer.isPlaying()) {
                            int currentPos = mediaPlayer.getCurrentPosition();
                            int totalDuration = mediaPlayer.getDuration();
                            if (totalDuration > 0) {
                                waveSeekBar.setMax(totalDuration);
                                waveSeekBar.setProgress(currentPos);
                                textViewDuration.setText(formatTime(currentPos) + " - " + formatTime(totalDuration));
                            }
                            if (ambienceOverlay.getVisibility() == View.VISIBLE) {
                                seekBarAmbience.setMax(totalDuration);
                                seekBarAmbience.setProgress(currentPos);
                                textViewCurrentTimeAmbience.setText(formatTime(currentPos));
                                textViewRemainingTimeAmbience.setText("-" + formatTime(totalDuration - currentPos));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                seekBarHandler.postDelayed(this, 150);
            }
        };
        seekBarHandler.postDelayed(updateSeekBarRunnable, 0);
    }

    private void stopSeekBarUpdate() {
        if (seekBarHandler != null && updateSeekBarRunnable != null) {
            seekBarHandler.removeCallbacks(updateSeekBarRunnable);
        }
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = ((milliseconds / (1000 * 60)) % 60);
        int hours = ((milliseconds / (1000 * 60 * 60)) % 24);
        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        QariNameuIUpdate();
        updateBtnDownloadResource();
        pref = getSharedPreferences("QariName", MODE_PRIVATE);
        prefSura = getSharedPreferences("SuraName", MODE_PRIVATE);
        strQariName = pref.getString("nameQari", "أبو بکر الشاطري");
        folderName = pref.getString("folder", "shatri");
        qariUrl = pref.getString("url", "https://server11.mp3quran.net/shatri/");
        SuraName = prefSura.getString("name", "سُورَةُ الفَاتِحَةِ");
        SuraNameForUrl = prefSura.getString("link", "001.mp3");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetPlayer();
        if (ambienceVideoPlayer != null) { try { ambienceVideoPlayer.release(); } catch (Exception ignored) {} }
        if (ambienceAudioPlayer != null) { try { ambienceAudioPlayer.release(); } catch (Exception ignored) {} }
        if (ambienceSurface != null) ambienceSurface.release();
    }

    private void adjustWaveStyleByDuration(int durationMs) {
        if (waveSeekBar == null) return;
        long totalMinutes = (durationMs / 1000) / 60;
        float density = getResources().getDisplayMetrics().density;
        if (totalMinutes <= 3) {
            waveSeekBar.setBarWidth((int) (8 * density));
            waveSeekBar.setBarGap((int) (5 * density));
            waveSeekBar.setCornerRadius(4 * density);
        } else if (totalMinutes <= 15) {
            waveSeekBar.setBarWidth((int) (5 * density));
            waveSeekBar.setBarGap((int) (3 * density));
            waveSeekBar.setCornerRadius(2.5f * density);
        } else {
            waveSeekBar.setBarWidth((int) (3 * density));
        }
    }



    private void syncAmbienceAudioPlayback(boolean shouldPlay) {
        if (ambienceOverlay == null || ambienceOverlay.getVisibility() != View.VISIBLE) return;
        if (ambienceAudioPlayer == null || !ambienceAudioReady) return;
        try {
            if (shouldPlay) {
                if (!ambienceAudioPlayer.isPlaying()) ambienceAudioPlayer.start();
            } else {
                if (ambienceAudioPlayer.isPlaying()) ambienceAudioPlayer.pause();
            }
        } catch (Exception ignored) {}
    }


    private void setupAmbienceSounds() {
        ambienceSounds = new ArrayList<>();
        ambienceSounds.add(new AmbienceSound("bonfire", "Bonfire", "bonfire_video_full.mp4", "calm_bonfire.mp3"));
        ambienceSounds.add(new AmbienceSound("rain", "Rain", "rain_video_full.mp4", "calm_rain.mp3"));
        ambienceSounds.add(new AmbienceSound("wind", "Pouring Wind", "wind_video_full.mp4", "light_wind.mp3"));
        ambienceSounds.add(new AmbienceSound("morning_birds", "Morning Birds", "morning_birds_full.mp4", "morning_birds.mp3"));
        ambienceSounds.add(new AmbienceSound("waves", "Wave Effect", "wave_video_full.mp4", "wave_effect.mp3"));
    }

    private AmbienceSound findAmbienceByName(String name) {
        if (ambienceSounds == null) return null;
        for (AmbienceSound a : ambienceSounds) if (a.getName().equals(name)) return a;
        return null;
    }

    private void showAmbienceSelectionDialog() {

        View sheetView = getLayoutInflater()
                .inflate(R.layout.dialog_ambience_select, null);

        BottomSheetDialog sheet = new BottomSheetDialog(this);

        sheet.setContentView(sheetView);

        sheet.setOnShowListener(dialog -> {

            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;

            FrameLayout bottomSheet =
                    bottomSheetDialog.findViewById(
                            com.google.android.material.R.id.design_bottom_sheet
                    );

            if (bottomSheet != null) {
                bottomSheet.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.parseColor("#12121c")
                        )
                );
            }
        });

        sheetView.findViewById(R.id.optionBonfire).setOnClickListener(v -> {
            applyAmbience(findAmbienceByName("bonfire"));
            sheet.dismiss();
        });

        sheetView.findViewById(R.id.optionRain).setOnClickListener(v -> {
            applyAmbience(findAmbienceByName("rain"));
            sheet.dismiss();
        });

        sheetView.findViewById(R.id.optionWind).setOnClickListener(v -> {
            applyAmbience(findAmbienceByName("wind"));
            sheet.dismiss();
        });

        sheetView.findViewById(R.id.optionMorningBirds).setOnClickListener(v -> {
            applyAmbience(findAmbienceByName("morning_birds"));
            sheet.dismiss();
        });

        sheetView.findViewById(R.id.optionWaves).setOnClickListener(v -> {
            applyAmbience(findAmbienceByName("waves"));
            sheet.dismiss();
        });

        sheetView.findViewById(R.id.optionDefault).setOnClickListener(v -> {
            clearAmbience();
            sheet.dismiss();
        });

        sheet.show();
    }

    private void applyAmbience(AmbienceSound ambience) {
        if (ambience == null) return;
        currentAmbienceName = ambience.getName();
        prefAmbience.edit().putString("selected", ambience.getName()).apply();

        textViewSuraNameAmbience.setText(textViewSuraName.getText());
        textViewQariNameAmbience.setText(textViewQariName.getText());

        defaultScrollView.setVisibility(View.GONE);
        ambienceOverlay.setVisibility(View.VISIBLE);

        startAmbienceAudio(ambience.getAudioAsset());

        if (videoBackgroundView.isAvailable()) {
            startAmbienceVideo(ambience.getVideoAsset(), videoBackgroundView.getSurfaceTexture());
        } else {
            videoBackgroundView.setSurfaceTextureListener(new android.view.TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(android.graphics.SurfaceTexture surface, int width, int height) {
                    startAmbienceVideo(ambience.getVideoAsset(), surface);
                }
                @Override public void onSurfaceTextureSizeChanged(android.graphics.SurfaceTexture surface, int width, int height) {}
                @Override public boolean onSurfaceTextureDestroyed(android.graphics.SurfaceTexture surface) { return true; }
                @Override public void onSurfaceTextureUpdated(android.graphics.SurfaceTexture surface) {}
            });
        }
    }

    private void startAmbienceVideo(String assetFileName, android.graphics.SurfaceTexture surfaceTexture) {
        try {
            if (ambienceVideoPlayer != null) ambienceVideoPlayer.reset();
            else ambienceVideoPlayer = new MediaPlayer();

            ambienceSurface = new Surface(surfaceTexture);
            ambienceVideoPlayer.setSurface(ambienceSurface);

            android.content.res.AssetFileDescriptor afd = getAssets().openFd("videos/" + assetFileName);
            ambienceVideoPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            ambienceVideoPlayer.setLooping(true);
            ambienceVideoPlayer.setVolume(0f, 0f);
            ambienceVideoPlayer.setOnVideoSizeChangedListener((mp, width, height) -> {
                lastVideoWidth = width;
                lastVideoHeight = height;
                applyCenterCropTransform(width, height);
            });
            ambienceVideoPlayer.setOnPreparedListener(MediaPlayer::start);
            ambienceVideoPlayer.setOnErrorListener((mp, what, extra) -> {
                android.util.Log.e("Ambience", "Video error what=" + what + " extra=" + extra);
                return true;
            });
            ambienceVideoPlayer.prepareAsync();
        } catch (Exception e) {
            android.util.Log.e("Ambience", "Failed to load video asset: videos/" + assetFileName, e);
            Toast.makeText(this, "کێشەیەک لە کردنەوەی ڤیدیۆکەدا ڕوویدا", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAmbienceAudio(String assetFileName) {
        try {
            ambienceAudioReady = false;
            if (ambienceAudioPlayer != null) {
                try { ambienceAudioPlayer.stop(); } catch (Exception ignored) {}
                ambienceAudioPlayer.reset();
            } else {
                ambienceAudioPlayer = new MediaPlayer();
            }

            android.content.res.AssetFileDescriptor afd = getAssets().openFd("audio/" + assetFileName);
            ambienceAudioPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            ambienceAudioPlayer.setLooping(true);
            ambienceAudioPlayer.setVolume(0.4f, 0.4f); // keep it under the recitation
            ambienceAudioPlayer.setOnPreparedListener(mp -> {
                ambienceAudioReady = true;
                // Only auto-start if the recitation is already playing (e.g. user changed background mid-play)
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    try { mp.start(); } catch (Exception ignored) {}
                }
            });
            ambienceAudioPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearAmbience() {
        currentAmbienceName = null;
        ambienceAudioReady = false;
        prefAmbience.edit().remove("selected").apply();

        if (ambienceVideoPlayer != null) {
            try { ambienceVideoPlayer.stop(); } catch (Exception ignored) {}
            ambienceVideoPlayer.release();
            ambienceVideoPlayer = null;
        }
        if (ambienceSurface != null) { ambienceSurface.release(); ambienceSurface = null; }
        if (ambienceAudioPlayer != null) {
            try { ambienceAudioPlayer.stop(); } catch (Exception ignored) {}
            ambienceAudioPlayer.release();
            ambienceAudioPlayer = null;
        }

        ambienceOverlay.setVisibility(View.GONE);
        defaultScrollView.setVisibility(View.VISIBLE);
    }

    private void setPlayButtonIcon(int resId) {
        btnPlay.setImageResource(resId);
        if (btnPlayAmbience != null) btnPlayAmbience.setImageResource(resId);
    }

    private void playStream(String url) {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    setPlayButtonIcon(R.drawable.play_dng);
                    setPlaybackAnimation(false);
                    stopSeekBarUpdate();
                    syncAmbienceAudioPlayback(false);
                    return;
                } else {
                    mediaPlayer.start();
                    setPlayButtonIcon(R.drawable.pause_dng);
                    setPlaybackAnimation(true);
                    startSeekBarUpdate();
                    syncAmbienceAudioPlayback(true);
                    return;
                }
            }

            resetPlayer();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                waveSeekBar.setProgress(0);
                adjustWaveStyleByDuration(mp.getDuration());
                waveSeekBar.setMax(mp.getDuration());
                textViewDuration.setText("00:00 - " + formatTime(mp.getDuration()));

                mp.start();
                startSeekBarUpdate();
                setPlayButtonIcon(R.drawable.pause_dng);
                setPlaybackAnimation(true);
                syncAmbienceAudioPlayback(true);
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                setPlayButtonIcon(R.drawable.play_dng);
                stopSeekBarUpdate();
                waveSeekBar.setProgress(0);
                textViewDuration.setText("00:00 - " + formatTime(mp.getDuration()));
                setPlaybackAnimation(false);
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Streaming failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void playMp3File(File mp3File) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(mp3File.getAbsolutePath());
                mediaPlayer.prepare();
            }

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                setPlayButtonIcon(R.drawable.play_dng);
                setPlaybackAnimation(false);
                stopSeekBarUpdate();
                syncAmbienceAudioPlayback(false);
            } else {
                waveSeekBar.setProgress(0);
                adjustWaveStyleByDuration(mediaPlayer.getDuration());
                waveSeekBar.setMax(mediaPlayer.getDuration());
                textViewDuration.setText("00:00 - " + formatTime(mediaPlayer.getDuration()));

                mediaPlayer.start();
                setPlayButtonIcon(R.drawable.pause_dng);
                setPlaybackAnimation(true);
                startSeekBarUpdate();
                syncAmbienceAudioPlayback(true);
            }

            mediaPlayer.setOnCompletionListener(mp -> {
                setPlayButtonIcon(R.drawable.play_dng);
                stopSeekBarUpdate();
                waveSeekBar.setProgress(0);
                textViewDuration.setText("00:00 - " + formatTime(mp.getDuration()));
                setPlaybackAnimation(false);
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "ناتوانرێت ئەم دەنگە لێبدرێت", Toast.LENGTH_SHORT).show();
        }
    }


    private void applyCenterCropTransform(int videoWidth, int videoHeight) {
        if (videoBackgroundView == null || videoWidth == 0 || videoHeight == 0) return;
        int viewWidth = videoBackgroundView.getWidth();
        int viewHeight = videoBackgroundView.getHeight();
        if (viewWidth == 0 || viewHeight == 0) return;

        float scaleX = 1f, scaleY = 1f;
        float viewRatio = (float) viewWidth / viewHeight;
        float videoRatio = (float) videoWidth / videoHeight;

        if (viewRatio > videoRatio) {
            scaleY = viewRatio / videoRatio;
        } else {
            scaleX = videoRatio / viewRatio;
        }

        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY, viewWidth / 2f, viewHeight / 2f);
        videoBackgroundView.setTransform(matrix);
    }
    @SuppressLint("SetTextI18n")
    private void resetPlayer() {
        stopSeekBarUpdate();
        if (waveSeekBar != null) {
            waveSeekBar.setProgress(0);
        }
        if (discAnimator != null) {
            discAnimator.end();
        }
        if (needleAnimator != null) {
            needleAnimator.end();
            imgVinylNeedle.setRotation(0f);
        }
        if (textViewDuration != null) {
            textViewDuration.setText("00:00 - 00:00");
        }

        updateBtnDownloadResource();

        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Exception ignored) {}
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        setPlayButtonIcon(R.drawable.play_dng);
    }



}