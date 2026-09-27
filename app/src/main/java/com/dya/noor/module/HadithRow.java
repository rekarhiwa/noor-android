package com.dya.noor.module;

public class HadithRow {
    public static final int TYPE_CHAPTER = 0;
    public static final int TYPE_HADITH = 1;

    public int type;

    // chapter
    public String chapterTitleAr;
    public String chapterTitleKr;
    public int chapterSort;

    // hadith
    public HadithItem hadith;

    public HadithRow(int type) {
        this.type = type;
    }
}
