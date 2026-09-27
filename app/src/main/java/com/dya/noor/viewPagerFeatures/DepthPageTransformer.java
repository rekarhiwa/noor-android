package com.dya.noor.viewPagerFeatures;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import org.jspecify.annotations.NonNull;

public class DepthPageTransformer implements ViewPager2.PageTransformer {

    @Override
    public void transformPage(@NonNull View page, float position) {

        page.setCameraDistance(20000);

        if (position < -1) {
            page.setAlpha(0f);
        } else if (position <= 0) {
            page.setAlpha(1f);
            page.setTranslationX(0f);
            page.setScaleX(-1f);
        } else if (position <= 1) {
            page.setAlpha(1 - position);
            page.setTranslationX(page.getWidth() * -position);
            page.setScaleX(-1 - 0.2f * position);
        } else {
            page.setAlpha(0f);
        }
    }
}
