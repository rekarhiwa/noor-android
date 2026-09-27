package com.dya.noor.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;

public class YoutubeChuser extends BaseActivity {

    ImageButton back;
    CardView nor_quran,nor;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_chuser);

        back = findViewById(R.id.back);
        nor_quran = findViewById(R.id.nor_quran);
        nor = findViewById(R.id.nor);


        back.setOnClickListener(v -> onBackPressed());

        nor_quran.setOnClickListener(view -> {

             url="https://youtube.com/channel/UCd9AeVetvPLO8tAxSObg_ZA";
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            builder.setShowTitle(true);
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(YoutubeChuser.this, Uri.parse(url));

        });
        nor.setOnClickListener(view -> {
            url="https://www.youtube.com/channel/UCIPW54WrawTSD8VJA5pR8Mw";
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            builder.setShowTitle(true);
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(YoutubeChuser.this, Uri.parse(url));


        });


    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}