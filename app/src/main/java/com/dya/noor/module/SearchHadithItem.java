package com.dya.noor.module;

public class SearchHadithItem {


    int id;
    int book_id;
    int kitab_id;
    String normalized_text_ar;
    String normalized_text;

    String kitabName;
    String BookNAme;

    public SearchHadithItem(int id, int book_id, int kitab_id, String normalized_text_ar,
                            String normalized_text, String kitabName, String bookNAme) {
        this.id = id;
        this.book_id = book_id;
        this.kitab_id = kitab_id;
        this.normalized_text_ar = normalized_text_ar;
        this.normalized_text = normalized_text;
        this.kitabName = kitabName;
        BookNAme = bookNAme;
    }

    public int getId() {
        return id;
    }

    public int getBook_id() {
        return book_id;
    }

    public int getKitab_id() {
        return kitab_id;
    }

    public String getNormalized_text_ar() {
        return normalized_text_ar;
    }

    public String getNormalized_text() {
        return normalized_text;
    }

    public String getKitabName() {
        return kitabName;
    }

    public String getBookNAme() {
        return BookNAme;
    }
}
