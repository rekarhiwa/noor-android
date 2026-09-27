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
import com.dya.noor.module.BookItem;
import com.dya.noor.activities.FarmwdaView;
import com.dya.noor.activities.KitabActivity;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BooksViewHolder> {

    Context context;
    List<BookItem> bookItems;


    public BooksAdapter(Context context, List<BookItem> bookItems) {
        this.context = context;
        this.bookItems = bookItems;
    }

    @NonNull
    @Override
    public BooksAdapter.BooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.item_book,parent,false);
        return new BooksAdapter.BooksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksAdapter.BooksViewHolder holder, int position) {

        BookItem item = bookItems.get(position);

        holder.bookName.setText(item.getName_kr());
        holder.hadithCount.setText(item.getCount()+" فەرموودە");

        holder.itemView.setOnClickListener(v -> {
            int ids = item.getId();
            if (ids == 37){
                Intent  intent = new Intent(context, FarmwdaView.class);
                context.startActivity(intent);
            }
            else {
                Intent intent = new Intent(context, KitabActivity.class);
                intent.putExtra("book_id",item.getId());
                intent.putExtra("book_name",item.getName_kr());
                context.startActivity(intent);
            }



        });

    }

    @Override
    public int getItemCount() {
        return bookItems.size();
    }

    public static class BooksViewHolder extends RecyclerView.ViewHolder {

        TextView bookName , hadithCount;
        public BooksViewHolder(@NonNull View itemView) {
            super(itemView);

            bookName = itemView.findViewById(R.id.bookName);
            hadithCount = itemView.findViewById(R.id.hadithCount);
        }
    }
}
