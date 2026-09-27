package com.dya.noor.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.PdfAdapter;

import java.io.File;

public class ActivityQuranPdf extends BaseActivity {

    SharedPreferences.Editor editor;
    SharedPreferences prefs;
   // PDFView pdfView;
    int pageNumber = 0;

    ImageButton back , retry;
    private RecyclerView pdfRecyclerView;
    private PdfAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran_pdf);
      //  pdfView = findViewById(R.id.pdfView);
        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();


        back = findViewById(R.id.back);
        retry = findViewById(R.id.retry);
        back.setOnClickListener(v -> onBackPressed());

        retry.setOnClickListener(v -> {
            // Scroll the RecyclerView to the first item, which is position 0.
            if (pdfRecyclerView != null) {
                pdfRecyclerView.scrollToPosition(0);
            }
        });


     /** pageNumber = prefs.getInt("pageNumber", 0);
        pdfView.fromFile(new File(getExternalFilesDir(null) + "/DOCUMENTS/Quran.pdf"))
                .defaultPage(pageNumber)
                .onLoad(nbPages -> pdfView.fromFile(new File(getExternalFilesDir(null) + "/DOCUMENTS/Quran.pdf"))
                        .defaultPage(pageNumber)
                        .enableSwipe(true)
                        .enableDoubletap(true)
                        .swipeHorizontal(true)
                        .pageSnap(true)
                        .spacing(5)
                        .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
                        .fitEachPage(true)
                        .autoSpacing(true)
                        .pageFling(true)
                        .load())
                .load();

      */


        pdfRecyclerView = findViewById(R.id.pdfRecyclerView);
        // Set the LayoutManager to scroll horizontally
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        pdfRecyclerView.setLayoutManager(layoutManager);

// Add a SnapHelper for page-by-page snapping effect
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(pdfRecyclerView);

        // --- ✅ GET FILE FROM DEVICE STORAGE ---
        // 1. Get the standard "Documents" folder for your app.
        File documentsFolder = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        // 2. Create a File object pointing to your PDF inside that folder.
        File pdfFile = new File(documentsFolder, "Quran.pdf");


        // --- LOAD THE PDF ---
        if (pdfFile.exists()) {
            adapter = new PdfAdapter(this, pdfFile);
            pdfRecyclerView.setAdapter(adapter);

            // ✅ ADD THIS CODE HERE to restore the page
             pageNumber = prefs.getInt("pageNumber", 0); // Get the saved page, default to 0
            if (pageNumber > 0) {
                pdfRecyclerView.scrollToPosition(pageNumber);
            }
        } else {
            // Show an error message if the PDF file is not found.
            Toast.makeText(this, "Quran.pdf not found in storage!", Toast.LENGTH_LONG).show();
            finish(); // Close the activity if the file doesn't exist.
        }





    }
    @Override
    protected void onPause() {
        super.onPause();

        // ✅ ADD THIS CODE to save the current page
        LinearLayoutManager layoutManager = (LinearLayoutManager) pdfRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            // Find the position of the page currently on screen
             pageNumber = layoutManager.findFirstVisibleItemPosition();

            // Save it to SharedPreferences
            if (editor != null) {
                editor.putInt("pageNumber", pageNumber);
                editor.apply();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // The saving is now done in onPause(), so we don't need code here anymore.
        super.onBackPressed();
    }
}