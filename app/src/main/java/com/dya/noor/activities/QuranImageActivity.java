package com.dya.noor.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager2.widget.ViewPager2;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.ViewPagerAdapter;
import com.dya.noor.adapters.ViewPagerKhatmAdapter;
import com.dya.noor.database.MydbClass;
import com.dya.noor.utlis.QuranPageUtils;
import com.dya.noor.viewPagerFeatures.DepthPageTransformer;

import java.util.Objects;

public class QuranImageActivity extends BaseActivity implements ViewPagerAdapter.SurahNameListener, ViewPagerKhatmAdapter.SurahNameListener {

    ViewPager2 viewPager;
    int id ;
    String folderName = "quranPaage"; // Replace with your actual folder name
    ViewPagerAdapter adapter;
    ViewPagerKhatmAdapter khatmAdapter;

    ImageView btnBack ,
            btnSave;
    TextView txtNamesTop;

    private static final String PREFS_NAME = "QuranPagePreferences";
    private static final String KEY_SAVED_PAGE = "savedPage";
    private static final String KEY_SAVED_ID = "savedId";
    private static final String KEY_SAVED_SURAH_NAME = "savedSurahName";
    private static String ACtNAme = "";
    SharedPreferences khatmPreferences ;
    SharedPreferences.Editor khatmEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran_image);



        viewPager = findViewById(R.id.ViewPager);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        txtNamesTop = findViewById(R.id.txtNamesTop);
        // Create ViewPagerAdapter with your folder name

         khatmPreferences = getSharedPreferences("Khatm", MODE_PRIVATE);
         khatmEditor = khatmPreferences.edit();

        btnBack.setOnClickListener(v -> {



            onBackPressed();
        });

        adapter = new ViewPagerAdapter(this, folderName ,this);


        khatmAdapter = new ViewPagerKhatmAdapter(this, folderName ,this);

        // Extract page number from intent
        Intent intent = getIntent();
        // id =Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("id")));
        id =intent.getIntExtra("id",1);
        ACtNAme = intent.getStringExtra("ACTName");

        // Get the actual image count
        int actualImageCount = adapter.calculateImageCount(folderName);


        int requestedPage = id* 5; // Adjust multiplier if pages per Surah differ
        // Set initial page within bounds

        viewPager.setCurrentItem(Math.min(requestedPage - QuranPageUtils.pageNumber, actualImageCount - 1), false);
       if (Objects.equals(ACtNAme, "Khatm")){
           viewPager.setAdapter(khatmAdapter);

       }else {
           viewPager.setAdapter(adapter);
       }
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        viewPager.setScaleX(-1);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // Update the Surah name when the page changes
                int currentPageNumber = position + QuranPageUtils.pageNumber;

                MydbClass mydbClass = new MydbClass(QuranImageActivity.this);
                String surahName = mydbClass.getSurahNameByPage(currentPageNumber);

                txtNamesTop.setText(surahName.isEmpty() ? "Unknown Surah" : surahName);
            }
        });

        viewPager.setPageTransformer(new DepthPageTransformer());

        btnSave.setOnClickListener(v -> saveCurrentPage());

    }

    @Override
    public void onSurahNameChanged(String surahName) {
        txtNamesTop.setText(surahName);
    }

    private void saveCurrentPage() {


        if (Objects.equals(ACtNAme, "Khatm")) {
            onBackPressed();
        } else {


            int currentPage = viewPager.getCurrentItem() + QuranPageUtils.pageNumber;
            String currentSurahName = txtNamesTop.getText().toString();

            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_SAVED_PAGE, String.valueOf(currentPage));
            editor.putInt(KEY_SAVED_ID, id);
            editor.putString(KEY_SAVED_SURAH_NAME, currentSurahName);
            editor.apply();

            Toast.makeText(this, " کۆتا خوێندنەوە لاپەڕەی " + currentPage + " " + currentSurahName, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onBackPressed() {

        if (Objects.equals(ACtNAme, "Khatm")){
            // Get the current visible page in ViewPager
            int currentPage = QuranPageUtils.pageNumber + viewPager.getCurrentItem();

            khatmEditor.putInt("lastReadPage", currentPage);
            khatmEditor.apply();
        }


        super.onBackPressed();
    }
}