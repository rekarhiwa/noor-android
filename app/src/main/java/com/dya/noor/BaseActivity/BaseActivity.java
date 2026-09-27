package com.dya.noor.BaseActivity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * A base activity for all other activities in the app to extend.
 * It handles common setup like enabling the edge-to-edge display.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // This line enables edge-to-edge for ANY activity that extends BaseActivity.
        // It's called here so you don't have to repeat it in every single activity.
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
    }
}
