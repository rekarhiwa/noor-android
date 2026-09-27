package com.dya.noor.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.module.wallamakanquestionItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class WallamakanQuestionAdapter extends RecyclerView.Adapter<WallamakanQuestionAdapter.WallamakanQuestionViewHolder> {

    Context context;
    List<wallamakanquestionItem> questionItems;
    public WallamakanQuestionAdapter(Context context, List<wallamakanquestionItem> questionItems) {
        this.context = context;
        this.questionItems = questionItems;
    }

    @NonNull
    @Override
    public WallamakanQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.wallamakan_question_row,parent,false);
        return new WallamakanQuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallamakanQuestionViewHolder holder, int position) {

        wallamakanquestionItem item = questionItems.get(position);

        Spanned spanned = HtmlCompat.fromHtml(item.getShortTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY);
        holder.tvWallamakanQuestion.setText(spanned);
        holder.wallamakanQuestionCardView.setOnClickListener(v -> {

            BottomSheetDialog dialog = new BottomSheetDialog(v.getContext(), R.style.BottomSheetStyle);
            dialog.setContentView(R.layout.wallamakan_bottomshet);
            TextView tvWallam= dialog.findViewById(R.id.tvWallam);
            ImageView btnCopy= dialog.findViewById(R.id.btnCopy);
            Spanned wSpanned = HtmlCompat.fromHtml(item.getTitle()+"<br>"+item.getAsk()+"<br>"
                    +item.getQuestion()+"<br>"+item.getKwrte(), HtmlCompat.FROM_HTML_MODE_LEGACY);

            tvWallam.setText(wSpanned);

            StringBuilder copyText = new StringBuilder();

            // Ayah
            copyText.append(tvWallam.getText());
            copyText.append("https://play.google.com/store/apps/details?id=com.dya.noor");
            copyText.append("\n\n")
                    .append("#ئەپڵیکەیشنی_نور");

            assert btnCopy != null;
            btnCopy.setOnClickListener(v1 -> {
                ClipboardManager clipboardManager =
                        (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

                ClipData clipData = ClipData.newPlainText("text", copyText.toString());
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(context, "کۆپی بوو", Toast.LENGTH_SHORT).show();

            });

            dialog.show();


        });

    }

    @Override
    public int getItemCount() {
        return questionItems.size();
    }

    public static class WallamakanQuestionViewHolder extends RecyclerView.ViewHolder{

        public CardView wallamakanQuestionCardView;
        LinearLayout wallamakanQuestionLayout;
        TextView tvWallamakanQuestion;

        public WallamakanQuestionViewHolder(@NonNull View itemView) {
            super(itemView);

            wallamakanQuestionCardView=itemView.findViewById(R.id.wallamakanQuestionCardView);
            wallamakanQuestionLayout=itemView.findViewById(R.id.wallamakanQuestionLayout);
            tvWallamakanQuestion=itemView.findViewById(R.id.tvWallamakanQuestion);
        }
    }



}
