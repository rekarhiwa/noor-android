package com.dya.noor.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.WallamakanCategoryAdapter;
import com.dya.noor.database.MydbClass;
import com.dya.noor.module.wallamakanCategoryItem;

import java.util.ArrayList;

public class Wallamakan extends BaseActivity {

    RecyclerView wallamakanRecyclerView;


    MydbClass myDbClass;
    WallamakanCategoryAdapter categoryAdapter;
    ArrayList<wallamakanCategoryItem> categoryItems ;
    ImageView btnBack , btnSearch;
    TextView txtWallamakanLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallamakan);


        wallamakanRecyclerView = findViewById(R.id.wallamakanRecyclerView);
        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        txtWallamakanLink = findViewById(R.id.txtWallamakanLink);
        // Define the URL
        String url = "https://walamakan.com/";
        txtWallamakanLink.setOnClickListener(v -> {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary)); // Set toolbar color
            builder.setShowTitle(true); // Show the title of the page
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(Wallamakan.this, Uri.parse(url)); });

        btnSearch.setOnClickListener(v -> {
            startActivity(new Intent(this,Wallamakan_search.class));
        });

        myDbClass = new MydbClass(this);
        categoryItems= new ArrayList<>();

        StoreDataInArrayList();

        categoryAdapter = new WallamakanCategoryAdapter(this,categoryItems);

        wallamakanRecyclerView.setAdapter(categoryAdapter);
        wallamakanRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

    }


    @SuppressLint("Range")
    void StoreDataInArrayList() {

        Cursor cursor = myDbClass.wallamakanCategory();

        while (cursor.moveToNext()) {
                    categoryItems.add(new wallamakanCategoryItem(
                    cursor.getString(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("title"))
            ));


        }
    }
}