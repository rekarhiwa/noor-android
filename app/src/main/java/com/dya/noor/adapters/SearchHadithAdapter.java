package com.dya.noor.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.activities.HadithActivity;
import com.dya.noor.module.SearchHadithItem;

import java.util.List;

public class SearchHadithAdapter  extends RecyclerView.Adapter<SearchHadithAdapter.SearchHadithViewHolder> {
    Context context;
    List<SearchHadithItem> searchHadithItems ;

    String keyword = "";

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public SearchHadithAdapter(Context context, List<SearchHadithItem> searchHadithItems) {
        this.context = context;
        this.searchHadithItems = searchHadithItems;
    }

    @NonNull
    @Override
    public SearchHadithAdapter.SearchHadithViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.item_search_hadith,parent,false);
        return new SearchHadithAdapter.SearchHadithViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHadithAdapter.SearchHadithViewHolder holder, int position) {


        SearchHadithItem item = searchHadithItems.get(position);

        String ArabicText = item.getNormalized_text_ar();
        String KurdishText = item.getNormalized_text();
        ArabicText = ArabicText.replace("<>", "(")
                .replace("</>", ")");

        KurdishText = KurdishText.replace("<>", "(")
                .replace("</>", ")");

        holder.textSearchViewHadithArabic.setText(
                highlight(ArabicText, keyword)
        );

        holder.textSearchViewHadithKurdish.setText(
                highlight(KurdishText, keyword)
        );

        holder.textSearchKitabNameSearch.setText(item.getKitabName());
        holder.textBookNameSearch.setText(item.getBookNAme());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HadithActivity.class);
            intent.putExtra("kitab_id",item.getKitab_id());
            intent.putExtra("book_name", item.getBookNAme());
            intent.putExtra("kitab_name", item.getKitabName());
            intent.putExtra("Hadith_id", item.getId());
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return searchHadithItems.size();
    }



    private Spannable highlight(String text, String keyword) {
        SpannableString spannable = new SpannableString(text);

        int start = text.toLowerCase().indexOf(keyword.toLowerCase());
        while (start >= 0) {
            int end = start + keyword.length();
            spannable.setSpan(
                    new ForegroundColorSpan(Color.RED),
                    start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            start = text.toLowerCase().indexOf(keyword.toLowerCase(), end);
        }
        return spannable;
    }


    public static class SearchHadithViewHolder extends RecyclerView.ViewHolder {

        TextView textSearchViewHadithArabic, textSearchViewHadithKurdish,
                textSearchKitabNameSearch, textBookNameSearch;
        public SearchHadithViewHolder(@NonNull View itemView) {
            super(itemView);

            textSearchViewHadithArabic =itemView.findViewById(R.id.textSearchViewHadithArabic);
            textSearchViewHadithKurdish =itemView.findViewById(R.id.textSearchViewHadithKurdish);
            textBookNameSearch =itemView.findViewById(R.id.textBookNameSearch);
            textSearchKitabNameSearch =itemView.findViewById(R.id.textSearchKitabNameSearch);

        }
    }
}
