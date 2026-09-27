package com.dya.noor.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.Compass.CompassClass;
import com.dya.noor.Compass.Constants;
import com.dya.noor.Compass.GPSTracker;
import com.dya.noor.R;

import java.util.Locale;

public class CompassActivity extends BaseActivity {

    private static final String TAG =
            CompassActivity.class.getSimpleName();

    private static final int RC_Permission = 1221;

    private CompassClass compass;

    private ImageView qiblatIndicator;
    private ImageView imageDial;
    private ImageView back;

    private TextView tvYourLocation;
    private TextView tvQiblaAngle;
    private TextView tvQiblaDirection;

    private float currentAzimuth;

    private SharedPreferences prefs;
    private SharedPreferences sharedPreferences;

    private GPSTracker gps;

    private boolean nightMod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compass);


        // ---------------------------------------------------------
        // Night mode
        // ---------------------------------------------------------

        sharedPreferences =
                getSharedPreferences(
                        "MODE",
                        Context.MODE_PRIVATE
                );

        nightMod = sharedPreferences.getBoolean("nightMod", false
        );

        if (nightMod) {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        // ---------------------------------------------------------
        // Views
        // ---------------------------------------------------------

        back = findViewById(R.id.back);

        qiblatIndicator = findViewById(R.id.qibla_indicator);

        imageDial = findViewById(R.id.dial);

        tvYourLocation = findViewById(R.id.your_location);

        tvQiblaAngle = findViewById(R.id.QiblaAngle);

        tvQiblaDirection = findViewById(R.id.tvQiblaDirection);


        // ---------------------------------------------------------
        // Back button
        // ---------------------------------------------------------

        if (back != null) {

            back.setOnClickListener(v ->
                    onBackPressed());
        }


        // ---------------------------------------------------------
        // Preferences
        // ---------------------------------------------------------

        prefs = getSharedPreferences("", MODE_PRIVATE);


        // ---------------------------------------------------------
        // Keep screen on
        // ---------------------------------------------------------

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        // ---------------------------------------------------------
        // Initially hide Qibla arrow
        // ---------------------------------------------------------

        if (qiblatIndicator != null) {

            qiblatIndicator.setVisibility(
                    View.GONE
            );
        }


        // ---------------------------------------------------------
        // Apply custom drawable settings
        // ---------------------------------------------------------

        setUserChanges(getIntent());


        // ---------------------------------------------------------
        // Start compass
        // ---------------------------------------------------------

        setupCompass();
    }


    // =============================================================
    // Activity lifecycle
    // =============================================================

    @Override
    public void onStart() {
        super.onStart();

        Log.d(
                TAG,
                "start compass"
        );

        /*
         * GPSTracker may have been released in onStop().
         * Create it again when Activity starts.
         */
        if (gps == null) {

            gps = new GPSTracker(this);
        }


        if (compass != null) {

            compass.start(this);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        /*
         * Make sure GPS object exists.
         */
        if (gps == null) {

            gps = new GPSTracker(this);
        }


        /*
         * Check location again when returning
         * from Android location settings.
         */
        if (hasLocationPermission()) {

            getBearing();
        }


        if (compass != null) {

            compass.start(this);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (compass != null) {

            compass.stop();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        Log.d(
                TAG,
                "stop compass"
        );

        if (compass != null) {

            compass.stop();
        }

        if (gps != null) {

            gps.stopUsingGPS();
            gps = null;
        }
    }


    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {

        super.onBackPressed();

        finish();
        overridePendingTransition(0, 0);
    }


    // =============================================================
    // Set custom compass drawables
    // =============================================================

    private void setUserChanges(Intent intent) {

        try {

            if (intent == null) {
                return;
            }


            // -----------------------------------------------------
            // Compass dial
            // -----------------------------------------------------

            if (imageDial != null) {

                imageDial.setImageResource(

                        intent.getExtras() != null
                                && intent.getExtras()
                                .containsKey(
                                        Constants.DRAWABLE_DIAL
                                )

                                ? intent.getExtras()
                                .getInt(
                                        Constants.DRAWABLE_DIAL
                                )

                                : R.drawable.compass_dail
                );
            }


            // -----------------------------------------------------
            // Qibla indicator
            // -----------------------------------------------------

            if (qiblatIndicator != null) {

                qiblatIndicator.setImageResource(

                        intent.getExtras() != null
                                && intent.getExtras()
                                .containsKey(
                                        Constants.DRAWABLE_QIBLA
                                )

                                ? intent.getExtras()
                                .getInt(
                                        Constants.DRAWABLE_QIBLA
                                )

                                : R.drawable.compass_qibla
                );
            }


            // -----------------------------------------------------
            // Location text visibility
            // -----------------------------------------------------

            if (tvYourLocation != null) {

                tvYourLocation.setVisibility(

                        intent.getExtras() != null && intent.getExtras()
                                .containsKey(Constants.LOCATION_TEXT_VISIBLE)
                                ? intent.getExtras()
                                .getInt(Constants.LOCATION_TEXT_VISIBLE)
                                : View.VISIBLE);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    // =============================================================
    // Compass setup
    // =============================================================

    private void setupCompass() {

        /*
         * Check the real Android permission instead of
         * relying only on SharedPreferences.
         */
        if (hasLocationPermission()) {

            SaveBoolean(
                    "permission_granted",
                    true
            );

            getBearing();

        } else {

            tvYourLocation.setText(R.string.location_access_not_available_yet);

            requestLocationPermission();
        }


        // ---------------------------------------------------------
        // Compass sensor
        // ---------------------------------------------------------

        compass = new CompassClass(this);


        CompassClass.CompassListener cl =
                azimuth -> {

                    adjustGambarDial(
                            azimuth
                    );

                    adjustArrowQiblat(
                            azimuth
                    );
                };


        compass.setListener(cl);
    }


    // =============================================================
    // Location permission
    // =============================================================

    private boolean hasLocationPermission() {

        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

                ||

                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;
    }


    private void requestLocationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    RC_Permission
            );

        } else {

            fetch_GPS();
        }
    }


    // =============================================================
    // Rotate compass dial
    // =============================================================

    public void adjustGambarDial(float azimuth) {

        if (imageDial == null) {
            return;
        }


        Animation animation =
                new RotateAnimation(

                        -currentAzimuth,
                        -azimuth,

                        Animation.RELATIVE_TO_SELF,
                        0.5f,

                        Animation.RELATIVE_TO_SELF,
                        0.5f
                );


        currentAzimuth = azimuth;


        animation.setDuration(500);

        animation.setRepeatCount(0);

        animation.setFillAfter(true);


        imageDial.startAnimation(
                animation
        );
    }


    // =============================================================
    // Rotate Qibla arrow
    // =============================================================

    public void adjustArrowQiblat(float azimuth) {

        if (qiblatIndicator == null) {
            return;
        }


        float kiblatDerajat =
                GetFloat(
                        "kiblat_derajat"
                );


        Animation animation =
                new RotateAnimation(

                        -currentAzimuth
                                + kiblatDerajat,

                        -azimuth,

                        Animation.RELATIVE_TO_SELF,
                        0.5f,

                        Animation.RELATIVE_TO_SELF,
                        0.5f
                );


        currentAzimuth = azimuth;


        animation.setDuration(500);

        animation.setRepeatCount(0);

        animation.setFillAfter(true);


        qiblatIndicator.startAnimation(
                animation
        );


        if (kiblatDerajat > 0) {

            qiblatIndicator.setVisibility(View.VISIBLE);

        } else {

            qiblatIndicator.setVisibility(View.GONE);
        }
    }


    // =============================================================
    // Get bearing / existing Qibla calculation
    // =============================================================

    @SuppressLint({
            "MissingPermission",
            "SetTextI18n"
    })
    public void getBearing() {

        if (!hasLocationPermission()) {

            return;
        }


        if (gps == null) {

            gps = new GPSTracker(this);
        }


        float kaabaDegs =
                GetFloat(
                        "kiblat_derajat"
                );


        /*
         * If we already have a saved Qibla angle,
         * show it immediately.
         */
        if (kaabaDegs > 0.0001f) {

            updateQiblaAngleUI(
                    kaabaDegs
            );
        }


        /*
         * Get current location.
         */
        if (gps.getLocation() != null) {

            double latitude =
                    gps.getLocation()
                            .getLatitude();

            double longitude =
                    gps.getLocation()
                            .getLongitude();


            updateLocationText(
                    latitude,
                    longitude
            );


            /*
             * Calculate fresh Qibla angle.
             */
            calculateQibla(
                    latitude,
                    longitude
            );

        } else {

            fetch_GPS();
        }
    }


    // =============================================================
    // Update Qibla angle UI
    // =============================================================

    private void updateQiblaAngleUI(
            float qiblaDegrees
    ) {

        if (tvQiblaAngle != null) {

            tvQiblaAngle.setText(String.format(Locale.US, "%.1f°", qiblaDegrees));
        }


        if (tvQiblaDirection != null) {

            tvQiblaDirection.setText(getDirectionString(qiblaDegrees));
        }


        if (qiblatIndicator != null) {

            qiblatIndicator.setVisibility(
                    View.VISIBLE
            );
        }
    }


    // =============================================================
    // Update location UI
    // =============================================================

    private void updateLocationText(
            double latitude,
            double longitude
    ) {

        if (tvYourLocation == null) {
            return;
        }


        String locationText =
                String.format(
                        Locale.US,
                        "%.5f, %.5f",
                        latitude,
                        longitude
                );


        tvYourLocation.setText(
                locationText
        );
    }


    // =============================================================
    // Calculate Qibla direction
    // =============================================================

    private void calculateQibla(
            double myLat,
            double myLng
    ) {

        /*
         * Kaaba coordinates
         */
        double kaabaLng =
                39.826206;

        double kaabaLat =
                Math.toRadians(
                        21.422487
                );


        double myLatRad =
                Math.toRadians(
                        myLat
                );


        double longDiff =
                Math.toRadians(
                        kaabaLng - myLng
                );


        double y =
                Math.sin(longDiff)
                        * Math.cos(kaabaLat);


        double x =
                Math.cos(myLatRad)
                        * Math.sin(kaabaLat)

                        -

                        Math.sin(myLatRad)
                                * Math.cos(kaabaLat)
                                * Math.cos(longDiff);


        float result =
                (float)
                        (
                                Math.toDegrees(
                                        Math.atan2(
                                                y,
                                                x
                                        )
                                )
                                        + 360
                        )
                        % 360;


        /*
         * Save result
         */
        SaveFloat(
                "kiblat_derajat",
                result
        );


        /*
         * Update UI
         */
        updateQiblaAngleUI(
                result
        );


        Log.d(
                TAG,
                "Qibla angle: " + result
        );
    }


    // =============================================================
    // Direction text
    // =============================================================

    private String getDirectionString(
            float azimuthDegrees
    ) {

        if (azimuthDegrees >= 350
                || azimuthDegrees <= 10) {

            return "باکور";
        }


        if (azimuthDegrees < 350
                && azimuthDegrees > 280) {

            return "باکوری ڕۆژئاوا";
        }


        if (azimuthDegrees <= 280
                && azimuthDegrees > 260) {

            return "ڕۆژئاوا";
        }


        if (azimuthDegrees <= 260
                && azimuthDegrees > 190) {

            return "باشوری ڕۆژئاوا";
        }


        if (azimuthDegrees <= 190
                && azimuthDegrees > 170) {

            return "باشور";
        }


        if (azimuthDegrees <= 170
                && azimuthDegrees > 100) {

            return "باشوری ڕۆژهەڵات";
        }


        if (azimuthDegrees <= 100
                && azimuthDegrees > 80) {

            return "ڕۆژهەڵات";
        }


        return "باکوری ڕۆژهەڵات";
    }


    // =============================================================
    // GPS
    // =============================================================

    @SuppressLint({
            "MissingPermission",
            "SetTextI18n"
    })
    public void fetch_GPS() {

        if (!hasLocationPermission()) {

            return;
        }


        if (gps == null) {

            gps = new GPSTracker(this);
        }


        if (gps.canGetLocation()) {

            double myLat =
                    gps.getLatitude();

            double myLng =
                    gps.getLongitude();


            Log.d(
                    TAG,
                    "GPS latitude: " + myLat
            );

            Log.d(
                    TAG,
                    "GPS longitude: " + myLng
            );


            /*
             * Check if GPS returned a usable location.
             *
             * Do NOT use:
             *
             * myLat < 0.001 && myLng < 0.001
             *
             * because valid coordinates can be negative.
             */
            if (Math.abs(myLat) < 0.000001
                    && Math.abs(myLng) < 0.000001) {

                if (qiblatIndicator != null) {

                    qiblatIndicator.setVisibility(
                            View.GONE
                    );
                }

                if (tvYourLocation != null) {

                    tvYourLocation.setText(
                            "شوێن ئامادە نییە"
                    );
                }

                return;
            }


            // -----------------------------------------------------
            // Show location
            // -----------------------------------------------------

            updateLocationText(
                    myLat,
                    myLng
            );


            // -----------------------------------------------------
            // Calculate Qibla
            // -----------------------------------------------------

            calculateQibla(
                    myLat,
                    myLng
            );


            Log.d(
                    TAG,
                    "GPS is ON"
            );

        } else {

            if (qiblatIndicator != null) {

                qiblatIndicator.setVisibility(
                        View.GONE
                );
            }


            if (tvYourLocation != null) {

                tvYourLocation.setText(
                        "تکایە شوێنەکەت چالاک بکە"
                );
            }


            /*
             * Open Android Location settings.
             */
            gps.showSettingsAlert();
        }
    }


    // =============================================================
    // Permission result
    // =============================================================

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );


        if (requestCode != RC_Permission) {

            return;
        }


        if (hasLocationPermission()) {

            SaveBoolean(
                    "permission_granted",
                    true
            );


            if (tvYourLocation != null) {

                tvYourLocation.setText(
                        "شوێن دیاری دەکرێت..."
                );
            }


            if (qiblatIndicator != null) {

                qiblatIndicator.setVisibility(
                        View.GONE
                );
            }


            /*
             * Recreate GPS tracker.
             */
            gps =
                    new GPSTracker(this);


            fetch_GPS();

        } else {

            Toast.makeText(
                    getApplicationContext(),
                    "بۆ بەکارهێنانی قیبڵەنما دەبێت دەستگەیشتن بە شوێن بدەیت",
                    Toast.LENGTH_LONG
            ).show();


            finish();
        }
    }


    // =============================================================
    // SharedPreferences - Boolean
    // =============================================================

    public void SaveBoolean(
            String key,
            Boolean value
    ) {

        if (prefs == null) {

            prefs =
                    getSharedPreferences(
                            "",
                            MODE_PRIVATE
                    );
        }


        SharedPreferences.Editor edit =
                prefs.edit();


        edit.putBoolean(
                key,
                value
        );


        edit.apply();
    }


    public Boolean GetBoolean(
            String key
    ) {

        if (prefs == null) {

            prefs =
                    getSharedPreferences(
                            "",
                            MODE_PRIVATE
                    );
        }


        return prefs.getBoolean(
                key,
                false
        );
    }


    // =============================================================
    // SharedPreferences - Float
    // =============================================================

    public void SaveFloat(
            String key,
            Float value
    ) {

        if (prefs == null) {

            prefs =
                    getSharedPreferences(
                            "",
                            MODE_PRIVATE
                    );
        }


        SharedPreferences.Editor edit =
                prefs.edit();


        edit.putFloat(
                key,
                value
        );


        edit.apply();
    }


    public Float GetFloat(
            String key
    ) {

        if (prefs == null) {

            prefs =
                    getSharedPreferences(
                            "",
                            MODE_PRIVATE
                    );
        }


        return prefs.getFloat(
                key,
                0
        );
    }


    // =============================================================
    // Menu
    // =============================================================

    @Override
    public boolean onCreateOptionsMenu(
            Menu menu
    ) {

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(
            MenuItem item
    ) {

        return super.onOptionsItemSelected(
                item
        );
    }
}