package com.dya.noor.activities;

import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;

public class Tutorial extends BaseActivity {
    ImageView gifArabic , gifEnglish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        gifArabic = findViewById(R.id.gifArabic);
        gifEnglish = findViewById(R.id.gifEnglish);

        Glide.with(this)
                .asGif()
                .load(R.raw.english_gif)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(26))) // radius in px
                .into(gifEnglish);

        Glide.with(this)
                .asGif()
                .load(R.raw.arabic_gif)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(26))) // radius in px
                .into(gifArabic);
        findViewById(R.id.sBack).setOnClickListener(view -> {
            onBackPressed();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            findViewById(R.id.hScroll).setVerticalScrollbarThumbDrawable(getDrawable(R.drawable.scroll_thumb));
        }


    }
}