package com.dya.noor.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.BooksAdapter;
import com.dya.noor.database.MydbClass;
import com.dya.noor.module.BookItem;

import java.util.ArrayList;
import java.util.List;

public class BooksActivity extends BaseActivity {


    MydbClass mydbClass;

    RecyclerView bookRecyclerView;

    BooksAdapter adapter;
    List<BookItem>bookItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_books);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mydbClass = new MydbClass(this);
        bookItems = new ArrayList<>();
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            Intent intent = new Intent(BooksActivity.this ,SearchHadithActivity.class );
            intent.putExtra("searchText","");
            startActivity(intent);
        });

        bookRecyclerView = findViewById(R.id.bookRecyclerView);

        readBooks();

        adapter = new BooksAdapter(this,bookItems);
        bookRecyclerView.setAdapter(adapter);
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(this));



    }


    @SuppressLint("Range")
    public void readBooks(){
        Cursor cursor = mydbClass.readAllBook();

        while (cursor.moveToNext()){

            bookItems.add( new BookItem( cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("name_ar")),
                    cursor.getInt(cursor.getColumnIndex("count")),
                    cursor.getInt(cursor.getColumnIndex("type")),
                    cursor.getInt(cursor.getColumnIndex("sort")),
                    cursor.getInt(cursor.getColumnIndex("sum_of_writer"))));


        }
    }
}