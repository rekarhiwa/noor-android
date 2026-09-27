package com.dya.noor.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.SearchHadithAdapter;
import com.dya.noor.database.MydbClass;
import com.dya.noor.module.SearchHadithItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchHadithActivity extends BaseActivity {

    RecyclerView searchRecyclerView;
    EditText editSearch;

    MydbClass db;
    SearchHadithAdapter adapter;
    List<SearchHadithItem> list = new ArrayList<>();

    LinearLayout emptyView;

    String searchGetText="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_hadith);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchGetText=getIntent().getStringExtra("searchText");


        findViewById(R.id.btnBack).setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        editSearch = findViewById(R.id.editSearch);
        emptyView = findViewById(R.id.emptyViews);
        db = new MydbClass(this);



        adapter = new SearchHadithAdapter(this, list);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(adapter);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s.toString().trim());
                updateEmptyState();
            }
        });

        if (searchGetText != null && !searchGetText.isEmpty()){
            editSearch.setText(searchGetText);
            editSearch.setSelection(searchGetText.length()); // cursor لە کۆتایی
            search(searchGetText);
        }



    }


    @Override
    protected void onResume() {
        super.onResume();
        updateEmptyState();
    }

    private void search(String keyword) {

        list.clear();

        if (keyword.isEmpty()) {
            adapter.notifyDataSetChanged();
            return;
        }

        Cursor c = db.searchHadith(keyword);

        if (c != null && c.moveToFirst()) {
            do {
                list.add(new SearchHadithItem(
                        c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getInt(c.getColumnIndexOrThrow("book_id")),
                        c.getInt(c.getColumnIndexOrThrow("kitab_id")),
                        c.getString(c.getColumnIndexOrThrow("normalized_text_ar")),
                        c.getString(c.getColumnIndexOrThrow("normalized_text")),
                        c.getString(c.getColumnIndexOrThrow("kitab_name")),
                        c.getString(c.getColumnIndexOrThrow("book_name"))
                ));
            } while (c.moveToNext());
            c.close();
        }

        adapter.setKeyword(keyword);
        adapter.notifyDataSetChanged();
    }

    private void updateEmptyState() {
        if (list.isEmpty()) {
            searchRecyclerView.setVisibility(GONE);
            emptyView.setVisibility(VISIBLE);
        } else {
            searchRecyclerView.setVisibility(VISIBLE);
            emptyView.setVisibility(GONE);
        }
    }


}