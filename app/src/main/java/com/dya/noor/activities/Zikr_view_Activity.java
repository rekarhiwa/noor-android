package com.dya.noor.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.database.MydbClass;
import com.dya.noor.R;
import com.dya.noor.adapters.ZikrViewAdapter;
import com.dya.noor.module.ZikrCount;

import java.util.ArrayList;

public class Zikr_view_Activity extends BaseActivity {


    MydbClass mydbClass;
    SQLiteDatabase db;
    Cursor myCursor;
    String id;
    RecyclerView zRecyclerView;
    TextView txtZikrName;
    ImageButton back;
    ArrayList aArZ, aKrZ ,reference;
    SharedPreferences pref;
    Switch langSwitch;
    String savedLang;

    ArrayList<Integer> minList;
    ArrayList<Integer> maxList ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zikr_view);

        zRecyclerView = findViewById(R.id.zRecyclerView);

        txtZikrName = findViewById(R.id.txtZikrName);


        minList = new ArrayList<>();
        maxList = new ArrayList<>();

        langSwitch = findViewById(R.id.Lang);
        pref = getSharedPreferences("LangSettings", MODE_PRIVATE);
        savedLang = pref.getString("lang", "ckb");

        langSwitch.setChecked(savedLang.equals("ckb_BADINI"));

// text initial
        langSwitch.setText(langSwitch.isChecked() ? "سۆرانی" : "بادینی");

        back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());

        mydbClass = new MydbClass(this);

        db = mydbClass.getWritableDatabase();

        aArZ = new ArrayList<>();
        aKrZ = new ArrayList<>();
        reference = new ArrayList<>();
        id = getIntent().getStringExtra("id");

        ZikrViewAdapter zikrViewAdapter = new ZikrViewAdapter(this, aArZ, aKrZ,reference,
                minList, maxList);
        zRecyclerView.setAdapter(zikrViewAdapter);
        zRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        txtZikrName.setText(getIntent().getStringExtra("sura"));


        StoreDataInArrayList();

        langSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            String lang;

            if(isChecked){
                lang = "ckb_BADINI";
                langSwitch.setText("سۆرانی");
            }else{
                lang = "ckb";
                langSwitch.setText("بادینی");
            }

            // save
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("lang", lang);
            editor.apply();

            // reload data
            StoreDataInArrayList();
        });



    }

    @SuppressLint("Range")
    public void StoreDataInArrayList() {

        aArZ.clear();
        aKrZ.clear();
        reference.clear();

        SharedPreferences pref = getSharedPreferences("LangSettings", MODE_PRIVATE);
        String lang = pref.getString("lang", "ckb");

        Cursor cursor = mydbClass.readAllAzkar_item(id, lang);

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "no data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {

                String arText = cursor.getString(1);

                aArZ.add(arText);
                aKrZ.add(cursor.getString(cursor.getColumnIndex("kr_text")));
                reference.add(cursor.getString(cursor.getColumnIndex("ref_text")));

                ZikrCount count = extractNumbers(arText);

                minList.add(count.min);
                maxList.add(count.max);


            }
        }

        zRecyclerView.getAdapter().notifyDataSetChanged();
    }


    public ZikrCount extractNumbers(String text) {

        if (text == null) return new ZikrCount(1,1);

        ArrayList<Integer> numbers = new ArrayList<>();

        if (text.contains("واحدة")) numbers.add(1);
        if (text.contains("مرتين") || text.contains("اثنتين") || text.contains("اثنين")) numbers.add(2);
        if (text.contains("ثلاث")) numbers.add(3);
        if (text.contains("أربع")) numbers.add(4);
        if (text.contains("خمس")) numbers.add(5);
        if (text.contains("ست")) numbers.add(6);
        if (text.contains("سبع")) numbers.add(7);
        if (text.contains("ثمان")) numbers.add(8);
        if (text.contains("تسع")) numbers.add(9);
        if (text.contains("عشر")) numbers.add(10);
        if (text.contains("مائة")) numbers.add(100);

        if (numbers.size() == 0) return new ZikrCount(1,1);

        int min = numbers.get(0);
        int max = numbers.get(0);

        for (int n : numbers) {
            if (n < min) min = n;
            if (n > max) max = n;
        }

        return new ZikrCount(min, max);
    }


    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}