package com.dya.noor.splash_screen;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.dya.noor.R;
import com.dya.noor.database.MydbClass;
import com.dya.noor.activities.ActivityPrayersTime;
import com.dya.noor.activities.HelpNoor;
import com.dya.noor.activities.MainActivity;
import com.dya.noor.utlis.Utils;
import com.dya.noor.widget.SalatWidget;
import com.dya.noor.widget.SalatWidgetVertical;
import com.makeramen.roundedimageview.RoundedImageView;


@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    RoundedImageView imageView;
    Animation fromTop, logoAnim , textAnim;
    MydbClass mydbClass;
    Intent intent;

    static PowerManager powerManager;
    static String pkgs;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Boolean openFirst;
    TextView txtNameApp;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        pkgs =getPackageName();
        powerManager =getSystemService(PowerManager.class);
        AlarmManager alarmManager   = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        logoAnim = AnimationUtils.loadAnimation(this, R.anim.splash_logo_anim);
        textAnim = AnimationUtils.loadAnimation(this, R.anim.splash_text_anim);

        sharedPreferences = getSharedPreferences("OpenFirst", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        openFirst = sharedPreferences.getBoolean("OpenFirst", true);


        mydbClass = new MydbClass(this);
        mydbClass.StartWork();
        new Utils(this);
        Utils.activeContext = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            if (alarmManager.canScheduleExactAlarms()) {
            MydbClass.setNextPrayer(this);
            SalatWidget.updateAllWidgetViews(this);
            SalatWidgetVertical.updateAllWidgetViews(this);


        }
        }
        else {
            MydbClass.setNextPrayer(this);
            SalatWidget.updateAllWidgetViews(this);
            SalatWidgetVertical.updateAllWidgetViews(this);

        }




        //bakardet bo goryny rangy status bar wata shryty nyshandany shabaka u sha7n
        /** Window window =this.getWindow();
         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
         window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
         window.setStatusBarColor(this.getResources().getColor(R.color.SplashScreenColor));
         */

        imageView = findViewById(R.id.cardeView);
        txtNameApp = findViewById(R.id.txtNameApp);
        fromTop = AnimationUtils.loadAnimation(this, R.anim.frometope);

        imageView.startAnimation(logoAnim);
        txtNameApp.startAnimation(textAnim);

        Thread splashThread = new Thread(() -> {
            try {
                Thread.sleep(1200);


                if (openFirst){
                    intent = new Intent(getApplicationContext(), HelpNoor.class);
                    intent.putExtra("Activity", "SplashScreen");
                    editor.putBoolean("OpenFirst", false);
                    editor.apply();
                }
                else {
                    if (getIntent().hasExtra("open_prayer")) {
                        Log.d("HAX","has open prayer.");
                        intent = new Intent(getApplicationContext(), ActivityPrayersTime.class);
                        intent.putExtra("open_prayer", getIntent().getBooleanExtra("open_prayer", false));


                    }
                    else {
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("dowmload", "f");
                    }
                }




                startActivity(intent);
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        splashThread.start();

    }
}