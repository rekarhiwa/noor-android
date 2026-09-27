package com.dya.noor.activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.WallamakanSearchAdapter;
import com.dya.noor.database.MydbClass;
import com.dya.noor.module.wallamakanquestionItem;

import java.util.ArrayList;

public class Wallamakan_search extends BaseActivity {

    ImageView btnBack;
    RecyclerView wallamakanSearchRecyclerView;
    MydbClass mydbClass;
    WallamakanSearchAdapter questionAdapter;
    ArrayList<wallamakanquestionItem> questionItems ;

    EditText WallamakanSearchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallamakan_search);

        btnBack = findViewById(R.id.btnBack);
        WallamakanSearchEditText = findViewById(R.id.WallamakanSearchEditText);
        wallamakanSearchRecyclerView = findViewById(R.id.wallamakanSearchRecyclerView);
        mydbClass = new MydbClass(this);
        questionItems= new ArrayList<>();
        StoreDataInArrayList();
        questionAdapter = new WallamakanSearchAdapter(this,questionItems);

        wallamakanSearchRecyclerView.setAdapter(questionAdapter);
        wallamakanSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        WallamakanSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                questionAdapter.getFilter().filter(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        btnBack.setOnClickListener(v -> {

            onBackPressed();

        });
    }

    @SuppressLint("Range")
    private void StoreDataInArrayList() {

        Cursor cursor = mydbClass.wallamakanAnswerSearch();

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
}