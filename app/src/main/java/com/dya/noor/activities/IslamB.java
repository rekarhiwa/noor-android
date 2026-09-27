package com.dya.noor.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageButton;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;


public class IslamB extends BaseActivity {
    ImageButton back ;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_islam_b);

        back =findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}