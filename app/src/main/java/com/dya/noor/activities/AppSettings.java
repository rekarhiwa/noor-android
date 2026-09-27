package com.dya.noor.activities;

import static android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;
import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class AppSettings extends BaseActivity {



    TextView tvB , tvE , tvActic , tvActic2 ,tvhijaz , tvrad
    , tvpeshewa ,tvbast,tvislam , tvActicNotfi;
    ImageButton btntvB , btntvE ,btntvhijaz , btntvrad , btntvpeshewa ,btntvbast,btntvislam ;
    int AnimationDuration = 2000;
    ImageButton sBack ;
    LinearLayout linearLayout , quranSize ,textSize;
    SharedPreferences sharedPreferences ;
    SharedPreferences sharedPreferencesSize ;
    SharedPreferences.Editor editor ;
    SharedPreferences.Editor editorSize ;
    TextView btnQuranSizeMinus ,btnQuranSizePlus,txtTestQuran, txtQuranSizeValue , btnTextSizePlus ,
            btnTextSizeMinus , txtTextSizeValue , txtTestText , tvAlarm,tvChina ,tvHuawei;
    String bangs ="";
    int quranSizeNum , textSizeNum  ;
    int TextColor;
    int TextColorBlack ;

    static PowerManager powerManager;
    static String pkgs;
    AlarmManager alarmManager;

    private MediaPlayer mediaPlayer;
    private ImageButton currentPlayingButton; // Track the currently playing button
    private int currentSoundResId = -1; // Track the currently playing sound

    LinearLayout DangLayout,PirmessionLayout,textSizLayout;

    public static String activityName ="";

    @SuppressLint({"MissingInflatedId", "SetTextI18n", "BatteryLife"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

         TextColor = ContextCompat.getColor(this, R.color.textColorWhite);
         TextColorBlack = ContextCompat.getColor(this, R.color.textColorBlack);

        sBack = findViewById(R.id.sBack);
        tvChina = findViewById(R.id.tvChina);
        tvB = findViewById(R.id.tvB);
        tvE = findViewById(R.id.tvE);
        tvActic = findViewById(R.id.tvActicShasha);
        tvActic2 = findViewById(R.id.tvActiveBattery);
        tvActicNotfi = findViewById(R.id.tvActicNotfi);
        tvAlarm = findViewById(R.id.tvAlarm);

        tvhijaz = findViewById(R.id.tvhijaz);
        tvrad = findViewById(R.id.tvrad);
        tvpeshewa= findViewById(R.id.tvpeshewa);
        tvbast = findViewById(R.id.tvbast);
        tvislam = findViewById(R.id.tvislam);

        btntvB = findViewById(R.id.btntvB);
        btntvE = findViewById(R.id.btntvE);
        btntvhijaz = findViewById(R.id.btntvhijaz);
        btntvrad = findViewById(R.id.btntvrad);
        btntvpeshewa= findViewById(R.id.btntvpeshewa);
        btntvbast = findViewById(R.id.btntvbast);
        btntvislam = findViewById(R.id.btntvislam);
        tvHuawei = findViewById(R.id.tvHuawei);


        DangLayout= findViewById(R.id.DangLayout);
        PirmessionLayout= findViewById(R.id.PirmessionLayout);
        textSizLayout= findViewById(R.id.textSizLayout);

        pkgs =getPackageName();
        powerManager =getSystemService(PowerManager.class);
        alarmManager   = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        if (Objects.equals(activityName, "sura")){
            PirmessionLayout.setVisibility(View.GONE);
            DangLayout.setVisibility(View.GONE);
        }else {
            PirmessionLayout.setVisibility(View.VISIBLE);
            DangLayout.setVisibility(View.VISIBLE);
        }


        tvChina.setOnClickListener(view -> {
            MainActivity.checkBatteryAndSpecialSettings(AppSettings.this);

        });

        tvHuawei.setOnClickListener(view -> {
            startActivity(new Intent(AppSettings.this, Tutorial.class));

        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            tvActicNotfi.setVisibility(View.VISIBLE);
        }else {
            tvActicNotfi.setVisibility(View.GONE);
        }
        linearLayout = findViewById(R.id.linearLayout);

        quranSize = findViewById(R.id.quranSize);
        textSize = findViewById(R.id.textSize);

        txtTestQuran = findViewById(R.id.txtTestQuran);
        txtTestText = findViewById(R.id.txtTestText);

        txtQuranSizeValue = findViewById(R.id.txtQuranSizeValue);
        btnQuranSizeMinus = findViewById(R.id.btnQuranSizeMinus);
        btnQuranSizePlus = findViewById(R.id.btnQuranSizePlus);

        btnTextSizePlus = findViewById(R.id.btnTextSizePlus);
        btnTextSizeMinus = findViewById(R.id.btnTextSizeMinus);
        txtTextSizeValue = findViewById(R.id.txtTextSizeValue);

        // texts size
        sharedPreferencesSize = getSharedPreferences("size",MODE_PRIVATE);
        editorSize = sharedPreferencesSize.edit();

        quranSizeNum = sharedPreferencesSize.getInt("quransize", 27);

        textSizeNum = sharedPreferencesSize.getInt("textSize", 20);

        txtQuranSizeValue.setText(String.valueOf(quranSizeNum));
        txtTestQuran.setTextSize(TypedValue.COMPLEX_UNIT_SP, quranSizeNum); // Set the text size in scaled pixels (sp)

        txtTextSizeValue.setText(String.valueOf(textSizeNum));
        txtTestText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeNum); // Set the text size in scaled pixels (sp)

        btnQuranSizePlus.setOnClickListener(v -> {
            if (quranSizeNum<38){
                quranSizeNum++;
                editorSize.putInt("quransize",quranSizeNum);
                editorSize.apply();
                txtQuranSizeValue.setText(String.valueOf(quranSizeNum));
                txtTestQuran.setTextSize(TypedValue.COMPLEX_UNIT_SP, quranSizeNum); // Set the text size in scaled pixels (sp)
            }

        });
        btnQuranSizeMinus.setOnClickListener(v -> {
            if (quranSizeNum>10){
                quranSizeNum--;
                editorSize.putInt("quransize",quranSizeNum);
                editorSize.apply();
                txtQuranSizeValue.setText(String.valueOf(quranSizeNum));
                txtTestQuran.setTextSize(TypedValue.COMPLEX_UNIT_SP, quranSizeNum); // Set the text size in scaled pixels (sp)

            }



        });


        btnTextSizePlus.setOnClickListener(v -> {
            if (textSizeNum<38){

                textSizeNum++;
                editorSize.putInt("textSize",textSizeNum);
                editorSize.apply();

                txtTextSizeValue.setText(String.valueOf(textSizeNum));
                txtTestText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeNum); // Set the text size in scaled pixels (sp)
            }

        });
        btnTextSizeMinus.setOnClickListener(v -> {
            if (textSizeNum>10){

                textSizeNum--;
                editorSize.putInt("textSize",textSizeNum);
                editorSize.apply();

                txtTextSizeValue.setText(String.valueOf(textSizeNum));
                txtTestText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeNum); // Set the text size in scaled pixels (sp)

            }



        });

        // create an instance of the snackbar
        final Snackbar snackbar = Snackbar.make( linearLayout,"", Snackbar.LENGTH_LONG);

        // inflate the custom_snackbar_view created previously
        @SuppressLint("InflateParams")
        View customSnackView = getLayoutInflater().inflate(R.layout.toast_layout_done, null);

        // set the background of the default snackbar as transparent
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);

        // now change the layout of the snackbar
        @SuppressLint("RestrictedApi")
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

        // set padding of the all corners as 0
        snackbarLayout.setPadding(0, 0, 0, 0);

        // register the button from the custom_snackbar_view layout file
        TextView  snackbarText = customSnackView.findViewById(R.id.txtToast);

        // add the custom snack bar layout to snackbar layout
        snackbarLayout.addView(customSnackView, 0);


      //  snackbarText.setText("کارا کراوە");
      //  snackbar.show();


        sBack.setOnClickListener(v -> onBackPressed());

        sharedPreferences = getSharedPreferences("key",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        bangs = sharedPreferences.getString("bang", "1");
        tvActicNotfi.setOnClickListener(v ->
                {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                            PackageManager.PERMISSION_DENIED) {
                        // Open the notification permission settings.
                        Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        settingsIntent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        startActivity(settingsIntent);

                    }else {
                        snackbarText.setText("کارا کراوە");
                        snackbar.show();
                    }
                 }
                );
        tvAlarm.setOnClickListener(v ->
                {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()){

                            snackbarText.setText("کارا کراوە");
                            snackbar.show();

                        }
                        else {
                            startActivity(new Intent( ACTION_REQUEST_SCHEDULE_EXACT_ALARM , Uri.parse("package:"+pkgs)));

                        }
                    }

                }
                );


        tvB.setOnClickListener(v ->
                {

                     editor.putString("bang", "1");
                     editor.putString("name", "بانگی کورت");
                     editor.apply();
                    snackbarText.setText("بانگی کورت جێگیرکرا");
                    snackbar.show();
                    tvB.setBackgroundResource(R.drawable.backgrownd_done);
                    tvE.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvhijaz.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvrad.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvpeshewa.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvbast.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvislam .setBackgroundResource(R.drawable.backgrownd_btn_taf);

                    tvB.setTextColor(TextColor);
                    tvE.setTextColor(TextColorBlack);
                    tvhijaz.setTextColor(TextColorBlack);
                    tvrad.setTextColor(TextColorBlack);
                    tvpeshewa.setTextColor(TextColorBlack);
                    tvbast.setTextColor(TextColorBlack);
                    tvislam .setTextColor(TextColorBlack);

                }
                );


        tvE.setOnClickListener(v ->
                {
                    editor.putString("bang", "2");
                    editor.putString("name", "بانگی تەواو");
                    editor.apply();
                    snackbarText.setText("بانگی تەواو جێگیرکرا");
                    snackbar.show();

                    tvB.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvE.setBackgroundResource(R.drawable.backgrownd_done);
                    tvhijaz.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvrad.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvpeshewa.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvbast.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvislam .setBackgroundResource(R.drawable.backgrownd_btn_taf);



                    tvB.setTextColor(TextColorBlack);
                    tvE.setTextColor(TextColor);
                    tvhijaz.setTextColor(TextColorBlack);
                    tvrad.setTextColor(TextColorBlack);
                    tvpeshewa.setTextColor(TextColorBlack);
                    tvbast.setTextColor(TextColorBlack);
                    tvislam .setTextColor(TextColorBlack);


                }
                );

        tvhijaz .setOnClickListener(v ->
                {

                    editor.putString("bang", "3");
                    editor.putString("name", "بانگی حیجاز");
                    editor.apply();
                    snackbarText.setText("بانگی حیجاز جێگیرکرا");
                    snackbar.show();

                    tvB.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvE.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvhijaz.setBackgroundResource(R.drawable.backgrownd_done);
                    tvrad.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvpeshewa.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvbast.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvislam .setBackgroundResource(R.drawable.backgrownd_btn_taf);


                    tvB.setTextColor(TextColorBlack);
                    tvE.setTextColor(TextColorBlack);
                    tvhijaz.setTextColor(TextColor);
                    tvrad.setTextColor(TextColorBlack);
                    tvpeshewa.setTextColor(TextColorBlack);
                    tvbast.setTextColor(TextColorBlack);
                    tvislam .setTextColor(TextColorBlack);

                }
        );





        tvrad.setOnClickListener(v ->
                {

                    editor.putString("bang", "4");
                    editor.putString("name", "رعد محمد");
                    editor.apply();
                    snackbarText.setText("رعد محمد جێگیرکرا");
                    snackbar.show();

                    tvB.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvE.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvhijaz.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvrad.setBackgroundResource(R.drawable.backgrownd_done);
                    tvpeshewa.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvbast.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvislam .setBackgroundResource(R.drawable.backgrownd_btn_taf);

                    tvB.setTextColor(TextColorBlack);
                    tvE.setTextColor(TextColorBlack);
                    tvhijaz.setTextColor(TextColorBlack);
                    tvrad.setTextColor(TextColor);
                    tvpeshewa.setTextColor(TextColorBlack);
                    tvbast.setTextColor(TextColorBlack);
                    tvislam .setTextColor(TextColorBlack);


                }
        );
        tvpeshewa.setOnClickListener(v ->
                {

                    editor.putString("bang", "5");
                    editor.putString("name", "پێشەوا قادر");
                    editor.apply();
                    snackbarText.setText("پێشەوا قادر جێگیرکرا");
                    snackbar.show();


                    tvB.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvE.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvhijaz.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvrad.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvpeshewa.setBackgroundResource(R.drawable.backgrownd_done);
                    tvbast.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvislam .setBackgroundResource(R.drawable.backgrownd_btn_taf);

                    tvB.setTextColor(TextColorBlack);
                    tvE.setTextColor(TextColorBlack);
                    tvhijaz.setTextColor(TextColorBlack);
                    tvrad.setTextColor(TextColorBlack);
                    tvpeshewa.setTextColor(TextColor);
                    tvbast.setTextColor(TextColorBlack);
                    tvislam .setTextColor(TextColorBlack);


                }
        );
        tvbast.setOnClickListener(v ->
                {
                    editor.putString("bang", "6");
                    editor.putString("name", "عبد الباصت عبد الصمد");
                    editor.apply();
                    snackbarText.setText("عبد الباصت عبد الصمد جێگیرکرا");
                    snackbar.show();


                    tvB.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvE.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvhijaz.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvrad.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvpeshewa.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvbast.setBackgroundResource(R.drawable.backgrownd_done);
                    tvislam .setBackgroundResource(R.drawable.backgrownd_btn_taf);


                    tvB.setTextColor(TextColorBlack);
                    tvE.setTextColor(TextColorBlack);
                    tvhijaz.setTextColor(TextColorBlack);
                    tvrad.setTextColor(TextColorBlack);
                    tvpeshewa.setTextColor(TextColorBlack);
                    tvbast.setTextColor(TextColor);
                    tvislam .setTextColor(TextColorBlack);


                }
        );
        tvislam .setOnClickListener(v ->
                {
                    editor.putString("bang", "7");
                    editor.putString("name", "اسلام سبحی");
                    editor.apply();
                    snackbarText.setText("اسلام سبحی");
                    snackbar.show();
                    tvB.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvE.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvhijaz.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvrad.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvpeshewa.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvbast.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                    tvislam .setBackgroundResource(R.drawable.backgrownd_done);

                    tvB.setTextColor(TextColorBlack);
                    tvE.setTextColor(TextColorBlack);
                    tvhijaz.setTextColor(TextColorBlack);
                    tvrad.setTextColor(TextColorBlack);
                    tvpeshewa.setTextColor(TextColorBlack);
                    tvbast.setTextColor(TextColorBlack);
                    tvislam .setTextColor(TextColor);

                }
        );

///

        // **Button Click Listeners**
        btntvB.setOnClickListener(v -> sound(btntvB, R.raw.bang2));
        btntvE.setOnClickListener(v -> sound(btntvE, R.raw.bang_dan));
        btntvhijaz.setOnClickListener(v -> sound(btntvhijaz, R.raw.hijaz_azan));
        btntvrad.setOnClickListener(v -> sound(btntvrad, R.raw.rad_muhammad_alkurdi));
        btntvpeshewa.setOnClickListener(v -> sound(btntvpeshewa, R.raw.peshawa_qadir));
        btntvbast.setOnClickListener(v -> sound(btntvbast, R.raw.abdul_basit_abdul_samad));
        btntvislam.setOnClickListener(v -> sound(btntvislam, R.raw.islam_sobhi));

        ///

        tvActic.setOnClickListener(v -> {

            if (!Settings.canDrawOverlays(this)) {

                    startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));

                }else {
                snackbarText.setText("کارا کراوە");
                snackbar.show();

            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WAKE_LOCK}, 225);
            }else {
                snackbarText.setText("کارا کراوە");
                snackbar.show();

            }

        });
        tvActic2.setOnClickListener(v -> {


            String pkg=getPackageName();
            PowerManager pm=getSystemService(PowerManager.class);

            if (!pm.isIgnoringBatteryOptimizations(pkg)) {
                startActivity(new Intent(ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:"+pkg)));


            }else {
                snackbarText.setText("کارا کراوە");
                snackbar.show();
            }
        });



    }
    public void updateAthinName(){


        switch (bangs) {
            case "1":
                tvB.setBackgroundResource(R.drawable.backgrownd_done);
                tvE.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvhijaz.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvrad.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvpeshewa.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvbast.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvislam .setBackgroundResource(R.drawable.backgrownd_btn_taf);

                tvB.setTextColor(TextColor);
                tvE.setTextColor(TextColorBlack);
                tvhijaz.setTextColor(TextColorBlack);
                tvrad.setTextColor(TextColorBlack);
                tvpeshewa.setTextColor(TextColorBlack);
                tvbast.setTextColor(TextColorBlack);
                tvislam .setTextColor(TextColorBlack);
                break;
            case "2":
                tvB.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvE.setBackgroundResource(R.drawable.backgrownd_done);
                tvhijaz.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvrad.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvpeshewa.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvbast.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvislam .setBackgroundResource(R.drawable.backgrownd_btn_taf);



                tvB.setTextColor(TextColorBlack);
                tvE.setTextColor(TextColor);
                tvhijaz.setTextColor(TextColorBlack);
                tvrad.setTextColor(TextColorBlack);
                tvpeshewa.setTextColor(TextColorBlack);
                tvbast.setTextColor(TextColorBlack);
                tvislam .setTextColor(TextColorBlack);

                break;
            case "3":


                tvB.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvE.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvhijaz.setBackgroundResource(R.drawable.backgrownd_done);
                tvrad.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvpeshewa.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvbast.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvislam .setBackgroundResource(R.drawable.backgrownd_btn_taf);


                tvB.setTextColor(TextColorBlack);
                tvE.setTextColor(TextColorBlack);
                tvhijaz.setTextColor(TextColor);
                tvrad.setTextColor(TextColorBlack);
                tvpeshewa.setTextColor(TextColorBlack);
                tvbast.setTextColor(TextColorBlack);
                tvislam .setTextColor(TextColorBlack);

                break;
            case "4":
                tvB.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvE.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvhijaz.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvrad.setBackgroundResource(R.drawable.backgrownd_done);
                tvpeshewa.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvbast.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvislam .setBackgroundResource(R.drawable.backgrownd_btn_taf);

                tvB.setTextColor(TextColorBlack);
                tvE.setTextColor(TextColorBlack);
                tvhijaz.setTextColor(TextColorBlack);
                tvrad.setTextColor(TextColor);
                tvpeshewa.setTextColor(TextColorBlack);
                tvbast.setTextColor(TextColorBlack);
                tvislam .setTextColor(TextColorBlack);

                break;
            case "5":

                tvB.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvE.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvhijaz.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvrad.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvpeshewa.setBackgroundResource(R.drawable.backgrownd_done);
                tvbast.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvislam .setBackgroundResource(R.drawable.backgrownd_btn_taf);

                tvB.setTextColor(TextColorBlack);
                tvE.setTextColor(TextColorBlack);
                tvhijaz.setTextColor(TextColorBlack);
                tvrad.setTextColor(TextColorBlack);
                tvpeshewa.setTextColor(TextColor);
                tvbast.setTextColor(TextColorBlack);
                tvislam .setTextColor(TextColorBlack);

                break;

            case "6":


                tvB.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvE.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvhijaz.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvrad.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvpeshewa.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvbast.setBackgroundResource(R.drawable.backgrownd_done);
                tvislam .setBackgroundResource(R.drawable.backgrownd_btn_taf);


                tvB.setTextColor(TextColorBlack);
                tvE.setTextColor(TextColorBlack);
                tvhijaz.setTextColor(TextColorBlack);
                tvrad.setTextColor(TextColorBlack);
                tvpeshewa.setTextColor(TextColorBlack);
                tvbast.setTextColor(TextColor);
                tvislam .setTextColor(TextColorBlack);

                break;
            case "7":

                tvB.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvE.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvhijaz.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvrad.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvpeshewa.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvbast.setBackgroundResource(R.drawable.backgrownd_btn_taf);
                tvislam .setBackgroundResource(R.drawable.backgrownd_done);

                tvB.setTextColor(TextColorBlack);
                tvE.setTextColor(TextColorBlack);
                tvhijaz.setTextColor(TextColorBlack);
                tvrad.setTextColor(TextColorBlack);
                tvpeshewa.setTextColor(TextColorBlack);
                tvbast.setTextColor(TextColorBlack);
                tvislam .setTextColor(TextColor);

                break;
        }




    }

    public void sound(ImageButton btn, int soundResId) {
        // If the same button is clicked while playing, stop and reset it
        if (mediaPlayer != null && currentPlayingButton == btn) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                btn.setBackgroundResource(R.drawable.ic_play); // Reset to play icon
                currentPlayingButton = null;
                currentSoundResId = -1;
                return; // Exit function
            }
        }

        // Stop and release previous sound if different sound is played
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;

            if (currentPlayingButton != null) {
                currentPlayingButton.setBackgroundResource(R.drawable.ic_play); // Reset previous button
            }
        }

        // Create and start the new MediaPlayer
        mediaPlayer = MediaPlayer.create(this, soundResId);
        mediaPlayer.start();

        // Update the button background
        btn.setBackgroundResource(R.drawable.ic_pause); // Change to pause icon
        currentPlayingButton = btn;
        currentSoundResId = soundResId;

        // Release MediaPlayer when the sound finishes
        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
            mediaPlayer = null;
            btn.setBackgroundResource(R.drawable.ic_play);
            currentPlayingButton = null;
            currentSoundResId = -1;
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onResume() {
        super.onResume();
        updateAthinName();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_DENIED) {
            tvActicNotfi.setBackgroundResource(R.drawable.btn_no_bg);
            tvActicNotfi .setTextColor(TextColor);


        }else {
            tvActicNotfi.setBackgroundResource(R.drawable.backgrownd_done);
            tvActicNotfi .setTextColor(TextColor);
        }
        if (!android.provider.Settings.canDrawOverlays(this)) {
            tvActic.setBackgroundResource(R.drawable.btn_no_bg);
            tvActic .setTextColor(TextColor);
        }else {
            tvActic.setBackgroundResource(R.drawable.backgrownd_done);
            tvActic .setTextColor(TextColor);
        }

      /*  if (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {
            tvActic.setBackgroundResource(R.drawable.btn_no_bg);
            tvActic .setTextColor(TextColor);
        }else {
            tvActic.setBackgroundResource(R.drawable.backgrownd_done);
            tvActic .setTextColor(TextColor);

        }

       */


        String pkg2=getPackageName();
        PowerManager pm2=getSystemService(PowerManager.class);

        if (!pm2.isIgnoringBatteryOptimizations(pkg2)) {
            tvActic2.setBackgroundResource(R.drawable.btn_no_bg);
            tvActic2 .setTextColor(TextColor);
        }else {
            tvActic2.setBackgroundResource(R.drawable.backgrownd_done);
            tvActic2 .setTextColor(TextColor);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {


            if (!alarmManager.canScheduleExactAlarms()) {
                tvAlarm.setBackgroundResource(R.drawable.btn_no_bg);
                tvAlarm.setTextColor(TextColor);
            } else {
                tvAlarm.setBackgroundResource(R.drawable.backgrownd_done);
                tvAlarm.setTextColor(TextColor);
            }
        }else {

            tvAlarm.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mediaPlayer != null){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();

        }}
        finish();
        activityName ="";
    }
}