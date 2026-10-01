package com.dya.noor.activities;

import android.Manifest;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;

import java.util.Objects;

public class HelpNoor extends BaseActivity {

    ImageView btnBack , fastPayBtn , fibBtn , btnKorek , btnAsia;
    TextView   tvData , tvThree , tvFive , tvTen , tvIncrement , tvDecrement , tvSend , helpTextView;
    String phoneNumber = "07710500202";
    String n = "*123*19000*0770942824#";
    int Three = 3000;
    int Five = 5000;
    int Ten = 10000;
    int Data = 1000;

    LinearLayout SendLayout ;

    String companyName="Korek";
    String ActivityNAme;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    NestedScrollView nestedScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_noor);
        btnBack = findViewById(R.id.btnBack);
        fastPayBtn = findViewById(R.id.fastPayBtn);
        tvData = findViewById(R.id.tvData);
        fibBtn = findViewById(R.id.fibBtn);
        nestedScrollView = findViewById(R.id.NestedScrollView);

        ActivityNAme = getIntent().getStringExtra("Activity");
        tvIncrement = findViewById(R.id.tvIncrement);
        tvDecrement = findViewById(R.id.tvDecrement);

        SendLayout = findViewById(R.id.SendLayout);

        sharedPreferences = getSharedPreferences("OpenFirst", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("OpenFirst", false);
        editor.apply();


        tvThree = findViewById(R.id.tvThree);
        helpTextView = findViewById(R.id.helpTextView);
        tvFive = findViewById(R.id.tvFive);
        tvTen = findViewById(R.id.tvTen);
        tvSend = findViewById(R.id.tvSend);

        btnKorek = findViewById(R.id.btnKorek);
        btnAsia = findViewById(R.id.btnAsia);

        tvData.setText(formatData(Data));




        String arabicText = helpTextView.getText().toString();

        // Find the index of the part you want to change color for
        int startIndex2 = arabicText.indexOf("نــــور");
        int endIndex2 = startIndex2 + "نــــور".length();

        // Check if the index is valid
        if (startIndex2 != -1 && endIndex2 <= arabicText.length()) {
            // Create a SpannableString
            SpannableString spannableString = new SpannableString(arabicText);

            // Set the color for the specified part
            int color = R.color.textColorRed;
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(color)), startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(20, true), startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);



            // Set the SpannableString to the TextView
            helpTextView.setText(spannableString);
        } else {
            // Log or handle the case where the text to be colored is not found
        }








        btnKorek.setOnClickListener(v -> {
            if (SendLayout.getVisibility() == View.GONE ){
                SendLayout.setVisibility(View.VISIBLE);
            }
            companyName="Korek";
            btnAsia.setBackgroundResource(R.drawable.backgraownd_asi_korek_no_select);
            btnKorek.setBackgroundResource(R.drawable.backgraownd_asi_korek);
            nestedScrollView.post(new Runnable() {
                @Override
                public void run() {
                    nestedScrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        });

        btnAsia.setOnClickListener(v -> {
            if (SendLayout.getVisibility() == View.GONE ){
                SendLayout.setVisibility(View.VISIBLE);
            }
            companyName="Asia";

            btnAsia.setBackgroundResource(R.drawable.backgraownd_asi_korek);
            btnKorek.setBackgroundResource(R.drawable.backgraownd_asi_korek_no_select);

            nestedScrollView.post(new Runnable() {
                @Override
                public void run() {
                    nestedScrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        });


        tvIncrement.setOnClickListener(v -> {

            // Increment data point 1 by 1000 with a minimum of 0
            Data = Math.max(0, Data + 1000);
            tvData.setText(formatData(Data));

        });

        tvDecrement.setOnClickListener(v -> {
            if (Data>1000) {
                Data = Math.max(0, Data - 1000);
                tvData.setText(formatData(Data));
            }
        });



        tvThree.setOnClickListener(v -> {
            Data=Three;
            tvData.setText(formatData(Three));

            tvThree.setBackgroundResource(R.drawable.backgraownd_pay);
            tvFive.setBackgroundResource(R.drawable.backgraownd_pay_re);
            tvTen.setBackgroundResource(R.drawable.backgraownd_pay_re);
        });

        tvFive.setOnClickListener(v -> {
            Data=Five;
            tvData.setText(formatData(Five));

            tvFive.setBackgroundResource(R.drawable.backgraownd_pay);
            tvThree.setBackgroundResource(R.drawable.backgraownd_pay_re);
            tvTen.setBackgroundResource(R.drawable.backgraownd_pay_re);
        });

        tvTen.setOnClickListener(v -> {
            Data=Ten;
            tvData.setText(formatData(Ten));

            tvTen.setBackgroundResource(R.drawable.backgraownd_pay);
            tvThree.setBackgroundResource(R.drawable.backgraownd_pay_re);
            tvFive.setBackgroundResource(R.drawable.backgraownd_pay_re);
        });

        tvSend.setOnClickListener(v -> {
            sendMoney(companyName, String.valueOf(Data));
        });


        fastPayBtn.setOnClickListener(v -> {

            // Access the system clipboard
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

            // Create a new clip with the text
           android.content.ClipboardManager clipboardManager2 =
                   (android.content.ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData2 = ClipData.newPlainText("text", phoneNumber);
            clipboardManager2.setPrimaryClip(clipData2);
            Toast.makeText(this, "کۆپی بوو", Toast.LENGTH_SHORT).show();
/**
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 101);
            } else {
                // Permission already granted, make call or launch dialer intent
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + Uri.encode("*123*19000*07709442824#"))); // Encode the # symbol
                startActivity(intent);

            }

 */

           // openApp("com.sslwireless.fastpay");
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(
                    "com.sslwireless.fastpay",   // Package name
                    "com.sslwireless.fastpay.view.activity.transaction.SendMoneyActivity"// Full activity name
            ));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        });
        fibBtn.setOnClickListener(v -> {

            // Copy phone number to clipboard
            android.content.ClipboardManager clipboardManager2 =
                    (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData2 = ClipData.newPlainText("text", phoneNumber);
            clipboardManager2.setPrimaryClip(clipData2);
            Toast.makeText(this, "کۆپی بوو", Toast.LENGTH_SHORT).show();


            // The package name for the FIB app
            openApp("com.firstiraqibank.personal");


        });

        btnBack.setOnClickListener(v -> {

            if (ActivityNAme.equals("MainActivity") || ActivityNAme.equals("Khatm")){
                onBackPressed();
            }else{
                Intent intent = new Intent(HelpNoor.this , MainActivity.class);
                startActivity(intent);
                finish();
            }

        });
    }

    private void openApp(String PackageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(PackageName);
        if (intent !=null){

            startActivity(intent);
        }
    }

    public String formatData(int data) {
        if (data >= 1000) {
            return String.format("%,d", data); // Use commas for thousands separators
        } else {
            return String.valueOf(data);
        }
    }

    public void sendMoney(String CompanyName , String money){



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 101);
        } else {
            // Permission already granted, make call or launch dialer intent
            Intent intent = new Intent(Intent.ACTION_CALL);
            if (Objects.equals(CompanyName, "Asia")) {
                intent.setData(Uri.parse("tel:" + Uri.encode("*123*"+money+"*07710500202#"))); // Encode the # symbol
            }else {
                intent.setData(Uri.parse("tel:" + Uri.encode("*215*07511935352*"+ money+"#"))); // Encode the # symbol
            }
            startActivity(intent);

        }

    }

    @Override
    public void onBackPressed() {
        if (ActivityNAme.equals("MainActivity") || ActivityNAme.equals("Khatm")){
            super.onBackPressed();
        }else{
            Intent intent = new Intent(HelpNoor.this , MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}