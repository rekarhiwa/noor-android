package com.dya.noor.adapters;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.module.wallamakanquestionItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class WallamakanSearchAdapter extends RecyclerView.Adapter<WallamakanSearchAdapter.WallamakanSearchViewHolder> implements Filterable {

    Context context;
    List<wallamakanquestionItem> questionItems;
    List<wallamakanquestionItem> questionItemsSearch;
    public WallamakanSearchAdapter(Context context, List<wallamakanquestionItem> questionItems) {
        this.context = context;
        this.questionItems = questionItems;
        this.questionItemsSearch = new ArrayList<>(questionItems);;
    }

    @NonNull
    @Override
    public WallamakanSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.wallamakan_question_row,parent,false);
        return new WallamakanSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallamakanSearchViewHolder holder, int position) {

        wallamakanquestionItem item = questionItems.get(position);

        Spanned spanned = HtmlCompat.fromHtml(item.getShortTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY);
        holder.tvWallamakanQuestion.setText(spanned);
        holder.wallamakanQuestionCardView.setOnClickListener(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(v.getContext(), R.style.BottomSheetStyle);
            dialog.setContentView(R.layout.wallamakan_bottomshet);
            TextView tvWallam= dialog.findViewById(R.id.tvWallam);
            Spanned wSpanned = HtmlCompat.fromHtml(item.getTitle()+"<br>"+item.getAsk()+"<br>"
                    +item.getQuestion()+"<br>"+item.getKwrte(), HtmlCompat.FROM_HTML_MODE_LEGACY);

            tvWallam.setText(wSpanned);
            dialog.show();


        });

    }

    @Override
    public int getItemCount() {
        return questionItems.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List <wallamakanquestionItem> filterList = new ArrayList<>();

            if (charSequence.toString().isEmpty()){
                filterList.addAll(questionItemsSearch);
            }else {

                for (wallamakanquestionItem text : questionItemsSearch ){
                    if (text.getShortTitle().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filterList.add(text);
                    }else if (text.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filterList.add(text);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            questionItems.clear();
            questionItems.addAll((List)filterResults.values);
            notifyDataSetChanged();

        }
    };
    public static class WallamakanSearchViewHolder extends RecyclerView.ViewHolder{

        CardView wallamakanQuestionCardView;
        LinearLayout wallamakanQuestionLayout;
        TextView tvWallamakanQuestion;

        public WallamakanSearchViewHolder(@NonNull View itemView) {
            super(itemView);

            wallamakanQuestionCardView=itemView.findViewById(R.id.wallamakanQuestionCardView);
            wallamakanQuestionLayout=itemView.findViewById(R.id.wallamakanQuestionLayout);
            tvWallamakanQuestion=itemView.findViewById(R.id.tvWallamakanQuestion);
        }
    }
}
