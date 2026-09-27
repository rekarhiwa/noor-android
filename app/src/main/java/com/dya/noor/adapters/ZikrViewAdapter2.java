package com.dya.noor.adapters;

import android.content.Context;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;

import java.util.ArrayList;

public class ZikrViewAdapter2 extends RecyclerView.Adapter<ZikrViewAdapter2.MyViewHolder> {

    ArrayList  aArZ ;
    ArrayList aKrZ ;
    Context context;
    ArrayList<Integer> minList;
    ArrayList<Integer> maxList;
    int current;
    public ZikrViewAdapter2(Context ct , ArrayList aArZ, ArrayList aKrZ,
                            ArrayList<Integer> minList,
                            ArrayList<Integer> maxList){
        context = ct;
        this.aArZ =aArZ;
        this.aKrZ =aKrZ;
        this.minList = minList;
        this.maxList = maxList;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.my_dhikr_view_row2,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.zAr.setText(String.valueOf(aArZ.get(position)));
        holder.zKr.setText(String.valueOf(aKrZ.get(position)));
        current = maxList.get(position);
        int min = minList.get(position);
        int max = maxList.get(position);

        if(current == 0){
            holder.countText.setText("✔");
        }else if(min == max){
            holder.countText.setText(String.valueOf(min));
        }else{
            holder.countText.setText(min + " / " + max);
        }



        holder.countText.setOnClickListener(v -> {

            current = maxList.get(position);

            if(current > 0){
                current--;
                maxList.set(position, current);


                // 🔥 when reaches 0
                if(current == 0){
                    holder.countText.setText("✔");

                    // 📳 vibrate
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.vibrate(100);
                    }
                }

                if(minList.get(position) == current){
                    // optional: reached minimum ✔
                }

                notifyItemChanged(position);
            }

        });


    }

    @Override
    public int getItemCount() {
        return aArZ.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView zAr , zKr , countText;
        LinearLayout constraintLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            zAr= itemView.findViewById(R.id.zAr);
            zKr= itemView.findViewById(R.id.zKr);
            countText= itemView.findViewById(R.id.countText);
            constraintLayout = itemView.findViewById(R.id.zikrViewLayout2);




        }
    }
}
