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
import com.dya.noor.module.wallamakanCategoryItem;
import com.dya.noor.activities.WallamakanCategory;
import com.dya.noor.activities.WallamakanQuestion;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WallamakanCategoryAdapter extends RecyclerView.Adapter<WallamakanCategoryAdapter.WallamakanCategoryViewHolder> {

    Context context;
    List<wallamakanCategoryItem> categoryItems;

    public WallamakanCategoryAdapter(Context context, List<wallamakanCategoryItem> categoryItems) {
        this.context = context;
        this.categoryItems = categoryItems;
    }

    @NonNull
    @Override
    public WallamakanCategoryAdapter.WallamakanCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.wallamakan_row,parent,false);
        return new WallamakanCategoryAdapter.WallamakanCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallamakanCategoryAdapter.WallamakanCategoryViewHolder holder, int position) {

        wallamakanCategoryItem item = categoryItems.get(position);
        holder.tvWallamakan.setText(item.getTitle());
        holder.wallamakanCardView.setOnClickListener(v -> {

            String id = item.getId();
            Set<String> validIds = new HashSet<>(Arrays.asList("3", "14", "18", "20", "21", "22", "25", "128", "132"));
            if (validIds.contains(id)) {
                Intent intent = new Intent(context, WallamakanQuestion.class);
                intent.putExtra("catId", item.getId());
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, WallamakanCategory.class);
                intent.putExtra("catId", item.getId());
                context.startActivity(intent);
            }

        });


    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }

    public static class WallamakanCategoryViewHolder extends RecyclerView.ViewHolder{
        TextView tvWallamakan;
        LinearLayout wallamakanLayout;
        CardView wallamakanCardView;
        public WallamakanCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWallamakan = itemView.findViewById(R.id.tvWallamakan);
            wallamakanLayout = itemView.findViewById(R.id.wallamakanLayout);
            wallamakanCardView = itemView.findViewById(R.id.wallamakanCardView);
        }
    }
}
