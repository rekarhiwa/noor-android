package com.dya.noor.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.interfaces.SuraSelectListener;
import com.dya.noor.module.ActivityItem;
import com.dya.noor.module.QuranSuraLisMP3Item;

import java.util.List;

public class QuranSuraListAdapterMP3 extends RecyclerView.Adapter<QuranSuraListAdapterMP3.QuranSuraMP3ViewHolder> {

    Context context;
    List<QuranSuraLisMP3Item> suraLisMP3Items;
    SuraSelectListener listener;

    public QuranSuraListAdapterMP3(Context context, List<QuranSuraLisMP3Item> suraLisMP3Items,
                                   SuraSelectListener listener) {
        this.context = context;
        this.suraLisMP3Items = suraLisMP3Items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuranSuraListAdapterMP3.QuranSuraMP3ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.sura_list_item_mp3,parent,false);
        return new QuranSuraMP3ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuranSuraListAdapterMP3.QuranSuraMP3ViewHolder holder, int position) {

        QuranSuraLisMP3Item lisMP3Item = suraLisMP3Items.get(position);

        String links = lisMP3Item.getLink();

        holder.textViewId.setText(String.valueOf(lisMP3Item.getId()));
        holder.textViewName.setText(lisMP3Item.getName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSuraSelected(lisMP3Item);
            }
        });



    }

    @Override
    public int getItemCount() {
        return suraLisMP3Items.size();
    }

    public static class QuranSuraMP3ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewId,textViewName;

        public QuranSuraMP3ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
            textViewId = itemView.findViewById(R.id.textViewId);

        }
    }
}
