package com.dya.noor.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.activities.Tasbih;
import com.dya.noor.utlis.UtilsT;

public class TasbihAdapter extends RecyclerView.Adapter<TasbihAdapter.MyViewHolder> {


    String data1[];
    Context context;
    OnItemClickListener listener;

    public TasbihAdapter(Context ct, String s1[], OnItemClickListener listener){
        context = ct;
        data1 = s1;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TasbihAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.tasbih_item,parent,false);
        return new TasbihAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TasbihAdapter.MyViewHolder holder, int position) {
        holder.mZikr.setText(data1[position]);
        String item=data1[position];

        holder.constraintLayout.setOnClickListener(v -> {

            listener.onItemClick(item);


        });
    }

    @Override
    public int getItemCount() {
        return data1.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mZikr;
        CardView constraintLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mZikr= itemView.findViewById(R.id.TxtZikr);
            constraintLayout = itemView.findViewById(R.id.ZikrLayout);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(String item);
    }


}
