package com.dya.noor.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.module.HadithRow;

import java.util.List;

public class HadithAdapter extends RecyclerView.Adapter<HadithAdapter.ViewHolder> {

    Context context;
    List<HadithRow> rows;

    public String BooksName;
    private int highlightedPosition = -1;

    public void setHighlightedPosition(int position) {
        highlightedPosition = position;
        notifyItemChanged(position);
    }


    public HadithAdapter(Context context, String BooksName ,List<HadithRow> rows) {
        this.context = context;
        this.BooksName = BooksName;
        this.rows = rows;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_hadith, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HadithRow row = rows.get(position);



        if (row.type == HadithRow.TYPE_CHAPTER) {

            // Show chapter
            holder.layoutChapter.setVisibility(VISIBLE);
            holder.txtArabic.setText(row.chapterTitleAr);
            holder.txtKurdish.setText(row.chapterTitleKr);
            holder.txtSort.setText(String.valueOf(row.chapterSort));



            // Hide hadith
            holder.layoutHadith.setVisibility(GONE);
            holder.textViewHadithArabic.setText("");
            holder.textViewHadithKurdish.setText("");

        }
        else if (row.type == HadithRow.TYPE_HADITH && row.hadith != null) {


            if (position == highlightedPosition) {
                holder.itemView.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.hadith_highlight)
                );
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }

            // Show hadith
            holder.layoutHadith.setVisibility(VISIBLE);

            //String fullArabic = row.hadith.getSort_in_book() + ". " + row.hadith.getText_ar();

            // Format the Arabic and Kurdish text
            holder.textViewHadithArabic.setText(formatCustomTags(row.hadith.getSort_in_book()
                    + ". " + row.hadith.getText_ar()));

            holder.textViewHadithKurdish.setText(formatCustomTags(row.hadith.getText_kr()));
            holder.textTebiny.setText(formatCustomTags(row.hadith.getNote()));
            holder.textSanad.setText(formatCustomTags(row.hadith.getFootnote()));

            String note = row.hadith.getNote();

            if (note == null || note.trim().isEmpty()) {
                holder.textTebiny.setVisibility(GONE);
                holder.textTebiny.setText("");
            } else {
                holder.textTebiny.setVisibility(VISIBLE);
                holder.textTebiny.setText(
                        formatCustomTags("تێبینی :\n" + note)
                );
            }




            String sanad = row.hadith.getFootnote();

            if (sanad == null || sanad.trim().isEmpty()) {
                holder.textSanad.setVisibility(GONE);
                holder.textSanad.setText("");
            } else {
                holder.textSanad.setVisibility(VISIBLE);
                holder.textSanad.setText(formatCustomTags(sanad));
            }


            // Hide chapter
            holder.layoutChapter.setVisibility(GONE);
            holder.txtArabic.setText("");
            holder.txtKurdish.setText("");
        }

        String packageName = context.getPackageName();

        holder.copy.setOnClickListener(v -> {


            StringBuilder sb = new StringBuilder();

            appendIfNotEmpty(sb, row.hadith.getNormalized_text_ar());
            appendIfNotEmpty(sb, row.hadith.getNormalized_text());
            appendIfNotEmpty(sb, row.hadith.getNote());
            appendIfNotEmpty(sb, row.hadith.getFootnote());
            appendIfNotEmpty(sb, row.chapterTitleAr);
            appendIfNotEmpty(sb, row.chapterTitleKr);
            appendIfNotEmpty(sb, "لە کتێبی : "+BooksName);
            appendIfNotEmpty(sb, "https://play.google.com/store/apps/details?id=" + packageName);

            String textToCopy = sb.toString();



            ClipboardManager clipboard = (ClipboardManager)
                    context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("hadith", textToCopy);
            clipboard.setPrimaryClip(clipData);
            Toast.makeText(context, "کۆپی کرا", Toast.LENGTH_SHORT).show();


        });
        holder.share.setOnClickListener(v -> {
            StringBuilder sb = new StringBuilder();

            appendIfNotEmpty(sb, row.hadith.getNormalized_text_ar());
            appendIfNotEmpty(sb, row.hadith.getNormalized_text());
            appendIfNotEmpty(sb, row.hadith.getNote());
            appendIfNotEmpty(sb, row.hadith.getFootnote());
            appendIfNotEmpty(sb, row.chapterTitleAr);
            appendIfNotEmpty(sb, row.chapterTitleKr);
            appendIfNotEmpty(sb, "لە کتێبی : "+BooksName);
            appendIfNotEmpty(sb, "https://play.google.com/store/apps/details?id=" + packageName);

            String textToShare = sb.toString();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);

            context.startActivity(
                    Intent.createChooser(shareIntent, "ئەم حەدیثە بخوێنەرەوە ")
            );
        });
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView layoutChapter;
        LinearLayout layoutHadith;
        TextView txtArabic, txtKurdish , txtSort;
        TextView textViewHadithArabic, textViewHadithKurdish;
        TextView textTebiny, textSanad;
        ImageView copy ,share;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutChapter = itemView.findViewById(R.id.layoutChapter);
            layoutHadith = itemView.findViewById(R.id.layoutHadith);

            txtArabic = itemView.findViewById(R.id.txtArabic);
            txtKurdish = itemView.findViewById(R.id.txtKurdish);

            textViewHadithArabic = itemView.findViewById(R.id.textViewHadithArabic);
            textViewHadithKurdish = itemView.findViewById(R.id.textViewHadithKurdish);

            textSanad = itemView.findViewById(R.id.textSanad);
            textTebiny = itemView.findViewById(R.id.textTebiny);
            txtSort = itemView.findViewById(R.id.txtSort);

            share = itemView.findViewById(R.id.btnShare);
            copy = itemView.findViewById(R.id.btnCopy);
        }
    }


    private void appendIfNotEmpty(StringBuilder sb, String text) {
        if (text != null && !text.trim().isEmpty() && !"null".equals(text)) {
            if (sb.length() > 0) {
                sb.append("\n\n");
            }
            sb.append(text);
        }
    }


    private SpannableStringBuilder formatCustomTags(String input) {
        if (input == null || input.equals("null")) {
            return new SpannableStringBuilder("");
        }

        input = input.replace("null", "");

        // Use a StringBuilder to manage the text transformations
        SpannableStringBuilder sb = new SpannableStringBuilder(input);

        // 1. Process <rr> tags (Replacement logic)
        processReplacementTags(sb, "<rr>", "</rr>");

        // 2. Process <gg> tags (Spacing logic)
        // Note: You mentioned </gg>...<gg>, but usually tags are <gg>...</gg>
        // This logic handles the text INSIDE the tags.
        processSpacingTags(sb, "<gg>", "</gg>");

        // 3. Process standard styling tags
        applyStyle(sb, "<zz>", "</zz>", Color.parseColor("#13832F"), 1.2f); // Green/Large
        applyStyle(sb, "<ww>", "</ww>", Color.parseColor("#13832F"), 0.9f); // Gray/Small

        applyPeetFontForLetters(sb);
        return sb;
    }

    private void processReplacementTags(SpannableStringBuilder sb, String openTag, String closeTag) {
        int start;
        while ((start = sb.toString().indexOf(openTag)) != -1) {
            int end = sb.toString().indexOf(closeTag, start);
            if (end == -1) break;

            // Get the letter inside, e.g., "h" or "k"
            String innerText = sb.toString().substring(start + openTag.length(), end);
            String replacement = innerText;



            // Replace the whole tag and content with the replacement word
            sb.replace(start, end + closeTag.length(), replacement);

            // Apply Red Color to the replacement text
            sb.setSpan(new ForegroundColorSpan(Color.parseColor("#F88103")), start, start + replacement.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void processSpacingTags(SpannableStringBuilder sb, String openTag, String closeTag) {
        int start;
        while ((start = sb.toString().indexOf(openTag)) != -1) {
            int end = sb.toString().indexOf(closeTag, start);
            if (end == -1) break;

            // Get text and add spaces
            String content = " " + sb.toString().substring(start + openTag.length(), end) + " ";

            // Replace tag with spaced content
            sb.replace(start, end + closeTag.length(), content);

            // Apply specific color for <gg> if needed
            sb.setSpan(new ForegroundColorSpan(Color.parseColor("#006FBA")), start, start + content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void applyStyle(SpannableStringBuilder sb, String openTag, String closeTag, int color, float size) {
        int start;
        while ((start = sb.toString().indexOf(openTag)) != -1) {
            int end = sb.toString().indexOf(closeTag, start);
            if (end == -1) break;

            sb.setSpan(new ForegroundColorSpan(color), start, end + closeTag.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new RelativeSizeSpan(size), start, end + closeTag.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            sb.delete(end, end + closeTag.length());
            sb.delete(start, start + openTag.length());
        }
    }

    private void applyPeetFontForLetters(SpannableStringBuilder sb) {

        // letters & characters you want
        String targets = "hskijbn﴿﴾";

        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);

            if (targets.indexOf(c) != -1) {

                sb.setSpan(
                        new android.text.style.MetricAffectingSpan() {
                            @Override
                            public void updateDrawState(android.text.TextPaint tp) {
                                tp.setTypeface(
                                        ResourcesCompat.getFont(context, R.font.peet_bold)

                                );
                            }

                            @Override
                            public void updateMeasureState(android.text.TextPaint tp) {
                                tp.setTypeface(
                                        ResourcesCompat.getFont(context, R.font.peet_bold)
                                );
                            }
                        },
                        i,
                        i + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }
    }


}

