package com.dya.noor.activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.WallamakanQuestionAdapter;
import com.dya.noor.database.MydbClass;
import com.dya.noor.module.wallamakanquestionItem;

import java.util.ArrayList;
import java.util.Objects;

public class WallamakanQuestion extends BaseActivity {

    RecyclerView wallamakanQuestionRecyclerView;
    String id;
    MydbClass myDbClass;
    WallamakanQuestionAdapter questionAdapter;
    ArrayList<wallamakanquestionItem> questionItems ;

    ImageView btnBack;

    String ActName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallamakan_question);

        wallamakanQuestionRecyclerView = findViewById(R.id.wallamakanQuestionRecyclerView);

        id = getIntent().getStringExtra("catId");
        ActName = getIntent().getStringExtra("ActName");
        myDbClass = new MydbClass(this);
        questionItems= new ArrayList<>();

        StoreDataInArrayList(id);
        questionAdapter = new WallamakanQuestionAdapter(this,questionItems);

        wallamakanQuestionRecyclerView.setAdapter(questionAdapter);
        wallamakanQuestionRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        // Auto-click the item with the given ID

        if (Objects.equals(ActName, "Khatm")) {

            autoClickItem();

        }
    }


    @SuppressLint("Range")
    void StoreDataInArrayList(String id) {

        Cursor cursor = myDbClass.wallamakanAnswer(id);

        while (cursor.moveToNext()) {
            questionItems.add(new wallamakanquestionItem(
                    cursor.getString(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("short_title")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("ask")),
                    cursor.getString(cursor.getColumnIndex("question")),
                    cursor.getString(cursor.getColumnIndex("cat_id")),
                    cursor.getString(cursor.getColumnIndex("count")),
                    cursor.getString(cursor.getColumnIndex("kwrte"))
            ));


        }


    }



    private void autoClickItem() {

        wallamakanQuestionRecyclerView.post(() -> {
            // Scroll to the item
            wallamakanQuestionRecyclerView.scrollToPosition(44);

            // Delay clicking to ensure the view is fully created
            wallamakanQuestionRecyclerView.postDelayed(() -> {
                WallamakanQuestionAdapter.WallamakanQuestionViewHolder holder =
                        (WallamakanQuestionAdapter.WallamakanQuestionViewHolder) wallamakanQuestionRecyclerView
                                .findViewHolderForAdapterPosition(44);

                if (holder != null) {
                    holder.wallamakanQuestionCardView.performClick(); // This will trigger the dialog
                }
            }, 500); // Delay for 500ms to ensure proper rendering
        });

    }

}