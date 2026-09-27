package com.dya.noor.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.dya.noor.R;

import java.util.ArrayList;

public class AllahNameAdapter extends RecyclerView.Adapter <AllahNameAdapter.MyViewHolder>{

    ArrayList allahNameList, soraniList, badiniyList;
    Context context;

    public AllahNameAdapter(Context ct , ArrayList allahNameList, ArrayList soraniList , ArrayList badiniyList){
        context = ct;
        this.allahNameList = allahNameList;
        this.soraniList = soraniList;
        this.badiniyList = badiniyList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.allah_name_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        SharedPreferences sharedPreferencesSize = context.getSharedPreferences("size",MODE_PRIVATE);
        int TextSize = sharedPreferencesSize.getInt("textSize", 20);
       // holder.mName.setTextSize(TypedValue.COMPLEX_UNIT_SP, TextSize);
       // holder.mMana.setTextSize(TypedValue.COMPLEX_UNIT_SP, TextSize);
        //holder.mMana2.setTextSize(TypedValue.COMPLEX_UNIT_SP, TextSize);

        holder.NameTextView.setText(String.valueOf(allahNameList.get(position)));
        holder.SoranTextView.setText(String.valueOf(soraniList.get(position)));
        holder.BadiniyTextView.setText(String.valueOf(badiniyList.get(position)));
        holder.BtnCopy.setOnClickListener(view -> {

            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(
                    Context.CLIPBOARD_SERVICE
            );
            ClipData clipData = ClipData.newPlainText("text", allahNameList.get(position) +"\n"+ soraniList.get(position) +"\n"+ badiniyList.get(position) +"\n"+"\n"+"#ئەپڵیکەیشنی_نور"+"\n"+"\n");
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, "کۆپی کرا", Toast.LENGTH_SHORT).show();

        });



    }




    @Override
    public int getItemCount() {
        return allahNameList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView NameTextView, SoranTextView, BadiniyTextView;
        ImageView BtnCopy;
         LinearLayout constraintLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            NameTextView = itemView.findViewById(R.id.name);
            SoranTextView = itemView.findViewById(R.id.mana);
            BadiniyTextView = itemView.findViewById(R.id.mana2);
            BtnCopy = itemView.findViewById(R.id.BtnCopy);
            constraintLayout  = itemView.findViewById(R.id.constraintLayout);

        }
    }
}
