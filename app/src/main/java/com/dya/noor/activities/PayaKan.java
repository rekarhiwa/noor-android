package com.dya.noor.activities;

import android.os.Bundle;
import android.widget.ImageButton;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;


public class PayaKan extends BaseActivity {

    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paya_kan);

        back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}