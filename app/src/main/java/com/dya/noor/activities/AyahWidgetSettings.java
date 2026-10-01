package com.dya.noor.activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.database.MydbClass;
import com.dya.noor.widget.AyahWidgetHelper;

import java.util.ArrayList;
import java.util.List;

public class AyahWidgetSettings extends BaseActivity {

    private String selectedMode = AyahWidgetHelper.MODE_DAILY;
    private LinearLayout modeDaily, modeScreenshot, modeFixed, fixedPickLayout;
    private Spinner spinnerSura, spinnerAyah;
    private TextView previewMeta, previewText, previewTranslation, btnApply;
    private final List<Integer> suraIds = new ArrayList<>();
    private final List<String> suraLabels = new ArrayList<>();
    private final List<Integer> ayahNumbers = new ArrayList<>();
    private int fixedSura = 1;
    private int fixedAyah = 1;
    private boolean ignoreSpinner;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayah_widget_settings);

        ImageButton btnBack = findViewById(R.id.btnBack);
        modeDaily = findViewById(R.id.modeDaily);
        modeScreenshot = findViewById(R.id.modeScreenshot);
        modeFixed = findViewById(R.id.modeFixed);
        fixedPickLayout = findViewById(R.id.fixedPickLayout);
        spinnerSura = findViewById(R.id.spinnerSura);
        spinnerAyah = findViewById(R.id.spinnerAyah);
        previewMeta = findViewById(R.id.previewMeta);
        previewText = findViewById(R.id.previewText);
        previewTranslation = findViewById(R.id.previewTranslation);
        btnApply = findViewById(R.id.btnApply);

        selectedMode = AyahWidgetHelper.mode(this);
        fixedSura = AyahWidgetHelper.prefs(this).getInt(AyahWidgetHelper.KEY_FIXED_SURA, 1);
        fixedAyah = AyahWidgetHelper.prefs(this).getInt(AyahWidgetHelper.KEY_FIXED_AYAH, 1);

        btnBack.setOnClickListener(v -> finish());
        modeDaily.setOnClickListener(v -> selectMode(AyahWidgetHelper.MODE_DAILY));
        modeScreenshot.setOnClickListener(v -> selectMode(AyahWidgetHelper.MODE_SCREENSHOT));
        modeFixed.setOnClickListener(v -> selectMode(AyahWidgetHelper.MODE_FIXED));
        btnApply.setOnClickListener(v -> apply());

        loadSuras();
        selectMode(selectedMode);
        loadPreview();
    }

    private void selectMode(String mode) {
        selectedMode = mode;
        highlightModes();
        fixedPickLayout.setVisibility(
                AyahWidgetHelper.MODE_FIXED.equals(mode) ? View.VISIBLE : View.GONE);
    }

    private void highlightModes() {
        modeDaily.setAlpha(AyahWidgetHelper.MODE_DAILY.equals(selectedMode) ? 1f : 0.55f);
        modeScreenshot.setAlpha(AyahWidgetHelper.MODE_SCREENSHOT.equals(selectedMode) ? 1f : 0.55f);
        modeFixed.setAlpha(AyahWidgetHelper.MODE_FIXED.equals(selectedMode) ? 1f : 0.55f);
    }

    private void loadSuras() {
        suraIds.clear();
        suraLabels.clear();
        MydbClass db = new MydbClass(this);
        Cursor c = db.readAllData();
        if (c != null) {
            while (c.moveToNext()) {
                try {
                    int id = Integer.parseInt(c.getString(0));
                    String name = c.getString(1);
                    suraIds.add(id);
                    suraLabels.add(name == null ? String.valueOf(id) : name);
                } catch (Exception ignored) {
                }
            }
            c.close();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, suraLabels);
        spinnerSura.setAdapter(adapter);

        int suraPos = Math.max(0, suraIds.indexOf(fixedSura));
        ignoreSpinner = true;
        if (!suraIds.isEmpty()) {
            spinnerSura.setSelection(suraPos);
            fixedSura = suraIds.get(suraPos);
            loadAyahs(fixedSura);
        }
        ignoreSpinner = false;

        spinnerSura.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ignoreSpinner || position < 0 || position >= suraIds.size()) return;
                fixedSura = suraIds.get(position);
                loadAyahs(fixedSura);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerAyah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ignoreSpinner || position < 0 || position >= ayahNumbers.size()) return;
                fixedAyah = ayahNumbers.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadAyahs(int suraId) {
        ayahNumbers.clear();
        List<String> labels = new ArrayList<>();
        MydbClass db = new MydbClass(this);
        Cursor c = db.readAllAyahData(String.valueOf(suraId));
        if (c != null) {
            while (c.moveToNext()) {
                try {
                    int n = Integer.parseInt(c.getString(c.getColumnIndexOrThrow("ayah")));
                    ayahNumbers.add(n);
                    labels.add("ئایەت " + n);
                } catch (Exception ignored) {
                }
            }
            c.close();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, labels);
        spinnerAyah.setAdapter(adapter);
        int ayahPos = Math.max(0, ayahNumbers.indexOf(fixedAyah));
        if (!ayahNumbers.isEmpty()) {
            ignoreSpinner = true;
            spinnerAyah.setSelection(ayahPos);
            fixedAyah = ayahNumbers.get(ayahPos);
            ignoreSpinner = false;
        }
    }

    private void loadPreview() {
        previewMeta.setText(AyahWidgetHelper.prefs(this).getString(AyahWidgetHelper.KEY_META, ""));
        previewText.setText(AyahWidgetHelper.prefs(this).getString(AyahWidgetHelper.KEY_TEXT, ""));
        previewTranslation.setText(
                AyahWidgetHelper.prefs(this).getString(AyahWidgetHelper.KEY_TRANSLATION, ""));
    }

    private void apply() {
        AyahWidgetHelper.setMode(this, selectedMode);
        if (AyahWidgetHelper.MODE_FIXED.equals(selectedMode)) {
            AyahWidgetHelper.setFixed(this, fixedSura, fixedAyah);
        }
        AyahWidgetHelper.refresh(this, true);
        loadPreview();
        Toast.makeText(this, "ویجێتی ئایەت نوێکرایەوە", Toast.LENGTH_SHORT).show();
    }
}
