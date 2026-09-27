package com.dya.noor.adapters;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dya.noor.R;
import com.dya.noor.module.AyahItem;
import com.dya.noor.utlis.QuranArabicUtilsd;
import com.dya.noor.utlis.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class AyahAdapter extends RecyclerView.Adapter<AyahAdapter.MyViewHolder> implements Filterable {


    Context context;
    public static boolean activate = true;
    public static boolean switched;
    int subItemVisibility;
    public String SurahNAme, SurahId, SurahLink;
    List<AyahItem> searchText1;
    List<AyahItem> aText;

    String strTypeOfTafseer;
    SharedPreferences TypeOfTafseer;
    SharedPreferences.Editor editorTypeOfTafseer;


    SharedPreferences tafsirPref;
    SharedPreferences.Editor tafsirEditor;

    // Cached tafsir switches
    boolean puxta, asan, raman, zhian, hazhar, rebar,
            sanahi, tawhid, muyesar, roshn, muxtasar, runahy , sanhay;

    // Cached text sizes
    int TextSize;
    int quranTextSize;


    // In your Adapter:
    private boolean showAmazha; // Add this field


    private int highlightedAyah = -1;





    public void setHighlightedAyah(int position) {
        int oldPos = this.highlightedAyah;
        this.highlightedAyah = position;
        notifyItemChanged(oldPos);
        notifyItemChanged(highlightedAyah);
    }

    public int getPositionByAyah(int ayahNumber) {
        for (int i = 0; i < aText.size(); i++) {
            try {
                if (Integer.parseInt(aText.get(i).getAyahNumber()) == ayahNumber) {
                    return i;
                }
            } catch (Exception ignored) {}
        }
        return -1;
    }


    public AyahAdapter(Context ct, List<AyahItem> iText) {

        context = ct;
        aText = iText;
        this.searchText1 = new ArrayList<>(iText);
        setHasStableIds(true);

        // Tafsir preferences
        tafsirPref = context.getSharedPreferences("tafsir_switches", MODE_PRIVATE);
        TypeOfTafseer = context.getSharedPreferences("TypeOfTafseer", MODE_PRIVATE);

        strTypeOfTafseer = TypeOfTafseer.getString("TafseerType", "new");

        // Load tafsir switches once
        puxta = tafsirPref.getBoolean("puxta", false);
        asan = tafsirPref.getBoolean("asan", false);
        raman = tafsirPref.getBoolean("raman", false);
        zhian = tafsirPref.getBoolean("jyan", false);
        hazhar = tafsirPref.getBoolean("hajar", false);
        rebar = tafsirPref.getBoolean("rebar", false);
        sanahi = tafsirPref.getBoolean("sanahi", false);
        tawhid = tafsirPref.getBoolean("tawhid", false);
        muyesar = tafsirPref.getBoolean("muyesar", false);
        roshn = tafsirPref.getBoolean("roshn", false);
        muxtasar = tafsirPref.getBoolean("muxtasar", false);
        sanhay = tafsirPref.getBoolean("runahy", false);

        // Load text sizes once
        SharedPreferences sharedPreferencesSize = context.getSharedPreferences("size", MODE_PRIVATE);
        TextSize = sharedPreferencesSize.getInt("textSize", 20);
        quranTextSize = sharedPreferencesSize.getInt("quransize", 27);

        new Utils(ct);
    }

    public void setShowAmazha(boolean showAmazha) {
        this.showAmazha = showAmazha;
        notifyDataSetChanged(); // Crucial: Notify the adapter!
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_ayah_row, parent, false);
        return new MyViewHolder(view);
    }


    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AyahItem itemData = aText.get(position);


        String itemPosition = itemData.getAyahNumber();







        if (position == highlightedAyah) {
            holder.itemView.setBackgroundColor(Color.parseColor("#2000FF00")); // Very light green
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }


        holder.btnSave.setOnClickListener(v -> {


            // Save the position to shared preferences
            SharedPreferences sharedPref = context.getSharedPreferences("QuranPagePreferences",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("recycler_view_position", itemPosition);
            editor.putString("suraName", SurahNAme);
            editor.putString("suraId", SurahId);
            editor.putString("suraLink", SurahLink);
            editor.apply();
            Toast.makeText(context, "کۆتا خوێندنەوە ئایەتی : " + itemPosition + "  لە " + SurahNAme, Toast.LENGTH_SHORT).show();
        });


        holder.mNum.setText(itemData.getSuraId()+" : "+itemData.getAyahNumber());
        NumberFormat ArNum = NumberFormat.getInstance(Locale.forLanguageTag("AR"));


        //بۆتەجویید
        holder.mAyah.setTextSize(TypedValue.COMPLEX_UNIT_SP, quranTextSize);
        holder.krd.setTextSize(TypedValue.COMPLEX_UNIT_SP, TextSize);

        holder.mAyah.setText(itemData.getTajweed(context));
        holder.amazha.setText(itemData.getSearch());
        holder.juz_page.setText("پەڕەی "+itemData.getPage()+" | جزء "+itemData.getJuz());


        holder.krd.setText(itemData.getKrdSpanned());
        holder.krd.setVisibility(subItemVisibility);
        holder.krd2.setVisibility(subItemVisibility);


        if (showAmazha) {
            holder.amazha.setVisibility(VISIBLE);
            holder.amazha_title.setVisibility(VISIBLE);
        } else {
            holder.amazha.setVisibility(GONE);
            holder.amazha_title.setVisibility(GONE);
        }

        SpannableStringBuilder tafseerBuilder = new SpannableStringBuilder();

        if (Objects.equals(strTypeOfTafseer, "old")) {

            if (puxta)   addTafseer(tafseerBuilder, "تەفسیری پوختە", itemData.getPuxta());
            if (asan)    addTafseer(tafseerBuilder, "تەفسیری ئاسان", itemData.getAsan());
            if (raman)   addTafseer(tafseerBuilder, "تەفسیری ڕامان", itemData.getRaman());
            if (zhian)   addTafseer(tafseerBuilder, "تەفسیری ژیان", itemData.getZhin());
            if (hazhar)  addTafseer(tafseerBuilder, "تەفسیری هەژار", itemData.getHazhar());
            if (rebar)   addTafseer(tafseerBuilder, "تەفسیری ڕێبەر", itemData.getRebar());
            if (sanahi)  addTafseer(tafseerBuilder, "تەفسیری سەناهی", itemData.getSanahi());
            if (tawhid)  addTafseer(tafseerBuilder, "تەفسیری تەوحید", itemData.getTawhid());
            if (muyesar) addTafseer(tafseerBuilder, "تەفسیری مویەسەر", itemData.getMaisar());
            if (roshn)   addTafseer(tafseerBuilder, "تەفسیری ڕۆشن", itemData.getRoshn());
            if (muxtasar)addTafseer(tafseerBuilder, "تەفسیری موختەسەر", itemData.getMokhtasar());
            if (runahy)  addTafseer(tafseerBuilder, "تەفسیری ڕوناهی", itemData.getRunahi());
        }

        if (tafseerBuilder.length() > 0) {
            holder.allTafseer.setText(tafseerBuilder); // keep spans (colors)
            holder.allTafseer.setVisibility(View.VISIBLE);
        } else {
            holder.allTafseer.setVisibility(View.GONE);
        }


        holder.ayaLayout.setOnClickListener(v -> {

            if (Objects.equals(strTypeOfTafseer, "new")){


                BottomSheetDialog dialog = new BottomSheetDialog(v.getContext(), R.style.BottomSheetStyle);
                dialog.setContentView(R.layout.bottomshet_dialog_tafser);

                TextView tvH = dialog.findViewById(R.id.tvHAyah);
                TextView tvHName = dialog.findViewById(R.id.tvHName);
                TextView tvHAyahAr = dialog.findViewById(R.id.tvHAyahAr);

                ImageButton btnCopyAll = dialog.findViewById(R.id.btnCopyAll);

                ImageButton btnForward = dialog.findViewById(R.id.btnForward);
                ImageButton btnBackward = dialog.findViewById(R.id.btnBackward);


                tvH.setTextSize(TypedValue.COMPLEX_UNIT_SP, TextSize);
                tvHAyahAr.setTextSize(TypedValue.COMPLEX_UNIT_SP, quranTextSize);

                // Show the BottomSheetDialog.
                if (Utils.getStringPref("help", "").equals("raman")) {
                    tvH.setText(itemData.getRaman());
                    tvHAyahAr.setText(itemData.getText() + "(" + itemData.getAyahNumber() + ")");
                    tvHName.setText("تەفسیری ڕامان");
                    dialog.show();

                }
                else if (Utils.getStringPref("help", "").equals("puxta")) {
                    tvH.setText(itemData.getPuxta());
                    tvHAyahAr.setText(itemData.getText() + "(" + itemData.getAyahNumber() + ")");
                    tvHName.setText("تەفسیری پوختە");
                    dialog.show();


                }
                else if (Utils.getStringPref("help", "").equals("asan")) {
                    tvH.setText(itemData.getAsan());
                    tvHAyahAr.setText(itemData.getText() + "(" + itemData.getAyahNumber() + ")");
                    tvHName.setText("تەفسیری ئاسان");
                    dialog.show();
                }
                else if (Utils.getStringPref("help", "").equals("sanahi")) {
                    tvH.setText(itemData.getSanahi());
                    tvHAyahAr.setText(itemData.getText() + "(" + itemData.getAyahNumber() + ")");
                    tvHName.setText("تەفسیری سەناهی");
                    dialog.show();
                }
                else if (Utils.getStringPref("help", "").equals("zhin")) {

                    tvH.setText(itemData.getZhin());
                    tvHName.setText("تەفسیری ژیان");
                    tvHAyahAr.setText(itemData.getText() + "(" + itemData.getAyahNumber() + ")");
                    dialog.show();

                }
                else if (Utils.getStringPref("help", "").equals("hazhar")) {
                    tvH.setText(itemData.getHazhar());
                    tvHAyahAr.setText(itemData.getText() + "(" + itemData.getAyahNumber() + ")");
                    tvHName.setText("تەفسیری هەژار");
                    dialog.show();
                }
                else if (Utils.getStringPref("help", "").equals("rebar")) {
                    tvH.setText(itemData.getRebar().replaceAll("<br>", "\n"));
                    tvHAyahAr.setText(itemData.getText() + "(" + itemData.getAyahNumber() + ")");
                    tvHName.setText("تەفسیری ڕێبەر");
                    dialog.show();
                }
                else if (Utils.getStringPref("help", "").equals("tawhid")) {
                    tvH.setText(itemData.getTawhid().replaceAll("<br>", "\n"));
                    tvHAyahAr.setText(itemData.getText() + "(" + itemData.getAyahNumber() + ")");
                    tvHName.setText("تەفسیری تەوحیدی");
                    dialog.show();
                }
                else if (Utils.getStringPref("help", "").equals("roshn")) {
                    tvH.setText(itemData.getRoshn().replaceAll("<br>", "\n"));
                    tvHAyahAr.setText(itemData.getText() + "(" + itemData.getAyahNumber() + ")");
                    tvHName.setText("تەفسیری ڕۆشن");
                    dialog.show();

                }
                else if (Utils.getStringPref("help", "").equals("maisar")) {
                    tvH.setText(itemData.getMaisar().replaceAll("<br>", "\n"));
                    tvHAyahAr.setText(itemData.getText() + "(" + itemData.getAyahNumber() + ")");
                    tvHName.setText("تەفسیری مویەسەر");

                    dialog.show();

                }
                else if (Utils.getStringPref("help", "").equals("runahi")) {
                    tvH.setText(itemData.getRunahi().replaceAll("<br>", "\n"));
                    tvHAyahAr.setText(itemData.getText() + "(" + itemData.getAyahNumber() + ")");
                    tvHName.setText("تەفسیری ڕوناهی");

                    dialog.show();

                }
                else if (Utils.getStringPref("help", "").equals("mokhtasar")) {
                    tvH.setText(itemData.getMokhtasar().replaceAll("\n", "\n"));
                    tvHAyahAr.setText(itemData.getText() + "(" + itemData.getAyahNumber() + ")");
                    tvHName.setText("تەفسیری موختەسەر");

                    dialog.show();

                }
                else if (Utils.getStringPref("help", "").equals("hich")) {
                    Toast.makeText(context, "هیچ تەفسیرێک دیاری نەکراوە!", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(context, "هیچ تەفسیرێک دیاری نەکراوە!", Toast.LENGTH_SHORT).show();
                }




                btnCopyAll.setOnClickListener(v1 -> {

                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(
                            Context.CLIPBOARD_SERVICE
                    );
                    ClipData clipData;

                    clipData = ClipData.newPlainText("text", tvHAyahAr.getText() + "\n" + tvH.getText() + "\n" + "\n" + SurahNAme + "\n" + "\n" + "[" + tvHName.getText() + "]" + "\n" + "\n" + "#ئەپڵیکەیشنی_نور");
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(context, "کۆپی بوو", Toast.LENGTH_SHORT).show();



                });

                AtomicInteger currentPosition = new AtomicInteger(holder.getAdapterPosition());

                btnForward.setOnClickListener(v1 -> {

                    int position2 = currentPosition.get();


                    if (position2 < aText.size() - 1) {
                        currentPosition.getAndIncrement();
                        position2 = currentPosition.get();
                        AyahItem nextItemData = aText.get(position2);
                        if (Utils.getStringPref("help", "").equals("raman")) {


                            tvH.setText(nextItemData.getRaman().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری ڕامان");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("puxta")) {

                            tvH.setText(nextItemData.getPuxta().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری پوختە");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("asan")) {
                            tvH.setText(nextItemData.getAsan().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری ئاسان");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("sanahi")) {

                            tvH.setText(nextItemData.getSanahi().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری سەناهی");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("zhin")) {

                            tvH.setText(nextItemData.getZhin().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری ژیان");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("hazhar")) {

                            tvH.setText(nextItemData.getHazhar().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری هەژار");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("rebar")) {

                            tvH.setText(nextItemData.getRebar().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری ڕابەر");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("tawhid")) {

                            tvH.setText(nextItemData.getTawhid().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری تەوحیدی");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("roshn")) {
                            tvH.setText(nextItemData.getRoshn().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری ڕۆشن");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("maisar")) {
                            tvH.setText(nextItemData.getMaisar().replaceAll("\\n", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری مویەسەر");
                            dialog.show();

                        }if (Utils.getStringPref("help", "").equals("runahi")) {
                            tvH.setText(nextItemData.getRunahi().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری ڕوناهی");
                            dialog.show();

                        }if (Utils.getStringPref("help", "").equals("mokhtasar")) {
                            tvH.setText(nextItemData.getMokhtasar().replaceAll("\n", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری موختەسەر");
                            dialog.show();

                        }
                    }

                });


                btnBackward.setOnClickListener(v1 -> {

                    int position3 = currentPosition.get();


                    if (position3 > 0) {
                        currentPosition.decrementAndGet();
                        position3 = currentPosition.get();
                        AyahItem nextItemData = aText.get(position3);
                        if (Utils.getStringPref("help", "").equals("raman")) {


                            tvH.setText(nextItemData.getRaman().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری ڕامان");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("puxta")) {

                            tvH.setText(nextItemData.getPuxta().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری پوختە");

                            dialog.show();


                        }
                        if (Utils.getStringPref("help", "").equals("asan")) {
                            tvH.setText(nextItemData.getAsan().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری ئاسان");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("sanahi")) {

                            tvH.setText(nextItemData.getSanahi().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری سەناهی");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("zhin")) {

                            tvH.setText(nextItemData.getZhin().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری ژیان");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("hazhar")) {

                            tvH.setText(nextItemData.getHazhar().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری هەژار");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("rebar")) {

                            tvH.setText(nextItemData.getRebar().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری ڕابەر");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("tawhid")) {

                            tvH.setText(nextItemData.getTawhid().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری تەوحیدی");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("roshn")) {

                            tvH.setText(nextItemData.getRoshn().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری ڕۆشن");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("maisar")) {
                            tvH.setText(nextItemData.getMaisar().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری مویەسەر");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("runahi")) {
                            tvH.setText(nextItemData.getRunahi().replaceAll("<br>", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری ڕوناهی");
                            dialog.show();

                        }
                        if (Utils.getStringPref("help", "").equals("mokhtasar")) {
                            tvH.setText(nextItemData.getMokhtasar().replaceAll("\n", "\n"));
                            tvHAyahAr.setText(nextItemData.getText() + "(" + nextItemData.getAyahNumber() + ")");
                            tvHName.setText("تەفسیری موختەسەر");
                            dialog.show();

                        }
                    }


                });

            }});
        holder.btnCopy.setOnClickListener(view -> {

            StringBuilder copyText = new StringBuilder();

            // Ayah
            copyText.append(itemData.getText())
                    .append(" (")
                    .append(itemData.getAyahNumber())
                    .append(")\n\n");

            // If OLD tafseer mode copy selected tafseer
            if (Objects.equals(strTypeOfTafseer, "old")) {

                if (puxta)
                    copyText.append("تەفسیری پوختە\n")
                            .append(itemData.getPuxta())
                            .append("\n\n");

                if (asan)
                    copyText.append("تەفسیری ئاسان\n")
                            .append(itemData.getAsan())
                            .append("\n\n");

                if (raman)
                    copyText.append("تەفسیری ڕامان\n")
                            .append(itemData.getRaman())
                            .append("\n\n");

                if (zhian)
                    copyText.append("تەفسیری ژیان\n")
                            .append(itemData.getZhin())
                            .append("\n\n");

                if (hazhar)
                    copyText.append("تەفسیری هەژار\n")
                            .append(itemData.getHazhar())
                            .append("\n\n");

                if (rebar)
                    copyText.append("تەفسیری ڕێبەر\n")
                            .append(itemData.getRebar())
                            .append("\n\n");

                if (sanahi)
                    copyText.append("تەفسیری سەناهی\n")
                            .append(itemData.getSanahi())
                            .append("\n\n");

                if (tawhid)
                    copyText.append("تەفسیری تەوحید\n")
                            .append(itemData.getTawhid())
                            .append("\n\n");

                if (muyesar)
                    copyText.append("تەفسیری مویەسەر\n")
                            .append(itemData.getMaisar())
                            .append("\n\n");

                if (roshn)
                    copyText.append("تەفسیری ڕۆشن\n")
                            .append(itemData.getRoshn())
                            .append("\n\n");

                if (muxtasar)
                    copyText.append("تەفسیری موختەسەر\n")
                            .append(itemData.getMokhtasar())
                            .append("\n\n");

                if (runahy)
                    copyText.append("تەفسیری ڕوناهی\n")
                            .append(itemData.getRunahi())
                            .append("\n\n");
            }

            copyText.append("<<").append(SurahNAme).append(">>\n");
            copyText.append("#ئەپڵیکەیشنی_نور");

            ClipboardManager clipboardManager =
                    (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

            ClipData clipData = ClipData.newPlainText("text", copyText.toString());
            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(context, "کۆپی بوو", Toast.LENGTH_SHORT).show();
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void activateButton(boolean activate) {

        this.activate = activate;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void setSubItemVisibility(int visibility) {
        this.subItemVisibility = visibility;
    }


    public interface OnButtonClickListener{
        void onButtonClick(int position);
        void onAyahClick(int ayahNumber, String ayahText);
    }


    @Override
    public int getItemCount() {
        return aText.size();

    }



    private void addTafseer(SpannableStringBuilder builder, String title, String text) {

        if (text == null || text.trim().isEmpty()) return;

        int start = builder.length();

        builder.append(title).append("\n");

        builder.setSpan(
                new ForegroundColorSpan(Color.GRAY),
                start,
                start + title.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        builder.append(text).append("\n\n");
    }



    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<AyahItem> filterList = new ArrayList<>();

            if (charSequence.toString().isEmpty()) {
                filterList.addAll(searchText1);
            } else {

                for (AyahItem text : searchText1) {
                    if (text.getSearch().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filterList.add(text);
                    } else if (text.getAyahNumber().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filterList.add(text);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            aText.clear();
            aText.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public void refreshTafsirSwitches() {

        strTypeOfTafseer = TypeOfTafseer.getString("TafseerType", "new");
        puxta = tafsirPref.getBoolean("puxta", false);
        asan = tafsirPref.getBoolean("asan", false);
        raman = tafsirPref.getBoolean("raman", false);
        zhian = tafsirPref.getBoolean("jyan", false);
        hazhar = tafsirPref.getBoolean("hajar", false);
        rebar = tafsirPref.getBoolean("rebar", false);
        sanahi = tafsirPref.getBoolean("sanhay", false);
        tawhid = tafsirPref.getBoolean("tawhid", false);
        muyesar = tafsirPref.getBoolean("muyesar", false);
        roshn = tafsirPref.getBoolean("roshn", false);
        muxtasar = tafsirPref.getBoolean("muxtasar", false);
        runahy = tafsirPref.getBoolean("runahy", false);


        notifyDataSetChanged();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mAyah;
        TextView mNum , krd ,krd2 , amazha , amazha_title;
        ImageButton btnCopy, btnSave;
        LinearLayout ayaLayout;
        TextView allTafseer , juz_page;




        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mAyah = itemView.findViewById(R.id.qurann);
            mNum = itemView.findViewById(R.id.ayanum);
            krd = itemView.findViewById(R.id.krd);
            krd2 = itemView.findViewById(R.id.krd2);
            amazha = itemView.findViewById(R.id.amazha);
            amazha_title = itemView.findViewById(R.id.amazha_title);

            btnCopy = itemView.findViewById(R.id.btnCopy);
            btnSave = itemView.findViewById(R.id.btnSave);
            ayaLayout = itemView.findViewById(R.id.ayaLayout);
            allTafseer = itemView.findViewById(R.id.allTafseer);
            juz_page = itemView.findViewById(R.id.juz_page);


        }
    }


}