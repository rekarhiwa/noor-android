package com.dya.noor.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.module.KitabItem;
import com.dya.noor.activities.HadithActivity;

import java.util.List;

public class KitabAdapter extends RecyclerView.Adapter<KitabAdapter.KitabViewHolder> {


    Context context;

    List<KitabItem> kitabItems;

    public String BooksName;


    public KitabAdapter(Context context, String BooksName ,List<KitabItem> kitabItems) {
        this.context = context;
        this.BooksName = BooksName;
        this.kitabItems = kitabItems;
    }

    @NonNull
    @Override
    public KitabAdapter.KitabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_kitab,parent,false);
        return new KitabAdapter.KitabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KitabAdapter.KitabViewHolder holder, int position) {

        KitabItem item = kitabItems.get(position);
        holder.txtId.setText(""+item.getKitab_sort());
        holder.txtName_krd.setText(item.getTitle_kr());
        holder.txtNAme_ar.setText(item.getTitle_ar());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HadithActivity.class);
            intent.putExtra("kitab_id",item.getId());
            intent.putExtra("book_name", BooksName);
            intent.putExtra("kitab_name", item.getTitle_kr());
            intent.putExtra("Hadith_id", 0);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return kitabItems.size();
    }

    public  static class KitabViewHolder extends RecyclerView.ViewHolder {

        TextView txtId,txtName_krd,txtNAme_ar;

        public KitabViewHolder(@NonNull View itemView) {
            super(itemView);

            txtId = itemView.findViewById(R.id.txtId);
            txtName_krd = itemView.findViewById(R.id.txtKurdish);
            txtNAme_ar = itemView.findViewById(R.id.txtArabic);

        }
    }
}
