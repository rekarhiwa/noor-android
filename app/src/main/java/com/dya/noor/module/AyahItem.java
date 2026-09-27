package com.dya.noor.module;

import android.content.Context;
import android.text.Spanned;

import androidx.core.text.HtmlCompat;

import com.dya.noor.utlis.QuranArabicUtilsd;

public class AyahItem {


    private final String Text;
    private final String SuraId;
    private final String AyahNumber;
    private final String Raman;
    private final String Puxta;
    private final String Asan;
    private final String Sanahi;
    private final String Zhin;
    private final String Hazhar;
    private final String Rebar;

    private final String Search;
    private final String Tawhid;
    private final String Roshn;
    private final String Maisar;
    private final String Krd;
    private final String Runahi;
    private final String Juz;
    private final String Page;

    public String getPage() {
        return Page;
    }

    public String getJuz() {
        return Juz;
    }

    private final String mokhtasar;

    private Spanned krdSpanned;

    private CharSequence tajweedText;




    public AyahItem(String suraId,String juz, String page, String text, String ayahNumber, String rebar, String raman, String puxta, String asan,
                    String sanahi, String hazhar, String zhin , String search , String tawhid, String roshn, String maisar,
                    String krd, String runahi,  String mokhtasar) {

        SuraId = suraId;
        Text = text;
        AyahNumber = ayahNumber;
        Raman = raman;
        Puxta = puxta;
        Asan = asan;
        Sanahi = sanahi;
        Zhin = zhin;
        Hazhar = hazhar;
        Rebar = rebar;
        Search = search;
        Tawhid = tawhid;
        Roshn = roshn;
        Maisar = maisar;
        Krd = krd;
        Runahi = runahi;
        Juz = juz;
        Page = page;
        this.mokhtasar = mokhtasar;
    }


    public CharSequence getTajweed(Context context) {
        if (tajweedText == null) {
            tajweedText = QuranArabicUtilsd.getTajweed(Text, context);
        }
        return tajweedText;
    }
    public Spanned getKrdSpanned() {
        if (krdSpanned == null) {
            krdSpanned = HtmlCompat.fromHtml(Krd, HtmlCompat.FROM_HTML_MODE_LEGACY);
        }
        return krdSpanned;
    }
    public String getText() {
        return Text;
    }

    public String getAyahNumber() {
        return AyahNumber;
    }

    public String getRaman() {
        return Raman;
    }

    public String getPuxta() {
        return Puxta;
    }

    public String getAsan() {
        return Asan;
    }

    public String getSanahi() {
        return Sanahi;
    }

    public String getZhin() {
        return Zhin;
    }

    public String getHazhar() {
        return Hazhar;
    }

    public String getRebar() {
        return Rebar;
    }
    public String getSearch() {
        return Search;
    }
    public String getTawhid() {
        return Tawhid;
    }
    public String getRoshn() {
        return Roshn;
    }
    public String getMaisar() {
        return Maisar;
    }

    public String getKrd() {
        return Krd;
    }

    public String getRunahi() {
        return Runahi;
    }
    public String getMokhtasar() {
        return mokhtasar;
    }

    public String getSuraId() {
        return SuraId;
    }
}
