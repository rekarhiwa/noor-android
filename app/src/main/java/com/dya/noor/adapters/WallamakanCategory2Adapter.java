package com.dya.noor.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.module.wallamakanCategoryItem2;
import com.dya.noor.activities.WallamakanQuestion;

import java.util.List;

public class WallamakanCategory2Adapter extends RecyclerView.Adapter<WallamakanCategory2Adapter.WallamakanCategory2ViewHolder>{

    Context context;
    List<wallamakanCategoryItem2> category2Items;

    public WallamakanCategory2Adapter(Context context, List<wallamakanCategoryItem2> category2Items) {
        this.context = context;
        this.category2Items = category2Items;
    }

    @NonNull
    @Override
    public WallamakanCategory2ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.wallamakan_categore_row,parent,false);
        return new WallamakanCategory2Adapter.WallamakanCategory2ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallamakanCategory2ViewHolder holder, int position) {

        wallamakanCategoryItem2 item = category2Items.get(position);
        holder.tvWallamakanCategory.setText(item.getTitle());
        holder.wallamakanCategoryCardView.setOnClickListener(v -> {
                Intent intent = new Intent(context, WallamakanQuestion.class);
                intent.putExtra("catId",item.getId());
                intent.putExtra("ActName","WallamakanCategory2");
                context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return category2Items.size();
    }

    public static class WallamakanCategory2ViewHolder extends RecyclerView.ViewHolder{

        TextView tvWallamakanCategory;
        LinearLayout wallamakanCategoryLayout;
        CardView wallamakanCategoryCardView;
        public WallamakanCategory2ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWallamakanCategory = itemView.findViewById(R.id.tvWallamakanCategory);
            wallamakanCategoryLayout = itemView.findViewById(R.id.wallamakanCategoryLayout);
            wallamakanCategoryCardView = itemView.findViewById(R.id.wallamakanCategoryCardView);

        }
    }
}
