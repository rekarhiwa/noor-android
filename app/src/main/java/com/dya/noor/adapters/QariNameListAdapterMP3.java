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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.interfaces.QariSelectListener;
import com.dya.noor.interfaces.QariSelectListenerMp3;
import com.dya.noor.module.QariNameListMP3Item;
import com.dya.noor.module.QraiNameItem;

import java.util.ArrayList;
import java.util.List;

public class QariNameListAdapterMP3 extends RecyclerView.Adapter<QariNameListAdapterMP3.QariNameViewHolderMP3> {
    List<QariNameListMP3Item> qariNameListMP3Items = new ArrayList<>();
    Context context;
    QariSelectListenerMp3 listener;

    List<QariNameListMP3Item> originalList = new ArrayList<>();

    String searchText = "";


    public QariNameListAdapterMP3(List<QariNameListMP3Item> qariNameListMP3Items, Context context, QariSelectListenerMp3 listener) {
        this.qariNameListMP3Items = qariNameListMP3Items;
        this.context = context;
        this.listener = listener;
        originalList.clear();
        originalList.addAll(qariNameListMP3Items);
    }

    @NonNull
    @Override
    public QariNameViewHolderMP3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.qari_name_list_mp3,parent,false);
        return new QariNameListAdapterMP3.QariNameViewHolderMP3(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QariNameViewHolderMP3 holder, int position) {


        QariNameListMP3Item nameListMP3Item = qariNameListMP3Items.get(position);
        holder.textViewQariName.setText(nameListMP3Item.getName());


        if (!searchText.isEmpty()) {
            String name = nameListMP3Item.getName();
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
                holder.textViewQariName.setText(span);
            } else {
                holder.textViewQariName.setText(name);
            }
        }
        else {
            holder.textViewQariName.setText(nameListMP3Item.getName());
        }

        if (isSelected(nameListMP3Item.getName())) {
            holder.itemView.setBackgroundResource(R.drawable.background_search_item_select);

            holder.textViewQariName.setTextColor((context.getResources().getColor(R.color.textColorWhite)));
        }
        else {

            holder.itemView.setBackgroundResource(R.drawable.background_search_item);
            holder.textViewQariName.setTextColor(context.getResources().getColor(R.color.textColorBlack));
        }

        holder.itemView.setOnClickListener(v -> {

            if(listener != null){
                listener.onQariSelected(nameListMP3Item);
            }

        });
    }

    @Override
    public int getItemCount() {
        return qariNameListMP3Items.size();
    }




    private boolean isSelected(String name) {
        SharedPreferences pref = context.getSharedPreferences("QariName", MODE_PRIVATE);
        return name.equals(pref.getString("nameQari", "أبو بکر الشاطري"));
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
        qariNameListMP3Items.clear();

        if (text.isEmpty()) {
            qariNameListMP3Items.addAll(originalList);
        } else {
            String n = normalize(text);
            for (QariNameListMP3Item item : originalList) {
                if (normalize(item.getName()).contains(n)) {
                    qariNameListMP3Items.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
    public void setData(List<QariNameListMP3Item> newList) {
        qariNameListMP3Items.clear();
        qariNameListMP3Items.addAll(newList);

        originalList.clear();
        originalList.addAll(newList);

        notifyDataSetChanged();
    }


    public static class QariNameViewHolderMP3 extends RecyclerView.ViewHolder {

        TextView textViewQariName;
        public QariNameViewHolderMP3(@NonNull View itemView) {
            super(itemView);

            textViewQariName = itemView.findViewById(R.id.textViewQariName);
        }
    }
}
