package com.dya.noor.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.KitabAdapter;
import com.dya.noor.database.MydbClass;
import com.dya.noor.module.KitabItem;

import java.util.ArrayList;
import java.util.List;

public class KitabActivity extends BaseActivity {




    public int selectedBookId;
    RecyclerView kitabRecyclerView;

    MydbClass mydbClass;
    KitabAdapter adapter;
    List<KitabItem>  kitabItem ;
    String selectedBookName;

    TextView txtBookName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kitab);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectedBookId = getIntent().getIntExtra("book_id",1);
        selectedBookName = getIntent().getStringExtra("book_name");

        mydbClass = new MydbClass(this);
        kitabItem = new ArrayList<>();

        kitabRecyclerView = findViewById(R.id.kitabRecyclerView);
        txtBookName = findViewById(R.id.txtBookName);
        txtBookName.setText(selectedBookName);
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        readKitab(selectedBookId);

        adapter = new KitabAdapter(this,selectedBookName,kitabItem);
        kitabRecyclerView.setAdapter(adapter);
        kitabRecyclerView.setLayoutManager(new LinearLayoutManager(this));



    }

    private void readKitab(int id) {
        Cursor cursor = mydbClass .readAllKitab(id);
        while (cursor.moveToNext()){

            kitabItem.add( new KitabItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("book_id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("kitab_sort")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title_ar"))));


        }

    }
}