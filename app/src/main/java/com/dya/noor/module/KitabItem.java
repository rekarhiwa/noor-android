package com.dya.noor.module;

public class KitabItem {

    int id;
    int book_id;
    int kitab_sort;
    String title_kr;
    String title_ar;

    public KitabItem(int id, int book_id, int kitab_sort, String title_kr,
                     String title_ar) {
        this.id = id;
        this.book_id = book_id;
        this.kitab_sort = kitab_sort;
        this.title_kr = title_kr;
        this.title_ar = title_ar;
    }

    public int getId() {
        return id;
    }

    public int getBook_id() {
        return book_id;
    }

    public int getKitab_sort() {
        return kitab_sort;
    }

    public String getTitle_kr() {
        return title_kr;
    }

    public String getTitle_ar() {
        return title_ar;
    }
}
