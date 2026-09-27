package com.dya.noor.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.HadithAdapter;
import com.dya.noor.module.HadithRow;
import com.dya.noor.database.MydbClass;
import com.dya.noor.module.HadithItem;

import java.util.ArrayList;
import java.util.List;

public class HadithActivity extends BaseActivity {

    MydbClass mydbClass;
    RecyclerView hadithRecyclerView;
    HadithAdapter adapter;
    List<HadithRow> rows;

    String selectedBookName;
    int kitabId;
    int scrollPosition = -1;
    int targetHadithId = -1;

    TextView kitabName;
    String selectedKitabName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hadith);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mydbClass = new MydbClass(this);
        rows = new ArrayList<>();

        kitabId = getIntent().getIntExtra("kitab_id",0);
        targetHadithId = getIntent().getIntExtra("Hadith_id",0);
        selectedBookName = getIntent().getStringExtra("book_name");
        selectedKitabName = getIntent().getStringExtra("kitab_name");

        hadithRecyclerView = findViewById(R.id.hadithRecyclerView);
        kitabName = findViewById(R.id.kitabName);
        kitabName.setText(selectedKitabName);


        findViewById(R.id.btnBack).setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
        loadHadithWithChapters(kitabId);

        adapter = new HadithAdapter(this, selectedBookName,rows);
        hadithRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        hadithRecyclerView.setAdapter(adapter);

        scrollToPosition();

        adapter.notifyDataSetChanged();

    }

    void scrollToPosition(){

        if (scrollPosition != -1) {
            hadithRecyclerView.post(() -> {

                hadithRecyclerView.scrollToPosition(scrollPosition);

                hadithRecyclerView.postDelayed(() -> {
                    adapter.setHighlightedPosition(scrollPosition);
                }, 300); // دوای scroll
            });

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                adapter.setHighlightedPosition(-1);
                adapter.notifyDataSetChanged();
            }, 1100); // 3 چرکە
        }

    }


    private void loadHadithWithChapters(int kitabId) {

        String currentChapterAr = null;
        String currentChapterKr = null;
        int currentChapterSort = 0;

        Cursor cursor = mydbClass.readHadithWithChapters(kitabId);

        int lastChapterId = -1;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Integer chapterId = cursor.isNull(
                        cursor.getColumnIndexOrThrow("chapter_id")
                ) ? null : cursor.getInt(cursor.getColumnIndexOrThrow("chapter_id"));

                // ===== Chapter header ===== update
                if (chapterId != null && chapterId != lastChapterId) {

                    currentChapterAr =
                            cursor.getString(cursor.getColumnIndexOrThrow("title_ar"));
                    currentChapterKr =
                            cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    currentChapterSort =
                            cursor.getInt(cursor.getColumnIndexOrThrow("chapter_sort"));

                    HadithRow header = new HadithRow(HadithRow.TYPE_CHAPTER);
                    header.chapterTitleAr = currentChapterAr;
                    header.chapterTitleKr = currentChapterKr;
                    header.chapterSort = currentChapterSort;

                    rows.add(header);
                    lastChapterId = chapterId;
                }


                // ===== Hadith item =====
                HadithItem hadith = new HadithItem(

                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("sort")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("book_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("sort_in_book")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("kitab_id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("chapter_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("text")),
                        cursor.getString(cursor.getColumnIndexOrThrow("text_ar")),
                        cursor.getString(cursor.getColumnIndexOrThrow("note")),
                        cursor.getString(cursor.getColumnIndexOrThrow("footnote")),
                        cursor.getString(cursor.getColumnIndexOrThrow("normalized_text_ar")),
                        cursor.getString(cursor.getColumnIndexOrThrow("normalized_text"))
                );


                HadithRow row = new HadithRow(HadithRow.TYPE_HADITH);
                row.hadith = hadith;

                // ⭐ ADD THESE LINES
                row.chapterTitleAr = currentChapterAr;
                row.chapterTitleKr = currentChapterKr;
                row.chapterSort = currentChapterSort;
                rows.add(row);

                // ⬅️ now rows knows the REAL position
                if (hadith.getId() == targetHadithId) {
                    scrollPosition = rows.size() - 1;
                }


            } while (cursor.moveToNext());

            cursor.close();
        }


    }


}