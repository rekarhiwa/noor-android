package com.dya.noor.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.QariNameAdapter;
import com.dya.noor.adapters.QuranPageAdapter;
import com.dya.noor.adapters.RecentQariAdapter;
import com.dya.noor.database.MydbClass;
import com.dya.noor.database.TimingDbHelper;
import com.dya.noor.module.AyahTiming;
import com.dya.noor.module.QraiNameItem;
import com.dya.noor.module.QuranItem;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import org.jspecify.annotations.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class QuranPage extends BaseActivity implements  QuranPageAdapter.OnButtonClickListener{



    String id,SuaraName,fileUrl;
    RecyclerView QuranRecyclerView;
    ImageView btnSetting ;


    public static LinkedHashMap<Integer, ArrayList<QuranItem>> mpAllAyah = new LinkedHashMap<>();
    //
    MydbClass myDbClass;
    QuranPageAdapter quranAdapter;
    TextView SuraName ,SuraNumber ;
    ImageView  btnPlaySura , btnRewindSura , btnForwardSura;
    ImageView btnBAck , bismillahImageView;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchNumer;
    String Mp3Url ;
    String SuraNameForUrl="";
    String FolderName;


    String dataBaseUrl ="https://github.com/w-coding/Quran-Database-Timings/raw/refs/heads/main/Database/";
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    int currentAyah = -1;
    TimingDbHelper timingDb;
    ArrayList<AyahTiming> ayahTimings;
    int lastPage = -1;

    QariNameAdapter qariNameAdapter;
    ArrayList <QraiNameItem> qraiNameItems;

    private int pendingAyahToPlay = -1;

    String DATABASE_NAME="Peshawa";

    TextView TextViewNameQari;

    AppBarLayout appBarLayout;

    String strQariName;
    @SuppressLint("StaticFieldLeak")
    public static BottomSheetDialog dialog;

    @SuppressLint("StaticFieldLeak")
    public  static  EditText editSearch;
    ArrayList<QraiNameItem> recentQaris = new ArrayList<>();

    SeekBar seek_barMedia;
    TextView durationMedia;
    private Runnable seekBarRunnable;
    private boolean isUserSeeking = false;

    Snackbar snackbar;

    ConnectivityManager.NetworkCallback networkCallback;


    @SuppressLint({"NotifyDataSetChanged", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran_page);

        QuranRecyclerView = findViewById(R.id.pageRecyclerView);
        myDbClass = new MydbClass(this);



        TextViewNameQari = findViewById(R.id.nameQari);
        QariNameuIUpdate();
        SuraName = findViewById(R.id.txtSuratNAm);
        btnBAck = findViewById(R.id.buttonBack);
        btnSetting = findViewById(R.id.btnSetting);
        bismillahImageView = findViewById(R.id.bismillahImageView);
        seek_barMedia = findViewById(R.id.seek_barMedia);
        durationMedia = findViewById(R.id.durationMedi);



        id = getIntent().getStringExtra("id");
        String sss = SuraName.getText().toString();
        StoreNameData(id);

        appBarLayout = findViewById(R.id.appBarLayout);



        switchNumer = findViewById(R.id.switchNumber);
        btnPlaySura = findViewById(R.id.btnPlaySura);
        btnRewindSura = findViewById(R.id.btnRewindSura);
        btnForwardSura = findViewById(R.id.btnForwardSura);


        TextViewNameQari.setText(strQariName);

        if (id.equals("1") || id.equals("9")){
            bismillahImageView.setVisibility(View.GONE);
        }
        StoreDataInArrayList();

        QuranPageAdapter.suraId= Integer.parseInt(id);
        quranAdapter = new QuranPageAdapter(this, new ArrayList<>(mpAllAyah.keySet()),this);


        qraiNameItems = new ArrayList<>();

        StoreQraiName();

        qariNameAdapter = new QariNameAdapter(
                this,
                new ArrayList<>(),
                item -> {
                    strQariName = item.getName();
                    DATABASE_NAME = item.getDatabaseName();
                    FolderName = item.getDatabaseName();
                    Mp3Url = item.getUrl();

                    TextViewNameQari.setText(strQariName);

                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                        btnPlaySura.setImageResource(R.drawable.ic_play);
                    }
                }
        );

        qariNameAdapter.setData(qraiNameItems); // 🔥 IMPORTANT



        dialog = new BottomSheetDialog(this, R.style.BottomSheetStyle);


        QuranRecyclerView.setAdapter(quranAdapter);
        QuranRecyclerView.setLayoutManager(new LinearLayoutManager(this ,RecyclerView.VERTICAL, false));
        quranAdapter.SurahNAme = sss;
        quranAdapter.SurahId = id;





        btnBAck.setOnClickListener(v -> {
            onBackPressed();
        });

        btnSetting.setOnClickListener(v -> {
            AppSettings.activityName="sura";
            Intent intent = new Intent(QuranPage.this, AppSettings.class);
            startActivity(intent);
        });

        btnPlaySura.setOnClickListener(v -> {

            prepareTimingDbAndPlay();

        });

        btnForwardSura.setOnClickListener(v -> goToNextAyah());
        btnRewindSura.setOnClickListener(v -> goToPreviousAyah());

        seek_barMedia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    int newPos = seekBar.getProgress();
                    mediaPlayer.seekTo(newPos);

                    // 🔥 UPDATE AYAH AFTER SEEK
                    int ayah = findAyahByTime(newPos);
                    if (ayah != -1) {
                        currentAyah = ayah;
                        int page = findPageForAyah(ayah);
                        quranAdapter.setHighlightedAyah(ayah, page);

                        scrollToAyah(ayah);
                    }
                }
                isUserSeeking = false;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    durationMedia.setText(
                            formatTime(progress) + " / " +
                                    formatTime(mediaPlayer != null ? mediaPlayer.getDuration() : 0)
                    );
                }
            }
        });


        TextViewNameQari.setOnClickListener(v->{


            dialog.setContentView(R.layout.qari_botomsheet);

            RecyclerView recyclerAll = dialog.findViewById(R.id.qariNameRecyclerView);
            RecyclerView recyclerRecent = dialog.findViewById(R.id.recyclerRecentQari);
            editSearch = dialog.findViewById(R.id.editTextSearchQariName);

            // Load recents
            loadRecentQariList();
            // Recent adapter
            RecentQariAdapter recentAdapter =
                    new RecentQariAdapter(this, recentQaris, item -> {
                        strQariName = item.getName();
                        DATABASE_NAME = item.getDatabaseName();
                        FolderName = item.getDatabaseName();
                        Mp3Url = item.getUrl();
                        TextViewNameQari.setText(strQariName);
                    });

            assert recyclerRecent != null;
            recyclerRecent.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerRecent.setAdapter(recentAdapter);

            // ADD THIS LINE:
            recyclerRecent.setNestedScrollingEnabled(false);


            // All qaris
            assert recyclerAll != null;
            recyclerAll.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false));

          //  recyclerAll.setHasFixedSize(true);
            recyclerAll.setNestedScrollingEnabled(true);
            recyclerAll.setAdapter(qariNameAdapter);

            // Search + clear ❌

            editSearch.setOnTouchListener((v2, event) -> {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editSearch.getRight()
                            - editSearch.getCompoundDrawables()[2].getBounds().width())) {
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

            dialog.show();

            View bottomSheet =
                    dialog.findViewById(R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

               // 1. Force Expanded State so it doesn't try to "drag up" while you scroll
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);

                // 2. Fix the nested scrolling
                recyclerAll.setNestedScrollingEnabled(true);
            }

        });

        SharedPreferences preferences = getSharedPreferences("myPreferences", MODE_PRIVATE);
        final boolean[] isSubItemVisible = {preferences.getBoolean("isSubItemVisible", false)};
        switchNumer.setChecked(isSubItemVisible[0]);
        switchNumer.setOnClickListener(v -> {
            // Toggle the visibility flag
            isSubItemVisible[0] = !isSubItemVisible[0];

            // Save the updated state to SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isSubItemVisible", isSubItemVisible[0]);
            editor.apply();

            // Notify the adapter that the data has changed
            quranAdapter.notifyDataSetChanged();
        });


        // Inside onCreate, at the very bottom
        int jumpToAyah = getIntent().getIntExtra("jumpToAyah", -1);
        if (jumpToAyah != -1) {
            // We use a small delay to ensure the RecyclerView has finished drawing its items
            new Handler().postDelayed(() -> {
                scrollToAyah(jumpToAyah);
            }, 500);
        }

        QuranRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private boolean isAppBarCollapsed = false;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Any downward scroll (user OR auto)
                if (dy > 0 && !isAppBarCollapsed) {
                    appBarLayout.setExpanded(false, true);
                    isAppBarCollapsed = true;
                }

                // Any upward scroll
                else if (dy < 0 && isAppBarCollapsed) {
                    appBarLayout.setExpanded(true, true);
                    isAppBarCollapsed = false;
                }
            }
        });



    }


    @SuppressLint("DefaultLocale")
    private String formatTime(int millis) {
        int totalSeconds = millis / 1000;

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            // hh:mm:ss
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            // mm:ss
            return String.format("%02d:%02d", minutes, seconds);
        }
    }



    private void scrollToAyah(int ayahNumber) {
        int pageOfAyah = findPageForAyah(ayahNumber);
        int adapterPos = quranAdapter.findPositionForAyah(ayahNumber);

        if (adapterPos != -1) {
            // 1. Highlight the Ayah visually
            quranAdapter.setHighlightedAyah(ayahNumber, pageOfAyah);

            // 2. Scroll the page into view
            LinearLayoutManager layoutManager = (LinearLayoutManager) QuranRecyclerView.getLayoutManager();
            assert layoutManager != null;
            layoutManager.scrollToPositionWithOffset(adapterPos, 0);

            // 3. Scroll to the specific line inside that page
            QuranRecyclerView.postDelayed(() -> {
                QuranPageAdapter.QuranViewHolder holder =
                        (QuranPageAdapter.QuranViewHolder) QuranRecyclerView.findViewHolderForAdapterPosition(adapterPos);

                if (holder != null) {
                    int[] offsets = quranAdapter.getAyahOffsets(ayahNumber, pageOfAyah);
                    if (offsets != null && holder.lblAyahText.getLayout() != null) {
                        int line = holder.lblAyahText.getLayout().getLineForOffset(offsets[0]);
                        int y = holder.lblAyahText.getLayout().getLineTop(line);
                        QuranRecyclerView.smoothScrollBy(0, y);
                    }
                }
            }, 200);
        }
    }

    private int findAyahByTime(long position) {
        for (int i = ayahTimings.size() - 1; i >= 0; i--) {
            if (position >= ayahTimings.get(i).time) {
                return ayahTimings.get(i).ayah;
            }
        }
        return -1;
    }

    private int findPageForAyah(int ayahNumber) {
        for (Integer page : mpAllAyah.keySet()) {
            for (QuranItem item : Objects.requireNonNull(mpAllAyah.get(page))) {
                if (item.getAyahNumberInt() == ayahNumber) {
                    return page;
                }
            }
        }
        return -1;
    }

    private void playSurah() {
        File audioDir = new File(
                getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                FolderName
        );
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }

        File mp3File = new File(audioDir, SuraNameForUrl);

        if (mp3File.exists()) {
            playMp3File(mp3File);
        } else {
            downloadMp3AndPlay(mp3File);
        }
    }

    @SuppressLint({"ObsoleteSdkInt", "SetTextI18n"})
    private void downloadMp3AndPlay(File mp3File) {

        if (!isNetworkAvailable()) {
            Toast.makeText(this,
                    "تکایە پەیوەندی بە ئینتەرنێتەوە بکە",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // 1️⃣ Show download dialog
        View progressView = getLayoutInflater().inflate(R.layout.dialog_download, null);
        ProgressBar progressBar = progressView.findViewById(R.id.downloadProgressBar);
        TextView txtPercentage = progressView.findViewById(R.id.txtPercentage);

        AlertDialog progressDialog =
                new AlertDialog.Builder(this).setView(progressView).create();

        if (progressDialog.getWindow() != null)
            progressDialog.getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);

        progressDialog.setCancelable(false);
        progressDialog.show();

        // 2️⃣ Notification setup
        String channelId = "download_channel";
        NotificationManager notificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new android.app.NotificationChannel(
                            channelId,
                            "Downloads",
                            android.app.NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle("دابەزاندنی " + SuraName.getText())
                        .setContentText("خەریکی دابەزاندنە...")
                        .setSmallIcon(R.drawable.ic_download)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setOngoing(true)
                        .setOnlyAlertOnce(true)
                        .setProgress(100, 0, false);

        int notificationId = 101;
        notificationManager.notify(notificationId, notificationBuilder.build());

        // 3️⃣ Download thread
        new Thread(() -> {
            BufferedInputStream input = null;
            BufferedOutputStream output = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(Mp3Url + mp3File.getName());
                connection = (HttpURLConnection) url.openConnection();

                // 🔥 SPEED OPTIMIZATIONS
                connection.setRequestProperty("Accept-Encoding", "identity");
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(20000);
                connection.setUseCaches(true);
                connection.connect();

                int fileLength = connection.getContentLength();

                input = new BufferedInputStream(connection.getInputStream(), 256 * 1024);
                output = new BufferedOutputStream(
                        new FileOutputStream(mp3File), 256 * 1024);

                byte[] buffer = new byte[256 * 1024]; // 🔥 256 KB buffer
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
                        notificationManager.notify(notificationId,
                                notificationBuilder.build());

                        lastUpdateTime = now;
                    }
                }

                output.flush();

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    notificationManager.cancel(notificationId);
                    Toast.makeText(this,
                            "فایلەکە بەسەرکەوتی دابەزی",
                            Toast.LENGTH_LONG).show();

                    // ▶️ Uncomment if you want auto-play
                    // playMp3File(mp3File);
                });

            } catch (Exception e) {
                if (mp3File.exists()) mp3File.delete();

                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    notificationManager.cancel(notificationId);
                    Toast.makeText(this,
                            "هەڵەیەک ڕوویدا",
                            Toast.LENGTH_SHORT).show();
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


    @SuppressLint("SetTextI18n")
    private void playMp3File(File mp3File) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(mp3File.getAbsolutePath());
                mediaPlayer.prepare();

                // 🔹 SET SEEK BAR MAX = AUDIO DURATION
                seek_barMedia.setMax(mediaPlayer.getDuration());

                // 🔹 SET TOTAL DURATION TEXT
                durationMedia.setText("00:00 / " + formatTime(mediaPlayer.getDuration()));

                // 🔹 START SEEK BAR UPDATES

                startSeekBarUpdate();


                // Check if we clicked an Ayah while it was loading
                if (pendingAyahToPlay != -1) {
                    int ayahToJump = pendingAyahToPlay;
                    pendingAyahToPlay = -1; // Reset it
                    playAyahByNumber(ayahToJump);
                    return; // Exit so we don't play from the start
                }

            }

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlaySura.setImageResource(R.drawable.ic_play);
                //Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show();

            } else {
                mediaPlayer.start();

                btnPlaySura.setImageResource(R.drawable.ic_pause);
                startTrackingAyah();
                // Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
            }

            mediaPlayer.setOnCompletionListener(mp -> {
                btnPlaySura.setImageResource(R.drawable.ic_play);
                seek_barMedia.setProgress(0);
                durationMedia.setText("00:00 / " + formatTime(mediaPlayer.getDuration()));
                // 🔼 SHOW TOOLBAR AGAIN
                appBarLayout.setExpanded(true, true);

            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Cannot play audio", Toast.LENGTH_SHORT).show();
        }
    }


    private void startSeekBarUpdate() {
        if (seekBarRunnable != null) {
            handler.removeCallbacks(seekBarRunnable);
        }

        seekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying() && !isUserSeeking) {
                    int currentPos = mediaPlayer.getCurrentPosition();
                    seek_barMedia.setProgress(currentPos);

                    durationMedia.setText(
                            formatTime(currentPos) + " / " +
                                    formatTime(mediaPlayer.getDuration())
                    );

                    // 🔥 UPDATE AYAH BASED ON SEEK
                    int ayah = findAyahByTime(currentPos);
                    if (ayah != -1 && ayah != currentAyah) {
                        currentAyah = ayah;
                        int page = findPageForAyah(ayah);
                        quranAdapter.setHighlightedAyah(ayah, page);
                    }
                }
                handler.postDelayed(this, 500);
            }
        };

        handler.post(seekBarRunnable);
    }


    private void startTrackingAyah() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    if (ayahTimings == null || ayahTimings.isEmpty()) return;

                    long pos = mediaPlayer.getCurrentPosition();
                    int ayah = findAyahByTime(pos);

                    if (ayah != currentAyah && ayah != -1) {
                        currentAyah = ayah;

                        // 🔥 FORCE HIDE TOOLBAR WHEN AUDIO AUTO-SCROLLS
                        appBarLayout.setExpanded(false, true);
                        int pageOfAyah = findPageForAyah(ayah);
                        quranAdapter.setHighlightedAyah(ayah, pageOfAyah);

                        int adapterPos = quranAdapter.findPositionForAyah(ayah);
                        if (adapterPos != -1) {
                            // 1. Ensure the page itself is visible
                            LinearLayoutManager layoutManager = (LinearLayoutManager) QuranRecyclerView.getLayoutManager();
                            layoutManager.scrollToPositionWithOffset(adapterPos, 0);

                            // 2. Small delay to let the UI update, then scroll to the specific Ayah line
                            handler.postDelayed(() -> {
                                QuranPageAdapter.QuranViewHolder holder =
                                        (QuranPageAdapter.QuranViewHolder) QuranRecyclerView.findViewHolderForAdapterPosition(adapterPos);

                                // Get the character start/end from the adapter
                                int[] offsets = quranAdapter.getAyahOffsets(ayah, pageOfAyah);

                                if (offsets != null) {
                                    // Find which line the highlighted text is on
                                    int line = holder.lblAyahText.getLayout().getLineForOffset(offsets[0]);
                                    // Get the Y coordinate of that line
                                    int y = holder.lblAyahText.getLayout().getLineTop(line);

                                    // Scroll the RecyclerView down to that specific line
                                    QuranRecyclerView.smoothScrollBy(0, y);
                                }
                            }, 100);
                        }
                    }
                    handler.postDelayed(this, 150);
                }
            }
        }, 150);
    }

    private boolean isNetworkAvailable() {
        android.net.ConnectivityManager connectivityManager
                = (android.net.ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void StoreDataInArrayList() {

        // data pak akainawa nakw data peshw tya mabet.
        // chwnka ba static krdwmana data peshw amenetawa ka swraty tr bkatawa.
        // paki akainawa ta data am swrata halgret ka krawatawa.
        mpAllAyah.clear();

        Cursor cursor = myDbClass.readAllAyahData(id);

        while (cursor.moveToNext()) {
            // hawl bda dawai data ba zhmara maka - male index 9 bo wargrtni [aya_text], baw shewai xwarawa bika esra7at tra.
            // tanha la sarw nawi class (sari sarawa) am dera ziad bka ba error nadat hichkat :
            // @SuppressLint("Range")
            @SuppressLint("Range")
            QuranItem qt = new QuranItem(
                    cursor.getString(cursor.getColumnIndex("text")),
                    cursor.getString(cursor.getColumnIndex("suraId")),
                    cursor.getString(cursor.getColumnIndex("ayah")),
                    cursor.getString(cursor.getColumnIndex("text")),
                    cursor.getString(cursor.getColumnIndex("juzz")),
                    cursor.getString(cursor.getColumnIndex("page")),
                    cursor.getString(cursor.getColumnIndex("sura_name_ar")),
                    cursor.getString(cursor.getColumnIndex("aya_no_arabic")),
                    cursor.getString(cursor.getColumnIndex("aya_no_arabic_rev"))
            );

            if (mpAllAyah.containsKey(qt.getPagAsInt())) {
                mpAllAyah.get(qt.getPagAsInt()).add(qt);
            } else {
                ArrayList<QuranItem> ar = new ArrayList<>();
                ar.add(qt);
                mpAllAyah.put(qt.getPagAsInt(), ar);

            }
        }




        //
    }
    public void showNoInternetSnackbar(View parentView) {

        snackbar = Snackbar.make(parentView, "", Snackbar.LENGTH_INDEFINITE);
        View snackbarLayout = snackbar.getView();
        snackbarLayout.setBackgroundColor(Color.TRANSPARENT);

        View customView = LayoutInflater.from(parentView.getContext())
                .inflate(R.layout.snackbar_no_internet, null);

        TextView btnRetry = customView.findViewById(R.id.btnRetry);
        ProgressBar progressRetry = customView.findViewById(R.id.progressRetry);

        btnRetry.setOnClickListener(v -> {

            // UI → loading state
            btnRetry.setEnabled(false);
            btnRetry.setText("پشکنین...");
            progressRetry.setVisibility(View.VISIBLE);

            // Small delay for better UX (animation visible)
            new Handler().postDelayed(() -> {

                if (isNetworkAvailable()) {
                    dismissWithAnimation(snackbarLayout);
                    snackbar.dismiss();
                    showTimingDbDownloadDialog();
                } else {
                    // Still no internet → reset UI
                    btnRetry.setEnabled(true);
                    btnRetry.setText("هەوڵبدەرەوە");
                    progressRetry.setVisibility(View.GONE);
                }

            }, 1200); // 1.2 sec feels perfect
        });

        @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbarLayout;
        layout.setPadding(0, 0, 0, 0);
        layout.addView(customView, 0);

        // Show animation
        snackbarLayout.setAlpha(0f);
        snackbarLayout.setTranslationY(200f);
        snackbarLayout.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(300)
                .start();

        snackbar.show();

        registerNetworkCallback(snackbarLayout);
    }



    private void registerNetworkCallback(View snackbarView) {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) return;

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                runOnUiThread(() -> {
                    if (snackbar != null && snackbar.isShown()) {
                        dismissWithAnimation(snackbarView);
                        snackbar.dismiss();
                        showTimingDbDownloadDialog();
                    }
                });
            }
        };

        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        cm.registerNetworkCallback(request, networkCallback);
    }


    private void dismissWithAnimation(View view) {
        view.animate()
                .alpha(0f)
                .translationY(200f)
                .setDuration(250)
                .start();
    }


    private void prepareTimingDbAndPlay() {

        if (isTimingDbReady()) {
            // ✅ DB already exists
            timingDb = new TimingDbHelper(this, DATABASE_NAME);
            ayahTimings = timingDb.getTimings(Integer.parseInt(id));
            playSurah();
            return;
        }

        if (isNetworkAvailable()){
        // ❌ DB NOT downloaded → show dialog
        showTimingDbDownloadDialog();
        }
        else {
            // ne Internet
            showNoInternetSnackbar(findViewById(android.R.id.content));
        }
    }

    private boolean isTimingDbReady() {
        File dbFile = getDatabasePath(DATABASE_NAME + ".db");
        return dbFile.exists() && dbFile.length() > 1024;
    }
    private void showTimingDbDownloadDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_download, null);
        ProgressBar progressBar = view.findViewById(R.id.downloadProgressBar);
        TextView txtPercent = view.findViewById(R.id.txtPercentage);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.show();

        new Thread(() -> {
            try {
                File dbFile = getDatabasePath(DATABASE_NAME + ".db");
                dbFile.getParentFile().mkdirs();

                HttpURLConnection connection =
                        (HttpURLConnection) new URL(dataBaseUrl + DATABASE_NAME + ".db").openConnection();

                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.connect();

                int length = connection.getContentLength();

                InputStream input = new BufferedInputStream(connection.getInputStream());
                FileOutputStream output = new FileOutputStream(dbFile);

                byte[] buffer = new byte[64 * 1024];
                long total = 0;
                int count;
                long lastUpdate = 0;

                while ((count = input.read(buffer)) != -1) {
                    total += count;

                    long now = System.currentTimeMillis();
                    if (now - lastUpdate > 200 && length > 0) {
                        int progress = (int) (total * 100 / length);
                        runOnUiThread(() -> {
                            progressBar.setProgress(progress);
                            txtPercent.setText(progress + "%");
                        });
                        lastUpdate = now;
                    }

                    output.write(buffer, 0, count);
                }

                output.close();
                input.close();
                connection.disconnect();

                // 🔥 FORCE 100% + CLOSE DIALOG + CONTINUE
                runOnUiThread(() -> {
                    progressBar.setProgress(100);
                    txtPercent.setText("100%");
                    dialog.dismiss();

                    timingDb = new TimingDbHelper(this, DATABASE_NAME);
                    ayahTimings = timingDb.getTimings(Integer.parseInt(id));

                    playSurah(); // ✅ CONTINUE PLAY
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    dialog.dismiss();
                    Toast.makeText(this,
                            "هەڵەیەک ڕوویدا لە دابەزاندنی داتابەیس",
                            Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    @SuppressLint("Range")
    void StoreNameData(String id) {

        Cursor cursor = myDbClass.readAllNameData(id);

        while (cursor.moveToNext()) {
            SuraName.setText(cursor.getString(cursor.getColumnIndex("name")));
            SuraNameForUrl=(cursor.getString(cursor.getColumnIndex("link")));
        }
    }

    void StoreQraiName() {

        //loadRecentQari(); // 🔥 ADD THIS FIRST
        ArrayList<QraiNameItem> favs = new ArrayList<>();
        ArrayList<QraiNameItem> normal = new ArrayList<>();

        Cursor cursor = myDbClass.readQariName();
        SharedPreferences favPref = getSharedPreferences("FavQari", MODE_PRIVATE);

        while (cursor.moveToNext()) {
            QraiNameItem item = new QraiNameItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("url")),
                    cursor.getString(cursor.getColumnIndexOrThrow("db_name"))
            );

            if (favPref.getBoolean(item.getName(), false))
                favs.add(item);
            else
                normal.add(item);
        }

        qraiNameItems.clear();
        qraiNameItems.addAll(favs);
        qraiNameItems.addAll(normal);

    }


    private void loadRecentQariList() {
        recentQaris.clear();
        SharedPreferences pref = getSharedPreferences("RecentQari", MODE_PRIVATE);
        String data = pref.getString("list", "");

        if (data.isEmpty()) return;

        String[] items = data.split("##");
        for (String s : items) {
            String[] p = s.split("\\|\\|");
            if (p.length == 3) {
                recentQaris.add(new QraiNameItem(0, p[0], p[2], p[1]));
            }
        }
    }



    private void goToNextAyah() {

        if (ayahTimings == null || ayahTimings.isEmpty()) return;

        int nextAyah = -1;

        for (int i = 0; i < ayahTimings.size(); i++) {
            if (ayahTimings.get(i).ayah == currentAyah) {

                // make sure not last ayah
                if (i < ayahTimings.size() - 1) {
                    nextAyah = ayahTimings.get(i + 1).ayah;
                }
                break;
            }
        }

        if (nextAyah != -1) {
            playAyahByNumber(nextAyah);
            scrollToAyah(nextAyah);
            currentAyah = nextAyah;
        }
    }


    private void goToPreviousAyah() {

        if (ayahTimings == null || ayahTimings.isEmpty()) return;

        int prevAyah = -1;

        for (int i = 0; i < ayahTimings.size(); i++) {
            if (ayahTimings.get(i).ayah == currentAyah) {

                // make sure not first ayah
                if (i > 0) {
                    prevAyah = ayahTimings.get(i - 1).ayah;
                }
                break;
            }
        }

        if (prevAyah != -1) {
            playAyahByNumber(prevAyah);
            scrollToAyah(prevAyah);
            currentAyah = prevAyah;
        }
    }


    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        QuranPageAdapter.suraId= Integer.parseInt(id);
        QuranPageAdapter.suraId=QuranPageAdapter.suraId-1;
        finish();
    }

    @Override
    public void onButtonClick(int position) {
        int id2= Integer.parseInt(id);

        if (id2<114) {
            id2++;
            QuranPageAdapter.suraId=id2;
            id= String.valueOf(id2);
            Intent intent = new Intent(this,QuranPage.class);
            intent.putExtra("id",id);
            startActivity(intent);

            if (mediaPlayer != null) {

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlaySura.setImageResource(R.drawable.ic_play);
                }

            }


            finish();
        }



    }

    @Override
    public void onAyahClick(int ayahNumber, String ayahText) {
        // 1. Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_ayah_menu, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        //  MAKE BACKGROUND TRANSPARENT
        // This removes the default white square box
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // 2. Find the views inside the custom layout
        TextView title = dialogView.findViewById(R.id.dialogTitle);
        LinearLayout btnPlay = dialogView.findViewById(R.id.btnPlayAyah);
        LinearLayout btnCopy = dialogView.findViewById(R.id.btnCopyAyah);
        LinearLayout btnBookmark = dialogView.findViewById(R.id.btnBookmarkAyah); // Add this

        title.setText("ئایەتی ژمارە " + ayahNumber);

        // 3. Set click listeners for the custom buttons
        btnPlay.setOnClickListener(v -> {
            playAyahByNumber(ayahNumber);
            dialog.dismiss();
        });

        btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Ayah", ayahText
                    +"("+ayahNumber+")"+"\n\n"+SuraName.getText()+"\n" + "#ئەپڵیکەیشنی_نور");
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "دەقەکە کۆپی کرا", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        // --- NEW BOOKMARK LISTENER ---
        btnBookmark.setOnClickListener(v -> {
            SharedPreferences pref = getSharedPreferences("BookmarkPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            // Save Sura ID and Ayah Number
            editor.putString("lastSuraId", id);
            editor.putInt("lastAyahNo", ayahNumber);
            editor.putString("lastSuraName", SuraName.getText().toString());
            editor.apply();

            showCustomToast(
                    "کۆتا خوێندنەوە ئایەتی " + ayahNumber + " لە " + SuraName.getText().toString()
            );
             dialog.dismiss();
        });

        dialog.show();
    }

    // Separate method to handle the play logic
    private void playAyahByNumber(int ayahNumber) {
        // 1. Check if Timing DB is missing
        prepareTimingDbAndPlay();

        // 2. Check if Media is missing or not initialized
        if (mediaPlayer == null) {
            pendingAyahToPlay = ayahNumber; // Remember this ayah!
            playSurah(); // This starts the download/loading logic
            return;
        }

        // 3. If everything is ready, find the time and jump
        long startTime = -1;
        for (AyahTiming timing : ayahTimings) {
            if (timing.ayah == ayahNumber) {
                startTime = timing.time;
                break;
            }
        }

        if (startTime != -1) {
            mediaPlayer.seekTo((int) startTime);
            currentAyah = ayahNumber;
            int page = findPageForAyah(ayahNumber);
            quranAdapter.setHighlightedAyah(ayahNumber, page);
            scrollToAyah(ayahNumber);
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                btnPlaySura.setImageResource(R.drawable.ic_pause);
                startTrackingAyah();
            }
        }
    }








    public void showCustomToast(String message) {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout,
                findViewById(R.id.toast_view));

        TextView txtToast = layout.findViewById(R.id.txtToast);
        txtToast.setText(message);

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 120);
        toast.show();
    }


    public void QariNameuIUpdate(){

        strQariName = getSharedPreferences("QariName",MODE_PRIVATE).getString("name","أبو بکر الشاطري");
        DATABASE_NAME = getSharedPreferences("QariName",MODE_PRIVATE).getString("db_name","shatri");
        FolderName = getSharedPreferences("QariName",MODE_PRIVATE).getString("db_name","shatri");
        Mp3Url = getSharedPreferences("QariName",MODE_PRIVATE).getString("url","https://server11.mp3quran.net/shatri/");
        TextViewNameQari.setText(strQariName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        QariNameuIUpdate();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (networkCallback != null) {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                cm.unregisterNetworkCallback(networkCallback);
            }
        }

        if (handler != null && seekBarRunnable != null) {
            handler.removeCallbacks(seekBarRunnable);
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }




}