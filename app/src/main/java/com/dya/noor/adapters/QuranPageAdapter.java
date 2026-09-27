package com.dya.noor.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.module.QuranItem;
import com.dya.noor.activities.QuranPage;

import java.util.ArrayList;
import java.util.List;

public class QuranPageAdapter extends RecyclerView.Adapter<QuranPageAdapter.QuranViewHolder> {

    Context context;
    public String SurahNAme, SurahId;
    List<Integer> arPageNumbers;

    public static String suraName;
    public static int suraId;

    public static boolean isSubItemVisible;


    private OnButtonClickListener listener;

    private int highlightedAyah = -1;
    private int highlightedPage = -1;


    public void setHighlightedAyah(int ayah, int page) {
        // If the Ayah is the same, don't do anything (saves battery)
        if (this.highlightedAyah == ayah) return;

        int oldPage = this.highlightedPage;
        this.highlightedAyah = ayah;
        this.highlightedPage = page;

        // Find the positions in the RecyclerView
        int oldPos = findPositionForPage(oldPage);
        int newPos = findPositionForPage(page);

        // Only refresh the pages that need to change color
        if (oldPos != -1) {
            notifyItemChanged(oldPos);
        }

        // If the new Ayah is on a different page, refresh that too
        if (newPos != -1 && newPos != oldPos) {
            notifyItemChanged(newPos);
        } else if (newPos != -1) {
            // If it's the same page, we still need to refresh to move the highlight
            notifyItemChanged(newPos);
        }

    }

    // Inside QuranPageAdapter.java
    public int[] getAyahOffsets(int ayahNumber, int page) {
        ArrayList<QuranItem> arAyahForPage = QuranPage.mpAllAyah.get(page);
        int cursor = 0;
        for (QuranItem ayah : arAyahForPage) {
            String ayahText = ayah.getAya_text() + " " + (isSubItemVisible ? ayah.getAya_number_arabic_rev() : ayah.getAya_number_arabic()) + " ";
            if (ayah.getAyahNumberInt() == ayahNumber) {
                return new int[]{cursor, cursor + ayahText.length()};
            }
            cursor += ayahText.length();
        }
        return null;
    }

    public QuranPageAdapter(Context context, ArrayList<Integer> arPageNumbers ,OnButtonClickListener listener ) {
        this.context = context;
        this.arPageNumbers = arPageNumbers;
        this.listener=listener;
    }
    
    @NonNull
    @Override
    public QuranViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.quran_page_row, parent, false);
        return new QuranViewHolder(view);
    }
    
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull QuranViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SharedPreferences preferences = context.getSharedPreferences("myPreferences", MODE_PRIVATE);
        isSubItemVisible=preferences.getBoolean("isSubItemVisible", false);

        int currentPage = arPageNumbers.get(position);
        boolean isThisHighlightedPage = currentPage == highlightedPage;

        ArrayList<QuranItem> arAyahForPage = QuranPage.mpAllAyah.get(currentPage);
        
        StringBuilder allText = new StringBuilder("");


        // datay ayatakani har paraiak ziad akain bo ayahText
        for (QuranItem qt : arAyahForPage) {

            if (!isSubItemVisible) {
                allText.append(qt.getAya_text() + " "+qt.getAya_number_arabic());
            } else {
                allText.append(qt.getAya_text() + " "+qt.getAya_number_arabic_rev());
                }

        }

        SpannableStringBuilder span = new SpannableStringBuilder();
        int cursor = 0;

        for (QuranItem ayah : arAyahForPage) {

            String ayahText;
            if (!isSubItemVisible) {
                ayahText = ayah.getAya_text() + " " + ayah.getAya_number_arabic() + " ";
            } else {
                ayahText = ayah.getAya_text() + " " + ayah.getAya_number_arabic_rev() + " ";
            }

            int start = cursor;
            span.append(ayahText);
            int end = cursor + ayahText.length();
            cursor = end;

            // ✅ HIGHLIGHT BY TIMING
            if (isThisHighlightedPage && ayah.getAyahNumberInt() == highlightedAyah) {
                span.setSpan(
                        new BackgroundColorSpan(0x55FFD54F),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }


            // ✅ CLICKABLE AYAH
            span.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {

                    if (listener != null) {
                        listener.onAyahClick(ayah.getAyahNumberInt(), ayah.getAya_text());
                    }

                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(false); // Keeps text looking clean
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        suraName = arAyahForPage.get(0).getSura_name();
        holder.lblPageNumber.setText("لاپەڕە " + currentPage);
       // holder.lblSuraNAme.setText(suraName);
        if (arAyahForPage != null && arAyahForPage.size() > 0) {
            int juzzNumber = arAyahForPage.get(0).getJozzAsInt();
            holder.lblPageNumber.setText(holder.lblPageNumber.getText()+" | جوزء " + juzzNumber);
        }
        //atwanit esh bdait baw bashai sarawai parakash (heli awai tyay nwsrawa پەڕەی ).
        // Layout aw nwai loTopbar
        holder.loTopbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // agar atawet esh bdait baw bashai sarawa lera binwsa
            }
        });
        SharedPreferences sharedPreferencesSize = context.getSharedPreferences("size", MODE_PRIVATE);

        int quranTextSize = sharedPreferencesSize.getInt("quransize", 27);
        holder.lblAyahText.setMovementMethod(LinkMovementMethod.getInstance());
        holder.lblAyahText.setText(span);


        holder.lblAyahText.setTextSize(TypedValue.COMPLEX_UNIT_SP, quranTextSize);

        if (position==arPageNumbers.size()-1){
            holder.btnNextSura.setVisibility(View.VISIBLE);
            holder.iv_sadaqa.setVisibility(View.VISIBLE);
        }else {
            holder.btnNextSura.setVisibility(View.GONE);
            holder.iv_sadaqa.setVisibility(View.GONE);
        }

        if (suraId==114){
            holder.btnNextSura.setVisibility(View.GONE);
        }

        holder.btnNextSura.setOnClickListener(v -> {
            listener.onButtonClick(position);
        });





    }
    
    
    @Override
    public int getItemCount() {
        return arPageNumbers.size();
    }
    
    public static class QuranViewHolder extends RecyclerView.ViewHolder {
        public TextView lblAyahText;
        TextView lblPageNumber;
        TextView lblJuzNumber;
        TextView lblSuraNAme;
        TextView btnNextSura;
        // TextView quran_aya_number;
        // ImageButton btnCopy, btnSave;
        LinearLayout LinearLayout;
        LinearLayout loTopbar;

        ImageView iv_sadaqa;
        
        
        public QuranViewHolder(@NonNull View itemView) {
            super(itemView);
            
            lblAyahText = itemView.findViewById(R.id.lblAyahText);
            lblPageNumber = itemView.findViewById(R.id.lblPageNumber);
            btnNextSura = itemView.findViewById(R.id.btnNextSura);
            iv_sadaqa = itemView.findViewById(R.id.iv_sadaqa);

            LinearLayout = itemView.findViewById(R.id.ayaLayout);
            loTopbar = itemView.findViewById(R.id.loTopbar);
            
        }
    }

    public int findPositionForAyah(int ayahNumber) {
        // We need to return the INDEX of the page in arPageNumbers
        for (int i = 0; i < arPageNumbers.size(); i++) {
            int pageNumber = arPageNumbers.get(i);
            ArrayList<QuranItem> itemsInPage = QuranPage.mpAllAyah.get(pageNumber);

            if (itemsInPage != null) {
                for (QuranItem item : itemsInPage) {
                    if (item.getAyahNumberInt() == ayahNumber) {
                        return i; // Return the position of the PAGE in the RecyclerView
                    }
                }
            }
        }
        return -1;
    }


    private int findPositionForPage(int pageNumber) {
        for (int i = 0; i < arPageNumbers.size(); i++) {
            if (arPageNumbers.get(i) == pageNumber) {
                return i;
            }
        }
        return -1;
    }
    public interface OnButtonClickListener{
        void onButtonClick(int position);
        void onAyahClick(int ayahNumber, String ayahText);
    }
}
