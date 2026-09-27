package com.dya.noor.module;

public class HadithItem {

    int id;
    int sort;
    int book_id;
    String sort_in_book;
    int kitab_id;
    int chapter_id;
    String text_kr;
    String text_ar;
    String note;
    String footnote;
    String normalized_text_ar;
    String normalized_text;


    public HadithItem(int id, int sort, int book_id, String sort_in_book, int kitab_id,
                      int chapter_id, String text_kr, String text_ar,
                      String note, String footnote, String normalized_text_ar,
                      String normalized_text) {
        this.id = id;
        this.sort = sort;
        this.book_id = book_id;
        this.sort_in_book = sort_in_book;
        this.kitab_id = kitab_id;
        this.chapter_id = chapter_id;
        this.text_kr = text_kr;
        this.text_ar = text_ar;
        this.note = note;
        this.footnote = footnote;
        this.normalized_text_ar = normalized_text_ar;
        this.normalized_text = normalized_text;
    }

    public int getId() {
        return id;
    }

    public int getSort() {
        return sort;
    }

    public int getBook_id() {
        return book_id;
    }

    public String getSort_in_book() {
        return sort_in_book;
    }

    public int getKitab_id() {
        return kitab_id;
    }

    public int getChapter_id() {
        return chapter_id;
    }

    public String getText_kr() {
        return text_kr;
    }

    public String getText_ar() {
        return text_ar;
    }

    public String getNote() {
        return note;
    }

    public String getFootnote() {
        return footnote;
    }

    public String getNormalized_text_ar() {
        return normalized_text_ar;
    }

    public String getNormalized_text() {
        return normalized_text;
    }
}
