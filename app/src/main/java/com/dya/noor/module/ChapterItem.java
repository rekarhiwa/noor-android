package com.dya.noor.module;

public class ChapterItem {
    int id;
    int kitab_id;
    int chapter_sort;
    String title_kr;
    String title_ar;

    public ChapterItem(int id, int kitab_id, int chapter_sort,
                       String title_kr, String title_ar) {
        this.id = id;
        this.kitab_id = kitab_id;
        this.chapter_sort = chapter_sort;
        this.title_kr = title_kr;
        this.title_ar = title_ar;
    }

    public int getId() {
        return id;
    }

    public int getKitab_id() {
        return kitab_id;
    }

    public int getChapter_sort() {
        return chapter_sort;
    }

    public String getTitle_kr() {
        return title_kr;
    }

    public String getTitle_ar() {
        return title_ar;
    }
}
