package com.dya.noor.activities;

import static com.dya.noor.R.color.black;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.BaseActivity.BaseActivity;
import com.dya.noor.R;
import com.dya.noor.adapters.TasbihAdapter;
import com.dya.noor.utlis.UtilsT;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class Tasbih extends BaseActivity {

    RecyclerView recyclerView;
    String[] s1;
    ImageButton back;
    String getItem;
    ScrollView test;
    LinearLayout layoutBar;
    TextView txTextView , txtNum ,txtNum2;
    int num=0, num3=33, num2=-1;

    ImageButton btnZHm , btnMult , btnRetry;
    public static BottomSheetDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasbih);


        back =findViewById(R.id.back);
        btnMult = findViewById(R.id.multy);
        btnRetry =findViewById(R.id.retry);

        s1 =getResources().getStringArray(R.array.Zikr);
        test =findViewById(R.id.testl);
        txtNum =findViewById(R.id.txtNum);
        txtNum2 =findViewById(R.id.txtNum2);
        txTextView =findViewById(R.id.txt) ;

        btnZHm =findViewById(R.id.btnzhmardn);
        layoutBar =findViewById(R.id.layoutBar);
        txtNum2.setText(String.valueOf(num3));

        SharedPreferences pref = getSharedPreferences("Tasbih", MODE_PRIVATE);

// default value if nothing saved
        String saved = pref.getString("tasbihText", "اَسْتَغْفِرُ اللّٰه");

        txTextView.setText(saved);
        UtilsT.TasbihText = saved;


        dialog = new BottomSheetDialog(this, R.style.BottomSheetStyle);





        btnMult.setOnClickListener(v -> {




           if(num3==33) {
               num3=100;
               txtNum2.setText(String.valueOf(num3));
           }
           else if (num3==100){
               num3=500;
               txtNum2.setText(String.valueOf(num3));
           }
           else if (num3==500){
               num3=1000;
               txtNum2.setText(String.valueOf(num3));
           }
           else if (num3==1000){
               num3=33;
               txtNum2.setText(String.valueOf(num3));
           }

        });


        btnRetry.setOnClickListener(v -> {
            num3=33;
            num=0;
            txtNum.setText(String.valueOf(num));
            txtNum2.setText(String.valueOf(num3));

        });

        btnZHm.setOnClickListener(v -> {
            num++;
            num2++;

            if (num>33 && num3==33){

                num=0;


            }else if (num>100 && num3==100){
                num=0;

            }
            else if (num>500 && num3==500){
                num=0;

            }
            else if (num>1000 && num3==1000){
                num=0;

            }
            txtNum.setText(String.valueOf(num));




        });



        if (getItem!=null){
            test.setVisibility(View.VISIBLE);
            layoutBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            txTextView.setText(getItem);
        }

        TasbihAdapter tasbihAdapter = new TasbihAdapter(this, s1, item -> {

            txTextView.setText(item);   // ✅ REAL-TIME UPDATE
            UtilsT.TasbihText = item;
            // ✅ SAVE HERE;
            pref.edit().putString("tasbihText", item).apply();
            dialog.dismiss();           // close bottom sheet
        });


        back.setOnClickListener(v -> onBackPressed());
        txTextView.setText( UtilsT.TasbihText);

        txTextView.setOnClickListener(v -> {
            dialog.setContentView(R.layout.tasbih_botomsheet);
            recyclerView =dialog.findViewById(R.id.MrecyclerView);
            assert recyclerView != null;
            recyclerView.setAdapter(tasbihAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            dialog.show();

            View bottomSheet =
                    dialog.findViewById(R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

                // 1. Force Expanded State so it doesn't try to "drag up" while you scroll
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);



            }
        });


    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}