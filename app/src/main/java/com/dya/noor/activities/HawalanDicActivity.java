package com.dya.noor.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;

public class HawalanDicActivity extends BaseActivity {

    String Name , dic;

    TextView nameHawalan ,nameHawalan2, HawalanDic;

    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hawalan_dic);

        Name = getIntent().getStringExtra("HawalanName");
        dic=getIntent().getStringExtra("HawalanDic");

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        nameHawalan = findViewById(R.id.nameHawalan);
        nameHawalan2 = findViewById(R.id.nameHawalan2);
        HawalanDic = findViewById(R.id.HawalanDic);

        nameHawalan2.setText(Name);
        HawalanDic.setText(dic);




    }
}