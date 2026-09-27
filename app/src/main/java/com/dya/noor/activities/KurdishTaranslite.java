package com.dya.noor.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.PdfAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class KurdishTaranslite extends BaseActivity {

    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    int pageNumber = 0;

    ImageButton back , retry;
    private RecyclerView pdfRecyclerView;
    private PdfAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kurdish_taranslite);
        prefs = getSharedPreferences("MyPrefsKrd", MODE_PRIVATE);
        editor = getSharedPreferences("MyPrefsKrd", MODE_PRIVATE).edit();


        back = findViewById(R.id.back);
        retry = findViewById(R.id.retry);
        back.setOnClickListener(v -> onBackPressed());

        pdfRecyclerView = findViewById(R.id.pdfRecyclerView);
        // Set the LayoutManager to scroll horizontally
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        pdfRecyclerView.setLayoutManager(layoutManager);

// Add a SnapHelper for page-by-page snapping effect
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(pdfRecyclerView);


        retry.setOnClickListener(v ->{
            // Scroll the RecyclerView to the first item, which is position 0.
            if (pdfRecyclerView != null) {
                pdfRecyclerView.scrollToPosition(0);
            }
        });




        pageNumber = prefs.getInt("pageNumber", 0);



        try {
            // We need to copy the PDF from assets to a file in the app's cache directory
            File pdfFile = copyAssetToFile("kurdish_transliteration.pdf");

            adapter = new PdfAdapter(this, pdfFile);
            pdfRecyclerView.setAdapter(adapter);
            // ✅ ADD THIS CODE HERE to restore the page
            pageNumber = prefs.getInt("pageNumber", 0); // Get the saved page, default to 0
            if (pageNumber > 0) {
                pdfRecyclerView.scrollToPosition(pageNumber);
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Handle error, e.g., show a toast message
        }
    }

    private File copyAssetToFile(String assetName) throws IOException {
        File cacheFile = new File(getCacheDir(), assetName);
        if (cacheFile.exists()) {
            return cacheFile; // Return the file if it's already been copied
        }

        InputStream inputStream = getAssets().open(assetName);
        FileOutputStream outputStream = new FileOutputStream(cacheFile);
        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
        inputStream.close();
        outputStream.flush();
        outputStream.close();
        return cacheFile;
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
    protected void onDestroy() {
        super.onDestroy();
        // IMPORTANT: Close the adapter to release the PdfRenderer and file descriptor
        if (adapter != null) {
            adapter.close();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}