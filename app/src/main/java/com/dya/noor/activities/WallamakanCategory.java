package com.dya.noor.activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.WallamakanCategory2Adapter;
import com.dya.noor.database.MydbClass;
import com.dya.noor.module.wallamakanCategoryItem2;

import java.util.ArrayList;

public class WallamakanCategory extends BaseActivity {

    RecyclerView wallamakanCategoryRecyclerView;
    MydbClass myDbClass;
    WallamakanCategory2Adapter categoryAdapter;
    ArrayList<wallamakanCategoryItem2> categoryItems ;
    ImageView btnBack;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallamakan_category);


        wallamakanCategoryRecyclerView = findViewById(R.id.wallamakanCategoryRecyclerView);
        id = getIntent().getStringExtra("catId");

        btnBack = findViewById(R.id.btnBack);


        myDbClass = new MydbClass(this);
        categoryItems= new ArrayList<>();

        StoreDataInArrayList(id);

        categoryAdapter = new WallamakanCategory2Adapter(this,categoryItems);

        wallamakanCategoryRecyclerView.setAdapter(categoryAdapter);

        wallamakanCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });



    }


    @SuppressLint("Range")
    void StoreDataInArrayList(String id) {

        Cursor cursor = myDbClass.wallamakanCategory2(id);

        while (cursor.moveToNext()) {
            categoryItems.add(new wallamakanCategoryItem2(
                    cursor.getString(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("main_id"))
            ));


        }
    }



}