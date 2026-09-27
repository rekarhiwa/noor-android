package com.dya.noor.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.activities.QuranPage;
import com.dya.noor.interfaces.QariSelectListener;
import com.dya.noor.module.QraiNameItem;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class RecentQariAdapter extends RecyclerView.Adapter<RecentQariAdapter.Holder> {

    Context context;
    List<QraiNameItem> list;
    QariSelectListener listener;

    public RecentQariAdapter(Context context, List<QraiNameItem> list, QariSelectListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView tv = new TextView(context);

        //or to support all versions use
        Typeface typeface = ResourcesCompat.getFont(context, R.font.rabar015);
        tv.setTypeface(typeface);



        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        tv.setLayoutParams(params);
        tv.setPadding(24,16,24,16);
        tv.setBackgroundResource(R.drawable.background_search_item);
        tv.setTextColor(context.getColor(R.color.textColorBlack));
        return new Holder(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        QraiNameItem item = list.get(position);
        holder.textView.setText(item.getName());

        holder.textView.setOnClickListener(v -> {
            listener.onQariSelected(item);
            QuranPage.dialog.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView textView;
        Holder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
