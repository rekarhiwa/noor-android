package com.dya.noor.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.activities.QuranPage;
import com.dya.noor.interfaces.QariSelectListener;
import com.dya.noor.module.QraiNameItem;

import java.util.ArrayList;
import java.util.List;

public class QariNameAdapter extends RecyclerView.Adapter<QariNameAdapter.QariNameViewHolder> {


    List<QraiNameItem> qraiNameItems = new ArrayList<>();
    Context context;
    QariSelectListener listener;

    List<QraiNameItem> originalList = new ArrayList<>();

    String searchText = "";



    public QariNameAdapter(Context context,List<QraiNameItem> qraiNameItems ,
                           QariSelectListener listener) {
        this.context = context;
        this.qraiNameItems = qraiNameItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QariNameAdapter.QariNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.qari_name_item,parent,false);
        return new QariNameAdapter.QariNameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QariNameAdapter.QariNameViewHolder holder, int position) {


        QraiNameItem nameItem = qraiNameItems.get(position);

        holder.textViewQariNameList.setText(nameItem.getName());



        if (!searchText.isEmpty()) {
            String name = nameItem.getName();
            String lower = normalize(name);
            String s = normalize(searchText);

            int start = lower.indexOf(s);
            if (start >= 0) {
                android.text.SpannableString span =
                        new android.text.SpannableString(name);
                span.setSpan(
                        new ForegroundColorSpan(
                                context.getColor(R.color.colorPrimary)),
                        start, start + s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                holder.textViewQariNameList.setText(span);
            } else {
                holder.textViewQariNameList.setText(name);
            }
        } else {
            holder.textViewQariNameList.setText(nameItem.getName());
        }


        holder.itemView.setOnClickListener(v->{

            SharedPreferences sharedPref = context.getSharedPreferences("QariName",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("name", nameItem.getName());
            editor.putString("db_name", nameItem.getDatabaseName());
            editor.putString("url", nameItem.getUrl());
            editor.apply();
            saveRecentQari(nameItem);



            if (listener != null) {
                listener.onQariSelected(nameItem); // 🔥 REAL-TIME CALLBACK
            }

            QuranPage.editSearch.setText("");
            QuranPage.dialog.dismiss();

        });

        if (isFavorite(nameItem.getName())) {
            holder.textViewQariNameList.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.star, 0, 0, 0
            );
        } else {
            holder.textViewQariNameList.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, 0, 0
            );
        }
        if (isSelected(nameItem.getName())) {
            holder.itemView.setBackgroundResource(R.drawable.background_search_item_select);

            holder.textViewQariNameList.setTextColor((context.getResources().getColor(R.color.textColorWhite)));
        } else {

            holder.itemView.setBackgroundResource(R.drawable.background_search_item);
            holder.textViewQariNameList.setTextColor(context.getResources().getColor(R.color.textColorBlack));
        }

        holder.itemView.setOnLongClickListener(v -> {

            SharedPreferences pref = context.getSharedPreferences("FavQari", MODE_PRIVATE);

            boolean fav = pref.getBoolean(nameItem.getName(), false);

            pref.edit().putBoolean(nameItem.getName(), !fav).apply();

            holder.textViewQariNameList.animate()
                    .scaleX(isFavorite(nameItem.getName()) ? 1.05f : 1f)
                    .scaleY(isFavorite(nameItem.getName()) ? 1.05f : 1f)
                    .setDuration(200)
                    .start();



            notifyDataSetChanged();
            Toast.makeText(context,
                    fav ? "لابردرا" : "وەک دڵخواز نیشانکرا",
                    Toast.LENGTH_SHORT).show();
            return true;
        });



    }

    @Override
    public int getItemCount() {
        return qraiNameItems.size();
    }


    private boolean isSelected(String name) {
        SharedPreferences pref = context.getSharedPreferences("QariName", MODE_PRIVATE);
        return name.equals(pref.getString("name", ""));
    }


    private String normalize(String s) {
        return s.replace("أ","ا")
                .replace("إ","ا")
                .replace("آ","ا")
                .replace("ة","ه")
                .replace("ى","ي")
                .toLowerCase();
    }


    public void filter(String text) {
        searchText = text;
        qraiNameItems.clear();

        if (text.isEmpty()) {
            qraiNameItems.addAll(originalList);
        } else {
            String n = normalize(text);
            for (QraiNameItem item : originalList) {
                if (normalize(item.getName()).contains(n)) {
                    qraiNameItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
    private boolean isFavorite(String name) {
        SharedPreferences pref = context.getSharedPreferences("FavQari", MODE_PRIVATE);
        return pref.getBoolean(name, false);
    }

    public void setData(List<QraiNameItem> newList) {
        qraiNameItems.clear();
        qraiNameItems.addAll(newList);

        originalList.clear();
        originalList.addAll(newList);

        notifyDataSetChanged();
    }


    private void saveRecentQari(QraiNameItem item) {
        SharedPreferences pref = context.getSharedPreferences("RecentQari", MODE_PRIVATE);
        String old = pref.getString("list", "");

        String entry = item.getName() + "||" + item.getDatabaseName() + "||" + item.getUrl();

        if (!old.contains(entry)) {
            old = entry + "##" + old;
        }

        // keep only last 5
        String[] parts = old.split("##");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(5, parts.length); i++) {
            sb.append(parts[i]).append("##");
        }

        pref.edit().putString("list", sb.toString()).apply();
    }


    public static class QariNameViewHolder extends RecyclerView.ViewHolder {

        TextView textViewQariNameList;
        public QariNameViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewQariNameList=itemView.findViewById(R.id.textViewQariNameList);

        }
    }
}
