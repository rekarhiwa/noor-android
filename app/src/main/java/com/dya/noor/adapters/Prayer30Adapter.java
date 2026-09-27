package com.dya.noor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.module.PrayerModel;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class Prayer30Adapter extends RecyclerView.Adapter<Prayer30Adapter.MyHolder>{

    Context context;
    List<PrayerModel> list;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    public Prayer30Adapter(Context context, List<PrayerModel> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_prayer_30day,parent,false);

        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder h, int position) {

        PrayerModel p = list.get(position);

        if(getItemViewType(position) == TYPE_HEADER){

            h.date.setText("بەروار");
            h.bayani.setText("بەیانی");
            h.niwaro.setText("نیوەڕۆ");
            h.asr.setText("عەسر");
            h.eywara.setText("ئێوارە");
            h.esha.setText("خەوتنان");

        }else{

            h.bayani.setText(convertTo12Hour(p.bayani));
            h.niwaro.setText(convertTo12Hour(p.niwaro));
            h.asr.setText(convertTo12Hour(p.asr));
            h.eywara.setText(convertTo12Hour(p.eywara));
            h.esha.setText(convertTo12Hour(p.esha));
            h.date.setText(p.date);
        }
    }

    @Override
    public int getItemCount(){
        return list.size();
    }


    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_HEADER;
        }else{
            return TYPE_ITEM;
        }
    }

    private String convertTo12Hour(String time24){

        try{
            java.text.SimpleDateFormat input =
                    new java.text.SimpleDateFormat("HH:mm", java.util.Locale.US);

            java.text.SimpleDateFormat output =
                    new java.text.SimpleDateFormat("hh:mm", java.util.Locale.US);

            java.util.Date date = input.parse(time24);

            return output.format(date);

        }catch(Exception e){
            return time24; // if error return original
        }
    }


    static class MyHolder extends RecyclerView.ViewHolder{

        TextView date,bayani,niwaro,asr,eywara,esha;

        public MyHolder(@NonNull View v){
            super(v);

            date = v.findViewById(R.id.txtDate);
            bayani = v.findViewById(R.id.txtBayani);
            niwaro = v.findViewById(R.id.txtNiwaro);
            asr = v.findViewById(R.id.txtAsr);
            eywara = v.findViewById(R.id.txtEywra);
            esha = v.findViewById(R.id.txtEsha);
        }
    }
}

