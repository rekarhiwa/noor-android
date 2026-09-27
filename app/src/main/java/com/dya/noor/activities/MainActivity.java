package com.dya.noor.activities;

import static android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;

import static androidx.core.util.TypedValueCompat.dpToPx;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.ActivityAdapter;
import com.dya.noor.adapters.CalendarAdapter;
import com.dya.noor.adapters.HomeSliderAdapter;
import com.dya.noor.database.MydbClass;
import com.dya.noor.database.PrayerInfo;
import com.dya.noor.databinding.ActivityMainBinding;
import com.dya.noor.module.ActivityItem;
import com.dya.noor.module.DayModel;
import com.dya.noor.notifications.ScheduleNotification;
import com.dya.noor.utlis.Utils;
import com.dya.noor.widget.CallaUpdateWidget;
import com.dya.noor.widget.SalatWidget;
import com.dya.noor.widget.SalatWidgetVertical;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends BaseActivity {

    File file12;
    Context context = this;


    Calendar currentCalendar;
    TextView tvMonthYear;
    ImageView btnNext, btnPrev;
    RecyclerView recycler;
    CalendarAdapter calendarAdapter;


    DownloadManager manager;
    CardView Bayanyan, Ewara, Xawtn;
    Animation open;
    DrawerLayout drawerLayout;

    ImageView btnMenu, btnAya, btnBang, calender;
    RoundedImageView btnSuratFb;
    String CityNameEnglish, CityNameKurdish;
    List<String> listPermissionsNeeded = new ArrayList<>();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    BottomSheetDialog sheetDialog,sheetDialogThird;
    HijrahDate hijrahDate;
    DateTimeFormatter formatter , formatterM ,formatterY;
    String formatted , formattedM , formattedY;
    static boolean isActive = true;
    TextView date2, fajr, sunrise, dhuhr, asr, maghrib, isha, mCity, katymawa;
    ConstraintLayout cardPrayerTimeView;
    TextClock time2;
    String mDate, mDate2, dayOfWeek;
    private static final int RCP_AP_UPDATE = 100;
    private AppUpdateManager mAppUpdateManager;
    RecyclerView ActRecyclerView;
    DownloadManager downloadManager;
    Snackbar snackbar;
    TextView snackbarText;
    CardView cardviewTime;
    static PowerManager powerManager;
    static String pkgs;
    SharedPreferences prefe;
    private int numberOfOpens ;
    LinearLayout linearLayoutGotoSeeting ,donationLayout;
    ActivityMainBinding bind;

    // Variables for Permission and Battery Logic
    public static final int PERMISSIONS_REQUEST_CODE = 101;
    public static final String TAG = "PermissionHandler";
    public static final String PREFS_NAME = "MyPermissionPrefs";
    public static final String PREF_BATTERY_ASKED = "has_been_asked_for_battery";

    public String c="";
    ImageView btnThirty;
    NestedScrollView scrollView ;
    SharedPreferences ThirdNightPrefs;

    // noor api's for welcome msg
    private static final String WELCOME_API = "https://www.noor.krd/api/app-ad/welcome";
    // noor api's for SLIDER
    private static final String SLIDER_API = "https://www.noor.krd/api/app-ad/slider";


    private final ExecutorService sliderExecutor =
            Executors.newSingleThreadExecutor();


    private ViewPager2 homeSlider;

    private LinearLayout sliderDots;

    private final int DOT_ACTIVE_COLOR =
            Color.parseColor("#753E51");

    private final int DOT_INACTIVE_COLOR =
            Color.parseColor("#753E51");
    private HomeSliderAdapter sliderAdapter;

    private final Handler sliderHandler = new Handler(Looper.getMainLooper());

    private final Runnable sliderRunnable = new Runnable() {

                @Override
                public void run() {

                    if (homeSlider != null
                            && sliderAdapter != null
                            && sliderAdapter.getItemCount() > 1) {

                        int next =
                                homeSlider.getCurrentItem() + 1;

                        if (next >= sliderAdapter.getItemCount()) {
                            next = 0;
                        }

                        homeSlider.setCurrentItem(
                                next,
                                true
                        );

                        sliderHandler.postDelayed(
                                this,
                                5000
                        );
                    }
                }
            };




    private ColorStateList fajrOriginalColor;
    private ColorStateList dhuhrOriginalColor;
    private ColorStateList asrOriginalColor;
    private ColorStateList maghribOriginalColor;
    private ColorStateList ishaOriginalColor;

    private final Handler prayerHandler = new Handler(Looper.getMainLooper());

    private final Runnable prayerCountdownRunnable = new Runnable() {

                @Override
                public void run() {

                    updatePrayerCountdown();

                    prayerHandler.postDelayed(
                            this,
                            1000
                    );
                }
            };


    @RequiresApi(api = Build.VERSION_CODES.S)
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // IMPORTANT: the prayer card is now inside item_prayer_card.xml,
        // so all prayer views must be obtained from the adapter's prayer view.
        SharedPreferences prayerPreferences = getSharedPreferences("key", MODE_PRIVATE);

        CityNameEnglish = prayerPreferences.getString("City", "Kalar");

        CityNameKurdish = prayerPreferences.getString("City2", "کەلار");

        scrollView = findViewById(R.id.scrollView2);

        // This is the first and most important call.
        checkAndRequestPermissions();

        checkWelcomeMessage();

        sliderDots = findViewById(R.id.sliderDots);
        // ---------------------------------------------------------
        // HOME VIEWPAGER2
        // Position 0 = prayer card
        // Position 1+ = API advertisement slides
        // ---------------------------------------------------------
        homeSlider = findViewById(R.id.homeSlider);

        homeSlider.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        // Create the adapter immediately.
        // Empty JSONArray means the prayer card is available first.
        sliderAdapter = new HomeSliderAdapter(this, new JSONArray());
        homeSlider.setAdapter(sliderAdapter);


        homeSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        updateSliderDots(position);
                    }
                }
        );



        setupPrayerViews();
        loadHomeSlider();


        pkgs =getPackageName();
        powerManager =getSystemService(PowerManager.class);
        AlarmManager alarmManager   = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        donationLayout = findViewById(R.id.donationLayout);
        donationLayout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this , HelpNoor.class);
            intent.putExtra("Activity", "MainActivity");
            startActivity(intent);
        });

        linearLayoutGotoSeeting = findViewById(R.id.linearLayoutGotoSeeting);
        new Utils(this);
        Utils.activeContext = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (alarmManager.canScheduleExactAlarms()) {
                MydbClass.setNextPrayer(this);
                SalatWidget.updateAllWidgetViews(this);
                SalatWidgetVertical.updateAllWidgetViews(this);
                CallaUpdateWidget.setRepeatingAlarm(this);
            }
        }
        else {
            MydbClass.setNextPrayer(this);
            SalatWidget.updateAllWidgetViews(this);
            SalatWidgetVertical.updateAllWidgetViews(this);
            CallaUpdateWidget.setRepeatingAlarm(this);
        }



        drawerLayout = findViewById(R.id.navigation_drawer);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerOpened(View drawerView) {

                scrollView.setOnTouchListener((v, event) -> true); // block touch
            }

            @Override
            public void onDrawerClosed(View drawerView) {

                scrollView.setOnTouchListener(null); // enable again
            }
        });
        //Times();
        cardviewTime = findViewById(R.id.cardviewTime);
        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);



        List<ActivityItem> activities = new ArrayList<>();
        activities.add(new ActivityItem("Quran", "قورئانی پیرۆز", R.drawable.ic_reading_quran));
        activities.add(new ActivityItem("FarmwdaView", "فەرمودە", R.drawable.ic_farmwda));
        activities.add(new ActivityItem("ActivityQuranPdf", "قورئانی پیرۆز(کتێب)", R.drawable.ic_quran_icon_new));
        activities.add(new ActivityItem("ActivityQuranMP3", "دەنگی قورئان", R.drawable.sound));
        activities.add(new ActivityItem("Khatm", "خەتمی قورئان", R.drawable.quran_khatm));
        activities.add(new ActivityItem("Zhian_Nama", "ژیان نامەی پێغەمبەرﷺ", R.drawable.muhammad));
        activities.add(new ActivityItem("ActivityHajUmrah", "حەج و عمرە", R.drawable.umrah));
        activities.add(new ActivityItem("Hawalan", "هاوەڵان", R.drawable.muslim_man));
        activities.add(new ActivityItem("Tasbih", "تەسبیح", R.drawable.ic_tasbih));
        activities.add(new ActivityItem("PayaKan", "پایەکانی باوەڕ", R.drawable.ic_man));
        activities.add(new ActivityItem("Zikr_Activity", "زیکر", R.drawable.ic_dua_hands));
        activities.add(new ActivityItem("Wallamakan", "وەڵامەکان", R.drawable.walamakan));
        activities.add(new ActivityItem("Zakat", "زەکات", R.drawable.zakat));
        activities.add(new ActivityItem("Mirat", "میرات", R.drawable.islamic_icon));
        activities.add(new ActivityItem("Nawakany_xuda", "ناوەکانی خودا", R.drawable.allah));
        activities.add(new ActivityItem("Salah", "نوێژکردن", R.drawable.ic_praying_mat));
        activities.add(new ActivityItem("NameP", "پێغەمبەران", R.drawable.ic_kaaba_mecca));
        activities.add(new ActivityItem("CompassActivity", "قیبلە نما", R.drawable.qibla_location_));
        activities.add(new ActivityItem("Kteb", "کتێبە ئاسمانیەکان", R.drawable.ic_book));
        activities.add(new ActivityItem("ActivityPrayersTime", "کاتەکانی بانگ", R.drawable.ic_prayer_mat));
        activities.add(new ActivityItem("YoutubeChuser", "یوتیوب", R.drawable.ic_youtube));
        activities.add(new ActivityItem("Babatyrozh", "سایتی نور", R.drawable.noorlogo));

        cardviewTime.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("key", MODE_PRIVATE);
            String City = sharedPreferences.getString("City", "Kalar");
            Intent intent;
            if (City.equals("")) {
                intent = new Intent(MainActivity.this, CitiesSetting.class);
            } else {
                intent = new Intent(MainActivity.this, ActivityPrayersTime.class);
            }
            startActivity(intent);

        });

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),RECEIVER_EXPORTED);

        ActRecyclerView = findViewById(R.id.ActRecyclerView);
        ActivityAdapter adapter = new ActivityAdapter(MainActivity.this, activities);
        ActRecyclerView.setAdapter(adapter);
        ActRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));

        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener((OnSuccessListener<? super AppUpdateInfo>) appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                            AppUpdateType.FLEXIBLE, MainActivity.this, RCP_AP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

        mAppUpdateManager.registerListener(installStateUpdatedListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (alarmManager.canScheduleExactAlarms()) {
                ScheduleNotification.scheduleNotification(this, 2, 8, 0, "ویردەکانی بەیانیان", "ئیستا کاتی خویندنی ویردەکانی بەیانیانە ☀");
                ScheduleNotification.scheduleNotification(this, 3, 16, 40, "ویردەکانی ئیواران", "ئیستا کاتی خویندنی ویردەکانی ئیوارانە ✨");
                ScheduleNotification.scheduleNotification(this, 4, 21, 30, "ویردەکانی خەوتنان", "ئیستا کاتی خویندنی ویردەکانی خەوتنانە 💤");
                ScheduleNotification.scheduleNotification(this, 5, 22, 10, "سورەتی مولک", "شەوانە پێش خەوتن 🛌 سورەتی { الملک } بخوێنن چونکه  ① دەبێتە ڕێگر لە سزای گـۆڕ ② دەبێتە شەفاعەت و تکاکار بۆخوێنەرەکەی تاوەکو خوای گەورە لێی خۆش دەبێت ");
                ScheduleNotification.scheduleKhatmNotification(this);
            }}
        else {
            ScheduleNotification.scheduleNotification(this, 2, 8, 0, "ویردەکانی بەیانیان", "ئیستا کاتی خویندنی ویردەکانی بەیانیانە ☀");
            ScheduleNotification.scheduleNotification(this, 3, 16, 40, "ویردەکانی ئیواران", "ئیستا کاتی خویندنی ویردەکانی ئیوارانە ✨");
            ScheduleNotification.scheduleNotification(this, 4, 21, 30, "ویردەکانی خەوتنان", "ئیستا کاتی خویندنی ویردەکانی خەوتنانە 💤");
            ScheduleNotification.scheduleNotification(this, 5, 22, 10, "سورەتی مولک", "شەوانە پێش خەوتن 🛌 سورەتی { الملک } بخوێنن چونکه  ① دەبێتە ڕێگر لە سزای گـۆڕ ② دەبێتە شەفاعەت و تکاکار بۆخوێنەرەکەی تاوەکو خوای گەورە لێی خۆش دەبێت ");
            ScheduleNotification.scheduleKhatmNotification(this);
        }

        linearLayoutGotoSeeting.setOnClickListener(v -> {
            Intent SettingIntent = new Intent(MainActivity.this, AppSettings.class);
            startActivity(SettingIntent);
        });

        Button btnSaveAll = findViewById(R.id.btnSaveAllIcons);

        btnSaveAll.setOnClickListener(v -> {
            List<Integer> imageIds = adapter.getAllImageResources();
            for (int i = 0; i < imageIds.size(); i++) {
                Drawable drawable = ContextCompat.getDrawable(MainActivity.this, imageIds.get(i));
                if (drawable != null) {
                    Bitmap bitmap = getBitmapFromDrawable(drawable);
                    saveBitmapToMediaStore(MainActivity.this, bitmap, "icon_" + i);
                }
            }
        });
        prefe = getSharedPreferences("dialog", MODE_PRIVATE);
        numberOfOpens = prefe.getInt("numberOfOpens", 0);

        numberOfOpens++;
        SharedPreferences.Editor prefeEditor = prefe.edit();
        prefeEditor.putInt("numberOfOpens", numberOfOpens);
        prefeEditor.apply();
        pkgs = getPackageName();
        powerManager = getSystemService(PowerManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!powerManager.isIgnoringBatteryOptimizations(pkgs) || alarmManager.canScheduleExactAlarms() ||
                    !android.provider.Settings.canDrawOverlays(this)) {
                linearLayoutGotoSeeting.setVisibility(View.VISIBLE);
            } else {
                linearLayoutGotoSeeting.setVisibility(View.GONE);
            }
        }
        else {
            if (!powerManager.isIgnoringBatteryOptimizations(pkgs) ||
                    !android.provider.Settings.canDrawOverlays(this)) {
                linearLayoutGotoSeeting.setVisibility(View.VISIBLE);
            }
            else {
                linearLayoutGotoSeeting.setVisibility(View.GONE);
            }
        }
        if (numberOfOpens < 4) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                if (!powerManager.isIgnoringBatteryOptimizations(pkgs) ||alarmManager.canScheduleExactAlarms()||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                                PackageManager.PERMISSION_DENIED  || !android.provider.Settings.canDrawOverlays(this)) {
                }
            }else {
                if (!powerManager.isIgnoringBatteryOptimizations(pkgs)||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                                PackageManager.PERMISSION_DENIED  || !android.provider.Settings.canDrawOverlays(this)) {
                }
            }
        }
        katymawa = findViewById(R.id.katymawa);
        open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.frometope);
        btnMenu = findViewById(R.id.btnNav);
        btnSuratFb = findViewById(R.id.btnSuratFb);
        // navigationView = findViewById(R.id.nav_view);
        btnAya = findViewById(R.id.btnAya);
        btnBang = findViewById(R.id.btnBang);
        calender = findViewById(R.id.calender);
        Bayanyan = findViewById(R.id.Bayanyan);
        Ewara = findViewById(R.id.Ewaran);
        Xawtn = findViewById(R.id.xewtnan);

        SharedPreferences sharedPreferences3 = getSharedPreferences("pref", MODE_PRIVATE);
        boolean ShowOnStart = sharedPreferences3.getBoolean("show", true);

        if (ShowOnStart) {
            Agadari();
        }

        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        preferences = getSharedPreferences("Save", MODE_PRIVATE);
        editor = preferences.edit();
        hijrahDate = HijrahDate.now();

        formatter = DateTimeFormatter.ofPattern("dd");
        formatterM = DateTimeFormatter.ofPattern("MMMM");
        formatterY = DateTimeFormatter.ofPattern("yyyy");
        formatted = formatter.format(hijrahDate);
        formattedM = formatterM.format(hijrahDate);
        formattedY = formatterY.format(hijrahDate);

        String formattedM2 = formattedM
                .replace("Muharram", "مُحَرَّم")
                .replace("Safar", "صَفَر")


                .replace("Rabiʻ II", "رَبِيع ٱلْآخِر")
                .replace("Rabiʻ I", "رَبِيع ٱلْأَوَّل")


                .replace("Jumada II", "جُمَادَىٰ ٱلْآخِرَة")
                .replace("Jumada I", "جُمَادَىٰ ٱلْأُولَىٰ")

                .replace("Rajab", "رَجَب")
                .replace("Shaʻban", "شَعْبَان")

                .replace("Ramadan", "رَمَضَان")
                .replace("Shawwal", "شَوَّال")

                .replace("Dhuʻl-Qiʻdah", "ذُو ٱلْقَعْدَة")

                .replace("Dhuʻl-Hijjah", "ذُو ٱلْحِجَّة");

        String formatted2 = (formatted).replaceAll("/", " ")
                .replaceAll("1", "١")
                .replaceAll("2", "٢")
                .replaceAll("3", "٣")
                .replaceAll("4", "٤")
                .replaceAll("5", "٥")
                .replaceAll("6", "٦")
                .replaceAll("7", "٧")
                .replaceAll("8", "٨")
                .replaceAll("9", "٩")
                .replaceAll("0", "٠");

        String formattedY2 = (formattedY).replaceAll("/", " ")
                .replaceAll("1", "١")
                .replaceAll("2", "٢")
                .replaceAll("3", "٣")
                .replaceAll("4", "٤")
                .replaceAll("5", "٥")
                .replaceAll("6", "٦")
                .replaceAll("7", "٧")
                .replaceAll("8", "٨")
                .replaceAll("9", "٩")
                .replaceAll("0", "٠");

        sheetDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetStyle);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        View vi = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottomshet_dialog,
                findViewById(R.id.sheet_calnder));
        TextView tvH = vi.findViewById(R.id.tvHijriDate);
        tvH.setText(formatted2+ " ی "+formattedM2 + "ی "+formattedY2+ " ی هجری ");
        recycler = vi.findViewById(R.id.recyclerCalendar);

        currentCalendar = Calendar.getInstance();

        tvMonthYear = vi.findViewById(R.id.tvMonthYear);
        btnNext = vi.findViewById(R.id.btnNext);
        btnPrev = vi.findViewById(R.id.btnPrev);
        recycler = vi.findViewById(R.id.recyclerCalendar);


        TextView[] daysView = {
                vi.findViewById(R.id.day1),
                vi.findViewById(R.id.day2),
                vi.findViewById(R.id.day3),
                vi. findViewById(R.id.day4),
                vi.findViewById(R.id.day5),
                vi.findViewById(R.id.day6),
                vi.findViewById(R.id.day7)
        };

        String[] daysName = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};

        for (int i = 0; i < daysName.length; i++) {
            daysView[i].setText(daysName[i].substring(0,1));
        }

        recycler.setLayoutManager(new GridLayoutManager(this, 7));

        updateCalendar();

        btnNext.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        btnPrev.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });


        Calendar now = Calendar.getInstance();

        int currentYear = now.get(Calendar.YEAR);
        int currentMonth = now.get(Calendar.MONTH);

        List<DayModel> days = generateMonth(currentYear, currentMonth);

        CalendarAdapter calendarAdapter = new CalendarAdapter(this, days);

        recycler.setAdapter(calendarAdapter);


        sheetDialog.setContentView(vi);


        snackbar = Snackbar.make(drawerLayout, "", Snackbar.LENGTH_LONG);
        View customSnackView = getLayoutInflater().inflate(R.layout.toast_layout_done, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        @SuppressLint("RestrictedApi")
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 0);
        snackbarText = customSnackView.findViewById(R.id.txtToast);
        snackbarLayout.addView(customSnackView, 0);

        String url = "https://noor.pages.dev/";

        ThirdNightPrefs = getSharedPreferences("ThirdNightPrefs", MODE_PRIVATE);


        findViewById(R.id.btnThirdOfNight).setOnClickListener(v -> {

            sheetDialogThird = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetStyle);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"})
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottomshet_dialog_third_of_night,
                    findViewById(R.id.sheet_Third));
            TextView tvThirdOfNight = view.findViewById(R.id.tvThirdOfNight);
            TextView tvThirdOfNightHadith = view.findViewById(R.id.tvThirdOfNightHadith);
            @SuppressLint("UseSwitchCompatOrMaterialCode")
            Switch switchSendNotifi = view.findViewById(R.id.switchSendNotifi);

            tvThirdOfNightHadith.setOnClickListener(v1 -> {
                Intent intent = new Intent(MainActivity.this ,SearchHadithActivity.class );
                intent.putExtra("searchText","سییەکی شەو");
                startActivity(intent);
            });

            boolean ThirdNightIsChecked;
            ThirdNightIsChecked = ThirdNightPrefs.getBoolean("thirdNightSwitch",false);
            switchSendNotifi.setChecked(ThirdNightIsChecked);

            switchSendNotifi.setChecked(ThirdNightIsChecked);


            sheetDialogThird.setContentView(view);

            // 🔥 GET DATA FROM DATABASE
            ArrayList<Long> prayer = MydbClass.getTodayPrayers(true);

            if (prayer != null && prayer.size() >= 5) {

                // fajr
                Calendar fajrCal = Calendar.getInstance();
                fajrCal.setTimeInMillis(prayer.get(0));

                int fajrHour = fajrCal.get(Calendar.HOUR_OF_DAY);
                int fajrMin  = fajrCal.get(Calendar.MINUTE);

                // maghrib
                Calendar maghribCal = Calendar.getInstance();
                maghribCal.setTimeInMillis(prayer.get(4));

                int maghribHour = maghribCal.get(Calendar.HOUR_OF_DAY);
                int maghribMin  = maghribCal.get(Calendar.MINUTE);

                // 🔥 CALCULATE LAST THIRD
                String time = getLastThirdStart(maghribHour, maghribMin, fajrHour, fajrMin);

                tvThirdOfNight.setText("کاتژمێر "+time+" ی شەو");

            } else {
                tvThirdOfNight.setText("No data");
            }



            switchSendNotifi.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ThirdNightPrefs.edit().putBoolean("thirdNightSwitch", isChecked).apply();

                // 🔥 call schedule
                ScheduleNotification.scheduleThirdNightNotification(MainActivity.this, isChecked);
            });

            sheetDialogThird.show();


        });

        findViewById(R.id.menu_web).setOnClickListener(v -> {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            builder.setShowTitle(true);
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(MainActivity.this, Uri.parse(url));
            drawerLayout.closeDrawer(GravityCompat.END);
        });
        findViewById(R.id.menu_about).setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.END);
        });

        findViewById(R.id.menu_star).setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.googl.com/store/apps/details?id=" + getPackageName())));
            }
            drawerLayout.closeDrawer(GravityCompat.END);
        });

        findViewById(R.id.menu_setting).setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, AppSettings.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.END);
        });
        findViewById(R.id.menu_exit).setOnClickListener(v -> {
            onBackPressed();
            drawerLayout.closeDrawer(GravityCompat.END);
        });
        findViewById(R.id.menu_help).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HelpNoor.class);
            intent.putExtra("Activity", "MainActivity");
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.END);
        });

        findViewById(R.id.menu_share).setOnClickListener(v -> {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareSubject = "noor Application  Download now";
            String shareBode = " ئەپڵیکەیشنی نور  \n  لەڕێگەی ئەم لینکەوە دایبەزێنە *** \n";
            String shareBode1 = "https://play.google.com/store/apps/details?id=com.dya.noor";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBode + "\n" + shareBode1);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
            startActivity(Intent.createChooser(shareIntent, "share using"));
            drawerLayout.closeDrawer(GravityCompat.END);

        });


        calender.setOnClickListener(v -> {

            sheetDialog.show();
        });
        Bayanyan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuickZikrViewActivity.class);
            intent.putExtra("sura", "بەیانیان");
            intent.putExtra("id", "27");
            startActivity(intent);
        });
        Ewara.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuickZikrViewActivity.class);
            intent.putExtra("sura", "ئێواران");
            intent.putExtra("id", "28");
            startActivity(intent);
        });
        Xawtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuickZikrViewActivity.class);
            intent.putExtra("sura", "پێشخەوتن");
            intent.putExtra("id", "29");
            startActivity(intent);
        });
        btnBang.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActivityBangdan.class);
            startActivity(intent);
        });
        btnAya.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AyatKursyActivity.class);
            startActivity(intent);
        });




        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));


        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        String dayOfWe = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
        switch (Objects.requireNonNull(dayOfWe)) {
            case "Saturday": dayOfWeek = "شەمە"; break;
            case "Sunday": dayOfWeek = "یەک شەمە"; break;
            case "Monday": dayOfWeek = "دوو شەمە"; break;
            case "Tuesday": dayOfWeek = "سێ شەمە"; break;
            case "Wednesday": dayOfWeek = "چوار شەمە"; break;
            case "Thursday": dayOfWeek = "پێنج شەمە"; break;
            case "Friday": dayOfWeek = "هەینی"; break;
        }

        mDate = day + "/" + (month + 1) + "/" + year + "\n" + dayOfWeek;
        date2.setText(mDate);
        int mon = month + 1;
        if (mon > 9) {
            if (day <= 9) { mDate2 = (month + 1) + "-0" + day; }
            else { mDate2 = (month + 1) + "-" + day; }
        } else if (mon <= 9) {
            if (day <= 9) { mDate2 = "0" + (month + 1) + "-0" + day;
            }
            else { mDate2 = "0" + (month + 1) + "-" + day;
            }
        }

        if (getIntent().hasExtra("open_prayer") && getIntent().getBooleanExtra("open_prayer", false)) {
            startActivity(new Intent(this, ActivityPrayersTime.class));
        }
    }

    public static String getLastThirdStart(int maghribHour, int maghribMin,
                                           int fajrHour, int fajrMin) {

        int maghrib = maghribHour * 60 + maghribMin;
        int fajr = fajrHour * 60 + fajrMin;

        // fajr is next day

        if (fajr < maghrib){
            fajr+=24*60;
        }


        int nightDuration = fajr - maghrib;
        int third = nightDuration/3;
        int lastThirdStart = fajr - third;
        lastThirdStart = lastThirdStart%(24*60);
        int hour = lastThirdStart/60;
        int min = lastThirdStart%60;
        return String.format("%02d:%02d",hour,min);

    }

    private void updateCalendar() {

        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);

        // Update Month Title
        String monthName = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
                .format(currentCalendar.getTime());

        tvMonthYear.setText(monthName);

        // Generate days
        List<DayModel> days = generateMonth(year, month);

        calendarAdapter = new CalendarAdapter(this, days);
        recycler.setAdapter(calendarAdapter);
    }


    @SuppressLint("SimpleDateFormat")
    public void refreshTodayPrayerTimes() {

        if (fajr == null || dhuhr == null || asr == null
                || maghrib == null || isha == null) {
            setupPrayerViews();
        }

        if (fajr == null || dhuhr == null || asr == null
                || maghrib == null || isha == null) {
            return;
        }

        ArrayList<Long> prayer = MydbClass.getTodayPrayers(true);

        if (prayer != null && prayer.size() >= 6) {
            fajr.setText(new SimpleDateFormat("hh:mm").format(new Date(prayer.get(0))));
            dhuhr.setText(new SimpleDateFormat("hh:mm").format(new Date(prayer.get(2))));
            asr.setText(new SimpleDateFormat("hh:mm").format(new Date(prayer.get(3))));
            maghrib.setText(new SimpleDateFormat("hh:mm").format(new Date(prayer.get(4))));
            isha.setText(new SimpleDateFormat("hh:mm").format(new Date(prayer.get(5))));
        }
    }


    public void downloadFiles() {
        snackbarText.setText("داونلۆد دەستی پێکرد");
        snackbar.show();
        String urls = "https://github.com/DdGit0/tafser_tewhidiy/raw/main/t2.pdf";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urls));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.allowScanningByMediaScanner();
        request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOCUMENTS, "QuranTafseer.pdf");
        manager.enqueue(request);
    }

    public void downloadQuranFiles() {
        snackbarText.setText("داونلۆد دەستی پێکرد");
        snackbar.show();
        String urls = "https://github.com/DdGit0/quranPdf/raw/main/q1.pdf";
        DownloadManager.Request request = new
                DownloadManager.Request(Uri.parse(urls));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.allowScanningByMediaScanner();
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOCUMENTS, "Quran.pdf");
        downloadManager.enqueue(request);
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            file12 = new File(ctxt.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "QuranTafseer.pdf");
            if (file12.exists()) {

            }
        }
    };

    void Times() {
        setupPrayerViews();
    }


    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        Button btnYes, btnNo;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.close_dialog_layout, viewGroup, false);
        builder.setCancelable(false);
        builder.setView(view);
        btnYes = view.findViewById(R.id.btnYes);
        btnNo = view.findViewById(R.id.btnNo);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnYes.setOnClickListener(v -> {
            System.exit(0);
            super.onBackPressed();
        });
        btnNo.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }


    private void updatePrayerCountdown() {

        if (katymawa == null) {
            return;
        }

        PrayerInfo next =
                MydbClass.getNextPrayer();

        if (next == null) {
            katymawa.setText("");
            return;
        }

        long remaining =
                next.milli -
                        System.currentTimeMillis();

        if (remaining < 0) {
            remaining = 0;
        }

        int seconds =
                (int) (remaining / 1000);

        String text =
                Utils.formatSecondsToTimeKurdish(
                        seconds
                )
                        + "ماوە بۆ بانگی "
                        + next.prayerNameKurdish;

        katymawa.setText(text);

        updatePrayerColors(next);
    }
    private void updatePrayerColors(PrayerInfo next) {

        if (next == null) {
            return;
        }

        // Restore original colors first
        if (fajr != null && fajrOriginalColor != null) {
            fajr.setTextColor(fajrOriginalColor);
        }

        if (dhuhr != null && dhuhrOriginalColor != null) {
            dhuhr.setTextColor(dhuhrOriginalColor);
        }

        if (asr != null && asrOriginalColor != null) {
            asr.setTextColor(asrOriginalColor);
        }

        if (maghrib != null && maghribOriginalColor != null) {
            maghrib.setTextColor(maghribOriginalColor);
        }

        if (isha != null && ishaOriginalColor != null) {
            isha.setTextColor(ishaOriginalColor);
        }


        // =========================================================
        // NEXT PRAYER = YELLOW
        // =========================================================

        int yellow =
                ContextCompat.getColor(
                        this,
                        R.color.yellow
                );


        if (next.prayerNameKurdish == null) {
            return;
        }

        String name =
                next.prayerNameKurdish;


        if (name.contains("بەیانی")) {

            fajr.setTextColor(yellow);

        } else if (name.contains("نیوەڕۆ")) {

            dhuhr.setTextColor(yellow);

        } else if (name.contains("عەسر")) {

            asr.setTextColor(yellow);

        } else if (name.contains("ئێوارە")) {

            maghrib.setTextColor(yellow);

        } else if (name.contains("خەوتنان")) {

            isha.setTextColor(yellow);
        }
    }
    
    
    @SuppressLint("MissingInflatedId")
    public void Agadari() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        Button btnNo;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, viewGroup, false);
        builder.setCancelable(false);
        builder.setView(view);
        btnNo = view.findViewById(R.id.btnSetting);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnNo.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
        SharedPreferences sharedPreferences4 = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editorShow = sharedPreferences4.edit();
        editorShow.putBoolean("show", false);
        editorShow.apply();
    }

    @SuppressLint("MissingInflatedId")
    public void AgadariInternet() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        Button btnNo;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_no_internet_layout, viewGroup, false);
        builder.setCancelable(false);
        builder.setView(view);
        btnNo = view.findViewById(R.id.btnSetting);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnNo.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }

    @SuppressLint("MissingInflatedId")
    public void AgadariDasteLat() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        Button btnSetting, btnCancel, btnCloseAll;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_no_pirmesion_layout, viewGroup, false);
        builder.setCancelable(false);
        builder.setView(view);
        btnSetting = view.findViewById(R.id.btnSetting);
        btnCancel = view.findViewById(R.id.btnCancelDialo);
        btnCloseAll = view.findViewById(R.id.btnCloseAll);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnSetting.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AppSettings.class);
            startActivity(intent);
            alertDialog.dismiss();
        });
        btnCancel.setOnClickListener(v -> alertDialog.dismiss());
        btnCloseAll.setOnClickListener(v -> {
            numberOfOpens = 4;
            SharedPreferences.Editor prefeEditor = prefe.edit();
            prefeEditor.putInt("numberOfOpens", numberOfOpens);
            prefeEditor.apply();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private final InstallStateUpdatedListener installStateUpdatedListener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            showCompletedUpdate();
        }
    };

    @Override
    protected void onStop() {
        if (mAppUpdateManager != null)
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        super.onStop();
    }

    private void showCompletedUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "ڤێرژنی نوێ بەردەستە", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("دابەزاندن", view -> mAppUpdateManager.completeUpdate());
        snackbar.show();
    }

    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void saveBitmapToMediaStore(Context context, Bitmap bitmap, String name) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/NoorIcons");
        values.put(MediaStore.Images.Media.IS_PENDING, 1);
        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try {
            if (uri != null) {
                OutputStream out = resolver.openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                if (out != null) out.close();
                values.clear();
                values.put(MediaStore.Images.Media.IS_PENDING, 0);
                resolver.update(uri, values, null, null);
                Toast.makeText(context, "Saved: " + name, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RCP_AP_UPDATE && resultCode != RESULT_OK) {
            Toast.makeText(this, "ڕەتکردنەوە", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void checkAndRequestPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    listPermissionsNeeded.toArray(new String[0]),
                    PERMISSIONS_REQUEST_CODE
            );
        } else {

            checkStandardBatteryOptimizations();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            checkStandardBatteryOptimizations();
        }
    }


    public static void checkBatteryAndSpecialSettings(Context context) {
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        if ("huawei".equals(manufacturer)) {
            showHuaweiManualInstructionDialog(context);
            Toast.makeText(context, "Huawei device", Toast.LENGTH_SHORT).show();
        }
        else if ("samsung".equals(manufacturer)) {

            Toast.makeText(context, "تەنها بۆ مۆبایلە چینیەکان!", Toast.LENGTH_SHORT).show();
        }
        else {
            openManufacturerPowerManager(context);
        }
    }
    private static void openManufacturerPowerManager(Context context) {
        final Intent[] POWERMANAGER_INTENTS = {
                new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
                new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
                new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
                new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
                new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
                new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.activities.phoneoptimize.AddWhiteListActivity")),
                new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.activities.phoneoptimize.BgStartUpManager")),
                new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
                // new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.battery.activities.BatteryActivity")),
                new Intent().setComponent(new ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
                new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"))
        };
        Intent foundIntent = null;
        for (Intent intent : POWERMANAGER_INTENTS) {
            if (context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                foundIntent = intent;
                break;
            }
        }
        if (foundIntent != null) {
            final Intent finalFoundIntent = foundIntent;

            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_settings_dialog);
            dialog.setCancelable(true);
            // 🔥 This removes the system's default background
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            TextView title = dialog.findViewById(R.id.dialogTitle);
            TextView message = dialog.findViewById(R.id.dialogMessage);
            Button btnLater = dialog.findViewById(R.id.btnLater);
            Button btnSettings = dialog.findViewById(R.id.btnSettings);

            btnSettings.setOnClickListener(v -> {
                try {
                    context.startActivity(finalFoundIntent);
                } catch (Exception e) {
                    Log.e(TAG, "Could not open manufacturer specific settings", e);
                }
                dialog.dismiss();
            });

            btnLater.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        } else {
            Log.d(TAG, "No specific manufacturer settings found. Falling back to standard Android settings.");
        }

    }

    private static void showHuaweiManualInstructionDialog(Context context) {

        Dialog dialogs = new Dialog(context);
        dialogs.setContentView(R.layout.custom_settings_huawie_dialog);
        dialogs.setCancelable(true);

        // 🔥 This removes the system's default background
        if (dialogs.getWindow() != null) {
            dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView title = dialogs.findViewById(R.id.dialogTitle);
        TextView message = dialogs.findViewById(R.id.dialogMessage);
        Button btnLater = dialogs.findViewById(R.id.btnLater);
        Button btnSettings = dialogs.findViewById(R.id.btnSettings);

        dialogs.findViewById(R.id.btnTutorial).setOnClickListener(view -> {
            Intent intent = new Intent(context, Tutorial.class);
            context.startActivity(intent);
            dialogs.dismiss();
        });

        btnSettings.setOnClickListener(v -> {
            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
            dialogs.dismiss();
        });

        btnLater.setOnClickListener(v -> dialogs.dismiss());

        dialogs.show();

    }

    private void checkStandardBatteryOptimizations() {
        String pkg = getPackageName();
        PowerManager pm = getSystemService(PowerManager.class);
        if (pm != null && !pm.isIgnoringBatteryOptimizations(pkg)) {
            @SuppressLint("BatteryLife") Intent intent = new Intent(ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + pkg));
            startActivity(intent);

        } else {
            Log.d(TAG, "App is already ignoring battery optimizations.");
        }
    }



    private List<DayModel> generateMonth(int year, int month) {

        List<DayModel> list = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 🔵 previous month
        Calendar prevCal = Calendar.getInstance();
        prevCal.set(year, month - 1, 1);
        int daysInPrevMonth = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 🔵 add previous month days
        for (int i = firstDayOfWeek - 1; i >= 0; i--) {
            DayModel model = new DayModel();

            int day = daysInPrevMonth - i;

            model.day = day;
            model.month = month - 1;
            model.year = year;
            model.isCurrentMonth = false;

            list.add(model);
        }

        // 🟢 current month
        for (int day = 1; day <= daysInMonth; day++) {
            DayModel model = new DayModel();

            model.day = day;
            model.month = month;
            model.year = year;
            model.isCurrentMonth = true;

            Calendar today = Calendar.getInstance();
            model.isToday =
                    (day == today.get(Calendar.DAY_OF_MONTH) &&
                            month == today.get(Calendar.MONTH) &&
                            year == today.get(Calendar.YEAR));

            list.add(model);
        }

        // 🔵 next month
        int remaining = 35 - list.size(); // 6 rows * 7 days

        for (int i = 1; i <= remaining; i++) {
            DayModel model = new DayModel();

            model.day = i;
            model.month = month + 1;
            model.year = year;
            model.isCurrentMonth = false;

            list.add(model);
        }

        return list;
    }



    private boolean isInternetAvailable() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(
                        Context.CONNECTIVITY_SERVICE
                );

        if (cm == null) {
            return false;
        }

        Network network = cm.getActiveNetwork();

        if (network == null) {
            return false;
        }

        NetworkCapabilities capabilities =
                cm.getNetworkCapabilities(network);

        if (capabilities == null) {
            return false;
        }

        return capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
        );
    }


    private void checkWelcomeMessage() {

        // No internet = don't show anything
        if (!isInternetAvailable()) {
            return;
        }

        new Thread(() -> {

            HttpURLConnection connection = null;

            try {

                URL url = new URL(WELCOME_API);

                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setUseCaches(false);

                int responseCode = connection.getResponseCode();

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return;
                }

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream()
                                )
                        );

                StringBuilder response =
                        new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                JSONObject json =
                        new JSONObject(response.toString());

                boolean enabled =
                        json.optBoolean("enabled", false);

                if (!enabled) {
                    return;
                }

                String image =
                        json.optString("image", "");

                String title =
                        json.optString("title", "");

                String description =
                        json.optString("description", "");

                String link =
                        json.optString("link", "");

                runOnUiThread(() -> {

                    showWelcomeDialog(
                            image,
                            title,
                            description,
                            link
                    );

                });

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }

        }).start();
    }

    private void showWelcomeDialog(String imageUrl, String title, String description, String link) {

        View view = getLayoutInflater()
                .inflate(R.layout.dialog_welcome, null);

        ImageView image =
                view.findViewById(R.id.welcomeImage);

        TextView titleView =
                view.findViewById(R.id.welcomeTitle);

        TextView descriptionView =
                view.findViewById(R.id.welcomeDescription);

        MaterialButton button =
                view.findViewById(R.id.welcomeButton);

        TextView close =
                view.findViewById(R.id.welcomeClose);

        TextView closeX =
                view.findViewById(R.id.welcomeCloseX);


        // --------------------------------
        // RTL
        // --------------------------------

        view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        // --------------------------------
        // API DATA
        // --------------------------------

        titleView.setText(title);
        descriptionView.setText(description);


        // --------------------------------
        // IMAGE
        // --------------------------------

        if (!imageUrl.isEmpty()) {

            Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .into(image);
        }


        // --------------------------------
        // DIALOG
        // --------------------------------

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setView(view)
                        .create();


        // --------------------------------
        // OPEN LINK
        // --------------------------------

        button.setOnClickListener(v -> {

            if (!link.isEmpty()) {

                try {

                    Intent intent =
                            new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(link)
                            );

                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            dialog.dismiss();
        });


        // --------------------------------
        // CLOSE
        // --------------------------------

        close.setOnClickListener(v ->
                dialog.dismiss()
        );

        closeX.setOnClickListener(v ->
                dialog.dismiss()
        );


        // --------------------------------
        // SHOW
        // --------------------------------

        dialog.show();


        Window window = dialog.getWindow();

        if (window != null) {

            window.setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );

            window.setDimAmount(0.72f);

            window.setLayout(
                    (int) (
                            getResources()
                                    .getDisplayMetrics()
                                    .widthPixels * 0.90f
                    ),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }


        // --------------------------------
        // ANIMATION
        // --------------------------------

        view.setAlpha(0f);
        view.setScaleX(0.88f);
        view.setScaleY(0.88f);
        view.setTranslationY(40f);

        view.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(350)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();
    }


    private void loadHomeSlider() {

        // ---------------------------------------------------------
        // NO INTERNET
        // ---------------------------------------------------------

        if (!isInternetAvailable()) {

            sliderHandler.removeCallbacks(
                    sliderRunnable
            );

            homeSlider.setCurrentItem(
                    0,
                    false
            );

            return;
        }


        // ---------------------------------------------------------
        // LOAD API
        // ---------------------------------------------------------

        sliderExecutor.execute(() -> {

            HttpURLConnection connection = null;

            try {

                URL url =
                        new URL(SLIDER_API);

                connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setRequestMethod("GET");

                connection.setConnectTimeout(
                        8000
                );

                connection.setReadTimeout(
                        8000
                );

                connection.setUseCaches(false);

                connection.setRequestProperty(
                        "Accept",
                        "application/json"
                );


                int responseCode =
                        connection.getResponseCode();


                if (responseCode !=
                        HttpURLConnection.HTTP_OK) {

                    return;
                }


                InputStream inputStream =
                        connection.getInputStream();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        inputStream
                                )
                        );


                StringBuilder result =
                        new StringBuilder();

                String line;

                while ((line =
                        reader.readLine()) != null) {

                    result.append(line);
                }


                reader.close();
                inputStream.close();


                JSONObject json =
                        new JSONObject(
                                result.toString()
                        );


                boolean enabled =
                        json.optBoolean(
                                "enabled",
                                false
                        );


                if (!enabled) {

                    runOnUiThread(() -> {

                        sliderHandler.removeCallbacks(
                                sliderRunnable
                        );

                        homeSlider.setCurrentItem(
                                0,
                                false
                        );
                    });

                    return;
                }


                JSONArray slides =
                        json.optJSONArray(
                                "slides"
                        );


                if (slides == null ||
                        slides.length() == 0) {

                    return;
                }


                // -------------------------------------------------
                // UPDATE UI
                // -------------------------------------------------

                runOnUiThread(() -> {

                    sliderAdapter.setSlides(slides);

                    createSliderDots();

                    homeSlider.setCurrentItem(
                            0,
                            false
                    );

                    updateSliderDots(0);

                    sliderHandler.removeCallbacks(
                            sliderRunnable
                    );

                    if (sliderAdapter.getItemCount() > 1) {

                        sliderHandler.postDelayed(
                                sliderRunnable,
                                5000
                        );
                    }
                });


            } catch (Exception e) {

                Log.e(
                        "HOME_SLIDER",
                        "Slider error",
                        e
                );

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }


    private void setupPrayerViews() {

        if (sliderAdapter == null) {
            return;
        }

        View prayerView = sliderAdapter.getPrayerView();

        if (prayerView == null) {
            Log.e("HOME_SLIDER", "Prayer view is null");
            return;
        }

        // ---------------------------------------------------------
        // PRAYER CARD VIEWS
        // These views are inside item_prayer_card.xml, NOT activity_main.xml.
        // ---------------------------------------------------------
        date2 = prayerView.findViewById(R.id.date2);
        time2 = prayerView.findViewById(R.id.time2);

        fajr = prayerView.findViewById(R.id.fajr2);
        dhuhr = prayerView.findViewById(R.id.dhuhr2);
        asr = prayerView.findViewById(R.id.asr2);
        maghrib = prayerView.findViewById(R.id.maghrib2);
        isha = prayerView.findViewById(R.id.isha2);

        katymawa = prayerView.findViewById(R.id.katymawa);
        mCity = prayerView.findViewById(R.id.city);
        btnThirty = prayerView.findViewById(R.id.btnThirty);
        cardPrayerTimeView = prayerView.findViewById(R.id.cardPrayerTimeView);


        // Save the original colors from item_prayer_card.xml
        fajrOriginalColor = fajr.getTextColors();
        dhuhrOriginalColor = dhuhr.getTextColors();
        asrOriginalColor = asr.getTextColors();
        maghribOriginalColor = maghrib.getTextColors();
        ishaOriginalColor = isha.getTextColors();

        // ---------------------------------------------------------
        // CITY
        // ---------------------------------------------------------
        if (mCity != null) {
            mCity.setText(CityNameKurdish);

            mCity.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, CitiesSetting.class);
                intent.putExtra("act", "main");
                startActivity(intent);
            });
        }

        // ---------------------------------------------------------
        // 30 DAYS BUTTON
        // ---------------------------------------------------------
        if (btnThirty != null) {
            btnThirty.setOnClickListener(v -> {
                Intent intent = new Intent(
                        MainActivity.this,
                        ActivityThirtyDayPrayerTime.class
                );

                intent.putExtra("City", CityNameEnglish);
                intent.putExtra("City2", CityNameKurdish);
                startActivity(intent);
            });
        } else {
            Log.e(
                    "HOME_SLIDER",
                    "btnThirty was not found inside item_prayer_card.xml"
            );
        }// ---------------------------------------------------------
        if (cardPrayerTimeView != null) {
            cardPrayerTimeView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ActivityPrayersTime.class
                );
                startActivity(intent);
            });
        }
    }

    private void createSliderDots() {

        if (sliderDots == null || sliderAdapter == null) {
            return;
        }

        sliderDots.removeAllViews();

        // Prayer page is position 0.
        // Only API slides need dots.
        int slideCount =
                sliderAdapter.getItemCount() - 1;

        if (slideCount <= 0) {
            sliderDots.setVisibility(View.GONE);
            return;
        }

        sliderDots.setVisibility(View.VISIBLE);

        for (int i = 0; i < slideCount; i++) {

            View dot = new View(this);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            dpToPx(7),
                            dpToPx(7)
                    );

            params.setMargins(
                    dpToPx(4),
                    0,
                    dpToPx(4),
                    0
            );

            dot.setLayoutParams(params);

            dot.setBackground(
                    createDotDrawable(
                            i == 0
                    )
            );

            sliderDots.addView(dot);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void updateSliderDots(int position) {

        if (sliderDots == null) {
            return;
        }

        int slideIndex = position - 1;

        int slideCount = sliderDots.getChildCount();

        for (int i = 0; i < slideCount; i++) {

            View dot = sliderDots.getChildAt(i);

            boolean selected = i == slideIndex;

            animateDot(dot, selected);
        }
    }


    private void animateDot(View dot, boolean selected) {

        float targetWidth =
                dpToPx(selected ? 18 : 7);

        float targetAlpha =
                selected ? 1f : 0.55f;

        dot.animate()
                .alpha(targetAlpha)
                .setDuration(220)
                .setInterpolator(
                        new DecelerateInterpolator()
                )
                .start();

        ViewGroup.LayoutParams lp =
                dot.getLayoutParams();

        int currentWidth =
                dot.getWidth();

        if (currentWidth <= 0) {
            currentWidth =
                    dpToPx(7);
        }

        ValueAnimator animator =
                ValueAnimator.ofInt(
                        currentWidth,
                        (int) targetWidth
                );

        animator.setDuration(220);

        animator.setInterpolator(
                new DecelerateInterpolator()
        );

        animator.addUpdateListener(
                animation -> {

                    ViewGroup.LayoutParams params =
                            dot.getLayoutParams();

                    params.width =
                            (int) animation.getAnimatedValue();

                    dot.setLayoutParams(params);
                }
        );

        animator.start();

        GradientDrawable background =
                createDotDrawable(selected);

        dot.setBackground(background);
    }


    private GradientDrawable createDotDrawable(boolean active) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setShape(
                GradientDrawable.OVAL
        );

        if (active) {

            drawable.setColor(
                    DOT_ACTIVE_COLOR
            );

        } else {

            drawable.setColor(
                    DOT_INACTIVE_COLOR
            );
        }

        return drawable;
    }



    @Override
    protected void onResume() {

        super.onResume();

        isActive = true;

        // Make sure prayer views exist
        setupPrayerViews();

        // Update next prayer
        MydbClass.setNextPrayer(this);

        // Update today's prayer times
        refreshTodayPrayerTimes();

        // Start countdown
        prayerHandler.removeCallbacks(
                prayerCountdownRunnable
        );

        prayerHandler.post(
                prayerCountdownRunnable
        );


        // Restart home slider
        if (sliderAdapter != null
                && sliderAdapter.getItemCount() > 1) {

            sliderHandler.removeCallbacks(
                    sliderRunnable
            );

            sliderHandler.postDelayed(
                    sliderRunnable,
                    5000
            );
        }


        powerManager =
                getSystemService(PowerManager.class);

        if (!powerManager.isIgnoringBatteryOptimizations(pkgs)
                || !android.provider.Settings.canDrawOverlays(this)) {

            linearLayoutGotoSeeting.setVisibility(
                    View.VISIBLE
            );

        } else {

            linearLayoutGotoSeeting.setVisibility(
                    View.GONE
            );
        }
    }

    @Override
    protected void onPause() {

        isActive = false;

        prayerHandler.removeCallbacks(
                prayerCountdownRunnable
        );

        sliderHandler.removeCallbacks(
                sliderRunnable
        );

        super.onPause();
    }
    
    @Override
    protected void onDestroy() {

        sliderHandler.removeCallbacks(sliderRunnable);

        if (sliderExecutor != null) {
            sliderExecutor.shutdownNow();
        }

        super.onDestroy();
    }
}
